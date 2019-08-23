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
<!DOCTYPE html PUBLIC "-//IETF//DTD HTML 2.0//EN">
<html>
    <head>
        <title>Suites list</title>
    </head>
    <body>
        <h1>Total ${size} projects are present in database.</h1><hr>
        <#list data as key, value>
            Project: <b>${key.getProject()}</b> in company: <b>${key.getCompany()}</b> has total <b>${value?size}</b> unique suites.
            <#list value as suite>
                <br>Suite <i>${suite._id}</i>
                <ul>

                </ul>
            </#list>
        </#list>
    </body>
</html>