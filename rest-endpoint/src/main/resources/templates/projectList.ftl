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
<h1>Total ${size} projects are present in database.</h1><hr>
<div class="container-fluid">
  <div class="list-group">
  <#list data as project>
    <a href="?project=${project.getProject()}&company=${project.getCompany()}" class="list-group-item list-group-item-action">
        Project: <strong>${project.getProject()}</strong> in company: <strong>${project.getCompany()}</strong>.
    </a>
  </#list>
  </div>
</div>
</@u.page>