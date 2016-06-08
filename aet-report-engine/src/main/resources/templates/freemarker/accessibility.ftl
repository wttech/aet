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
<#assign report = data.report>
<p>Errors count: <span class="badge alert-danger">${report.errorCount}</span>.</p>
<#if (report.showWarning)>
	<p>Warnings count: <span class="badge alert-warning">${report.warningCount}</span>.</p>
</#if>
<#if (report.showNotice)>
	<p>Notice count: <span class="badge alert-info">${report.noticeCount}</span>.</p>
</#if>
<table class="table table-condensed table-bordered table-hover">
	<thead>
		<tr>
			<th>No.</th>
			<th>Validation Output</th>
		</tr>
	</thead>
	<tbody>
		<#list report.nonExcludedIssues as issue>
			<#switch issue.type>
				<#case "ERROR">
					<#assign type="danger">
				<#break>
				<#case "WARN">
					<#assign type="warning">
				<#break>
				<#case "NOTICE">
					<#assign type="info">
				<#break>
				<#default>
					<#assign type="">
			</#switch>
			<tr class=${type}>
				<td>${issue_index + 1}</td>
				<td>
					<p>
					<em>Line ${issue.lineNumber}, column ${issue.columnNumber}:</em> <strong>${issue.message}</strong>
					<pre>${issue.elementStringAbbreviated}</pre>
					<small><em>${issue.code}</em></small>
					</p>
				</td>
			</tr>
		</#list>
		<#if (report.excludedIssues?size > 0)>
			<tr class="info">
				<td colspan="3">Excluded issues: </td>
			</tr>
			<#list report.excludedIssues as issue>
				<tr class=${type}>
					<td>${issue_index + 1}</td>
					<td>
						<p>
						<em>Line ${issue.lineNumber}, column ${issue.columnNumber}:</em> <strong>${issue.message}</strong>
						<pre>${issue.elementStringAbbreviated}</pre>
						<small><em>${issue.code}</em></small>
						</p>
					</td>
				</tr>
			</#list>
		</#if>
	</tbody>
</table>