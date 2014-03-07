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

import com.jonnyzzz.teamcity.virtual.run.ScriptFile;
import com.jonnyzzz.teamcity.virtual.run.VMRunnerContext;
import com.jonnyzzz.teamcity.virtual.util.util.BuildProcessBase;
import com.jonnyzzz.teamcity.virtual.util.util.TryFinallyBuildProcess;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class OSRun {
  private final OSAware myOS;
  private final ScriptFile myScriptFile;

  public OSRun(@NotNull final OSAware OS,
               @NotNull final ScriptFile scriptFile) {
    myOS = OS;
    myScriptFile = scriptFile;
  }


  public void generateGuestCommandline(@NotNull final VMRunnerContext context,
                                       @NotNull final String guestWorkDir,
                                       @NotNull final TryFinallyBuildProcess builder,
                                       @NotNull final WithCommand continuation) {

    builder.addTryProcess(new BuildProcessBase() {
      @NotNull
      @Override
      protected BuildFinishedStatus waitForImpl() throws RunBuildException {
        final OSSpecific spec = myOS.findSpecifics(context);

        myScriptFile.generateScriptFile(context, builder, new ScriptFile.Builder() {
          @Override
          public void buildWithScriptFile(@NotNull File script) throws RunBuildException {
            continuation.withCommand(spec.runScript(guestWorkDir, script.getName()));
          }
        });
        return BuildFinishedStatus.FINISHED_SUCCESS;
      }
    });


  }


  public interface WithCommand {
    void withCommand(@NotNull final String command);
  }
}
