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


<props:selectSectionProperty name="${ctx.vm}" title="Virtualization">
  <c:forEach var="it" items="${ctx.vms}">
    <props:selectSectionPropertyContent value="${it.name}" caption="${it.caption}">
      <jsp:include page="${it.edit}" />
    </props:selectSectionPropertyContent>
  </c:forEach>
</props:selectSectionProperty>

<tr>
  <th>Command:</th>
  <td>
    <props:multilineProperty name="${ctx.script}" linkTitle="Script to run in the VM" cols="49" rows="8" expanded="${true}"/>
    <span class="error" id="error:${ctx.script}"></span>
  </td>
</tr>
