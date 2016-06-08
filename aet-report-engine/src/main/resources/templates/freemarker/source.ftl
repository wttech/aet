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
<#function getTypeTitle(type)>
	<#switch type!>
		<#case 'DELETE'>
			<#assign title ='Line has been removed'>
			<#break>
		<#case 'CHANGE'>
			<#assign title ='Line has changed'>
			<#break>
		<#case 'INSERT'>
			<#assign title ='Line has been added'>
			<#break>
		<#default>
			<#assign title =''>
			<#break>
	</#switch>
	<#return title>
</#function>

<#function  getTypeCssClass(type)>
	<#switch type!>
		<#case 'DELETE'>
			<#assign cssClass ='danger'>
			<#break>
		<#case 'CHANGE'>
			<#assign cssClass ='warning'>
			<#break>
		<#case 'INSERT'>
			<#assign cssClass ='success'>
			<#break>
		<#default>
			<#assign cssClass ='info'>
			<#break>
	</#switch>
	<#return cssClass>
</#function>

<#if data.status == 'SUCCESS'>
	<p>No differences found.</p>
<#else>
<p>Differences were found.</p>
</#if> <#if (data.deltas?size > 0)>
<div class="diff-report">

	<div class="col-md-12">
		<div class="text-center">
			Show Full Source
			<div class="btn-group btn-toggle">
				<button class="btn btn-xs btn-default toggle-source-visibility-button"
					data-target="tr.info">OFF</button>
				<button class="btn btn-xs btn-primary toggle-source-visibility-button"
					data-target="tr.info">ON</button>
			</div>
		</div>
	</div>
	<div class="col-md-6 left">
		<table class="table table-striped table-condensed">
			<thead>
				<tr>
					<th class="num" title="Line Number">#</th>
					<th class="code">Pattern</th>
				</tr>
			</thead>
			<tbody>
				<#list data.deltas as delta>
				<tr class="${getTypeCssClass(delta.type)}"
					title="${getTypeTitle(delta.type)}"><td class="num">${(delta.original.position+1)?c}</td>
					<td class="code">${delta.original.prettyHtml}</td>
				</tr>
				</#list>
			</tbody>
		</table>
	</div>
	<div class="col-md-6 right">
		<table class="table table-striped table-condensed">
			<thead>
				<tr>
					<th class="num" title="Line Number">#</th>
					<th class="code">Source</th>
				</tr>
			</thead>
			<tbody>
				<#list data.deltas as delta>
				<tr class="${getTypeCssClass(delta.type)}" title="${getTypeTitle(delta.type)}">
					<td class="num">${(delta.revised.position+1)?c}</td>
					<td class="code">${delta.revised.prettyHtml}</td>
				</tr>
				</#list>
			</tbody>
		</table>
	</div>
</div>
</#if>

