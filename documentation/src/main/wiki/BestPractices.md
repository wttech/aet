## Best practices and tips for writing AET test scripts

#### Learn the basics of Selenium WebDriver and understand how to use this knowledge when working with AET
AET interacts with web pages through WebDriver so it's good to know how to use WebDriver. 
Knowing about it's limitations and what it requires from test scripts to work properly can help a lot when 
writing test scripts for AET. It's especially important to understand that in order to get stable tests 
AET scripts should include smartly located wait operations, just like WebDriver test scripts do.

#### Read the documentation
AET currently has a lot of useful [[features|Features]] described in the GitHub docs. 
It's good to know them if you don't want to reinvent the wheel.

#### If AET doesn't have the feature you need, you can suggest it or write it yourself
You can either write a module and include it in your local AET instance via OSGi or you can contribute 
to the open source community and create a pull request to the AET code repository. [Check out the tutorial here](https://github.com/Cognifide/aet/wiki/HowToExtendAET).

#### Use timeouts
Most commands for interacting with the elements of the page under test, such as [[click|ClickModifier]], [[hide|HideModifier]], [[replacetext|ReplaceTextModifier]] and more, have timeout variables. 
Setting a timeout means that the command will wait for an element to achieve the required state before the test will 
try to interact with it.

##### "click" with custom timeout
```xml
<test>
    <collect>
      ...
      <click css="#bigButton" timeout="5000"/>
      ...
    </collect>
    ...
</test>
```
The test doesn't click the button unless it's visible and clickable. The maximum waiting time is 5000ms, 
after that the "click" command is ignored, an exception is thrown in the logs and the test continues.

##### "click" with default timeout
```xml
<test>
    <collect>
      ...
      <click css="#bigButton"/>
      ...
    </collect>
    ...
</test>
```
The test doesn't click the button unless it's visible and clickable. 
The maximum waiting time is 1000ms because that's the default waiting time, after that the "click" command is ignored, 
an exception is thrown in the logs and the test continues.

##### "click" without timeout
```xml
<test>
    <collect>
      ...
      <click css="#bigButton" timeout="0"/>
      ...
    </collect>
    ...
</test>
```
The test doesn't click the button unless it's visible and clickable. The maximum waiting time is 0ms, 
so unless the button is ready right away, the "click" command is ignored, 
an exception is thrown in the logs and the test continues.

#### Supplement "wait" commands with "sleep" commands

Using "wait" commands (like [[wait-for-element-to-be-visible|WaitForElementToBeVisibleModifier]] and [[wait-for-image-completion|WaitForImageCompletionModifier]]) and commands with timeouts is good for interacting with specific elements of the page under test. 
Using the "sleep" command after that is even better, because it allows the page under test to load 
without having the user write a "wait" command for each element on the page that might still be loading.

##### Adding "sleep" before taking a screenshot, but after interacting with the page under test
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

#### Don't overuse the "sleep" command

Don't rely solely on "sleep" commands with huge durations. Using "wait" commands, such as [[wait-for-element-to-be-visible|WaitForElementToBeVisibleModifier]] and [[wait-for-image-completion|WaitForImageCompletionModifier]], instead of "sleep" allows the tests 
to run much faster in case if pages load properly and aren't broken.

##### "sleep" with large duration
```xml
<test>
    <collect>
      ...
      <sleep duration="20000"/>
      <screen/>
    </collect>
    ...
</test>
```

This test waits 20 seconds no matter how fast the page and the elements we're interested in load.

##### "click" with large timeout and "sleep" with small duration
```xml
<test>
    <collect>
      ...
      <click css="#bestButton" timeout="15000"/>
      <sleep duration="5000"/>
      <screen/>
    </collect>
    ...
</test>
```

This test might wait 20 seconds if the page loads slowly or fails to work properly, 
but if everything is OK then the test might take even 5 seconds.

##### "click" with default timeout and "wait-for-element-to-be-visible" with medium duration
```xml
<test>
    <collect>
      ...
      <wait-for-element-to-be-visible css="#bestButton" timeout="10000"/>
      <click css="#bestButton"/>
      <screen/>
    </collect>
    ...
</test>
```

This test might wait 10 seconds if the page loads slowly or fails to work properly, 
but if everything is OK then the test might take even 2 seconds (one second of default waiting for the "wait" command and the same for the "click" command).

#### When everything else fails - there's JavaScript

Sometimes it can be problematic to get the page under test in a stable state to get always the same screenshot.
 A good example are forms: if we want to have a screenshot for each step of a form we could use the "hide" command 
 and the "executejavascript" command in order to display only the required step of the form by manipulating 
 the state of the page without actually filling out multiple form fields. 
 Another case is when sticky page elements cover up the components we want to interact with - we can use JavaScript 
 to invoke the click() function of the selected element instead of 
 using the WebDriver API (which is called by the AET "click" command). 
 [[Click the following link to learn more about using JS in AET tests|ExecuteJavaScriptModifier]].

#### First test locally and make sure your test is stable

Having very stable tests is key for using AET so before joining any new tests to your main test suite:
- run the test case in development locally
- make sure that the test is doing everything what you expect from it and only that
- run the test multiple times to make sure that the test results are consistent