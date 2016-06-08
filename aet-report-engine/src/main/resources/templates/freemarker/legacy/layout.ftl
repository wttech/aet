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
    <hr/>

    <div class="col-md-5">
        <p>
            <a href="${data.patternImageUrl}" target="_blank" title="Click to open full size pattern">
                <img src="${data.patternImageUrl}" width="200" alt="${data.patternUrl} - baseline"/>
            </a>
        </p>

        <div class="row">
            <div class="col-xs-12">
                <p>
                ${data.patternCreateDate}<br/>
                ${data.patternTitle}<br/>
                    <a href="${data.patternUrl}" target="_blank" title="${data.patternTitle}">${data.decodedPatternUrl}</a>
                </p>
            </div>

        </div>
    </div>
    <div class="col-md-2">
        <a href="javascript:swapLayoutImages('${url_index}')" class="btn btn-info">Mask on/off</a><br/><br/>
        <a href='javascript:setAsBaseline(${utils.toJson(reportsProperties.rebasePatternsUrl)},${utils.getBaselineLinkParameters(data.properties.testName, data.properties.urlName)})' class="btn btn-primary">Set as baseline</a><br/><br/>
    </div>
    <div class="col-md-5">
        <p>
            <a href="${data.maskImageUrl}" target="_blank" title="Click to open full result">
                <img id="swap-${url_index}-r" src="${data.maskImageUrl}" width="200" alt="${data.decodedUrl} - result"/>
            </a>
            <a href="${data.currentImageUrl}" target="_blank" title="Click to open full current">
                <img id="swap-${url_index}-c" src="${data.currentImageUrl}" width="200" alt="${data.decodedUrl} - current" style="display: none;"/>
            </a>
        </p>

        <div class="row">
            <div class="col-xs-12" style="">
                <p>
                ${data.currentCreateDate}<br/>
                ${data.title}<br/>
                    <a href="${data.url}" target="_blank" title="${data.title}">${data.decodedUrl}</a>
                </p>
            </div>
        </div>
    </div>
</div>