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
<!doctype html>
<html>
    <head>
        <title>Suites list</title>

        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
    </head>
    <body>
        <h1>Total ${size} projects are present in database.</h1><hr>
        <div class="container-fluid">
            <#list data as key, value>
            <p>
                <button class="btn btn-link" type="button" data-toggle="collapse" data-target="#${key.getProject()}-${key.getCompany()}">
                    Project: <b>${key.getProject()}</b> in company: <b>${key.getCompany()}</b> has total <b>${value?size}</b> unique suites.
                </button>
            </p>
            <div class="collapse" id="${key.getProject()}-${key.getCompany()}">
                <#list value as suiteWrapper>
                <div class="card m-2" >
                    <div class="card-body">
                        <h5 class="card-title">Suite: <i>${suiteWrapper.suiteName}</i></h5>
                        <ul class="list-group list-group-flush">
                            <#list suiteWrapper.testItems as suite>
                            <li class="list-group-item">
                                <a href="${reportDomain}/report.html?company=${suite.company}&project=${suite.project}&correlationId=${suite.correlationId}">
                                    report for suite ${suite.name} in version ${suite.version}
                                </a>
                                [correlationId: ${suite.correlationId}] runned at ${suite.runTimestamp.get()?number_to_datetime}</br>&nbsp;&nbsp;&nbsp;
                                <a href="/api/metadata?company=${suite.company}&project=${suite.project}&correlationId=${suite.correlationId}"\>report metadata</a>
                            </li>
                            </#list>
                        </ul>
                        <#if suiteWrapper.count.get() gt 10>
                        <a href="#" class="card-link">See all</a> <br>
                        </#if>
                    </div>
                </div>
                </#list>
            </div>
            </#list>
        </div>

        <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
    </body>
</html>