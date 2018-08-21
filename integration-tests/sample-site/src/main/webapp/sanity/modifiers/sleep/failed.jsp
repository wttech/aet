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
<%@ include file="/includes/header.jsp" %>
<%
java.text.SimpleDateFormat dt = new java.text.SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss.SSSZ",java.util.Locale.ENGLISH);
java.util.Date date = new java.util.Date();
%>
<div class="panel panel-default" id="date-panel">
	<div class="panel-body">
		The time is now <%= dt.format(date) %>
	</div>
</div>
<script>
	setTimeout(function(){ document.getElementById("date-panel").style.display = 'none'; }, 10000);
</script>
<%@ include file="/includes/bodyContent.jsp" %>
<%@ include file="/includes/footer.jsp" %>
