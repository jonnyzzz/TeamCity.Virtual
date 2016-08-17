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
  <th>Image Name:<l:star/></th>
  <td>
    <props:textProperty name="${ctx.dockerImageName}" className="longField"/>
    <span class="error" id="error:${ctx.dockerImageName}"></span>
    <span class="smallNote">
      Use one of the following formats: <br />
      <strong>TRUSTED_IMAGE_NAME</strong> for trusted images,
      <br/>
      <strong>USERNAME/IMAGES_NAME</strong> for public images,
      <br/>
      <strong>private.repository:5000/IMAGE_NAME[:TAG]</strong> for private hosted images
    </span>
  </td>
</tr>

<tr class="advancedSetting">
  <th><label for="${ctx.mountMode}">Select mount mode to use:</label></th>
  <td>
    <props:selectProperty name="${ctx.mountMode}">
      <props:option value="rw">read-write (RW)</props:option>
      <props:option value="ro">read-only (RO)</props:option>
      <props:option value="z">shared volume label (z)</props:option>
      <props:option value="Z">private unshared volume label (Z)</props:option>
    </props:selectProperty>
  </td>
</tr>

<tr class="advancedSetting">
  <th><label for="${ctx.dockerCustomCommandLine}">Additional Docker Parameters:</label></th>
  <td>
    <props:multilineProperty name="${ctx.dockerCustomCommandLine}" linkTitle="Docker Parameters" cols="49" rows="3" expanded="${true}"/>
    <span class="smallNote">Additional commandline parameters to the <em>docker run</em> command. Write each parameter on a new line</span>
    <span id="error_${ctx.dockerCustomCommandLine}" class="error"></span>
  </td>
</tr>
