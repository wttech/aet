<%--

    AET

    Copyright (C) 2013 Cognifide Limited

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/includes/header.jsp" %>
<%
String timestamp = Long.toString(System.currentTimeMillis());
%>
<c:forEach var="height" items="<%=timestamp.toCharArray()%>">
    <div class="space" style="height: <c:out value="${height}"/>0px" />
        <%@ include file="dynamic_content.jsp" %>
    </div>
    <hr/>
</c:forEach>
<%@ include file="/includes/bodyContent.jsp" %>
<%@ include file="/includes/footer.jsp" %>
