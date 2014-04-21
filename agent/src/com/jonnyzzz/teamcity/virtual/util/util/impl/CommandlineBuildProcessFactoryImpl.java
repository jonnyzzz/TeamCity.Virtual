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
import jetbrains.buildServer.agent.BuildProcess;
import jetbrains.buildServer.agent.BuildProcessFacade;
import jetbrains.buildServer.agent.BuildRunnerContext;
import com.jonnyzzz.teamcity.virtual.util.util.CommandlineBuildProcessFactory;
import jetbrains.buildServer.runner.SimpleRunnerConstants;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 07.07.11 15:13
 */
public class CommandlineBuildProcessFactoryImpl implements CommandlineBuildProcessFactory {
  private final BuildProcessFacade myFacade;

  public CommandlineBuildProcessFactoryImpl(@NotNull final BuildProcessFacade facade) {
    myFacade = facade;
  }

  @NotNull
  public BuildProcess executeCommandLine(@NotNull BuildRunnerContext hostContext,
                                         @NotNull Collection<String> argz,
                                         @NotNull File workingDir,
                                         @NotNull final Map<String, String> additionalEnvironment) throws RunBuildException {
    BuildRunnerContext context = myFacade.createBuildRunnerContext(
            hostContext.getBuild(),
            SimpleRunnerConstants.TYPE,
            workingDir.getPath(),
            hostContext
    );

    for (Map.Entry<String, String> entry : additionalEnvironment.entrySet()) {
      context.addEnvironmentVariable(entry.getKey(), entry.getValue());
    }

    final List<String> newArgz = new ArrayList<>(argz);
    final String commandLine = joinCommandLineArguments(newArgz);

    hostContext.getBuild().getBuildLogger().message("Executing command: " + commandLine);

    context.addRunnerParameter(SimpleRunnerConstants.USE_CUSTOM_SCRIPT, "true");
    context.addRunnerParameter(SimpleRunnerConstants.SCRIPT_CONTENT, commandLine);

    return myFacade.createExecutable(hostContext.getBuild(), context);
  }

  //This is a same code as system uses in ProcessImpl class
  private String joinCommandLineArguments(@NotNull final Collection<String> cmd) {
    StringBuilder cmdbuf = new StringBuilder(80);
    boolean isFirst = true;
    for (String aCmd : cmd) {
      if (!isFirst) {
        cmdbuf.append(' ');
      } else {
        isFirst = false;
      }
      if (aCmd.indexOf(' ') < 0 && aCmd.indexOf('\t') < 0) {
        cmdbuf.append(aCmd);
        continue;
      }

      if (aCmd.charAt(0) != '"') {
        cmdbuf.append('"');
        cmdbuf.append(aCmd);

        if (aCmd.endsWith("\\")) {
          cmdbuf.append("\\");
        }

        cmdbuf.append('"');
      } else if (aCmd.endsWith("\"")) {
        /* The argument has already been quoted. */
        cmdbuf.append(aCmd);
      } else {
        /* Unmatched quote for the argument. */
        throw new IllegalArgumentException();
      }
    }
    return cmdbuf.toString();
  }

}
