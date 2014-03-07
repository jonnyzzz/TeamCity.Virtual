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

import org.jetbrains.annotations.NotNull;

import static com.jonnyzzz.teamcity.virtual.VMConstants.PARAMETER_VM_OS_LINUX;
import static com.jonnyzzz.teamcity.virtual.VMConstants.PARAMETER_VM_OS_WINDOWS;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public enum VMOS {

  WINDOWS(PARAMETER_VM_OS_WINDOWS, "Windows"),
  LINUX(PARAMETER_VM_OS_LINUX, "Linux/Unix"),

  ;

  private final String myName;
  private final String myCaption;


  VMOS(@NotNull final String name,
       @NotNull final String caption) {
    myName = name;
    myCaption = caption;
  }

  @NotNull
  public String getName() {
    return myName;
  }

  @NotNull
  public String getCaption() {
    return myCaption;
  }
}
