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
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static com.jonnyzzz.teamcity.virtual.VMConstants.RUN_TYPE;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class VMBuildRunner extends RunType {
  private final PluginDescriptor myDescriptor;

  public VMBuildRunner(@NotNull final PluginDescriptor descriptor) {
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
        return Collections.emptyList();
      }
    };
  }

  @Nullable
  @Override
  public String getEditRunnerParamsJspFilePath() {
    return myDescriptor.getParameterValue("vm-edit.jsp");
  }

  @Nullable
  @Override
  public String getViewRunnerParamsJspFilePath() {
    return myDescriptor.getParameterValue("vm-view.jsp");
  }

  @Nullable
  @Override
  public Map<String, String> getDefaultRunnerProperties() {
    return Collections.emptyMap();
  }
}
