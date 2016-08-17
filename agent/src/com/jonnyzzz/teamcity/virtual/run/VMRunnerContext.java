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

import com.jonnyzzz.teamcity.virtual.VMConstants;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class VMRunnerContext {
  protected final BuildRunnerContext myContext;

  public VMRunnerContext(@NotNull final BuildRunnerContext context) {
    myContext = context;
  }

  @NotNull
  public String getScript() throws RunBuildException {
    final String script = myContext.getRunnerParameters().get(VMConstants.PARAMETER_SCRIPT);
    if (StringUtil.isEmptyOrSpaces(script)) {
      throw new RunBuildException("Script should not be empty");
    }
    return script;
  }

  @Nullable
  protected File resolvePath(@Nullable final String path) {
    if (path == null) return null;
    return FileUtil.resolvePath(myContext.getBuild().getCheckoutDirectory(), path);
  }

  @NotNull
  public File getWorkingDirectory() {
    return myContext.getWorkingDirectory();
  }

  @NotNull
  public File getCheckoutDirectory() {
    return myContext.getBuild().getCheckoutDirectory();
  }

  @NotNull
  public File getAgentTempDirectory() {
    return myContext.getBuild().getAgentTempDirectory();
  }

  @NotNull
  public String getCheckoutMountPoint() {
    String mountPoint = myContext.getRunnerParameters().get(VMConstants.PARAMETER_CHECKOUT_MOUNT_POINT);
    return StringUtil.isEmptyOrSpaces(mountPoint) ? "/checkout" : mountPoint;
  }

  @NotNull
  public String getShellLocation() {
    String loc = myContext.getRunnerParameters().get(VMConstants.PARAMETER_SHELL);
    if (loc == null) {
      return "/bin/bash";
    }
    return loc;
  }

  @NotNull
  public String getDockerMountMode() {
    String mountMode = myContext.getRunnerParameters().get(VMConstants.DOCKER_MOUNT_MODE);
    if (mountMode == null) {
      return "rw";
    }
    return mountMode;
  }
}
