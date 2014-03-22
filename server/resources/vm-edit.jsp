<%--
  ~ Copyright 2000-2014 Eugene Petrenko
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<%@ include file="/include-internal.jsp"%>
<jsp:useBean id="ctx" class="com.jonnyzzz.teamcity.virtual.FormBean"/>

<c:set var="note">
  <span class="smallNote">Select the <em>virtual environment</em> to use</span>
</c:set>

<l:settingsGroup title="Virtual Environment">
  <props:selectSectionProperty name="${ctx.vm}" title="Virtualization" note="${note}">
    <c:forEach var="it" items="${ctx.vms}">
      <props:selectSectionPropertyContent value="${it.name}" caption="${it.caption}">
        <jsp:include page="${it.edit}" />
      </props:selectSectionPropertyContent>
    </c:forEach>
  </props:selectSectionProperty>
</l:settingsGroup>

<l:settingsGroup title="Commands to Execute">
  <tr>
    <th>&nbsp;</th>
    <td>
      <span class="smallNote">
        Virtual environment is started with
        the <em>build checkout directory <bs:help file="Build+Checkout+Directory"/></em> mounted for read/write into the virtual environment.
        <br />
        The <em>working directory path</em> and the <em>build checkout directory path</em> are automatically mapped into virtual environment paths.
        <br/>
        The virtual environment is destroyed after execution is completed
      </span>
    </td>
  </tr>
  <tr>
    <th>Command:</th>
    <td>
      <props:multilineProperty name="${ctx.script}" linkTitle="Script to run in the VM" cols="49" rows="8" expanded="${true}"/>
      <span class="error" id="error:${ctx.script}"></span>
      <span class="smallNote">
        Commands to be executed in the virtual environment in the <em>working directory</em>
      </span>
    </td>
  </tr>

  <tr>
    <th>
      <label for="${ctx.workingDirectory}">Working Directory: <bs:help file="Build+Working+Directory" /></label>
    </th>
    <td>
      <props:textProperty name="${ctx.workingDirectory}"  className="longField"/>
      <bs:vcsTree fieldId="${ctx.workingDirectory}" treeId="teamcity-build-workingDir" dirsOnly="true"/>
      <span class="smallNote">
        The <em>relative path</em> to the <em>build checkout directory <bs:help file="Build+Checkout+Directory"/></em>
        to run the script in the virtual environment
        <br/>
      </span>
    </td>
  </tr>
</l:settingsGroup>
