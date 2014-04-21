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

import com.jonnyzzz.teamcity.virtual.util.util.DelegatingBuildProcess;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildProcess;
import jetbrains.buildServer.agent.BuildProgressLogger;
import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class BaseVM {
  @NotNull
  public static BuildProcess block(@NotNull final BuildProgressLogger logger,
                                   @NotNull final String blockName,
                                   @NotNull final String blockText,
                                   @NotNull final BuildProcess proc) {
    return new DelegatingBuildProcess(new DelegatingBuildProcess.Action() {
      @NotNull
      @Override
      public BuildProcess startImpl() throws RunBuildException {
        logger.activityStarted(blockName, blockText, "vm");
        return proc;
      }

      @Override
      public void finishedImpl() {
        logger.activityFinished(blockName, "vm");
      }
    });
  }

}
