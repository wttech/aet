#### Execute Java Script Modifier

Execute Java Script Modifier is responsible for executing specified js command.

Module name: **executejavascript**

##### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
| `cmd` | js script | Javascript command that will be executed | yes |

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="hide-test">
        <collect>
            <open />
            ...
            <executejavascript cmd="document.body.style.background = 'green';" />
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
