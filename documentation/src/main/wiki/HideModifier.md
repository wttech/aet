#### Hide Modifier

Hide Modifier is responsible for hiding an element on the page that is redundant for testing and/or can make the page look different each time a screenshot is taken. It affects [[Screen Collector|ScreenCollector]] results. Hiding is performed by setting the css `visibility` property to `hidden`. It works with webDriver only. You can hide many elements by defining many `<hide>` nodes. If the xpath covers more than one element then all the elements matching the xpath will be hidden.

Module name: **hide**

| ! Important information |
|:----------------------- |
| In order to use this modifier it must be declared after the open module in the definition of the test suite XML. |

##### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
| `xpath` | xpath_to_element | Xpath to element(s) to hide | xpath or css |
| `css` | css_selector_to_element | css selector to element(s) to hide | xpath or css |
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
            <hide css="#logo > a" />            
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
