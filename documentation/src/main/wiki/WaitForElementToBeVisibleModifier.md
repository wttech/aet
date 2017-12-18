#### Wait For Element To Be Visible Modifier

Wait For Element To Be Visible Modifier allows to wait for an element to appear on the page. When the element has not been found (e.g. because of an improper xpath value or timeout) a warning message will be logged but the test will be passed to next steps.

Module name: **wait-for-element-to-be-visible**

| ! Important information |
|:----------------------- |
| In order to use this modifier it must be declared after the open module in the definition of the XML test suite.|

##### Parameters
| Parameter | Default value | Description | Mandatory |
| --------- | ------------- | ----------- | --------- |
| `xpath` | | xpath of the element to be found        | xpath or css |
| `css`   | | css selector of the element to be found | xpath or css |
| `timeout` | 1000ms | The timeout for the element to appear, in milliseconds. The max value of this parameter is 15000 milliseconds (15 seconds). | no (default will be used) |

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="wait-for-element-to-be-visible-test">
        <collect>
            <open />
            <resolution width="200" height="300"/>
            ...
            <wait-for-element-to-be-visible css="#search" timeout="3000"/>
            <wait-for-element-to-be-visible xpath="//div/div/a"/>
            <sleep duration="2000" />
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
