# FAQ
This section contains answers for frequently asked questions. If you can't find an answer for your question here
please use [Issues Tool](https://github.com/Cognifide/aet/issues) to ask a question.

- [1. Setup and first steps](#1-setup-and-first-steps)
  - [1.1. What do I need to star using AET?](#11-what-do-i-need-to-star-using-aet)
  - [1.2. I have AET environment, how can I run tests?](#12-i-have-aet-environment-how-can-i-run-tests)
  - [1.3. How can I integrate AET with continuous integration tools?](#13-how-can-i-integrate-aet-with-continuous-integration-tools)
  - [1.4. I run AET the first time, how the tool will know patterns?](#14-i-run-aet-the-first-time-how-the-tool-will-know-patterns)
- [2. Environment and software](#2-environment-and-software)
  - [2.1. Which browser is used by AET?](#21-which-browser-is-used-by-aet)
  - [2.2. Does AET use database for storing data?](#22-does-aet-use-database-for-storing-data)
  - [2.3. My page is not english and I have problem with missing fonts. What should I do?](#23-my-page-is-not-english-and-i-have-problem-with-missing-fonts-what-should-i-do)
- [3. User interface and reports](#3-user-interface-and-reports)
  - [3.1 I would like to compare pages between two environments/domains. How can I do that with the AET Tool?](#31-i-would-like-to-compare-pages-between-two-environmentsdomains-how-can-i-do-that-with-the-aet-tool)
  - [3.2 How can I access pages that require authentication to enter?](#32-how-can-i-access-pages-that-require-authentication-to-enter)
  - [3.3 How can I change User-Agent header?](#33-how-can-i-change-user-agent-header)
  - [3.4 What can I do with elements that render differently each time page is open and it occurs in changes detected each time?](#34-what-can-i-do-with-elements-that-render-differently-each-time-page-is-open-and-it-occurs-in-changes-detected-each-time)
  - [3.5 I want to check how my page behaves on mobile devices, how can I do that?](#35-i-want-to-check-how-my-page-behaves-on-mobile-devices-how-can-i-do-that)
- [4. Best practices](#4-best-practices)
  - [4.1 I want to check how my page looks on several devices - what is the best way do configure suite?](#41-i-want-to-check-how-my-page-looks-on-several-devices---what-is-the-best-way-do-configure-suite)


## 1. Setup and first steps

### 1.1. What do I need to star using AET?
You need Maven and Java installed to run AET and Chrome browser to see reports if you have AET environment
already set up. See more information in [[Running Suite|RunningSuite#requirements]] section.
To setup AET you need [VirtualBox 5.1.14](https://www.virtualbox.org/wiki/Downloads) to run AET VM.
See more details in [[Basic Setup|BasicSetup]] section.


### 1.2. I have AET environment, how can I run tests?
You need Maven and Java installed to run AET and Chrome browser to see reports if you have AET environment
already set up. See more information in [[Running Suite|RunningSuite#requirements]] section.
To start AET suite you need 2 XML files:
- [[pom.xml|RunningSuite#pomxml]] that defines how to run [[AET Client Application|ClientApplication]],
- [[suite.xml|DefiningSuite]] that defines [[AET suite|SuiteStructure]].

To run suite simply execute this command: 

`mvn aet:run`

from the directory where you have two above XML files.

Next, you will see [[processing progress|TrackingProgress]]. After a 
[[SUCCESSFUL BUILD|TrackingProgress#when-tests-successfully-finish---command-line]]
explore `target` directory and open `redirect.html` file to see the [[report|SuiteReport]].


### 1.3. How can I integrate AET with continuous integration tools?
AET tests are triggered by [[AET Maven Client Application|ClientApplication]]. Thanks to this you may simply
run `mvn aet:run` from your favourite CI tool (e.g. [Jenkins](https://jenkins.io/) or 
[Bamboo](https://www.atlassian.com/software/bamboo)).

### 1.4. I run AET the first time, how the tool will know patterns?
When running AET for the firs time AET has no patterns and the first collected screenshots will be treated
as patterns. So the first run will detect no changes. The same is true when a new test/url is added to
already existing suite - new records will have patterns collected during the first suite run.


---

## 2. Environment and software

### 2.1. Which browser is used by AET?
AET currently uses Fire Fox 38.6.0 ESR as default browser. Information about current version of components
used by AET can be found in [[Third-party software used by system|SystemComponents#third-party-software-used-by-system]]
section.


### 2.2. Does AET use database for storing data?
Yes, AET uses [MongoDB](https://www.mongodb.org/) where all data collected during AET suite processing are stored.


### 2.3. My page is not english and I have problem with missing fonts. What should I do?
You can add custom fonts to AET virtual machine. To do so, place desired fonts inside
`usr/share/fonts` or `/usr/share/fonts/truetype` directories, AET's FireFox should use them.
Other idea is to install fonts with e.g.:
`yum groupinstall chinese-support`
`yum groupinstall arabic-support`
See more details in answers for [this AET question](https://github.com/Cognifide/aet/issues/61).

---

## 3. User interface and reports

### 3.1 I would like to compare pages between two environments/domains. How can I do that with the AET Tool?
The AET System enables comparison of the same page (set of pages) between different environments (domains). 
To do so, simply change domain parameter 
(either by change in [[suite definition|DefiningSuite#suite]] or using [[aet run command|ClientApplication#parameters]]) 
when running following tests. All data collected during those suite runs will be compared with the 
same patterns (which are independent of domain attribute).

### 3.2 How can I access pages that require authentication to enter?
There are two possible solutions. For the Basic Authentication you may use [[Header Modifier|HeaderModifier]]
with `Authorization` key set:
```xml
<header key="Authorization" value="Basic emVuT2FyZXVuOnozbkdAckQZbiE=" />
```
The other way is to use [[Login Modifier|LoginModifier]] that can pass simple login form.


### 3.3 How can I simulate another user agent?
Use [[Header Modifier|HeaderModifier]] to achieve that. Just add header modifier with key `'User-Agent'`
 and requested value.

### 3.4 What can I do with elements that render differently each time page is open and it occurs in changes detected each time?
Use [[Hide Modifier|HideModifier]] to hide those elements with `css` or `xpath` selectors.

### 3.5 I want to check how my page behaves on mobile devices, how can I do that?
AET is not integrated with mobile devices. However, you may simulate mobile resolution and check if your
page changes its rendering breakpoints for different resolutions. Use [[Resolution Modifier|ResolutionModifier]]
to simulate different resolutions.

---

## 4. Best practices

### 4.1 I want to check how my page looks on several devices - what is the best way do configure suite?
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