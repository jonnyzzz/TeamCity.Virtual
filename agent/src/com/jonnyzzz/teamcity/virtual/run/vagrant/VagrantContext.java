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
import com.jonnyzzz.teamcity.virtual.run.VMRunnerContext;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class VagrantContext extends VMRunnerContext {
  public VagrantContext(@NotNull final BuildRunnerContext context) {
    super(context);
  }

  @NotNull
  public File getVagrantFile() throws RunBuildException {
    String file = myContext.getRunnerParameters().get(VMConstants.PARAMETER_VAGRANT_FILE);

    File path;
    if (StringUtil.isEmptyOrSpaces(file)) {
      path = new File(myContext.getBuild().getCheckoutDirectory(), VMConstants.VAGRANT_FILE);
      if (!path.isFile())
        throw new RunBuildException(VMConstants.VAGRANT_FILE + " is not found at " + getCheckoutDirectory());

    } else {
      path = resolvePath(file);
      if (path == null) throw new RunBuildException("Vagrant file '" + file + "' does not exist");

      if (path.isDirectory()) {
        path = new File(path, VMConstants.VAGRANT_FILE);
      } else if (!path.getName().equals(VMConstants.VAGRANT_FILE)) {
        throw new RunBuildException("File '" + file + "' must be named as " + VMConstants.VAGRANT_FILE);
      }
    }

    if (!path.isFile()) throw new RunBuildException("Vagrant file '" + file + "' does not exist");
    return path;
  }

  @NotNull
  public boolean getRequiresCustomFile() {
    String requiresCustomFile = myContext.getRunnerParameters().get(VMConstants.PARAMETER_VAGRANTFILE_FULL_OVERRIDE);
    return requiresCustomFile != null && requiresCustomFile.equals("yes");
  }

  @NotNull
  public String getCustomVagrantfileContent() {
    return myContext.getRunnerParameters().get(VMConstants.PARAMETER_VAGRANTFILE_CUSTOM_CONTENT);
  }
}
