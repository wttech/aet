#### Screen Collector

| ! Compare collected screenshot |
|:------------------------------ |
| Please remember that defining collector and not using it during comparison phase is configuration error. From now on suites that define screen collection and does not use it during comparison phase will be rejected during suite validation phase. |

| ! Notice    |
| :---------- |
| Screen Collector is responsible for collecting screenshot of the page or just part of it by specifying element locator (xpath or css)  under given URL. |
| Screenshot of the page only covers the current viewport, i.e. the screenshot size (both width and height) will be equal to the browser's window size set by the [`Resolution Modifier`](https://github.com/Cognifide/aet/wiki/ResolutionModifier). |  
| If you want to take a screenshot of entire page, you should either skip the `height` parameter of `resolution` modifier (to let it be computed by JavaScript), or set it to a value which will cover the whole page. 
If you want to take a screenshot of specific element on the page (using `xpath` or `css` selector), then this entire element must be visible in current viewport - otherwise you will get an processing error. | 

Module name: **screen** 

##### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
| `xpath` | xpath_to_element | Xpath to element(s) | optional (either xpath or css) |
| `css` | css_selector_to_element | css selector to element(s)| optional (either xpath or css) |
| `exclude-elements` | css_selector_to_element | Elements found with that selector will be ignored by layout comparator (they won't affect its results) but will be rendered on the report as captured. | no |
| `timeout` | 1000ms | The timeout for the element to appear, in milliseconds. The max value of this parameter is 15000 milliseconds (15 seconds). | no (default will be used) this parameter applies only in conjunction with xpath or css param |

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
| Before taking screenshot [Hide modifier](https://github.com/Cognifide/aet/wiki/HideModifier) can be applied in order to hide from the screen some elements that are not necessary for comparison, i.e. Twitter feed. <br/><br/> Also [Resolution Modifier](https://github.com/Cognifide/aet/wiki/ResolutionModifier) and [Wait For Page Loaded Modifier](https://github.com/Cognifide/aet/wiki/WaitForPageLoadedModifier) can be applied before Screen Collector usage to change expected collect result. <br/><br/> As in example presented above, `name` parameter can be very useful when using screen collector. More information about this parameter can be found in [Collectors](https://github.com/Cognifide/aet/wiki/Collectors) section. |
