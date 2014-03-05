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

package com.jonnyzzz.teamcity.virtual;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.util.SystemInfo;
import jetbrains.buildServer.ExecResult;
import jetbrains.buildServer.SimpleCommandLineProcessRunner;
import jetbrains.buildServer.agent.AgentLifeCycleAdapter;
import jetbrains.buildServer.agent.AgentLifeCycleListener;
import jetbrains.buildServer.agent.BuildAgent;
import jetbrains.buildServer.agent.BuildAgentConfiguration;
import jetbrains.buildServer.util.EventDispatcher;
import jetbrains.buildServer.util.StringUtil;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class VMDetector {
  private static final Logger LOG = Loggers.getLogger(VMDetector.class);

  public VMDetector(@NotNull final EventDispatcher<AgentLifeCycleListener> events) {
    events.addListener(new AgentLifeCycleAdapter() {
      @Override
      public void beforeAgentConfigurationLoaded(@NotNull BuildAgent agent) {
        detectVargant(agent.getConfiguration());
        detectDocker(agent.getConfiguration());
      }

      private void detectVargant(@NotNull final BuildAgentConfiguration config) {

        final String output = executeCommandWithShell("vagrant", "vagrant --version");
        if (output == null) return;

        String ver = output.toLowerCase().trim();
        if (ver.startsWith("vagrant")) ver = ver.substring("vagrant".length()).trim();

        if (StringUtil.isEmptyOrSpaces(ver)) {
          LOG.warn("Failed to parse vagrant version: " + output);
          return;
        }

        config.addConfigurationParameter(VMConstants.VAGRANT_PROPERTY, ver);
      }

      private void detectDocker(@NotNull final BuildAgentConfiguration config) {
        if (!SystemInfo.isLinux) {
          LOG.debug("Docker is only available under Linux");
          return;
        }

        final String output = executeCommandWithShell("docker", "docker --version");
        if (output == null) return;

        String ver = output.toLowerCase().trim();
        ver = ver.replaceAll("\\s*docker\\s+version\\s+", "");
        ver = ver.replaceAll(",?\\s+build\\s+", "-");

        if (StringUtil.isEmptyOrSpaces(ver)) {
          LOG.warn("Failed to parse vagrant version: " + output);
          return;
        }

        config.addConfigurationParameter(VMConstants.DOCKER_PROPERTY, ver);
      }


      @Nullable
      private String executeCommandWithShell(@NotNull final String name, @NotNull final String... command) {
        GeneralCommandLine cmd = new GeneralCommandLine();
        if (SystemInfo.isWindows) {
          cmd.setExePath("cmd");
          cmd.addParameters("/c");
        } else {
          cmd.setExePath("/bin/sh");
          cmd.addParameters("-c");
        }
        cmd.addParameters(command);

        LOG.info("Running: " + cmd.getCommandLineString());

        final ExecResult result = SimpleCommandLineProcessRunner.runCommand(cmd, new byte[0]);

        //noinspection ThrowableResultOfMethodCallIgnored
        if (result.getException() != null || result.getExitCode() != 0) {
          LOG.info(("Failed to find " + name + ". Exit code: " + result.getExitCode() + "\n " + result.getStdout() + "\n" + result.getStderr()).trim());
          return null;
        }

        return result.getStdout();
      }

    });
  }
}
