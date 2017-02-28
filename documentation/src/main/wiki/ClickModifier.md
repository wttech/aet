#### Click Modifier

Click Modifier allows to perform a click action on some element on the page. When the element has not been found (e.g. because of an improper xpath value) a warning message will be logged but the test will be passed to next steps.

Module name: **click**

| ! Important information |
|:----------------------- |
| In order to use this modifier it must be declared after the open module in the definition of the XML test suite.<br/><br/> Remember that the element that will be clicked **must be visible** at the moment of performing the click action. |

##### Parameters
| Parameter | Default value | Description | Mandatory |
| --------- | ------------- | ----------- | --------- |
| `xpath` | | xpath of the element to be clicked        | xpath or css |
| `css`   | | css selector of the element to be clicked | xpath or css |
| `timeout` | 1000ms | The timeout for the element to appear, in milliseconds. The max value of this parameter is 15000 milliseconds (15 seconds). | no (default will be used) |

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="click-test">
        <collect>
            <open />
            <resolution width="200" height="300"/>
            ...
            <click xpath="//*[@id='header_0_container1_0_pRow']/div[1]/div/div/a/img" timeout="3000" />
            <click css="#logo > a"/>
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
