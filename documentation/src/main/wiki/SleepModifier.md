#### Sleep Modifier

Sleep Modifier is responsible for temporarily ceasing execution, causes current thread to sleep. It is useful in situations when page resources have a long loading time - it suspends next collectors for some time.

Module name: **sleep**

##### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
| `duration` | int (1 to 30000) | Sleep time, in milliseconds | yes |

| ! Important information |
|:----------------------- |
| One sleep duration cannot be longer than 30000 milliseconds (30 seconds).<br/><br/> Two consecutive sleep modifiers are not allowed.<br/><br/> Total sleep duration (sum of all sleeps) in test collection phase cannot be longer than 120000 milliseconds (2 minutes). |

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="sleep-test">
        <collect>
            ...
            <open />
            ...
            <sleep duration="3000" />
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
Try using wait commands instead of "sleep" when possible: [[wait-for-element-to-be-visible|WaitForElementToBeVisibleModifier]] and [[wait-for-image-completion|WaitForImageCompletionModifier]].
A test can provide different results depending on how you use sleep commands.

##### First "sleep" then interact with the page and finally take a screenshot
```xml
<test>
    <collect>
      ...
      <sleep duration="5000"/>
      <click css="#bigButton" timeout="5000"/>
      <screen/>
    </collect>
    ...
</test>
```
The screenshot taken during this test captures how the page looks like immediately after the #bigButton is clicked. 
This means that maybe the page won't always manage to get to a stable state before the screenshot is taken.

##### First interact with the page then "sleep" and finally take a screenshot
```xml
<test>
    <collect>
      ...
      <click css="#bigButton" timeout="5000"/>
      <sleep duration="5000"/>
      <screen/>
    </collect>
    ...
</test>
```
The screenshot taken during this test captures how the page looks like five seconds after the #bigButton is clicked. 
First the button is clicked if it appears within five seconds, then the test waits five seconds for the page to load 
and eventually the screenshot is taken.
