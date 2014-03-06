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
import jetbrains.buildServer.agent.BuildProcess;
import com.jonnyzzz.teamcity.virtual.util.util.DelegatingBuildProcess;
import org.jetbrains.annotations.NotNull;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicReference;

import static jetbrains.buildServer.agent.BuildFinishedStatus.FINISHED_SUCCESS;
import static jetbrains.buildServer.agent.BuildFinishedStatus.INTERRUPTED;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 07.07.11 20:06
 */
public class DelegatingBuildProcessTest extends BuildProcessTestCase {

  @Test
  public void test_FailedToCreateDelegate() {
    DelegatingBuildProcess aaa = new DelegatingBuildProcess(new LoggingAction(FINISHED_SUCCESS) {
      @NotNull
      public BuildProcess startImpl() throws RunBuildException {
        super.startImpl();
        throw new RunBuildException("aaa");
      }
    });

    Assert.assertFalse(aaa.isFinished());
    assertRunException(aaa, "aaa");
    assertLog("start-impl", "finish-impl");
    Assert.assertTrue(aaa.isFinished());
  }

  @Test
  public void test_interrupted_before_start() {
    DelegatingBuildProcess aaa = new DelegatingBuildProcess(new LoggingAction(FINISHED_SUCCESS));
    aaa.interrupt();
    assertRunSuccessfully(aaa, INTERRUPTED);
    assertLog();
    Assert.assertTrue(aaa.isFinished());
    Assert.assertTrue(aaa.isInterrupted());
  }

  @Test
  public void test_interrupted_in_startImpl() {
    final AtomicReference<BuildProcess> bp = new AtomicReference<BuildProcess>();
    DelegatingBuildProcess aaa = new DelegatingBuildProcess(new LoggingAction(FINISHED_SUCCESS){
      @NotNull
      @Override
      public BuildProcess startImpl() throws RunBuildException {
        bp.get().interrupt();
        return super.startImpl();
      }
    });
    bp.set(aaa);

    assertRunSuccessfully(aaa, INTERRUPTED);
    assertLog("start-impl","finish-impl");
    Assert.assertTrue(aaa.isFinished());
    Assert.assertTrue(aaa.isInterrupted());
  }

  @Test
  public void test_interrupted_in_process_start() {
    final AtomicReference<BuildProcess> bp = new AtomicReference<BuildProcess>();
    DelegatingBuildProcess aaa = new DelegatingBuildProcess(new LoggingActionBase(){
      @Override
      protected RecordingBuildProcess createSub() {
        return new RecordingBuildProcess("i", BuildFinishedStatus.FINISHED_SUCCESS){
          @Override
          public void start() throws RunBuildException {
            super.start();
            bp.get().interrupt();
          }
        };
      }
    });
    bp.set(aaa);

    assertRunSuccessfully(aaa, INTERRUPTED);
    assertLog("start-impl","start-i","interrupt-i","waitFor-i","finish-impl");
    Assert.assertTrue(aaa.isFinished());
    Assert.assertTrue(aaa.isInterrupted());
  }

  @Test
  public void test_interrupted_in_process_finish() {
    final AtomicReference<BuildProcess> bp = new AtomicReference<BuildProcess>();
    DelegatingBuildProcess aaa = new DelegatingBuildProcess(new LoggingActionBase(){
      @Override
      protected RecordingBuildProcess createSub() {
        return new RecordingBuildProcess("i", BuildFinishedStatus.FINISHED_SUCCESS){
          @NotNull
          @Override
          public BuildFinishedStatus waitFor() throws RunBuildException {
            bp.get().interrupt();
            return super.waitFor();
          }
        };
      }
    });
    bp.set(aaa);

    assertRunSuccessfully(aaa, INTERRUPTED);
    assertLog("start-impl","start-i","interrupt-i","waitFor-i","finish-impl");
    Assert.assertTrue(aaa.isFinished());
    Assert.assertTrue(aaa.isInterrupted());
  }

  @Test
  public void test_interrupted_in_process_finishImpl() {
    final AtomicReference<BuildProcess> bp = new AtomicReference<BuildProcess>();
    DelegatingBuildProcess aaa = new DelegatingBuildProcess(new LoggingAction(BuildFinishedStatus.FINISHED_SUCCESS){
      @Override
      public void finishedImpl() {
        bp.get().interrupt();
        super.finishedImpl();
      }
    });
    bp.set(aaa);

    assertRunSuccessfully(aaa, INTERRUPTED);
    assertLog("start-impl","start-i","waitFor-i","finish-impl");
    Assert.assertTrue(aaa.isFinished());
    Assert.assertTrue(aaa.isInterrupted());
  }

  @Test(dataProvider = "buildFinishStatuses")
  public void test_with_sub_action(@NotNull BuildFinishedStatus status) {
    DelegatingBuildProcess aaa = new DelegatingBuildProcess(new LoggingAction(status));
    assertRunSuccessfully(aaa, status);
    assertLog("start-impl", "start-i", "waitFor-i", "finish-impl");
    Assert.assertTrue(aaa.isFinished());
  }

  private class LoggingAction extends LoggingActionBase {
    private final BuildFinishedStatus myStatus;

    private LoggingAction(BuildFinishedStatus status) {
      myStatus = status;
    }

    @Override
    protected RecordingBuildProcess createSub() {
      return new RecordingBuildProcess("i", myStatus);
    }
  }

  /**
   * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
   * Date: 07.07.11 20:30
   */
  private abstract class LoggingActionBase implements DelegatingBuildProcess.Action {
    @NotNull
    public BuildProcess startImpl() throws RunBuildException {
      log("start-impl");
      return createSub();
    }

    protected abstract RecordingBuildProcess createSub();

    public void finishedImpl() {
      log("finish-impl");
    }
  }
}
