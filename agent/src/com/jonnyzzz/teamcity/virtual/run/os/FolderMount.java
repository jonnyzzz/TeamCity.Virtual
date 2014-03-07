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

package com.jonnyzzz.teamcity.virtual.run.os;

import com.jonnyzzz.teamcity.virtual.run.RelativePaths;
import com.jonnyzzz.teamcity.virtual.run.VMRunnerContext;
import com.jonnyzzz.teamcity.virtual.util.util.BuildProcessBase;
import com.jonnyzzz.teamcity.virtual.util.util.TryFinallyBuildProcess;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildProgressLogger;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class FolderMount {
  private final OSAware myOS;

  public FolderMount(@NotNull final OSAware OS) {
    myOS = OS;
  }

  public void mountFolders(@NotNull final VMRunnerContext context,
                           @NotNull final BuildProgressLogger logger,
                           @NotNull final TryFinallyBuildProcess builder,
                           @NotNull final WithMount continuation) throws RunBuildException {

    builder.addTryProcess(new BuildProcessBase() {
      @NotNull
      @Override
      protected BuildFinishedStatus waitForImpl() throws RunBuildException {
        final OSSpecific specifics = myOS.findSpecifics(context);


        final File mountRoot = context.getCheckoutDirectory();
        final String basePath = specifics.getMountBasePath(context);
        final String workPath = specifics.getMountWorkPath(context, RelativePaths.resolveRelativePath(mountRoot, context.getWorkingDirectory()));

        logger.message("Mounting " + mountRoot + " into " + basePath);
        logger.message("Machine working directory: " + workPath);

        continuation.mount(mountRoot, basePath, workPath);
        return BuildFinishedStatus.FINISHED_SUCCESS;
      }
    });
  }


  public interface WithMount {
    void mount(@NotNull final File path,
               @NotNull final String guestBasePath,
               @NotNull final String guestWorkDir);
  }
}
