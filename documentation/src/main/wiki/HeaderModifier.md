#### Header Modifier

Header Modifier is responsible for injecting additional headers to the page before it is opened to test.

Module name: **header**

**Important information**

In order to use this modifier it must be declared before the open module in the definition of the test suite XML and [[proxy|SuiteStructure#proxy]] must be used.

##### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
| `key` | x | Key for the header | yes |
| `value` | y | Value for the header | yes |

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="header-modify-test" useProxy="rest">
        <collect>
            ...
            <header key="Authorization" value="Basic emVuT2FyZXVuOnozbkdAckQZbiE=" />
            ...
            <open />
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
