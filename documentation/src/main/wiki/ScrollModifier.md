#### Click Modifier

Scroll Modifier allows to scroll page to specific element. <br/>
If no parameter is provided, then `position="bottom"` is used as default. <br/>
If two or more parameters are provided, than exception will be thrown. <br/>
`position` parameter accepts only two values: `top` and `bottom`. If other value is provided, exception will be thrown.

Module name: **scroll**

| ! Important information |
|:----------------------- |
| In order to use this modifier it must be declared after the open module in the definition of the XML test suite.

##### Parameters
| Parameter | Default value | Description | Mandatory |
| --------- | ------------- | ----------- | --------- |
| `position` | `bottom` | values accepted: `top`, `bottom` - page position to scroll | no (default will be used) |
| `css`   | | css selector of the element to scroll page view into | no (default will be used) |
| `xpath` | | xpath of the element to scroll page view into        | no (default will be used) |

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="click-test">
        <collect>
            <open />
            ...
            <scroll css="#element-id"/>
            ...
            <screen />
            ...
        </collect>
        <compare>
            ...
            <screen comparator="layout" />
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
