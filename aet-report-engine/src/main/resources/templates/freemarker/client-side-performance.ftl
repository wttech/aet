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
<#if !(tabCounter??) ><#assign tabCounter=0></#if>
<#assign tabCounter=tabCounter+1>
<#assign type = "warning">
<#if data.report.prettyOverallScore = "A"><#assign type="success"></#if>
<#if data.report.prettyOverallScore = "F"><#assign type="danger"></#if>
<h3>Overall score: <span class="label label-${type}">${data.report.prettyOverallScore}</span></h3>
<div class="tabbable tabs-left">
	<ul class="nav nav-tabs">
		<#list data.report.g?keys as key>
		<#assign type = "warning"
				 rule = data.report.g[key]>
		<#if rule.prettyScore = "A"><#assign type="success"></#if>
		<#if rule.prettyScore = "F"><#assign type="danger"></#if>
		<li class="${inactive_tab!"active"}">
			<a href="#${key}-${tabCounter}" data-toggle="tab"><span class="label label-${type}">${rule.prettyScore}</span> <strong>${rule.name}</strong></a>
		</li>
		<#assign inactive_tab = "">
		</#list>
	</ul>
	<div class="tab-content">
		<#list data.report.g?keys as key>
			<#assign type="warning"
	        		 rule = data.report.g[key]>
	        <#if rule.prettyScore = "A"><#assign type="success"></#if>
	        <#if rule.prettyScore = "F"><#assign type="danger"></#if>
			<div class="tab-pane ${inactive_pane!"active"}" id="${key}-${tabCounter}">
				<h4>Grade <span class="label label-${type}">${rule.prettyScore}</span> on ${rule.name}</h4>
				<p>${rule.message}</p>
				<hr />
				<ul>
					<#list rule.components as component>
						<li>${component}</li>
					</#list>
				</ul>
			</div>
			<#assign inactive_pane = "">
		</#list>
	</div>
</div>

