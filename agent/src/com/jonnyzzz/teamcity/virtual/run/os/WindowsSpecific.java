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

package com.jonnyzzz.teamcity.virtual.run.os;

import com.jonnyzzz.teamcity.virtual.VMOS;
import com.jonnyzzz.teamcity.virtual.run.VMRunnerContext;
import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class WindowsSpecific implements OSSpecific {
  @NotNull
  @Override
  public VMOS getName() {
    return VMOS.WINDOWS;
  }

  @NotNull
  @Override
  public String getMountBasePath(@NotNull VMRunnerContext ctx) {
    return "c:\\jonnyzzz";
  }

  @NotNull
  @Override
  public String getMountWorkPath(@NotNull VMRunnerContext ctx, @NotNull String relativePath) {
    return getMountBasePath(ctx) + "\\" + relativePath.replace('/', '\\');
  }

  @NotNull
  @Override
  public String runScript(@NotNull final String guestWorkDir, @NotNull final String name) {
    return "cmd /c cd " + guestWorkDir + " && " + "cmd /c " + name;
  }
}
