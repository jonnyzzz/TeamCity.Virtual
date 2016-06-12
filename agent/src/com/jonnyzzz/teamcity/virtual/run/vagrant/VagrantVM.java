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
import com.jonnyzzz.teamcity.virtual.run.*;
import com.jonnyzzz.teamcity.virtual.util.util.TryFinallyBuildProcess;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildProcess;
import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.util.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.jonnyzzz.teamcity.virtual.VMConstants.PARAMETER_VAGRANT_CUSTOM_COMMANDLINE;
import static com.jonnyzzz.teamcity.virtual.run.vagrant.VagrantFilePatcher.WithGeneratedVagrantfile;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class VagrantVM extends BaseVM implements VMRunner {
  private final ScriptFile myScriptFile;
  private final VagrantFilePatcher myVagrantFilePatcher;

  public VagrantVM(@NotNull final ScriptFile scriptFile,
                   @NotNull final VagrantFilePatcher vagrantFilePatcher) {
    myScriptFile = scriptFile;
    myVagrantFilePatcher = vagrantFilePatcher;
  }

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


    myScriptFile.generateScriptFile(ctx, builder, new ScriptFile.Builder() {
      @Override
      public void buildWithScriptFile(@NotNull final File script) throws RunBuildException {

        myVagrantFilePatcher.generateVagrantFile(ctx, logger, vagrantFile, builder, new WithGeneratedVagrantfile() {
          @Override
          public void execute(@NotNull final String relativePath) throws RunBuildException {
            up();
            ssh(relativePath);
            destroy();
          }

          private void up() throws RunBuildException {
            final List<String> upArguments = new ArrayList<String>();
            upArguments.addAll(Arrays.asList("vagrant", "up"));
            upArguments.addAll(CommandLineUtils.additionalCommands(context.getRunnerParameters().get(PARAMETER_VAGRANT_CUSTOM_COMMANDLINE)));

            builder.addTryProcess(
                    block("Starting machine",
                            cmd.commandline(workDir, upArguments))
            );
          }

          private void ssh(String relativePath) throws RunBuildException {
            //TODO: not clear how workdir maps into VM path for Vagrant as we use Vagrantfile for that
            //TODO: fix windows case here ( bash => parameters ), slashes
            builder.addTryProcess(
                    block("Running the script using " + ctx.getShellLocation(),
                            cmd.commandline(
                                    workDir,
                                    Arrays.asList(
                                            "vagrant",
                                            "ssh",
                                            "-c",
                                            "\""
                                                    + ctx.getShellLocation()
                                                    + " -c 'cd "
                                                    + relativePath
                                                    + " && . " + script.getName() + "'\""
                                    )
                            ))

            );
          }

          private void destroy() throws RunBuildException {
            builder.addFinishProcess(
                    block("Destroying machine",
                            cmd.commandline(workDir, Arrays.asList("vagrant", "destroy", "-f")))
            );
          }

          @NotNull
          private BuildProcess block(@NotNull final String blockText,
                                     @NotNull final BuildProcess proc) {
            return BaseVM.block(logger, "vagrant", blockText, proc);
          }

        });
      }
    });
  }


}
