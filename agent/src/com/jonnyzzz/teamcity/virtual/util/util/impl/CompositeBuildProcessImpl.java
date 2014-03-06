/*
 * Copyright 2000-2012 JetBrains s.r.o.
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

package com.jonnyzzz.teamcity.virtual.util.util.impl;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildProcess;
import com.jonnyzzz.teamcity.virtual.util.util.BuildProcessBase;
import com.jonnyzzz.teamcity.virtual.util.util.CompositeBuildProcess;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 07.07.11 14:04
 */
public class CompositeBuildProcessImpl extends BuildProcessBase implements CompositeBuildProcess {
  private final BlockingQueue<BuildProcess> myProcessList = new LinkedBlockingQueue<BuildProcess>();
  private final AtomicReference<BuildProcess> myCurrentProcess = new AtomicReference<BuildProcess>();

  public void pushBuildProcess(@NotNull final BuildProcess process) {
    myProcessList.add(process);
  }

  @Override
  protected void interruptImpl() {
    BuildProcess process = myCurrentProcess.get();
    if (process != null) {
      process.interrupt();
    }
  }

  public void start() throws RunBuildException {
  }

  @NotNull
  protected BuildFinishedStatus waitForImpl() throws RunBuildException {
    if (isInterrupted()) return BuildFinishedStatus.INTERRUPTED;
    for (BuildProcess proc = myProcessList.poll(); proc != null; proc = myProcessList.poll()) {
      myCurrentProcess.set(proc);
      try {
        proc.start();
        final BuildFinishedStatus status = proc.waitFor();
        if (status != BuildFinishedStatus.INTERRUPTED && status != BuildFinishedStatus.FINISHED_SUCCESS) return status;
      } finally {
        myCurrentProcess.set(null);
      }
      if (isInterrupted()) return BuildFinishedStatus.INTERRUPTED;
    }
    if (isInterrupted()) return BuildFinishedStatus.INTERRUPTED;
    return BuildFinishedStatus.FINISHED_SUCCESS;
  }
}
