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
<#import "libs/utils.ftl" as utils>
<#import "libs/macros.ftl" as macros>
<!DOCTYPE html>
<html>
<#include "partials/head.ftl">
<body>
<#include "partials/navbar.ftl">
<div id="flash-message-container"></div>
<div id="container">
	<div id="tests-container" class="col-md-12">
	<#assign counter = 0/>
	<#assign tests = results?keys?sort>
	<#list tests as test>
		<section class="test-section" data-z-index="${testRunMap[test].zIndex}">
			<h2>
				${test}
				<@macros.commentsTooltip />
				<@macros.commentsPopover params=utils.getBaselineTestParameters(test)/>
				<#if utils.testHasPatterns(results[test]) >
					<@macros.baseLineButton params=utils.getBaselineTestParameters(test) index=counter/>
				</#if>
				<#assign counter = counter + 1/>
			</h2>
			<#assign urls = results[test]?keys?sort>
			<div class="test-results" data-test-name="${test}">
				<#list urls as urlName>
				<#assign resultList = results[test][urlName]>
				<#assign url = utils.getUrl(resultList)>
				<#assign title = url>
				<#assign status = utils.getStatusForUrl(resultList)>
				<#assign baselineParams= utils.getBaselineLinkParameters(test, urlName)>
				<#assign description=utils.getDescription(resultList)!>
				<#assign linkPatterns = utils.linkHasPatterns(resultList)>
				<@macros.resultPanel title=title status=status index=counter params=baselineParams description=description hasPattern=linkPatterns risksKey="">
					 <#assign counter = counter + 1/>
					 <#list resultList as result>
						<#assign data = result/>
						<#assign baselineParams = utils.toJson(data.properties)>
						<@macros.resultPanel title=data.properties.comparatorTitle status=data.status index=counter params=baselineParams description="" hasPattern=data.hasPattern risksKey=data.properties.comparatorModule+(data.compareAction)!"">
							<#include "${data.properties.comparatorModule}.ftl"/>
						</@macros.resultPanel>
						<#assign counter = counter + 1/>
					</#list>
				</@macros.resultPanel>
			</#list>
			</div>
			<hr/>
		</section>
	</#list>
	</div>
</div>
<#include "partials/help.ftl">
<#include "partials/footer.ftl">
</body>
</html>