/*
 * Copyright 2000-2014 Eugene Petrenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jonnyzzz.teamcity.virtual.util.util;

import com.jonnyzzz.teamcity.virtual.Loggers;
import com.jonnyzzz.teamcity.virtual.util.util.impl.CompositeBuildProcessImpl;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildProcess;
import jetbrains.buildServer.agent.BuildProgressLogger;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class TryFinallyBuildProcessImpl implements TryFinallyBuildProcess {

  private final ErrorLogger myLogger;
  private final CompositeBuildProcess myTryProcess = new CompositeBuildProcessImpl();
  private final CompositeBuildProcess myFinallyProcess = new CompositeBuildProcessImpl();

  public TryFinallyBuildProcessImpl(@NotNull final ErrorLogger logger) {
    myLogger = logger;
  }

  @NotNull
  public BuildProcess asBuildProcess() {
    return new DelegatingBuildProcess(new DelegatingBuildProcess.Action() {
      @NotNull
      @Override
      public BuildProcess startImpl() throws RunBuildException {
        return myTryProcess;
      }

      @Override
      public void finishedImpl() {
        catchIt("Failed to execute finally steps", new Action() {
          @Override
          public void execute() throws RunBuildException {
            myFinallyProcess.start();
          }
        });
        catchIt("Failed to execute finally steps", new Action() {
          @Override
          public void execute() throws RunBuildException {
            myFinallyProcess.waitFor();
          }
        });
      }
    });
  }

  @Override
  public void addTryProcess(@NotNull final BuildProcess proc) {
    myTryProcess.pushBuildProcess(proc);
  }

  @Override
  public void addFinishProcess(@NotNull final BuildProcess proc) {
    myFinallyProcess.pushBuildProcess(new BuildProcessBase() {
      @Override
      public void start() throws RunBuildException {
        super.start();
        catchIt("Finally process failed", new Action() {
          @Override
          public void execute() throws RunBuildException {
            proc.start();
          }
        });
      }

      @NotNull
      @Override
      protected BuildFinishedStatus waitForImpl() throws RunBuildException {
        catchIt("Finally process failed", new Action() {
          @Override
          public void execute() throws RunBuildException {
            proc.waitFor();
          }
        });
        return BuildFinishedStatus.FINISHED_SUCCESS;
      }
    });
  }


  private interface Action {
    void execute() throws RunBuildException;
  }

  private void catchIt(@NotNull final String message, @NotNull final Action action) {
    try {
      action.execute();
    } catch (Throwable e) {
      myLogger.onError(message, e);
    }
  }

  public interface ErrorLogger {
    void onError(@NotNull final String message, @NotNull final Throwable error);
  }

  public static class RunnerErrorLogger implements ErrorLogger {
    private final Logger LOG = Loggers.getLogger(RunnerErrorLogger.class);
    private BuildProgressLogger myLogger;

    public RunnerErrorLogger(@NotNull final BuildProgressLogger logger) {
      myLogger = logger;
    }

    @Override
    public void onError(@NotNull final String message, @NotNull final Throwable e) {
      myLogger.error(message + ". " + e.getMessage());
      LOG.warn(message + ". " + e.getMessage(), e);
    }
  }

}
