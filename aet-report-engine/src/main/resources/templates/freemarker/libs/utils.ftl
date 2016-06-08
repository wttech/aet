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
<#function toPercent value total>
	<#return ((value / total) * 100)?string("0")>
</#function >

<#function substringIfLonger string maxLength>
	<#if ((string?length) > maxLength)>
		<#return (string?substring(0,maxLength))?string + "...">
	</#if>
	<#return string>
</#function >

<#function stripDomainAccount url>
	<#if url?contains("@")>
		<#assign lastAmpIndex = url?last_index_of("@") + 1/>
		<#assign httpSlashesIndex = (url?index_of("//")) + 2/>
		<#return url?substring(0, httpSlashesIndex) + url?substring(lastAmpIndex, url?length) />
	</#if>
	<#return url />
</#function>

<#function cssStatusAdapter status prefix=''>
	<#assign cssStatus = "default">

	<#if status?lower_case == "success">
		<#assign cssStatus= "success"/>
	<#elseif status?lower_case == "failed">
		<#assign cssStatus= "danger"/>
	<#elseif status?lower_case == "warning">
		<#assign cssStatus= "warning"/>
	</#if>

	<#if prefix??>
		<#assign cssStatus= prefix + "-" + cssStatus />
	</#if>

	<#return cssStatus>
</#function>

<#function getStatusForUrl resultList>
	<#assign status="success">
	<#list resultList as result>
		<#if status!="failed" && result.status?lower_case == "warning">
			<#assign status="warning">
		<#elseif result.status?lower_case != "success">
			<#assign status="failed">
		</#if>
	</#list>
	<#return status>
</#function>

<#function getUrl resultList>
	<#assign result=resultList?first>
	<#assign url=(result.properties.url)!"">
	<#return url>
</#function>

<#function getDescription resultList>
	<#assign result=resultList?first>
	<#assign description="">
	<#assign description=(result.properties.description)!"">
	<#if description!="">
		<#assign description='&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;' +description>
	<#else>
		<#assign description=''>
	</#if>
	<#return description>
</#function>


<#function getBaselineTestParameters test>
	<#return '{ "testName" : "' + test + '" }'>
</#function>

<#function getBaselineLinkParameters test urlName>
	<#return '{ "testName" : "' + test + '", "urlName" : "' + urlName + '" }'>
</#function>

<#function toJson properties>
	<#assign result = '{ '>
	<#list properties?keys as key>
		<#assign keyValue = '"' + key + '" : "' + properties[key] + '"' >
		<#assign result = result + keyValue>
		<#if key_index < (properties?size - 1)>
			<#assign result = result + ', '>
		</#if>
	</#list>
	<#return result + ' }'>
</#function>

<#function hasLinkPattern linkItem>
	<#return linkItem.status?lower_case != "success" && linkItem.hasPattern>
</#function>


<#function linkHasPatterns resultList>
	<#assign linkHasPatternsResult = false>
	<#list resultList as item>
		<#if hasLinkPattern(item)>
			<#assign linkHasPatternsResult = true>
		</#if>
	</#list>
	<#return linkHasPatternsResult>
</#function>


<#function testHasPatterns urlsMap>
	<#assign testHasPatternsResult = false>
	<#list urlsMap?values as resultList>
		<#if linkHasPatterns(resultList)>
			<#assign testHasPatternsResult = true>
		</#if>
	</#list>
	<#return testHasPatternsResult>
</#function>
