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

package jetbrains.buildServer.nuget.agent.util;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildProcess;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 07.07.11 16:03
 */
public abstract class BuildProcessBase implements BuildProcess {
  private final AtomicBoolean myIsInterrupted = new AtomicBoolean();
  private final AtomicBoolean myIsFinished = new AtomicBoolean();

  public final boolean isInterrupted() {
    return myIsInterrupted.get();
  }

  public final boolean isFinished() {
    return myIsFinished.get();
  }

  public final void interrupt() {
    myIsInterrupted.set(true);
    interruptImpl();
  }

  @NotNull
  public final BuildFinishedStatus waitFor() throws RunBuildException {
    try {
      if (isInterrupted()) return BuildFinishedStatus.INTERRUPTED;
      BuildFinishedStatus status = waitForImpl();
      if (isInterrupted()) return BuildFinishedStatus.INTERRUPTED;

      return status;
    } finally {
      myIsFinished.set(true);
    }
  }

  @NotNull
  protected abstract BuildFinishedStatus waitForImpl() throws RunBuildException;

  protected void interruptImpl() {
  }

  public void start() throws RunBuildException {
  }
}
