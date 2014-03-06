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
import com.jonnyzzz.teamcity.virtual.run.CommandlineExecutor;
import com.jonnyzzz.teamcity.virtual.run.VMRunner;
import com.jonnyzzz.teamcity.virtual.util.util.BuildProcessContinuation;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.util.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class DockerVM implements VMRunner {
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
                                    @NotNull final BuildProcessContinuation start,
                                    @NotNull final BuildProcessContinuation finishing) throws RunBuildException {
    final DockerContext ctx = new DockerContext(context);

    final File baseDir = context.getBuild().getCheckoutDirectory();
    final File workDir = context.getWorkingDirectory();
    start.pushBuildProcess(cmd.commandline(
            workDir,
            Arrays.asList(
                    "sudo",
                    "docker",
                    "run",
                    "--rm=true",
                    "-v",
                    baseDir.getPath() + ":/jonnyzzz:rw",
                    "--workdir=/jonnyzzz/" + FileUtil.getRelativePath(baseDir, workDir),
                    "--interactive=false",
                    "--hostname=" + context.getBuild().getAgentConfiguration().getName() + "-docker",
                    "--tty=false",
                    ctx.getImageName(),
                    "/bin/sh",
                    "-c",
                    "\"" + ctx.getScript() + "\""
            )
    ));
      /*docker run --rm=true -v /home/shalupov/work/ui:/work:rw -i -t dockerfile/nodejs bash -c "cd /work && npm install && npm install grunt-cli && ./node_modules/.bin/grunt release"*/
  }
}
