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

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class VMConstants {
  public static final String RUN_TYPE = "jonnyzzz.vm";

  public static final String VAGRANT_PROPERTY = "vagrant";
  public static final String DOCKER_PROPERTY = "docker";
  public static final String DOCKER_HOST_OS_PROPERTY = "docker.host.os";

  public static final String PARAMETER_VM = "vm";
  public static final String VM_DOCKER = "docker";
  public static final String VM_VAGRANT = "vagrant";
  public static final String PARAMETER_SCRIPT = "script";
  public static final String PARAMETER_CHECKOUT_MOUNT_POINT = "checkout-mount-point";
  public static final String PARAMETER_SHELL = "default-shell-location";
  public static final String DOCKER_MOUNT_MODE = "docker-mount-mode";

  public static final String PARAMETER_DOCKER_IMAGE_NAME = "docker-image-name";
  public static final String PARAMETER_DOCKER_CUSTOM_COMMANDLINE = "docker-commandline";

  public static final String PARAMETER_VAGRANT_FILE = "vagrant-file";
  public static final String PARAMETER_VAGRANT_CUSTOM_COMMANDLINE = "vagrant-commandline";
  public static final String PARAMETER_VAGRANTFILE_CUSTOM_CONTENT = "vagrantfile-content";
  public static final String PARAMETER_VAGRANTFILE_DO_OVERRIDE = "vagrantfile-do-override";

  public static final String VAGRANT_FILE = "Vagrantfile";
}
