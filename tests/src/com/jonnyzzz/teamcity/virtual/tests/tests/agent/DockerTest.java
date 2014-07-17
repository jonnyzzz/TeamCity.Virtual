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

package com.jonnyzzz.teamcity.virtual.tests.tests.agent;

import com.jonnyzzz.teamcity.virtual.VMConstants;
import com.jonnyzzz.teamcity.virtual.VMRunner;
import com.jonnyzzz.teamcity.virtual.run.ScriptFile;
import com.jonnyzzz.teamcity.virtual.run.VMRunnerFactory;
import com.jonnyzzz.teamcity.virtual.run.docker.DockerVM;
import com.jonnyzzz.teamcity.virtual.run.docker.UserUIDAndGID;
import com.jonnyzzz.teamcity.virtual.run.vagrant.VagrantFilePatcher;
import com.jonnyzzz.teamcity.virtual.run.vagrant.VagrantVM;
import com.jonnyzzz.teamcity.virtual.util.util.BuildProcessBase;
import com.jonnyzzz.teamcity.virtual.util.util.CommandlineBuildProcessFactory;
import jetbrains.buildServer.BaseTestCase;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.*;
import jetbrains.buildServer.util.FileUtil;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class DockerTest extends BaseTestCase {

  private class DoDockerTest {
    final Mockery m = new Mockery();

    final AgentRunningBuild build = m.mock(AgentRunningBuild.class);
    final BuildRunnerContext context = m.mock(BuildRunnerContext.class);
    final BuildProgressLogger logger = m.mock(BuildProgressLogger.class);
    final CommandlineBuildProcessFactory cmd = m.mock(CommandlineBuildProcessFactory.class);

    final Map<String, String> runnerParameters = new TreeMap<String, String>();
    final File home = createTempDir();
    File work = new File(home, "some/work/dir");

    final VMRunner run = new VMRunner(new VMRunnerFactory(
            new com.jonnyzzz.teamcity.virtual.run.VMRunner[]{
                    new DockerVM(new ScriptFile(), new UserUIDAndGID() {
                      @Nullable
                      @Override
                      public String getUID() {
                        return "SID";
                      }

                      @Nullable
                      @Override
                      public String getGID() {
                        return "GID";
                      }
                    }),
                    new VagrantVM(new ScriptFile(), new VagrantFilePatcher())
            }, new CommandlineBuildProcessFactory() {
      @NotNull
      @Override
      public BuildProcess executeCommandLine(@NotNull BuildRunnerContext hostContext,
                                             @NotNull Collection<String> argz,
                                             @NotNull File workingDir,
                                             @NotNull Map<String, String> additionalEnvironment) throws RunBuildException {
        final List<String> processed = new ArrayList<String>();
        for (String arg : argz) {
          processed.add(
                  arg
                          .replace(home.getPath(), "!HOME!")
                          .replace(work.getPath(), "!WORK!")
                          .replace(FileUtil.getRelativePath(home, work), "!REL_WORK!")
          );
        }

        System.out.println("cmd>> " + processed);
        return cmd.executeCommandLine(hostContext, processed, workingDir, additionalEnvironment);
      }
    }
    ));

    public DoDockerTest() throws Exception {


      m.checking(new Expectations() {{
        allowing(context).getRunnerParameters();
        will(returnValue(Collections.unmodifiableMap(runnerParameters)));
        allowing(context).getBuild();
        will(returnValue(build));

        allowing(build).getBuildLogger();
        will(returnValue(logger));
        allowing(build).getCheckoutDirectory();
        will(returnValue(home));
        allowing(context).getWorkingDirectory();
        will(returnValue(work));

        allowing(build).getFailBuildOnExitCode();
        will(returnValue(true));

        allowing(logger).message(with(any(String.class)));
        allowing(logger).activityStarted(with(any(String.class)), with(any(String.class)), with(any(String.class)));
        allowing(logger).activityFinished(with(any(String.class)), with(any(String.class)));
      }});
    }

    @NotNull
    public DoDockerTest withRunParameter(@NotNull final String key, @NotNull final String value) {
      runnerParameters.put(key, value);
      return this;
    }

    @NotNull
    public DoDockerTest withWorkDir(@NotNull final String relPath) {
      work = new File(home, relPath).getAbsoluteFile();
      return this;
    }

    @NotNull
    private DoDockerTest expectCommand(@NotNull final BaseMatcher<Collection<String>> argz) throws RunBuildException {
      m.checking(new Expectations() {{
        //noinspection unchecked
        oneOf(cmd).executeCommandLine(
                with(any(BuildRunnerContext.class)),
                with(argz),
                with(equal(work)),
                with(any(Map.class))
        );
        will(returnValue(new BuildProcessBase() {
          @NotNull
          @Override
          protected BuildFinishedStatus waitForImpl() throws RunBuildException {
            return BuildFinishedStatus.FINISHED_SUCCESS;
          }
        }));
      }});

      return this;
    }

    @NotNull
    public DoDockerTest expectExactCall(@NotNull final String... argz) throws RunBuildException {
      return expectCommand(new BaseMatcher<Collection<String>>() {
        @Override
        public boolean matches(Object item) {
          //noinspection unchecked
          final List<String> processed = (List<String>) item;

          if (processed.equals(new ArrayList<String>(Arrays.asList(argz)))) {
            return true;
          }

          System.out.println("Was: " + processed);
          return false;
        }

        @Override
        public void describeTo(Description description) {
          description.appendText("Commandline: " + Arrays.toString(argz));
        }
      });
    }

    @NotNull
    public DoDockerTest expectStartsWith(@NotNull final String... argz) throws RunBuildException {
      return expectCommand(new BaseMatcher<Collection<String>>() {
        @Override
        public boolean matches(Object item) {
          //noinspection unchecked
          final List<String> processed = (List<String>) item;

          for (int i = 0; i < argz.length; i++) {
            String s = argz[i];
            if (!processed.get(i).equals(s)) return false;
          }

          return true;
        }

        @Override
        public void describeTo(Description description) {
          description.appendText("CommandlineStartsWith: " + Arrays.toString(argz));
        }
      });
    }

    public void test() throws IOException, RunBuildException {
      FileUtil.createDir(home);
      FileUtil.createDir(work);

      setupRunnerParameters();

      final BuildProcess process = run.createBuildProcess(build, context);
      process.start();
      process.waitFor();

      m.assertIsSatisfied();
    }

    public void setupRunnerParameters() {
      runnerParameters.put(VMConstants.PARAMETER_VM, VMConstants.VM_DOCKER);
      runnerParameters.put(VMConstants.PARAMETER_SCRIPT, "script");
      runnerParameters.put(VMConstants.PARAMETER_DOCKER_IMAGE_NAME, "image");
      runnerParameters.put(VMConstants.PARAMETER_DOCKER_CUSTOM_COMMANDLINE, "$CUSTOM$");
    }
  }

  @Test
  public void test_docker_pull_is_called() throws Exception {
    new DoDockerTest()
            .expectExactCall("docker", "pull", "image")
            .expectStartsWith("docker", "run")
            .expectStartsWith("docker", "kill")
            .expectStartsWith("docker", "run")
            .test();
  }

}
