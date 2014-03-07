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

package com.jonnyzzz.teamcity.virtual.run;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildProcess;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;
import java.util.Map;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public interface CommandlineExecutor {

  @NotNull
  BuildProcess commandline(@NotNull final Collection<String> arguments) throws RunBuildException;

  @NotNull
  BuildProcess commandline(@NotNull final File workdir,
                           @NotNull final Collection<String> arguments) throws RunBuildException;

  @NotNull
  BuildProcess commandline(@NotNull final File workdir,
                           @NotNull final Collection<String> arguments,
                           @NotNull final Map<String, String> additionalEnv) throws RunBuildException;
}
