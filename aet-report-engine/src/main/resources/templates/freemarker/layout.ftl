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
<div class="row">
	<div class="col-md-12">
		<div class="text-center">
			Show Mask
			<div class="btn-group btn-toggle">
				<button class="btn btn-xs btn-default toggle-visibility-button" data-target=".mask">OFF</button>
				<button class="btn btn-xs btn-primary toggle-visibility-button" data-target=".mask">ON</button>
			</div>
			Big screenshots
			<div class="btn-group btn-toggle">
				<button class="btn btn-xs btn-primary class-change-button" data-class="col-md-3 col-md-push-3" data-target="#pattern-container-${counter}, #source-container-${counter}">OFF</button>
				<button class="btn btn-xs btn-default class-change-button" data-class="col-md-6" data-target="#pattern-container-${counter}, #source-container-${counter}">ON</button>
			</div>
		</div>
	</div>
	<div class="col-md-3 col-md-push-3" id="pattern-container-${counter}">
		<p class="text-center">Pattern</p>
		<div class="image-wrapper">

		<a href="${data.patternImageUrl}" target="_blank" title="Click to open full size pattern" class="thumbnail">
			<img data-src="${data.patternImageUrl}" alt="${data.patternUrl} - baseline"/>
		</a>
		<#if data.maskImageUrl??>
        			<a href="${data.maskImageUrl}" target="_blank" class="thumbnail mask toggle-target">
            			<img data-src="${data.maskImageUrl}"
            			     alt="${data.decodedUrl} - result"/>
            		</a>
        </#if>
		</div>

		<div class="row">
			<div class="col-xs-12">
				<p class="text-center">
				${data.patternCreateDate}<br/>
				${data.patternTitle}<br/>
					<a href="${data.patternUrl}" target="_blank"
					   title="${data.patternTitle}">${utils.substringIfLonger(utils.stripDomainAccount(data.decodedPatternUrl),60)}</a>
				</p>
			</div>

		</div>
	</div>
	<div class="col-md-3 col-md-push-3" id="source-container-${counter}">
		<p class="text-center">Collected</p>
		<#if data.maskImageUrl??>
		<div class="image-wrapper">
		<a href="${data.currentImageUrl}" target="_blank" title="Click to open full"
		   class="thumbnail current toggle-target">
			<img data-src="${data.currentImageUrl}"
			     alt="${data.decodedUrl} - current"/>
		</a>
		<a href="${data.maskImageUrl}" target="_blank" class="thumbnail mask toggle-target">
			<img data-src="${data.maskImageUrl}"
			     alt="${data.decodedUrl} - result"/>
		</a>
        </div>
        <#else>
            <p class="text-center">No difference was found.</p>
            <p class="text-center">Screenshot wasn't saved.</p>
        </#if>
		<div class="row">
			<div class="col-xs-12" style="">
				<p class="text-center">
				${data.currentCreateDate}<br/>
				${data.title}<br/>
					<a href="${data.url}" target="_blank" title="${data.title}">${utils.substringIfLonger(utils.stripDomainAccount(data.decodedUrl),60)}</a>
				</p>
			</div>
		</div>

	</div>
</div>

<#if utils.hasLinkPattern(data)>
	<@macros.baseLineButton baselineParams counter/>
</#if>
