#### Resolution Modifier

Resolution Modifier is responsible for changing browser screen size so that it meets certain viewport/breakpoint. 
Affects [[Screen Collector | ScreenCollector]] results.  

| ! Note |
|:------ |
| Please note that final resolution of screenshots may be different when scrollbar is displayed. <br/><br/> 
Default  width of Firefox's Scrollbar is equal to 33px.  (so when you want to grab viewport of size 1024, then set width parameter to 1057px) |

Module name: **resolution**

##### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
| `width` | int (1 to 100000) | Window width | yes |
| `height` | int (1 to 100000) | Window height, will be taken into account only if original page height is smaller than `height` value. Otherwise will be ignored and full screenshot of a page will be taken. | no |

| ! Important information |
|:----------------------- |
| You don't have to specify height param because AET takes screenshot of full-page height. |

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="resolution-modify-test">
        <collect>
            ...
            <resolution width="1000"/>
            <sleep duration="2000"/>
            <screen name="full"/>
            ...
            <resolution width="800" height="600"/>
            <sleep duration="2000"/>
            <screen name="carousel" css=".carousel"/>
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
