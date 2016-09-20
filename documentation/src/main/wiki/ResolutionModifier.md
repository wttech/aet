#### Resolution Modifier

Resolution Modifier is responsible for changing browser screen size. Affects [[Screen Collector | ScreenCollector]] results.  

| ! Note |
|:------ |
| Please note that final resoulution of screenshots may be different when scrollbar is dispayed. <br/><br/> Default  width of Firefox's Scrollbar is equal to 33px.  (so when you want to grab viewport of size 1024, then set width parameter to 1057px) |

Module name: **resolution**

##### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
| ~~`maximize`~~ | ~~true <br/> false (default)~~ | ~~Maximize browser window~~ | This property is deprecated and will be removed in future release. |
| `width` | int (1 to 100000) | Window width | no |
| `height` | int (1 to 100000) | Window height | no |

| ! Important information |
|:----------------------- |
| You cannot maximize the window and specify the dimension at the same time. If you specify height param you have to also specify width param and vice versa. |

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="resolution-modify-test">
        <collect>
            ...
            <resolution width="200" height="300"/>
            <screen />
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
