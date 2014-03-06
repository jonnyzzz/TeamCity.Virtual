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

package com.jonnyzzz.teamcity.virtual.run;

import com.jonnyzzz.teamcity.virtual.VMConstants;
import com.jonnyzzz.teamcity.virtual.util.util.BuildProcessBase;
import com.jonnyzzz.teamcity.virtual.util.util.CompositeBuildProcess;
import com.jonnyzzz.teamcity.virtual.util.util.impl.CompositeBuildProcessImpl;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildProcess;
import jetbrains.buildServer.agent.BuildRunnerContext;
import org.jetbrains.annotations.NotNull;

import static jetbrains.buildServer.agent.BuildFinishedStatus.FINISHED_SUCCESS;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class VMRunnerFactory {
  private final VMRunner[] myRunners;

  public VMRunnerFactory(@NotNull final VMRunner[] runners) {
    myRunners = runners;
  }

  @NotNull
  public BuildProcess createBuildRunnerProcess(@NotNull final BuildRunnerContext context) throws RunBuildException {
    final CompositeBuildProcess proc = new CompositeBuildProcessImpl();

    final VMRunner runner = getVMBuildProcess(context);

    proc.pushBuildProcess(new BuildProcessBase() {
      @NotNull
      @Override
      protected BuildFinishedStatus waitForImpl() throws RunBuildException {
        context.getBuild().getBuildLogger().message("Staring " + runner.getCaption() + "\u2026");
        return FINISHED_SUCCESS;
      }
    });

    proc.pushBuildProcess(new BuildProcessBase() {
      @NotNull
      @Override
      protected BuildFinishedStatus waitForImpl() throws RunBuildException {
        runner.constructBuildProcess(context, proc);
        return FINISHED_SUCCESS;
      }
    });

    return proc;
  }

  @NotNull
  private VMRunner getVMBuildProcess(@NotNull final BuildRunnerContext context) throws RunBuildException {
    final String vm = context.getRunnerParameters().get(VMConstants.PARAMETER_VM);
    for (VMRunner runner : myRunners) {
      if (runner.getVMName().equals(vm)) {
        return runner;
      }
    }

    throw new RunBuildException("Parameter " + VMConstants.PARAMETER_VM + " in invalid: " + vm);
  }
}
