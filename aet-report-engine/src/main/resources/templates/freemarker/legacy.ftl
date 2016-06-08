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
<#import "/libs/utils.ftl" as utils>
<script type="text/javascript">
    var swapLayoutImages = function (index) {
        var currentImage = document.getElementById("swap-" + index + "-c");
        var resultImage = document.getElementById("swap-" + index + "-r");

        var displayResult = resultImage.getAttribute("style");
        console.log(displayResult);
        if (displayResult == null || displayResult == "") {
            resultImage.setAttribute("style", "display: none;");
            currentImage.setAttribute("style", "");
        } else {
            currentImage.setAttribute("style", "display: none;");
            resultImage.setAttribute("style", "");
        }
    }
</script>
<div class="row">


<#assign tests = results?keys>
	<#list tests as test>
		<#assign urls = results[test]?keys>
			<#list urls as url>
				<#assign resultList = results[test][url]>
				<#list resultList as result>
					<#assign data = result/>
				
				<#if data.properties.comparatorModule = "w3c">
				    <#include "/legacy/w3c.ftl">
				</#if>

				<#if data.properties.comparatorModule  = "layout">
				        <#include "/legacy/layout.ftl">
				</#if>
				</#list>
			</#list>
	</#list>
</div>