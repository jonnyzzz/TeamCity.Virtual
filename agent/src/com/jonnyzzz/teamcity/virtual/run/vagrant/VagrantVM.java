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

package com.jonnyzzz.teamcity.virtual.run.vagrant;

import com.jonnyzzz.teamcity.virtual.VMConstants;
import com.jonnyzzz.teamcity.virtual.run.CommandlineExecutor;
import com.jonnyzzz.teamcity.virtual.run.VMRunner;
import com.jonnyzzz.teamcity.virtual.util.util.DelegatingBuildProcess;
import com.jonnyzzz.teamcity.virtual.util.util.TryFinallyBuildProcess;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildProcess;
import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.util.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class VagrantVM implements VMRunner {
  @NotNull
  @Override
  public String getVMName() {
    return VMConstants.VM_VAGRANT;
  }

  @NotNull
  @Override
  public String getCaption() {
    return "Vagrant";
  }

  @Override
  public void constructBuildProcess(@NotNull final BuildRunnerContext context,
                                    @NotNull final CommandlineExecutor cmd,
                                    @NotNull final TryFinallyBuildProcess builder) throws RunBuildException {
    final File rootDir = context.getBuild().getCheckoutDirectory();

    final VagrantContext ctx = new VagrantContext(context);
    final File vagrantFile = ctx.getVagrantFile();
    final BuildProgressLogger logger = context.getBuild().getBuildLogger();
    logger.message("Found " + VMConstants.VAGRANT_FILE + ": " + FileUtil.getRelativePath(rootDir, vagrantFile));

    final File workDir = vagrantFile.getParentFile();
    if (workDir == null) throw new RunBuildException("Failed to resolve directory of " + vagrantFile);

    builder.addTryProcess(
            block(logger, "vagrant", "Starting machine",
                    cmd.commandline(workDir, Arrays.asList("vagrant", "up")))
    );

    builder.addTryProcess(
            block(logger, "vagrant", "Running the script",
                    cmd.commandline(workDir, Arrays.asList("vagrant", "ssh", "-c", "\"" + ctx.getScript() + "\"")))
    );

    builder.addFinishProcess(block(logger, "vagrant", "Destroying machine",
            cmd.commandline(workDir, Arrays.asList("vagrant", "destroy", "-f"))));
  }


  @NotNull
  private BuildProcess block(@NotNull final BuildProgressLogger logger,
                             @NotNull final String blockName,
                             @NotNull final String blockText,
                             @NotNull final BuildProcess proc) {
    return new DelegatingBuildProcess(new DelegatingBuildProcess.Action() {
      @NotNull
      @Override
      public BuildProcess startImpl() throws RunBuildException {
        logger.activityStarted(blockName, blockText, "vm");
        return proc;
      }

      @Override
      public void finishedImpl() {
        logger.activityFinished(blockName, "vm");
      }
    });
  }
}
