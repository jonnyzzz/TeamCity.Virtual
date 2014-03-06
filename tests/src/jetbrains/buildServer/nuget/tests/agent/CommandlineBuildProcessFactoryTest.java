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

package jetbrains.buildServer.nuget.tests.agent;

import jetbrains.buildServer.BaseTestCase;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.*;
import jetbrains.buildServer.nuget.agent.util.CommandlineBuildProcessFactory;
import jetbrains.buildServer.nuget.agent.util.impl.CommandlineBuildProcessFactoryImpl;
import jetbrains.buildServer.runner.SimpleRunnerConstants;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 08.12.11 15:34
 */
public class CommandlineBuildProcessFactoryTest extends BaseTestCase {
  private Mockery m;
  private BuildProcessFacade myFacade;
  private AgentRunningBuild myBuild;
  private BuildRunnerContext myRootContext;
  private BuildRunnerContext mySubContext;
  private CommandlineBuildProcessFactory myFactory;
  private BuildProcess myProcess;
  private BuildProgressLogger myLogger;
  private File myWorkDir;

  @BeforeMethod
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    m = new Mockery();
    myWorkDir = createTempDir();
    myFacade = m.mock(BuildProcessFacade.class);
    myRootContext = m.mock(BuildRunnerContext.class, "root-context");
    mySubContext = m.mock(BuildRunnerContext.class, "sub-context");
    myBuild =  m.mock(AgentRunningBuild.class);
    myProcess = m.mock(BuildProcess.class);
    myLogger = m.mock(BuildProgressLogger.class);
    myFactory = new CommandlineBuildProcessFactoryImpl(myFacade);

    m.checking(new Expectations(){{
      oneOf(myFacade).createBuildRunnerContext(myBuild, SimpleRunnerConstants.TYPE, myWorkDir.getPath(), myRootContext);
      will(returnValue(mySubContext));

      allowing(myRootContext).getBuild(); will(returnValue(myBuild));
      allowing(mySubContext).getBuild(); will(returnValue(myBuild));

      allowing(myBuild).getBuildLogger(); will(returnValue(myLogger));
      allowing(myLogger).message(with(any(String.class)));

      oneOf(myFacade).createExecutable(myBuild, mySubContext);
      will(returnValue(myProcess));
    }});
  }

  @Test
  public void testSupportQuotes() throws RunBuildException {
    m.checking(new Expectations(){{
      oneOf(mySubContext).addRunnerParameter(SimpleRunnerConstants.USE_CUSTOM_SCRIPT, "true");
      oneOf(mySubContext).addRunnerParameter(SimpleRunnerConstants.SCRIPT_CONTENT, "program \" foo \"");
    }});

    myFactory.executeCommandLine(myRootContext, "program", Arrays.asList("\"", "foo", "\""), myWorkDir, Collections.<String, String>emptyMap());

    m.assertIsSatisfied();
  }

  @Test
  public void testSupportQuotes2() throws RunBuildException {
    m.checking(new Expectations(){{
      oneOf(mySubContext).addRunnerParameter(SimpleRunnerConstants.USE_CUSTOM_SCRIPT, "true");
      oneOf(mySubContext).addRunnerParameter(SimpleRunnerConstants.SCRIPT_CONTENT, "program \" \" foo\" \"");
    }});

    myFactory.executeCommandLine(myRootContext, "program", Arrays.asList("\"", "\" foo\"", "\""), myWorkDir, Collections.<String, String>emptyMap());

    m.assertIsSatisfied();
  }

  @Test
  public void testQuoteArguments() throws RunBuildException {
    m.checking(new Expectations(){{
      oneOf(mySubContext).addRunnerParameter(SimpleRunnerConstants.USE_CUSTOM_SCRIPT, "true");
      oneOf(mySubContext).addRunnerParameter(SimpleRunnerConstants.SCRIPT_CONTENT, "program \" \"f o o\" \"z e\" \"");
    }});

    myFactory.executeCommandLine(myRootContext, "program", Arrays.asList("\"", "f o o", "z e", "\""), myWorkDir, Collections.<String, String>emptyMap());

    m.assertIsSatisfied();
  }

  @Test
  public void testSupportEnv() throws RunBuildException {
    m.checking(new Expectations(){{
      oneOf(mySubContext).addRunnerParameter(SimpleRunnerConstants.USE_CUSTOM_SCRIPT, "true");
      oneOf(mySubContext).addRunnerParameter(SimpleRunnerConstants.SCRIPT_CONTENT, "program");
      oneOf(mySubContext).addEnvironmentVariable("a", "b");
    }});

    myFactory.executeCommandLine(myRootContext, "program", Collections.<String>emptyList(), myWorkDir, Collections.singletonMap("a", "b"));

    m.assertIsSatisfied();
  }

  @Test
  public void testQuoteCommand() throws RunBuildException {
    m.checking(new Expectations(){{
      oneOf(mySubContext).addRunnerParameter(SimpleRunnerConstants.USE_CUSTOM_SCRIPT, "true");
      oneOf(mySubContext).addRunnerParameter(SimpleRunnerConstants.SCRIPT_CONTENT, "\"p r o g r a m\"");
      oneOf(mySubContext).addEnvironmentVariable("a", "b");
    }});

    myFactory.executeCommandLine(myRootContext, "p r o g r a m", Collections.<String>emptyList(), myWorkDir, Collections.singletonMap("a", "b"));

    m.assertIsSatisfied();
  }

  @Test
  public void testQuoteCommandArgs() throws RunBuildException {
    m.checking(new Expectations(){{
      oneOf(mySubContext).addRunnerParameter(SimpleRunnerConstants.USE_CUSTOM_SCRIPT, "true");
      oneOf(mySubContext).addRunnerParameter(SimpleRunnerConstants.SCRIPT_CONTENT, "\"p r o g r a m\" a \"b c d e\" f");
      oneOf(mySubContext).addEnvironmentVariable("a", "b");
    }});

    myFactory.executeCommandLine(myRootContext, "p r o g r a m", Arrays.asList("a", "b c d e", "f"), myWorkDir, Collections.singletonMap("a", "b"));

    m.assertIsSatisfied();
  }
}
