#### Header Modifier

Header Modifier is responsible for injecting additional headers to page before it is opened to test.

Module name: **header**

| ! Important information |
|:----------------------- |
| In order to use this modifier it must be declared before open module in test suite XML definition and *[[proxy|SuiteStructure#proxy]]* must be used. |

##### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
| `key` | x | Key for header | yes |
| `value` | y | Value for header | yes |

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="header-modify-test">
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
