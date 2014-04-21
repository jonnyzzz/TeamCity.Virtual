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

import com.intellij.execution.configurations.GeneralCommandLine;
import com.jonnyzzz.teamcity.virtual.Loggers;
import com.jonnyzzz.teamcity.virtual.VMConstants;
import jetbrains.buildServer.ExecResult;
import jetbrains.buildServer.SimpleCommandLineProcessRunner;
import jetbrains.buildServer.agent.AgentLifeCycleAdapter;
import jetbrains.buildServer.agent.AgentLifeCycleListener;
import jetbrains.buildServer.agent.BuildAgent;
import jetbrains.buildServer.agent.BuildAgentConfiguration;
import jetbrains.buildServer.util.EventDispatcher;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class UserUIDAndGIDImpl implements UserUIDAndGID {
  private static final Logger LOG = Loggers.getLogger(UserUIDAndGIDImpl.class);

  private String myUID = null;
  private String myGID = null;

  public UserUIDAndGIDImpl(@NotNull final EventDispatcher<AgentLifeCycleListener> events) {

    events.addListener(new AgentLifeCycleAdapter(){
      @Override
      public void afterAgentConfigurationLoaded(@NotNull final BuildAgent agent) {
        final BuildAgentConfiguration configuration = agent.getConfiguration();

        if (configuration.getConfigurationParameters().get(VMConstants.DOCKER_PROPERTY) == null) return;
        if (!configuration.getSystemInfo().isUnix()) return;
        if (configuration.getSystemInfo().isWindows()) return;
        if (configuration.getSystemInfo().isMac()) return;

        detectSidAndGid();
      }
    });
  }

  private void detectSidAndGid() {
    myUID = runCommand("id -u");
    myGID = runCommand("id -g");
    LOG.debug("Detected UID=" + myUID + ", GID=" + myGID);
  }

  @Override
  @Nullable
  public String getUID() {
    return myUID;
  }

  @Override
  @Nullable
  public String getGID() {
    return myGID;
  }

  @Nullable
  private String runCommand(@NotNull String command) {
    final GeneralCommandLine cmd = new GeneralCommandLine();
    cmd.setExePath("/bin/sh");
    cmd.addParameter("-c");
    cmd.addParameter(command);

    final ExecResult result = SimpleCommandLineProcessRunner.runCommand(cmd, new byte[0]);

    //noinspection ThrowableResultOfMethodCallIgnored
    if (result.getException() != null || result.getExitCode() != 0) {
      LOG.info(("Failed to call '" + command+ "'. Exit code: " + result.getExitCode() + "\n " + result.getStdout() + "\n" + result.getStderr()).trim());
      return null;
    }

    return result.getStdout().trim();
  }
}
