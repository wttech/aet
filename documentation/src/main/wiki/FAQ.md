# FAQ
This section contains answers for frequently asked questions. If you can't find an answer to your question here
please use the [Issues Tool](https://github.com/Cognifide/aet/issues) to raise a question.

- [1. Setup and first steps](#1-setup-and-first-steps)
  - [1.1. What do I need to start using AET?](#11-what-do-i-need-to-start-using-aet)
  - [1.2. I have an AET environment, how can I run tests?](#12-i-have-an-aet-environment-how-can-i-run-tests)
  - [1.3. How can I integrate AET with continuous integration tools?](#13-how-can-i-integrate-aet-with-continuous-integration-tools)
  - [1.4. I'm running AET for the first time. How will the tool know the patterns?](#14-im-running-aet-for-the-first-time-how-will-the-tool-know-the-patterns)
- [2. Environment and software](#2-environment-and-software)
  - [2.1. Which browser is used by AET?](#21-which-browser-is-used-by-aet)
  - [2.2. Does AET use database for storing data?](#22-does-aet-use-database-for-storing-data)
  - [2.3. I have a problem with missing fonts (my page is not English). What should I do?](#23-i-have-a-problem-with-missing-fonts-my-page-is-not-english-what-should-i-do)
- [3. User interface and reports](#3-user-interface-and-reports)
  - [3.1 What browser should I use to browse AET reports?](#31-what-browser-should-i-use-to-browse-aet-reports)
  - [3.2 I would like to compare pages between two environments/domains. How can I do that with the AET Tool?](#32-i-would-like-to-compare-pages-between-two-environmentsdomains-how-can-i-do-that-with-the-aet-tool)
  - [3.3 How can I access pages that require authentication to enter?](#33-how-can-i-access-pages-that-require-authentication-to-enter)
  - [3.4 How can I simulate another user agent?](#34-how-can-i-simulate-another-user-agent)
  - [3.5 What can I do with elements that render differently each time a page is opened and it occurs in changes detected each time?](#35-what-can-i-do-with-elements-that-render-differently-each-time-a-page-is-opened-and-it-occurs-in-changes-detected-each-time)
  - [3.6 I want to check how my page behaves on mobile devices, how can I do that?](#36-i-want-to-check-how-my-page-behaves-on-mobile-devices-how-can-i-do-that)
  - [3.7 How to get the latest report for the AET test suite?](#37-how-to-get-the-latest-report-for-the-aet-test-suite)
- [4. Best practices](#4-best-practices)
  - [4.1 I want to check how my page looks on several devices - what is the best way do configure a suite?](#41-i-want-to-check-how-my-page-looks-on-several-devices---what-is-the-best-way-do-configure-a-suite)

## 1. Setup and first steps

### 1.1. What do I need to start using AET?
You need Maven and Java installed to run AET and Chrome browser to see reports if you have AET environment
already set up. See more information in [[Running Suite|RunningSuite#requirements]] section.
To setup AET you need [VirtualBox 5.2.18](https://www.virtualbox.org/wiki/Downloads) to run AET VM.
See more details in [[Basic Setup|BasicSetup]] section.


### 1.2. I have an AET environment, how can I run tests?
You need Maven and Java installed to run AET and Chrome browser to see reports. 
See more information in [[Running Suite|RunningSuite#requirements]] section.
To start AET suite you need 2 XML files:
- [[pom.xml|RunningSuite#pomxml]] that defines how to run [[AET Client Application|ClientApplication]],
- [[suite.xml|DefiningSuite]] that defines [[AET suite|SuiteStructure]].

To run suite simply execute this command: 

`mvn aet:run`

from the directory where you have two above XML files.

Next, you will see [[processing progress|TrackingProgress]]. After a 
[[SUCCESSFUL BUILD|TrackingProgress#when-tests-successfully-finish---command-line]]
explore `target` directory and open `redirect.html` in Chrome browser file to see the [[report|SuiteReport]].


### 1.3. How can I integrate AET with continuous integration tools?
AET tests are triggered by [[AET Maven Client Application|ClientApplication]]. Thanks to this you may simply
run `mvn aet:run` from your favourite CI tool (e.g. [Jenkins](https://jenkins.io/) or 
[Bamboo](https://www.atlassian.com/software/bamboo)).

### 1.4. I'm running AET for the first time. How will the tool know the patterns?
When running AET for the first time AET has no patterns. The first collected screenshots will be treated
as patterns, so the first run will detect no changes. The same is true when a new test or url is added to
already existing suite - new records will have patterns collected during the first suite run.


---

## 2. Environment and software

### 2.1. Which browser is used by AET?
AET currently uses Firefox 38.6.0 ESR as default browser. Information about current versions of components
used by AET can be found in [[Third-party software used by system|SystemComponents#third-party-software-used-by-system]]
section.


### 2.2. Does AET use database for storing data?
Yes, AET uses [MongoDB](https://www.mongodb.org/) where all data collected during AET suite processing 
is stored.


### 2.3. I have a problem with missing fonts (my page is not English). What should I do?
You can add custom fonts to AET virtual machine. To do so, place desired fonts inside
`usr/share/fonts` or `/usr/share/fonts/truetype` directories, AET's FireFox should use them.
Other idea is to install fonts with e.g.:
`yum groupinstall chinese-support`
`yum groupinstall arabic-support`
See more details in answers for [this AET question](https://github.com/Cognifide/aet/issues/61).

---

## 3. User interface and reports

### 3.1 What browser should I use to browse AET reports?
AET reports are dedicated and tested with Chrome browser, however they should also work fine with other 
modern browsers like FireFox or Edge.

### 3.2 I would like to compare pages between two environments/domains. How can I do that with the AET Tool?
The AET System enables comparison of the same page (set of pages) between different environments (domains). 
To do so, simply change domain parameter 
(either by change in [[suite definition|DefiningSuite#suite]] or using [[aet run command|ClientApplication#parameters]]) 
when running following tests. All data collected during those suite runs will be compared with the 
same patterns (which are independent of domain attribute).

### 3.3 How can I access pages that require authentication to enter?
There are two possible solutions. For the Basic Authentication you may use [[Header Modifier|HeaderModifier]]
with `Authorization` key set:
```xml
<header key="Authorization" value="Basic emVuT2FyZXVuOnozbkdAckQZbiE=" />
```
The string in `value` is [base64 encoded](https://en.wikipedia.org/wiki/Basic_access_authentication) 
`username:password` string.
The other way is to use [[Login Modifier|LoginModifier]] that can pass simple login form.


### 3.4 How can I simulate another user agent?
Use [[Header Modifier|HeaderModifier]] to achieve that. Just add header modifier with key `'User-Agent'`
 and requested value.


### 3.5 What can I do with elements that render differently each time a page is opened and it occurs in changes detected each time?
Use [[Hide Modifier|HideModifier]] to hide those elements with `css` or `xpath` selectors.


### 3.6 I want to check how my page behaves on mobile devices, how can I do that?
AET is not integrated with mobile devices. However, you may simulate mobile resolution and check if your
page changes its rendering breakpoints for different resolutions. Use [[Resolution Modifier|ResolutionModifier]]
to simulate different resolutions.

### 3.7 How to get the latest report for the AET test suite?
AET report are accessible by an URL with suite execution identifier passed in query string, i.e.:
```
http://localhost:8181/report/report.html?company=aet&project=aet&correlationId=aet-main-1234567890
```
In the example above the latest version of suite with correlation ID of `aet-main-1234567890`.
To see tha last version of suite without knowing `correlationId` use the URL of following form:
```
http://localhost:8181/report/report.html?company=<company>&project=<project>&suite=<suite>
```
i.e.:
```
http://localhost:8181/report/report.html?company=aet&project=aet&suite=main
```

---

## 4. Best practices

### 4.1 I want to check how my page looks on several devices - what is the best way do configure a suite?
You may configure several [[Resolution Modifiers|ResolutionModifier]] and [[Screen Collectors|ScreenCollector]]
within single `<collect>` phase. Please remember that it is a good practice to give tested page
the time to adjust after changing the resolution and **before** collecting a screenshot.
Example test configuration may look like this:

```xml
<collect>
	<open/>
	<wait-for-page-loaded />
	<resolution width="1280" height="1024"/>
	<sleep duration="1000"/>
	<screen  name="desktop"/>
	<resolution width="320" height="480"/>
	<sleep duration="1000"/>
	<screen  name="mobile"/>
</collect>
```
