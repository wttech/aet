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
<#macro hilite issue>
${(issue.code1)?html?trim}<mark>${(issue.errorPosition)?html}</mark>${(issue.code2)?html}
</#macro>

<#if data.status == 'SUCCESS'>
<p>Document checking completed. No errors found.</p>
<#else>
<p>Validation errors count: <span class="badge alert-danger">${data.errorCount}</span>.</p>
<p>Validation warnings count: <span class="badge alert-warning">${data.warningCount}</span>.</p>
<table class="table table-condensed table-bordered table-hover">
	<thead>
		<tr>
			<th>No.</th>
			<th>Validation Output</th>
		</tr>
	</thead>
	<tbody>
		<#list data.issues as issue>
			<#switch issue.issueType>
				<#case "ERR">  <#assign type="danger">  <#break>
				<#case "WARN"> <#assign type="warning"> <#break>
				<#case "INFO"> <#assign type="info">    <#break>
				<#default>     <#assign type="">
			</#switch>
			<tr class=${type}>
				<td>${issue_index + 1}</td>
				<td>
					<p>
					<em>Line ${issue.line}, Column ${issue.column}:</em> <strong>${issue.message}</strong>
					<pre><@hilite issue /></pre>
					</p>
				</td>
			</tr>
		</#list>
		<#if (data.excludedIssues?size > 0)>
        		<tr>
        			<td colspan="3">Excluded issues: </td>
        		</tr>
        		<#list data.excludedIssues as issue>
        			<#switch issue.issueType>
                		<#case "ERR">  <#assign type="danger">  <#break>
                		<#case "WARN"> <#assign type="warning"> <#break>
                		<#case "INFO"> <#assign type="info">    <#break>
                		<#default>     <#assign type="">
                	</#switch>
                <tr class=${type}>
                    <td>${issue_index + 1}</td>
                    <td>
                        <p>
                        <em>Line ${issue.line}, Column ${issue.column}:</em> <strong>${issue.message}</strong>
                        <pre><@hilite issue /></pre>
                        </p>
                    </td>
        		</tr>
                </#list>
        </#if>
	</tbody>
</table>
</#if>