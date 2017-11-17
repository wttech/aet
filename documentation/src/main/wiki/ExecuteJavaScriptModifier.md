#### Execute Java Script Modifier

Execute Java Script Modifier is responsible for executing specified js command.

Module name: **executejavascript**

##### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
| `cmd` | js script | Javascript command that will be executed | `cmd` or `snippetUrl` |
| `snippetUrl` | url | The url to external js snippet that will be executed | `cmd` or `snippetUrl` |
| `basicAuth` | encoded Basic Auth header value | When value for this parameter is provided, it will be passed to the requested URL as an basic auth header | no |

Please remember, that only one parameter `cmd` or `snippetUrl` should be defined in single `<executejavascript>` usage.
In case, when both parameters are defined, `snippetUrl` parameter will be ignored.

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="hide-test">
        <collect>
            <open />
            ...
            <executejavascript cmd="document.body.style.background = 'green';" />
            <executejavascript snippetUrl="http://example.com/snippets/my-aet-snippet.js" />
            <executejavascript snippetUrl="http://secured.com/snippets/other-aet-snippet.js" basicAuth="emVuT2FyZXVuOnozbkdAckQZbiE="/>
            ...
            <resolution width="1200" height="760" />
            <screen />
            ...
        </collect>
        <compare>
            ...
        </compare>
        <urls>
        ...
        </urls>
    </test>
    ...
    <reports>
        ...
    </reports>
</suite>
```
