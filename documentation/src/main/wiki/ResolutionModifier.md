#### Resolution Modifier

Resolution Modifier is responsible for changing browser screen size. Affects [[Screen Collector | ScreenCollector]] results.  

| ! Note |
|:------ |
| Please note that final resoulution of screenshots may be different when scrollbar is dispayed. <br/><br/> Default  width of Firefox's Scrollbar is equal to 33px.  (so when you want to grab viewport of size 1024, then set width parameter to 1057px) |

Module name: **resolution**

##### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
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
            <sleep duration="1000"/>
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

#### Tips and tricks

In order to make sure that your screenshots have the resolution you expect them to have you need to test it first.
Run the test case your developing currently and check if the screenshot resolution fits your requirements.
If it's different from what you expected then you can try adding X pixels 
where X is the width of the browsers scrollbar.
Another thing that you can try is increasing the height of the screenshot to be 
greater or equal to the height of the page's content.
