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
<#if ((data.filteredStatusCodes?size > 0) || (data.excludedStatusCodes?size > 0))>
<table class="table table-striped table-hover">
	<thead>
	<tr>
		<th>No.</th>
		<th>Status Code</th>
		<th>URL</th>
	</tr>
	</thead>
	<tbody>
		<#list data.filteredStatusCodes as item>
		<tr class=<#if (item.code > 400) >danger</#if>>
			<td>${item_index + 1}.</td>
			<td>${item.code}</td>
			<td><a href="${item.url}" title="${item.url}">${utils.substringIfLonger(utils.stripDomainAccount(item.url),120)}</a></td>
		</tr>
		</#list>
		<#if (data.excludedStatusCodes?size > 0)>
		<tr class="info">
			<td colspan="3">Excluded status codes: </td>
		</tr>
		<#list data.excludedStatusCodes as item>
		<tr class="info">
			<td>${item_index + 1}.</td>
			<td>${item.code}</td>
			<td><a href="${item.url}" title="${item.url}">${utils.substringIfLonger(utils.stripDomainAccount(item.url),120)}</a></td>
		</tr>
		</#list>
		</#if>
	</tbody>
</table>
<#else>
<p>No errors found</p>
</#if>