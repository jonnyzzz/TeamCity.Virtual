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

import com.jonnyzzz.teamcity.virtual.VMConstants;
import com.jonnyzzz.teamcity.virtual.run.VMRunnerContext;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;

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
    final String serverArch = myContext.getConfigParameters().get(VMConstants.DOCKER_HOST_OS_PROPERTY);
    return serverArch.equals("windows");
  }

  @NotNull
  public String getShellLocationInsideContainer() throws RunBuildException {
    final String defaultShellLocation = isDockerServerWindowsBased() ? "cmd.exe" : "/bin/bash";
    final String loc = myContext.getRunnerParameters().get(VMConstants.PARAMETER_SHELL);
    if (loc.equals("default")) {
      return defaultShellLocation;
    }
    return loc;
  }

  @NotNull
  public String getCheckoutMountPointInsideContainer() throws RunBuildException {
    final String defaultMountPoint = isDockerServerWindowsBased() ? "C:\\checkout" : "/checkout";
    final String mountPoint = myContext.getRunnerParameters().get(VMConstants.PARAMETER_CHECKOUT_MOUNT_POINT);
    return StringUtil.isEmptyOrSpaces(mountPoint) ? defaultMountPoint : mountPoint;
  }

  public String getPathSeparatorInsideContainer() throws RunBuildException {
    return isDockerServerWindowsBased() ? "\\" : "/";
  }
}
