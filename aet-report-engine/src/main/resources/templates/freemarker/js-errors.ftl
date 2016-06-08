<#--

    Cognifide AET :: Report Engine

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

-->
<#if (data.jsErrorLogs?size > 0)>
<table class="table table-striped table-hover">
	<thead>
	<tr>
		<th>No.</th>
		<th>Error</th>
		<th>Source</th>
		<th>Line number</th>
	</tr>
	</thead>
	<tbody>
		<#list data.jsErrorLogs as item>
		<tr>
			<td>${item_index + 1}.</td>
			<td>${item.errorMessage}</td>
			<td>
				<#if item.sourceName??>
				<a href="${item.sourceName}">${item.sourceName}</a>
				<#else>
					UNDEFINED
				</#if>
			</td>
			<td>${item.lineNumber}</td>
		</tr>
		</#list>
	</tbody>
</table>
<#else>
<p>No errors found</p>
</#if>