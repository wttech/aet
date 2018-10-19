#### Resolution Modifier

Resolution Modifier is responsible for changing browser screen size. Affects [[Screen Collector | ScreenCollector]] results.

| ! Note |
|:------ |
| Firefox: Please note that final resolution of screenshots may be different when scrollbar is displayed. <br/><br/> Default  width of Firefox's Scrollbar is equal to 33px.  (so when you want to grab viewport of size 1024, then set width parameter to 1057px) |

Module name: **resolution**

##### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
| `width` | int (1 to 35000) | Window width | yes |
| `height` | int (1 to 35000) | Window height | no |

| Note |
|:------ |
| When height is not specified then it's computed by JavaScript (using `document.body.scrollHeight` property). | 
| **If the resolution is specified without height parameter it should be specified after [`open`](https://github.com/Cognifide/aet/wiki/Open)** and after all modifiers which may affect the page height (e.g. [`hide`](https://github.com/Cognifide/aet/wiki/HideModifier))  |

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

##### Known issues

[#357](https://github.com/Cognifide/aet/issues/357) - If you're using the auto-height calculation feature of [[Resolution Modifier|ResolutionModifier]], it may happen that the
height of collected screenshot is different every time you run the suite, which results in failures on the report.
Currently you can use one of following workarounds to fix this issues:
* specify the `height` parameter manually with a value which is equal or greater than the height of page you want to test, e.g.:
  ```$xml
    <open/>
    <resolution width="1366" height="5000"/>
    <scren/>
  ```
* use an additional `resolution` modifier with any `height` (value doesn't matter) before the `open` phase - to ensure that 
  the page will be opened with desired `width` and the 2nd `resolution` will only compute and change the height.
  ```$xml
  <resolution width="1366" height="100"/>
  <open/>
  <resolution width="1366"/>
  <scren/>
  ```
* use two `resolution` modifiers with the same `width` attribute (the first one may also have `height` attribute with any value)
 and a `sleep` modifier between them, e.g:
  ```$xml
  <open/>
  <resolution width="1366"/>
  <sleep duration="1000"/>
  <resolution width="1366"/>
  <scren/>
  ```

#### Tips and tricks

In order to make sure that your screenshots have the resolution you expect them to have you need to test it first.
Run the test case your developing currently and check if the screenshot resolution fits your requirements.
If it's different from what you expected then you can try adding X pixels 
where X is the width of the browsers scrollbar.
Another thing that you can try is increasing the height of the screenshot to be 
greater or equal to the height of the page's content.
