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
import jetbrains.buildServer.agent.BuildRunnerContext;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.jonnyzzz.teamcity.virtual.run.CommandLineUtils.additionalCommands;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class DockerVM implements VMRunner {
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

    myScriptFile.generateScriptFile(ctx, builder, new ScriptFile.Builder() {
      @Override
      public void buildWithScriptFile(@NotNull File script) throws RunBuildException {
        final List<String> arguments = new ArrayList<>();

        arguments.addAll(Arrays.asList(
                "docker",
                "run",
                "--rm=true",
                "-v",
                baseDir.getPath() + ":/jonnyzzz:rw",
                "--workdir=/jonnyzzz/" + RelativePaths.resolveRelativePath(baseDir, workDir),
                "--interactive=false",
                "--hostname=" + context.getBuild().getAgentConfiguration().getName() + "-docker",
                "--tty=false"));

        arguments.addAll(additionalCommands(context.getRunnerParameters().get(VMConstants.PARAMETER_DOCKER_CUSTOM_COMMANDLINE)));
        arguments.addAll(Arrays.asList(
                ctx.getImageName(),
                "/bin/bash",  ///TODO: imagine OS without bash
                "-c",
                "\"source " + script.getName() + "\""
        ));

        builder.addTryProcess(cmd.commandline(
                workDir,
                arguments
        ));
      }
    });

      /*docker run --rm=true -v /home/shalupov/work/ui:/work:rw -i -t dockerfile/nodejs bash -c "cd /work && npm install && npm install grunt-cli && ./node_modules/.bin/grunt release"*/
  }

}
