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

import com.jonnyzzz.teamcity.virtual.util.util.BuildProcessBase;
import com.jonnyzzz.teamcity.virtual.util.util.TryFinallyBuildProcess;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.util.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class ScriptFile {

  public interface Builder {
    void buildWithScriptFile(@NotNull final File script) throws RunBuildException;
  }

  public void generateScriptFile(@NotNull final VMRunnerContext context,
                                 @NotNull final TryFinallyBuildProcess builder,
                                 @NotNull final Builder continuation) {

    builder.addTryProcess(new BuildProcessBase() {
      @NotNull
      @Override
      protected BuildFinishedStatus waitForImpl() throws RunBuildException {
        final File file = createFile();

        try {
          FileUtil.writeFileAndReportErrors(file, context.getScript());
        } catch (IOException e) {
          throw new RunBuildException("Failed to generate temp file for script. " + e.getMessage(), e);
        }

        builder.addTryProcess(new BuildProcessBase() {
          @NotNull
          @Override
          protected BuildFinishedStatus waitForImpl() throws RunBuildException {
            continuation.buildWithScriptFile(file);
            return BuildFinishedStatus.FINISHED_SUCCESS;
          }
        });

        builder.addFinishProcess(removeFileProcess(file));
        return BuildFinishedStatus.FINISHED_SUCCESS;
      }

      private BuildProcessBase removeFileProcess(final File file) {
        return new BuildProcessBase() {
          @NotNull
          @Override
          protected BuildFinishedStatus waitForImpl() throws RunBuildException {
            FileUtil.delete(file);
            return BuildFinishedStatus.FINISHED_SUCCESS;
          }
        };
      }

      @NotNull
      private File createFile() throws RunBuildException {
        try {
          //TODO: make sure extension is OK for every OK (even Windows, MacOS, Linux)
          return FileUtil.createTempFile(context.getWorkingDirectory(), "teamcity", ".build.cmd", true);
        } catch (IOException e) {
          throw new RunBuildException("Failed to generate temp file for script. " + e.getMessage(), e);
        }
      }
    });
  }
}
