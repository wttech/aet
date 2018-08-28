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
<%
	java.text.SimpleDateFormat dt = new java.text.SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss.SSSZ", java.util.Locale.ENGLISH);
	java.util.Date date = new java.util.Date();
%>

<div class="panel-body" id="changeAfterDelay">
	The time is now <%= dt.format(date) %>
</div>
<div class="bs-docs-section">
	<div class="row">
		<div class="col-lg-12">
			<div class="bs-component">
				<blockquote>
					<p id="texts"></p>
				</blockquote>
			</div>
		</div>
	</div>
</div>

<script>
	var list = document.getElementById("texts");
	var elementToChange = document.getElementById("changeAfterDelay");
	var TIMEOUT_LIMIT = 10;
	var MIDDLE_OF_TIMEOUT_LIMIT = 5;
	var COUNTER = 0;
	function addElements(n) {
		for (var i = 0; i < n; i++) {
			var paragraph = document.createElement("p");
			var text = document.createTextNode("AET ");
			paragraph.appendChild(text);
			list.appendChild(paragraph);
		}
	}
	addElements(5);
	for (var t = 1; t <= TIMEOUT_LIMIT; ++t) {
		setTimeout(function () {
			addElements(5);
			if (COUNTER == (MIDDLE_OF_TIMEOUT_LIMIT)) {
				elementToChange.innerHTML = 'Final Text That is displayed after timeout while page elements are still loading.';
			}
			++COUNTER;
		}, t * 1000);
	}
</script>
