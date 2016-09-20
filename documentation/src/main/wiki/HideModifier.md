#### Hide Modifier

Hide Modifier is responsible for hiding some unnecessary for test element on page. Affects [[Screen Collector|ScreenCollector]] results. Hiding is done by setting css `visibility` property to `hidden`. Works with webDriver only. You can hide many elements by defining many `<hide>` nodes. If xpath covers more than one element then all elements that match defined xpath will be hidden.

Module name: **hide**

| ! Important information |
|:----------------------- |
| In order to use this modifier it must be declared after open module in test suite XML definition. |

##### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
| `xpath` | xpath_to_element | Xpath to element(s) to hide | yes |
| `leaveBlankSpace` | boolean | Defines if element(s) should be invisible (effect as using `display=none`) or should be not displayed (effect as using `visibility=hidden`). When set to `true`, blank and transparent space is left in place of the hidden element, otherwise, element is completely removed from the view. When not defined, hide modifier behaves as if `leaveBlankSpace` property was set to `true`. | no |

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="hide-test">
        <collect>
            <open />          
            ...
            <hide xpath="//*[@id='logo']" />
            <hide xpath="//*[@id='primaryNavMenu']/li[2]/a/div" />            
            ...
            <screen width="1200" height="760" />
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
