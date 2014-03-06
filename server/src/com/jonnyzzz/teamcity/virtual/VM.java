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
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public enum VM {
  DOCKER(VMConstants.VM_DOCKER, "Docker", "vm-docker-edit.jsp", "vm-docker-view.jsp") {
    @NotNull
    @Override
    public Collection<InvalidProperty> validate(@NotNull final Map<String, String> props) {
      if (StringUtil.isEmptyOrSpaces(VMConstants.PARAMETER_DOCKER_IMAGE_NAME)) {
        return Collections.singleton(new InvalidProperty(VMConstants.PARAMETER_DOCKER_IMAGE_NAME, "Image not defined"));
      }
      return Collections.emptyList();
    }
  },
  VAGRANT(VMConstants.VM_VAGRANT, "Vagrant", "vm-vagrant-edit.jsp", "vm-vagrant-view.jsp") {
    @NotNull
    @Override
    public Collection<InvalidProperty> validate(@NotNull Map<String, String> props) {
      if (StringUtil.isEmptyOrSpaces(VMConstants.PARAMETER_VAGRANT_FILE)) {
        return Collections.singleton(new InvalidProperty(VMConstants.PARAMETER_VAGRANT_FILE, "Vagrant file path is not defined"));
      }
      return Collections.emptyList();
    }
  },
  ;

  private final String myName;
  private final String myCaption;
  private final String myEdit;
  private final String myView;

  VM(@NotNull final String name,
     @NotNull final String caption,
     @NotNull final String edit,
     @NotNull final String view) {
    myName = name;
    myCaption = caption;
    myEdit = edit;
    myView = view;
  }

  @NotNull
  public String getName() {
    return myName;
  }

  @NotNull
  public String getCaption() {
    return myCaption;
  }

  @NotNull
  public String getEdit() {
    return myEdit;
  }

  @NotNull
  public String getView() {
    return myView;
  }

  @NotNull
  public abstract Collection<InvalidProperty> validate(@NotNull final Map<String, String> props);

  @Nullable
  public static VM find(@Nullable final String vm) {
    for (VM v :values()){
      if (v.getName().equals(vm)) return v;
    }
    return null;
  }
}
