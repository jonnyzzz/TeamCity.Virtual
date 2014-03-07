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
import jetbrains.buildServer.RunBuildException;
import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class OSAware {
  private final OSSpecific[] myOSSpecifics;

  public OSAware(@NotNull final OSSpecific[] OSSpecifics) {
    myOSSpecifics = OSSpecifics;
  }

  @NotNull
  public OSSpecific findSpecifics(@NotNull final VMRunnerContext context) throws RunBuildException {
    @NotNull final VMOS os = context.getOs();
    for (OSSpecific spc : myOSSpecifics) {
      if (spc.getName().equals(os)) return spc;
    }
    throw new RunBuildException("Failed to find OS specifics for " + os);
  }

}
