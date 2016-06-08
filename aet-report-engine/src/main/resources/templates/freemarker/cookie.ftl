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
<#if (data.compareAction == 'TEST')>
	Cookie with name: '${data.cookieName}'<#if (data.cookieValue??)> and value: '${data.cookieValue}'</#if> <#if (data.status != 'SUCCESS')>not </#if>found
<#elseif (data.compareAction == 'COMPARE')>
	<#if (data.status == 'SUCCESS')>
		Cookie names are identical.
	<#else>
		<#if (data.additionalCookies?size > 0)>
			Additional cookies:
			<table class="table table-striped table-hover">
				<thead>
				<tr>
					<th>No.</th>
					<th>Name</th>
				</tr>
				</thead>
				<tbody>
					<#list data.additionalCookies as item>
					<tr>
						<td>${item_index + 1}.</td>
						<td>${item}</td>
					</tr>
					</#list>
				</tbody>
			</table>
		</#if>
		<#if (data.notFoundCookies?size > 0)>
			Not found cookies:
			<table class="table table-striped table-hover">
				<thead>
				<tr>
					<th>No.</th>
					<th>Name</th>
				</tr>
				</thead>
				<tbody>
					<#list data.notFoundCookies as item>
					<tr>
						<td>${item_index + 1}.</td>
						<td>${item}</td>
					</tr>
					</#list>
				</tbody>
			</table>
		</#if>
	</#if>
	<#if (data.foundCookies?size > 0)>
			<br/>
			Cookies matching the pattern:
			<table class="table table-striped table-hover">
				<thead>
				<tr>
					<th>No.</th>
					<th>Name</th>
				</tr>
				</thead>
				<tbody>
					<#list data.foundCookies as item>
					<tr>
						<td>${item_index + 1}.</td>
						<td>${item}</td>
					</tr>
					</#list>
				</tbody>
			</table>
	</#if>		
<#else>
	<#if (data.cookies?size > 0)>
	<table class="table table-striped table-hover">
		<thead>
		<tr>
			<th>No.</th>
			<th>Name</th>
			<th>Value</th>
			<th>Expiry</th>
		</tr>
		</thead>
		<tbody>
			<#list data.cookies as item>
			<tr>
				<td>${item_index + 1}.</td>
				<td>${item.name}</td>
				<td>${item.value}</td>
				<td>${item.expiry!}</td>
			</tr>
			</#list>
		</tbody>
	</table>
	<#else>
	<p>No cookies found</p>
	</#if>
</#if>