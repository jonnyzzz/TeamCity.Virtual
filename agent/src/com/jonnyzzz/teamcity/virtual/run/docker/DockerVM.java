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
import com.jonnyzzz.teamcity.virtual.run.*;
import com.jonnyzzz.teamcity.virtual.util.util.TryFinallyBuildProcess;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildProcess;
import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.jonnyzzz.teamcity.virtual.run.CommandLineUtils.additionalCommands;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class DockerVM extends BaseVM implements VMRunner {
  private final ScriptFile myScriptFile;

  public DockerVM(@NotNull final ScriptFile scriptFile) {
    myScriptFile = scriptFile;
  }

  @NotNull
  @Override
  public String getVMName() {
    return VMConstants.VM_DOCKER;
  }

  @NotNull
  @Override
  public String getCaption() {
    return "Docker";
  }


  @Override
  public void constructBuildProcess(@NotNull final BuildRunnerContext context,
                                    @NotNull final CommandlineExecutor cmd,
                                    @NotNull final TryFinallyBuildProcess builder) throws RunBuildException {
    final DockerContext ctx = new DockerContext(context);

    final File baseDir = ctx.getCheckoutDirectory();
    final File workDir = ctx.getWorkingDirectory();
    final BuildProgressLogger logger = context.getBuild().getBuildLogger();

    myScriptFile.generateScriptFile(ctx, builder, new ScriptFile.Builder() {
      @Override
      public void buildWithScriptFile(@NotNull final File script) throws RunBuildException {
        final String name = "teamcity_" + StringUtil.generateUniqueHash();
        final List<String> additionalCommands = additionalCommands(context.getRunnerParameters().get(VMConstants.PARAMETER_DOCKER_CUSTOM_COMMANDLINE));

        builder.addTryProcess(
                block("Pulling the image", cmd.commandline(
                        workDir, Arrays.asList("docker", "pull", ctx.getImageName())
                ))
        );

        builder.addTryProcess(
                block("Executing the command", cmd.commandline(
                        workDir,
                        dockerRun(script, name, additionalCommands)
                ))
        );
        builder.addFinishProcess(block("Terminating images (if needed)", cmd.commandline(workDir, Arrays.asList("docker", "kill", name, "2>&1", "||", "true"))));
      }

      @NotNull
      private List<String> dockerRun(@NotNull final File script,
                                     @NotNull final String name,
                                     @NotNull final List<String> additionalCommands) throws RunBuildException {
        final List<String> arguments = new ArrayList<>();

        arguments.addAll(Arrays.asList(
                "docker",
                "run",
                "--rm=true",
                "--name=" + name,
                "-v",
                baseDir.getPath() + ":/jonnyzzz:rw",
                "--workdir=/jonnyzzz/" + RelativePaths.resolveRelativePath(baseDir, workDir),
                "--interactive=false",
                "--tty=false"));

        arguments.addAll(additionalCommands);
        arguments.addAll(Arrays.asList(
                ctx.getImageName(),
                "/bin/bash",  ///TODO: imagine OS without bash
                "-c",
                "\"source " + script.getName() + "\""
        ));

        return arguments;
      }

      @NotNull
      private BuildProcess block(@NotNull final String blockText,
                                 @NotNull final BuildProcess proc) {
        return BaseVM.block(logger, "docker", blockText, proc);
      }
    });
  }

}
