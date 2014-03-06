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

package com.jonnyzzz.teamcity.virtual.tests.tests.util;

import com.jonnyzzz.teamcity.virtual.util.util.TryFinallyBuildProcessImpl;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;

import static com.jonnyzzz.teamcity.virtual.util.util.TryFinallyBuildProcessImpl.ErrorLogger;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class TryFinallyBuildProcessTest extends BuildProcessTestCase {

  @Test
  public void should_call_finally_normal() {
    TryFinallyBuildProcessImpl bp = create();

    bp.addTryProcess(new RecordingBuildProcess("try-1", BuildFinishedStatus.FINISHED_SUCCESS));
    bp.addFinishProcess(new RecordingBuildProcess("finally-1", BuildFinishedStatus.FINISHED_SUCCESS));

    assertRunSuccessfully(bp.asBuildProcess(), BuildFinishedStatus.FINISHED_SUCCESS);
    assertLog("start-try-1",
            "waitFor-try-1",
            "start-finally-1",
            "waitFor-finally-1");

  }

  @Test
  public void should_not_fail_on_finally_error() {
    TryFinallyBuildProcessImpl bp = create();

    bp.addFinishProcess(new RecordingBuildProcess("finally-1", BuildFinishedStatus.FINISHED_SUCCESS) {
      {
        setFinishException(new RunBuildException("ppp"));
      }
    });
    bp.addFinishProcess(new RecordingBuildProcess("finally-2", BuildFinishedStatus.FINISHED_SUCCESS));

    //TODO: error status is not propagated here.
    //TODO: The only changes is we report error on that from the runner
    assertRunSuccessfully(bp.asBuildProcess(), BuildFinishedStatus.FINISHED_SUCCESS);
    assertLog("start-finally-1",
            "waitFor-finally-1",
            "error-ppp",
            "start-finally-2",
            "waitFor-finally-2");
  }

  private TryFinallyBuildProcessImpl create() {
    return new TryFinallyBuildProcessImpl(LOGGER);
  }


  private final ErrorLogger LOGGER = new ErrorLogger() {
    @Override
    public void onError(@NotNull String message, @NotNull Throwable error) {
      log("error-" + error.getMessage());
    }
  };

}
