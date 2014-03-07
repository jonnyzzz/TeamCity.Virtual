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

import jetbrains.buildServer.util.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class RelativePaths {
  @NotNull
  public static String resolveRelativePath(@NotNull final File baseDir, @NotNull final File path) {
    String result = FileUtil.getRelativePath(baseDir, path);
    if (result == null) return "";

    result = result.replace('\\', '/');

    while (result.endsWith(".") || result.endsWith("/")) result = result.substring(0, result.length() - 1);
    return result;
  }
}
