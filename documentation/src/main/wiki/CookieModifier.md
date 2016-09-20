#### Cookie Modifier

Cookie Modifier allows to modify cookies for given page, i.e. add or remove some cookies.

Module name: **modify-cookie**

| ! Important information |
|:----------------------- |
| In order to use this modifier it must be declared before open module in test suite XML definition. When declared after open module (but before Cookie Collector) it can be used as filter for Cookie Collector. |

##### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
| `action`  | add <br/> remove | Specifies what action should be taken with given cookie | yes |
| `cookie-name` | | Cookie name | yes |
| `cookie-value` | | Cookie value | Yes, if `add action` is chosen |
| `cookie-domain` | | Cookie domain attribute value | No, used only if `add action` is chosen |
| `cookie-path` | | Cookie path attribute value | No, used only if `add action` is chosen |

| ! Note |
|:------ |
| If `cookie-domain` provided WebDriver will reject cookies unless the Domain attribute specifies a scope for the cookie that would include the origin server. For example, the user agent will accept a cookie with a Domain attribute of `example.com` or of `foo.example.com` from `foo.example.com`, but the user agent will not accept a cookie with a Domain attribute of `bar.example.com` or of `baz.foo.example.com`. For more information read [here](https://tools.ietf.org/html/rfc6265#section-4.1.2.3). |

| ! Note |
|:------ |
| If `cookie-path` provided WebDriver will reject cookie unless the path portion of the url matches (or is a subdirectory of) the cookie's Path attribute, where the `%x2F` (`/`) character is interpreted as a directory separator. |

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="cookie-modify-test">
        <collect>
            ...
            <modify-cookie action="add" cookie-name="sample-cookie" cookie-value="sample-cookie-value"/>
            <modify-cookie action="remove" cookie-name="another-cookie"/>
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
