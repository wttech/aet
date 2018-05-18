## Best practices and tips for writing AET test scripts

#### Learn the basics of Selenium WebDriver and understand how to this knowledge when working with AET
AET interacts with web pages through WebDriver so it's good to know how to use WebDriver. Knowing about it's limitations and what it requires from test scripts to work properly can help a lot when writing test scripts for AET. It's especially important to understand that in order to get stable tests AET scripts should include smartly located wait operations, just like WebDriver test scripts do.

#### Read the documentation
AET currently has a lot of useful commands described in the GitHub docs. It's good to know them if you don't want to reinvent the wheel.

#### If AET doesn't have the feature you need, you can suggest it or write it yourself
You can either write a module and include it in your local AET instance via OSGi or you can contribute to the open source community and create a pull request to the AET code repository.

####  The order of commands matters
Each command in a test script can influence the state of the browser or the page under test, so it's important to experiment with different variations to get the results you're looking for.

##### Example #1 
A test can provide different results depending on whether first the browser window is opened or it's target resolution is defined.

Script A
```xml
<test>
	[...]
	<open/>
	<resolution width="1280" height="768"/>
	[...]
	<screen/>
</test>
```
The screenshot taken during this test captures how the page looks like when the user opens the page using the default resolution and then switches to 1280x768px.

Script B
```xml
<test>
	(...)
	<resolution width="1280" height="768"/>
	<open/>
	(...)
	<screen/>
</test>
```
The screenshot taken during this test captures how the page looks like when the user opens the page using the resolution 1280x768px from the very beginning.

##### Example #2 
A test can provide different results depending on how you use wait commands.

Script A
```xml
<test>
	(...)
	<sleep duration="5000"/>
	<click css="#bigButton" timeout="5000"/>
	<screen/>
</test>
```
The screenshot taken during this test captures how the page looks like immediately after the #bigButton is clicked. This means that maybe the page won't always manage to get to a stable state before the screenshot is taken.

Script B
```xml
<test>
	(...)
	<click css="#bigButton" timeout="5000"/>
	<sleep duration="5000"/>
	<screen/>
</test>
```
The screenshot taken during this test captures how the page looks like five seconds after the #bigButton is clicked. First the button is clicked if it appears within five seconds, then the test waits five seconds for the page to settle and eventually the screenshot is taken.

#### Use timeouts

Most commands for interacting with the elements of the page under test have timeout variables. Setting a timeout means that the command will wait for an element to achieve the required state before the test will try to interact with it.

##### Example
Script A
```xml
<test>
	(...)
	<click css="#bigButton" timeout="5000"/>
	<screen/>
</test>
```
The test doesn't click the button unless it's visible and clickable. The maximum waiting time is 5000ms, after that the click command is ignored, an exception is thrown in the logs and the test continues.

Script B
```xml
<test>
	(...)
	<click css="#bigButton"/>
	<screen/>
</test>
```
The test doesn't click the button unless it's visible and clickable. The maximum waiting time is 1000ms because that's the default waiting time, after that the click command is ignored, an exception is thrown in the logs and the test continues.

Script C
```xml
<test>
	(...)
	<click css="#bigButton" timeout="0"/>
	<screen/>
</test>
```
The test doesn't click the button unless it's visible and clickable. The maximum waiting time is 0ms, so unless the button is ready right away, the click command is ignored, an exception is thrown in the logs and the test continues.

# Suplement wait commands with sleep commands

Using wait commands and commands with timeouts is good for interacting with specific elements of the page under test. Using the sleep command after that is even better, because it allows the page under test to settle without having the user write a wait command for each element on the page that might still be loading.

Example:
<test>
	(...)
	<click css="#bigButton" timeout="5000"/>
	<sleep duration="5000"/>
	<screen/>
</test>
The screenshot taken during this test captures how the page looks like five seconds after the #bigButton is clicked. First the button is clicked if it appears within five seconds, then the test waits five seconds for the page to settle and eventually the screenshot is taken.