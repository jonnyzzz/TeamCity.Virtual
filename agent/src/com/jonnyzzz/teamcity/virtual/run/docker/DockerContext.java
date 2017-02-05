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

package com.jonnyzzz.teamcity.virtual.run.docker;

import com.intellij.openapi.util.SystemInfo;
import com.jonnyzzz.teamcity.virtual.VMConstants;
import com.jonnyzzz.teamcity.virtual.run.VMRunnerContext;
import com.oracle.tools.packager.Log;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class DockerContext extends VMRunnerContext {
  public DockerContext(@NotNull final BuildRunnerContext context) {
    super(context);
  }

  @NotNull
  public String getImageName() throws RunBuildException {
    final String image = myContext.getRunnerParameters().get(VMConstants.PARAMETER_DOCKER_IMAGE_NAME);
    if (StringUtil.isEmptyOrSpaces(image)) throw new RunBuildException("Docker image is not specified");
    return image;
  }

  public boolean isDockerServerWindowsBased() throws RunBuildException {
    final String serverArch = myContext.getConfigParameters().get(VMConstants.DOCKER_SERVER_OS_ARCH_PROPERTY);
    return serverArch.equals("windows/amd64");
  }

  @NotNull
  public String getShellLocationInsideContainer() throws RunBuildException {
    String defaultShellLocation;
    if(isDockerServerWindowsBased()) {
      defaultShellLocation = "cmd.exe";
    } else {
      defaultShellLocation = "/bin/bash";
    }
    String loc = myContext.getRunnerParameters().get(VMConstants.PARAMETER_SHELL);
    if (loc.equals("default")) {
      return defaultShellLocation;
    }
    return loc;
  }

  public String getPathSeparatorInsideContainer() throws RunBuildException {
    if(isDockerServerWindowsBased()) {
      return "\\";
    } else {
      return "/";
    }
  }
}
