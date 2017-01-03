#### Click Modifier

Click Modifier allows to perform a click action on some element on the page. When the element is not found (e.g. because of the improper xpath value) warning will be logged but the test will be passed to the next steps.

Module name: **click**

| ! Important information |
|:----------------------- |
| In order to use this modifier it must be declared after the open module in the definition of the XML test suite.<br/><br/> Remember that the element that will be clicked **must be visible** at the moment of performing the click action. |

##### Parameters
| Parameter | Default value | Description | Mandatory |
| --------- | ------------- | ----------- | --------- |
| `xpath` | | xpath of the element to be clicked        | yes |
| `css`   | | css selector of the element to be clicked | yes |
| `timeout` | | The timeout for the element to appear, in miliseconds. The max value of this parameter is 15000 miliseconds (15 seconds). | yes |

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="click-test">
        <collect>
            <open />
            ...
            <click xpath="//*[@id='header_0_container1_0_pRow']/div[1]/div/div/a/img" timeout="3000" />
            <click css="#logo > a" timeout="1000" />
            <sleep duration="2000" />
            ...
            <screen width="1280" height="800" name="desktop" />
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
