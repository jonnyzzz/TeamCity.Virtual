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
        detectVagrant(agent.getConfiguration());
        detectDocker(agent.getConfiguration());
      }

      private void detectVagrant(@NotNull final BuildAgentConfiguration config) {

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

        final String output = executeCommandWithShell("docker", "docker version");
        if (output == null) return;

        final String dockerVersion = executeCommandWithShell("docker", "docker version --format {{.Server.Version}}");
        final String dockerHostOS = executeCommandWithShell("docker", "docker version --format {{.Server.Os}}");

        if (StringUtil.isEmptyOrSpaces(dockerVersion)) {
          LOG.warn("Failed to parse docker version" + output);
          return;
        } else if (StringUtil.isEmptyOrSpaces(dockerHostOS)) {
          LOG.warn("Failed to parse docker host os" + output);
          return;
        }

        config.addConfigurationParameter(VMConstants.DOCKER_PROPERTY, cleanOutput(dockerVersion));
        config.addConfigurationParameter(VMConstants.DOCKER_HOST_OS_PROPERTY, cleanOutput(dockerHostOS));
      }

      private String cleanOutput(String rawOutput) {
        return rawOutput
                .trim()
                .replace(System.getProperty("line.separator"), "");
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

        LOG.info("Running: "+ cmd.getCommandLineString());

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
