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

<div class="panel panel-default" style="position: relative;">
	<div class="panel-body" id="test-element" style="position: absolute; left: 20px; top: 150px; z-index: 1;">
		<img src="/sample-site/sanity/modifiers/slow/conference.jpg" id="image-to-wait-for" alt="test-image" width="800"/>
	</div>
</div>

<%
	java.text.SimpleDateFormat dt = new java.text.SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss.SSSZ",java.util.Locale.ENGLISH);
	java.util.Date date = new java.util.Date();
%>

<div class="container">
	<div class="page-header" id="banner">
		<div class="row">
			<div class="col-lg-8 col-md-7 col-sm-6">
				<h1>AET Demo Page</h1>
				<p class="lead"><%= dt.format(date) %></p>
				<p class="lead">
					build with <a href="http://getbootstrap.com/">Bootstrap</a> and <a
						href="http://bootswatch.com/">Bootswatch</a>
				</p>
			</div>

			<div class="col-lg-4 col-md-5 col-sm-6">
				<div class="sponsor">
					<img src="/sample-site/assets/demo_files/logo.png" alt="Bootswatch">
				</div>
			</div>

		</div>
	</div>
</div>

<%@ include file="/includes/footer.jsp" %>
