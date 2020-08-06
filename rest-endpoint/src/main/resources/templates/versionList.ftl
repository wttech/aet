<!--

    AET

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
<#import "core.ftl" as u>

<@u.page>
<h1>Project: <strong>${project}</strong> in company: <strong>${company}</strong></h1>
<h2>Suite: ${name}</h2>
<h3>Suite has ${size} versions.</h3><hr>
<div class="container-fluid">
  <div class="card m-2" >
    <div class="card-body">
      <div class="container-fluid">
        <div class="list-group list-group-flush">
        <#list data as suiteVersion>
          <li class="list-group-item">
            <a href="${reportDomain}/report.html?company=${company}&project=${project}&correlationId=${suiteVersion.correlationId}">
                report for suite ${name} in version ${suiteVersion.version}
            </a>
            [correlationId: ${suiteVersion.correlationId}] runned at ${suiteVersion.runTimestamp.get()?number_to_datetime}</br>&nbsp;&nbsp;&nbsp;
            <a href="/api/metadata?company=${company}&project=${project}&correlationId=${suiteVersion.correlationId}"\>report metadata</a>
          </li>
        </#list>
        </div>
      </div>
    </div>
  </div>
</div>
</@u.page>
