#### Click Modifier

Click Modifier allows to perform click action on some element on page. When element is not found (e.g. by improper xpath value) warning will be logged but test will be passed to the next steps.

Module name: **click**

| ! Important information |
|:----------------------- |
| In order to use this modifier it must be declared after open module in test suite XML definition.<br/><br/> Remember that element that will be clicked **must be visible** in the moment of performing click action. |

##### Parameters
| Parameter | Default value | Description | Mandatory |
| --------- | ------------- | ----------- | --------- |
| `xpath` | | xpath of element to click | yes |
| `timeout` | | Timeout for element to appear, in miliseconds. Max value of this parameter is 15000 miliseconds (15 seconds). | yes |

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="click-test">
        <collect>
            <open />
            ...
            <click xpath="//*[@id='header_0_container1_0_pRow']/div[1]/div/div/a/img" timeout="3000" />
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
