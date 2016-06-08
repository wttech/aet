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
<#import "utils.ftl" as utils>

<#macro baseLineButton params index>
<div class="pull-right">
	<div class="btn btn-primary btn-xs baseline-action" data-parameters='${params}' id="baselineButton${index?c}">
		&nbsp<i class="glyphicon glyphicon-cloud-upload" data-placement="left" data-toggle="tooltip" title="Set as Baseline"></i>&nbsp
	</div>
	<div id="rebase-confirm"></div>
</div>
</#macro>
<#macro commentsTooltip>
	<i class="glyphicon glyphicon-comment" data-placement="right" data-toggle="commentTooltip"></i>
</#macro>

<#macro commentsPopover params>
	<div class="pull-right comment-div">
		<div class="btn btn-primary btn-xs" data-parameters='${params}' data-container="body" data-placement="left" data-toggle="commentPopover" data-title="Comments"></i>
			<i class="glyphicon glyphicon-edit" data-placement="left" data-toggle="tooltip" title="Edit comment"></i>
			<div class="popover">
				<div class="arrow"></div>
				<h4 class="popover-title"></h4>
				<div class="popover-input-group">
					<textarea class="form-control popover-content"></textarea>
				</div>
				<span>
					<button type="button" class="btn btn-default">Save</button>
					<button type="button" class="btn btn-default">Delete</button>
					<button type="button" class="btn btn-default">Close</button>
				</span>
			</div>
		</div>
	</div>
</#macro>

<#macro resultPanel title status index params description hasPattern risksKey>

<div class="panel report-row ${utils.cssStatusAdapter(status,"panel")}" data-status="${status?lower_case}">
	<div class="panel-heading">
		<h4 class="panel-title">
			<a data-toggle="collapse" data-parent="#tests-accordion" href="#test-${index?c}" data-source="${title}">
				<span class="glyphicon glyphicon-chevron-down"></span>
				<#if title?starts_with("http") >
					${utils.substringIfLonger(utils.stripDomainAccount(title),120)}
				<#else >
					${utils.substringIfLonger(title,120)?capitalize?replace("-"," ")!}
				</#if>
			<#if status?lower_case != "success" && risksKey?has_content && risksKey != "failure">
				<i class="glyphicon glyphicon-flash" data-placement="right" data-toggle="risksTooltip" data-html="true" data-risksKey="${risksKey}"></i>
			</#if>
			<@commentsTooltip />
			</a>${description}
			<@commentsPopover params/>
			<#if status?lower_case != "success" && hasPattern>
				<@baseLineButton params index/>
			</#if>
		</h4>
	</div>
	<div id="test-${index?c}" class="panel-collapse collapse">
		<div class="panel-body">
			<#nested>
		</div>
	</div>
</div>

</#macro>

