#### Wait For Image Completion Modifier

Wait For Image Completion Modifier allows to wait for an image to appear and load on the page. When the element has not been found or fails to finish loading (e.g. because of an improper xpath value or timeout) a warning message will be logged but the test will be passed to the next step.

Module name: **wait-for-element-to-be-visible**

| ! Important information |
|:----------------------- |
| In order to use this modifier it must be declared after the open module in the definition of the XML test suite.<br/><br/> The modifier checks if the img html element's "complete" attribute is true, so placeholders might cause the modifier not to work as expected.|

##### Parameters
| Parameter | Default value | Description | Mandatory |
| --------- | ------------- | ----------- | --------- |
| `xpath` | | an xpath locator which identifies the image to be found and loaded within a web page | xpath or css |
| `css`   | | a css selector which identifies the image to be found and loaded within a web page | xpath or css |
| `timeout` | 1000ms | a value of timeout for the image to load, in milliseconds. The max value of this parameter is 15000 milliseconds (15 seconds). | no (the default will be used) |

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="wait-for-image-completion-test">
        <collect>
            <open />
            <resolution width="200" height="300"/>
            ...
            <wait-for-image-completion css="img#important" timeout="3000"/>
            <wait-for-image-completion xpath="//div/div/img"/>
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
