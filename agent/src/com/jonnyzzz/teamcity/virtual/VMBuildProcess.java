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

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.runner.BuildServiceAdapter;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class VMBuildProcess extends BuildServiceAdapter {
  @NotNull
  @Override
  public ProgramCommandLine makeProgramCommandLine() throws RunBuildException {
    final Builder builder = createBuilder();

    getLogger().message("Staring " + builder.getCaption() + "\u2026");
    return builder.getProgramCommandline();
  }

  @NotNull
  private Builder createBuilder() throws RunBuildException{
    final String vm = getRunnerParameters().get(VMConstants.PARAMETER_VM);
    switch (vm) {
      case VMConstants.VM_DOCKER: return new DockerBuilder();
      case VMConstants.VM_VAGRANT: return new VagrantBuilder();
      default:
        throw new RunBuildException("Parameter " + VMConstants.PARAMETER_VM + " in invalid: " + vm);
    }
  }

  private abstract class Builder {
    @NotNull
    public abstract String getCaption();

    @NotNull
    public abstract ProgramCommandLine getProgramCommandline() throws RunBuildException;
  }

  private class DockerBuilder extends Builder {
    @NotNull
    @Override
    public String getCaption() {
      return "Docker";
    }

    @NotNull
    @Override
    public ProgramCommandLine getProgramCommandline() throws RunBuildException {
      throw new RunBuildException("Not implemented");
    }
  }

  private class VagrantBuilder extends Builder {
    @NotNull
    @Override
    public String getCaption() {
      return "Vagrant";
    }

    @NotNull
    @Override
    public ProgramCommandLine getProgramCommandline() throws RunBuildException {
      throw new RunBuildException("Not implemented");
    }
  }
}
