/*
 * Copyright 2000-2012 JetBrains s.r.o.
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

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import com.jonnyzzz.teamcity.virtual.util.util.impl.CompositeBuildProcessImpl;
import org.jetbrains.annotations.NotNull;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 07.07.11 19:04
 */
public class CompositeBuildProcessTest extends BuildProcessTestCase {
  @Test
  public void test_empty_build_process() {
    CompositeBuildProcessImpl i = new CompositeBuildProcessImpl();
    assertRunSuccessfully(i, BuildFinishedStatus.FINISHED_SUCCESS);
  }

  @Test(dataProvider = "buildFinishStatuses")
  public void test_one_build_process(BuildFinishedStatus result) throws RunBuildException {
    CompositeBuildProcessImpl i = new CompositeBuildProcessImpl();
    i.pushBuildProcess(new RecordingBuildProcess("1", result));
    assertRunSuccessfully(i, result == BuildFinishedStatus.INTERRUPTED ? BuildFinishedStatus.FINISHED_SUCCESS : result);

    assertLog("start-1", "waitFor-1");
  }

  @Test
  public void test_stopOnFirstError() throws RunBuildException {
    CompositeBuildProcessImpl i = new CompositeBuildProcessImpl();
    i.pushBuildProcess(new RecordingBuildProcess("1", BuildFinishedStatus.FINISHED_FAILED));
    i.pushBuildProcess(new RecordingBuildProcess("2", BuildFinishedStatus.FINISHED_SUCCESS));

    assertRunSuccessfully(i, BuildFinishedStatus.FINISHED_FAILED);
    assertLog("start-1", "waitFor-1");
  }

  @Test
  public void test_stopOnFirstError2() throws RunBuildException {
    CompositeBuildProcessImpl i = new CompositeBuildProcessImpl();
    i.pushBuildProcess(new RecordingBuildProcess("1", BuildFinishedStatus.FINISHED_SUCCESS));
    i.pushBuildProcess(new RecordingBuildProcess("2", BuildFinishedStatus.FINISHED_FAILED));
    i.pushBuildProcess(new RecordingBuildProcess("3", BuildFinishedStatus.FINISHED_SUCCESS));

    assertRunSuccessfully(i, BuildFinishedStatus.FINISHED_FAILED);
    assertLog("start-1", "waitFor-1", "start-2", "waitFor-2");
  }

  @Test
  public void test_stopOnStartException() throws RunBuildException {
    CompositeBuildProcessImpl i = new CompositeBuildProcessImpl();
    i.pushBuildProcess(new RecordingBuildProcess("1", BuildFinishedStatus.FINISHED_SUCCESS));
    i.pushBuildProcess(new RecordingBuildProcess("2", BuildFinishedStatus.FINISHED_SUCCESS) {{
      setStartException(new RunBuildException("aaa"));
    }});
    i.pushBuildProcess(new RecordingBuildProcess("3", BuildFinishedStatus.FINISHED_SUCCESS));

    assertRunException(i, "aaa");
    assertLog("start-1", "waitFor-1", "start-2");
  }

  @Test
  public void test_stopOnWaitForException() throws RunBuildException {
    CompositeBuildProcessImpl i = new CompositeBuildProcessImpl();
    i.pushBuildProcess(new RecordingBuildProcess("1", BuildFinishedStatus.FINISHED_SUCCESS));
    i.pushBuildProcess(new RecordingBuildProcess("2", BuildFinishedStatus.FINISHED_SUCCESS) {{
      setFinishException(new RunBuildException("aaa"));
    }});
    i.pushBuildProcess(new RecordingBuildProcess("3", BuildFinishedStatus.FINISHED_SUCCESS));

    assertRunException(i, "aaa");
    assertLog("start-1", "waitFor-1", "start-2", "waitFor-2");
  }

  @Test
  public void test_emptyInterrupted() {
    CompositeBuildProcessImpl i = new CompositeBuildProcessImpl();
    i.interrupt();

    Assert.assertFalse(i.isFinished());
    Assert.assertTrue(i.isInterrupted());
    assertRunSuccessfully(i, BuildFinishedStatus.INTERRUPTED);

    Assert.assertTrue(i.isInterrupted());
    Assert.assertTrue(i.isFinished());
  }

  @Test
  public void test_interruptCalledForFirst() {
    final CompositeBuildProcessImpl i = new CompositeBuildProcessImpl();
    final List<String> log = new ArrayList<String>();
    i.pushBuildProcess(new RecordingBuildProcess("1", BuildFinishedStatus.FINISHED_SUCCESS) {
      @Override
      public void start() throws RunBuildException {
        super.start();
        i.interrupt();
      }
    });
    i.pushBuildProcess(new RecordingBuildProcess("f", BuildFinishedStatus.FINISHED_SUCCESS));

    assertRunSuccessfully(i, BuildFinishedStatus.INTERRUPTED);
    assertLog("start-1", "interrupt-1", "waitFor-1");
  }

  @Test
  public void test_interruptCalledForFirst_WaitFor() {
    final CompositeBuildProcessImpl i = new CompositeBuildProcessImpl();
    i.pushBuildProcess(new RecordingBuildProcess("1", BuildFinishedStatus.FINISHED_SUCCESS) {
      @NotNull
      @Override
      public BuildFinishedStatus waitFor() throws RunBuildException {
        i.interrupt();
        return super.waitFor();
      }
    });
    i.pushBuildProcess(new RecordingBuildProcess("f", BuildFinishedStatus.FINISHED_SUCCESS));

    assertRunSuccessfully(i, BuildFinishedStatus.INTERRUPTED);
    assertLog("start-1", "interrupt-1", "waitFor-1");
  }

  @Test
  public void test_interruptCalledForTwo() {
    final CompositeBuildProcessImpl i = new CompositeBuildProcessImpl();
    i.pushBuildProcess(new RecordingBuildProcess("0", BuildFinishedStatus.FINISHED_SUCCESS));
    i.pushBuildProcess(new RecordingBuildProcess("1", BuildFinishedStatus.FINISHED_SUCCESS) {
      @Override
      public void start() throws RunBuildException {
        super.start();
        i.interrupt();
      }
    });
    i.pushBuildProcess(new RecordingBuildProcess("f", BuildFinishedStatus.FINISHED_SUCCESS));

    assertRunSuccessfully(i, BuildFinishedStatus.INTERRUPTED);
    assertLog("start-0", "waitFor-0", "start-1", "interrupt-1", "waitFor-1");
  }

  @Test
  public void test_interruptCalledForTwo_WaitFor() {
    final CompositeBuildProcessImpl i = new CompositeBuildProcessImpl();
    i.pushBuildProcess(new RecordingBuildProcess("0", BuildFinishedStatus.FINISHED_SUCCESS));
    i.pushBuildProcess(new RecordingBuildProcess("1", BuildFinishedStatus.FINISHED_SUCCESS) {
      @NotNull
      @Override
      public BuildFinishedStatus waitFor() throws RunBuildException {
        i.interrupt();
        return super.waitFor();
      }
    });
    i.pushBuildProcess(new RecordingBuildProcess("f", BuildFinishedStatus.FINISHED_SUCCESS));

    assertRunSuccessfully(i, BuildFinishedStatus.INTERRUPTED);
    assertLog("start-0", "waitFor-0", "start-1", "interrupt-1", "waitFor-1");
  }
}
