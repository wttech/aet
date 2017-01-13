#### Screen Collector

| ! Compare collected screenshot |
|:------------------------------ |
| Please remember that defining collector and not using it during comparison phase is configuration error. From now on suites that define screen collection and does not use it during comparison phase will be rejected during suite validation phase. |

| ! Notice    |
| :---------- |

Screen Collector is responsible for collecting screenshot of the page or just part of it by specifying element locator (xpatc or css)  under given URL.

Module name: **screen**

**Note that you cannot maximize the window and specify the dimension at the same time. If no parameters provided, default browser size is set before taking screenshot.**

##### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
| `xpath` | xpath_to_element | Xpath to element(s) | optional (either xpath or css) |
| `css` | css_selector_to_element | css selector to element(s)| optional (either xpath or css) |
| `timeout` | 1000ms | The timeout for the element to appear, in milliseconds. The max value of this parameter is 15000 milliseconds (15 seconds). | no (default will be used) |


##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="screen-test">
        <collect>
            ...
            <screen name="desktop" />
            <screen name="carouselComponent" css=".carousel"/>
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

 Instead of

```xml
<screen width="1280" height="1024" name="desktop" />
```

please use:

```xml
<resolution width="1280" height="1024"/>
<sleep duration="1000" />
<screen name="desktop" />
```

| ! Note |
|:------ |
| Before taking screenshot [[Hide modifier|HideModifier]] can be applied in order to hide from the screen some elements that are not necessary for comparison, i.e. Twitter feed. <br/><br/> Also [[Resolution Modifier|ResolutionModifier]] and [[Wait For Page Loaded Modifier|WaitForPageLoadedModifier]] can be applied before Screen Collector usage to change expected collect result. <br/><br/> As in example presented above, `name` parameter can be very useful when using screen collector. More information about this parameter can be found in [[Collectors|Collectors]] section. |
