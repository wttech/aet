<div align="center">

  <h1><code>AET</code></h1>

  <h3>
    <strong>Detect visual changes with ease</strong>
  </h3>

  <p>
    <img src="https://img.shields.io/github/workflow/status/wttech/aet/ci?style=for-the-badge" alt="CI status badge" />
    <a href="https://codecov.io/gh/wttech/aet">
      <img src="https://img.shields.io/codecov/c/github/wttech/aet?style=for-the-badge" alt="Code coverage"/>
    </a>
    <a href="https://github.com/wttech/aet">
      <img src="https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=for-the-badge" alt="License"/>
    </a>
    <a href="https://gitter.im/aet-tool/Lobby">
      <img src="https://img.shields.io/gitter/room/wttech/aet?style=for-the-badge" alt="Gitter"/>
    </a>
  </p>

  <h3>
    <a href="#about">About</a>
    <span> | </span>
    <a href="#installation">Installation</a>
    <span> | </span>
    <a href="#documentation">Documentation</a>
    <span> | </span>
    <a href="#license">License</a>
  </h3>

<sub><h4>Built with ❤️</h4></sub>
</div>

<p align="center">
    <img src="https://github.com/wttech/aet/raw/master/misc/img/WT_Logo_Blue_Positive_RGB.png" alt="Wunderman Thompson Logo" width="150"/>
</p>

# <p id="about">AET</p>
<p align="center">
  <img src="https://raw.githubusercontent.com/wttech/aet/master/misc/img/aet-logo-blue.png" width="400"
         alt="AET Logo"/>
</p>

AET is a system that detects visual changes on web sites and performs basic page health checks (like w3c
compliance, accessibility, HTTP status codes, JS Error checks and others).
AET is designed as a flexible system that can be adapted and tailored to the regression requirements of a given project.
The tool has been developed to aid front end client side layout regression testing of websites or portfolios,
in essence assessing the impact or change of a website from one snapshot to the next.

## What's the philosophy behind AET?
AET helps testers to ensure that a change in one part of the software has not introduced new defects in other parts of the software.

#### AET is suited for
* monitoring regression across large digital web platforms,
* managing visual changes across digital platform after changing common component (e.g. footer),
* regression tests at the end of an Hourly/Daily/Weekly/Per Sprint Completion,
* as part of an upgrade or migration process of a platform.
* client side performance tests of pages using [Lighthouse extension](hhttps://github.com/malaskowski/aet-lighthouse-extension)

#### A typical scenario of use
1. The AET user (Developer or QA) baselines a set of components or pages with URLs as an input to the tool.
2. The CMS user changes the page component or content.
3. The ‘current baseline’ is used to compare with the ‘new version’ and the change is assessed for one of the 3 possibilities:
   * There are no changes - no involvement required.
   * There is a change but the user accepts it, which means she/he re-baselines.
   * There is a change and the user does not accept it, so she/he has to fix it.
4. AET produces a report.

#### AET is NOT about
* functional testing
* cross-browser testing
* usability testing
* security or server-side performance

## What's inside?
*AET* uses several tools and frameworks that are used to check page quality in the following areas:

* Full page **visual comparison** using Google Chrome browser to render page and [Selenium](https://www.selenium.dev/documentation/en/webdriver) to capture screenshots inc.:
   * Hiding Page Items located by xpath or css selector,
   * Changing screen resolution (width/height setup),
* Page **sources** comparison.
* Page source **W3C compliance** with [nu.validator](https://validator.w3.org/nu).
* **JS errors** with [JSErrorCollector](https://github.com/mguillem/JSErrorCollector).
* **HTTP status codes** with [BrowserMob Proxy](https://bmp.lightbody.net).
* Page **accessibility** with [HTML_CodeSniffer](http://squizlabs.github.io/HTML_CodeSniffer).
* Page **cookies** comparison and modification.
* and many others ...

## <p id="installation">How to start</p>
To setup a fully functional AET instance use one of the following:
- [AET cookbook](https://github.com/wttech/aet-cookbook), you may run local instance using [AET Vagrant](https://github.com/wttech/aet//wiki/BasicSetup#set-up-vagrant).
- [AET Docker images](https://github.com/malaskowski/aet-docker), you may run local instance as follows:
    - follow the guide [here](https://github.com/malaskowski/aet-docker#developer-environment)
    - assume `AET_ROOT` mentioned in the guide above to be equal `../aet-docker`
    - this way, from the root directory of **this** project, you can run `./gradlew` or `./gradlew deployLocal` to automatically 
      install all core bundles, core configs and core features

For more details on how to run AET tests, see [AET in 10 minutes](https://github.com/wttech/aet//wiki/AETIn10Minutes) guide.

## AET architecture
The AET System consists of 7 units:

- Client (AET Maven Plugin)
- Runner cluster
- Worker cluster
- JMS Server
- Database
- REST API
- Reports web application

![aet-architecture](misc/img/aet-architecture.png)

Thanks to using AET Maven Plugin as a Client application, AET is easy to integrate with CI Tools like Jenkins or Bamboo.

## <p id="license">License</p>
**AET** is licensed under [Apache License, Version 2.0 (the "License")](https://www.apache.org/licenses/LICENSE-2.0.txt).


## Bugs and Feedback

For bugs, questions and discussions please use [Github Issues](https://github.com/wttech/aet/issues).
Please notice we use the [ZenHub](https://www.zenhub.com) extension to manage issues.

## <p id="documentation">Documentation</p>
* [AET Wiki](https://github.com/wttech/aet//wiki)

## Contact and Commercial Support

If you have any questions or require technical support please contact us at [aet@wundermanthompson.com](mailto:aet@wundermanthompson.com).
[Gitter Chat](https://gitter.im/aet-tool/Lobby) is a way for users to chat with the AET community. Feel free to leave a message, even if we’re not around, we will definitely respond to you when available.
