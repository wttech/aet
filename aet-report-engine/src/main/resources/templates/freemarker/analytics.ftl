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
<#if (data.requests?size > 0)>
Filter marked only
<div class="btn-group btn-toggle">
	<button class="btn btn-xs btn-primary filter-button" data-target="#analytics-container-${counter} > table tr" >OFF</button>
	<button class="btn btn-xs btn-default filter-button" data-target="#analytics-container-${counter} > table tr" data-filter-selector=".marked">ON</button>
</div>
<div id="analytics-container-${counter}">
	<#list data.requests as request>
		<h4>${request.datetime}</h4>
	<table class="table table-striped table-hover table-condensed">
		<thead>
		<tr>
			<th>No.</th>
			<th>Key</th>
			<th>Value</th>
		</tr>
		</thead>
	<tbody>
		<#list request.queries?sort_by('key') as query>
			<tr class="<#if query.isMarked>marked <#if query.value??>info<#else>danger</#if></#if>">
				<td>${query_index + 1}.</td>
				<td>${query.key!}</td>
				<td>${query.value!}</td>
			</tr>
			</#list>
		</tbody>
	</table>
	</#list>
	</div>
<#else>
<p>No items were processed found</p>
</#if>