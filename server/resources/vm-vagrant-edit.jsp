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
<%@ include file="/include-internal.jsp" %>
<jsp:useBean id="ctx" class="com.jonnyzzz.teamcity.virtual.FormBean"/>

<tr>
  <th>Path to Vagrant file:</th>
  <td>
    <props:textProperty name="${ctx.vagrantFile}" className="longField"/>
    <span class="error" id="error:${ctx.vagrantFile}"></span>
    <span class="smallNote">
      Relative path from the <em>build checkout directory <bs:help file="Build+Checkout+Directory"/></em>
      to the directory where <em>Vagrantfile</em> is located
    </span>
  </td>
</tr>

<tr class="advancedSetting">
  <th><label for="${ctx.vagrantCustomCommandLine}">Additional Vagrant Parameters:</label></th>
  <td>
    <props:multilineProperty name="${ctx.vagrantCustomCommandLine}" linkTitle="Vagrant Parameters" cols="49" rows="3" expanded="${true}"/>
    <span class="smallNote">Additional commandline parameters to the <em>vagrant up</em> command. Write each parameter on a new line</span>
    <span id="error_${ctx.vagrantCustomCommandLine}" class="error"></span>
  </td>
</tr>

<tr class="advancedSetting">
  <th><label for="${ctx.vagrantCustomCommandLine}">Override Vagrantfile:</label></th>
  <td>
    <props:checkboxProperty name="${ctx.vagrantfileDoOverride}" value="yes" uncheckedValue="no" />
    <label for="${ctx.vagrantfileDoOverride}">Do override Vagrantfile with custom content</label>
    <props:multilineProperty name="${ctx.vagrantfileCustomContent}" linkTitle="Vagrantfile content" cols="49" rows="3"
                             expanded="${true}" value=""/>
    <span class="smallNote">If you enter text here, the existing Vagrantfile is overridden with this string</span>
    <span id="error_${ctx.vagrantfileCustomContent}" class="error"></span>
  </td>
</tr>
