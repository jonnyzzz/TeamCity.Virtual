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

package com.jonnyzzz.teamcity.virtual.util.util;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildProcess;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 07.07.11 18:41
 */
public class DelegatingBuildProcess extends BuildProcessBase {
  private final AtomicReference<BuildProcess> myReference = new AtomicReference<BuildProcess>();
  private final Action myAction;

  public DelegatingBuildProcess(@NotNull final Action action) {
    myAction = action;
  }

  @Override
  protected final void interruptImpl() {
    super.interruptImpl();
    final BuildProcess process = myReference.get();
    if (process != null) process.interrupt();
  }

  @NotNull
  @Override
  protected final BuildFinishedStatus waitForImpl() throws RunBuildException {
    try {
      final BuildProcess process = myAction.startImpl();
      myReference.set(process);

      if (isInterrupted()) return BuildFinishedStatus.INTERRUPTED;
      process.start();
      return process.waitFor();
    } finally {
      myReference.set(null);
      myAction.finishedImpl();
    }
  }

  public static interface Action {
    @NotNull
    BuildProcess startImpl() throws RunBuildException;
    void finishedImpl();
  }

  public static abstract class ActionAdapter implements Action {
    @Override
    public void finishedImpl() {
      //NOP
    }
  }
}
