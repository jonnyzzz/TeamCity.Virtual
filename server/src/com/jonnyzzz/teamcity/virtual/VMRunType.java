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

import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.RunType;
import jetbrains.buildServer.util.StringUtil;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.jonnyzzz.teamcity.virtual.VMConstants.RUN_TYPE;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class VMRunType extends RunType {
  private final PluginDescriptor myDescriptor;

  public VMRunType(@NotNull final PluginDescriptor descriptor) {
    myDescriptor = descriptor;
  }

  @NotNull
  @Override
  public String getType() {
    return RUN_TYPE;
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return "Docker/Vagrant";
  }

  @NotNull
  @Override
  public String getDescription() {
    return "Runs a command under virtual machine";
  }

  @Nullable
  @Override
  public PropertiesProcessor getRunnerPropertiesProcessor() {
    return new PropertiesProcessor() {
      @NotNull
      public Collection<InvalidProperty> process(Map<String, String> properties) {
        List<InvalidProperty> result = new ArrayList<>();
        if (StringUtil.isEmptyOrSpaces(properties.get(VMConstants.PARAMETER_SCRIPT))) {
          result.add(new InvalidProperty(VMConstants.PARAMETER_SCRIPT, "Script should not be empty"));
        }

        final String vm = properties.get(VMConstants.PARAMETER_VM);
        final VM w = VM.find(vm);
        if (w == null) {
          result.add(new InvalidProperty(VMConstants.PARAMETER_VM, "Unknown VM"));
        } else {
          result.addAll(w.validate(properties));
        }
        return result;
      }
    };
  }

  @Nullable
  @Override
  public String getEditRunnerParamsJspFilePath() {
    return myDescriptor.getPluginResourcesPath("vm-edit.jsp");
  }

  @Nullable
  @Override
  public String getViewRunnerParamsJspFilePath() {
    return myDescriptor.getPluginResourcesPath("vm-view.jsp");
  }

  @Nullable
  @Override
  public Map<String, String> getDefaultRunnerProperties() {
    return Collections.emptyMap();
  }
}
