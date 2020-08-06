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
| `override` | `true`/`false` | Override option should be set at `true` when your want replace header added by Chrome instance, otherwise it should by `false` | no (default false) |

Important note.

When you want add another value to Accept header and leave the default value create by Chrome instance, you can use `override=false`.

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="header-modify-test" useProxy="rest">
        <collect>
            ...
            <header key="User-Agent" value="Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20120101 Firefox/33.0" 
                    override="true"/>
            <header key="Accept" value="application/json" override="true"/>
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
