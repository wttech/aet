<img src="https://github.com/wttech/aet/raw/master/misc/img/WT_Logo_Blue_Positive_RGB.png" alt="Wunderman Thompson Logo" width="250"/>

[![Build Status](https://travis-ci.org/Cognifide/aet.svg?branch=master)](https://travis-ci.org/Cognifide/aet)
[![Apache License, Version 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses)
[![][gitter img]][gitter]

# AET
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

#### AET is not about
* functional testing - checkout another Wunderman Thompson Technology's testing framework: [Bobcat](https://github.com/wttech/bobcat) for functional testing,
* cross-browser testing,
* usability testing,
* security or server-side performance.

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

## How to start
To setup a fully functional AET instance use one of the following:
- [AET cookbook](https://github.com/wttech/aet-cookbook), you may run local instance using [AET Vagrant](https://github.com/wttech/aet//wiki/BasicSetup#set-up-vagrant).
- [AET Docker images](https://github.com/malaskowski/aet-docker), you may run local instance using [sample AET Swarm](https://github.com/malaskowski/aet-docker/tree/master/example-aet-swarm).

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

## License
**AET** is licensed under [Apache License, Version 2.0 (the "License")](https://www.apache.org/licenses/LICENSE-2.0.txt).


## Bugs and Feedback

For bugs, questions and discussions please use [Github Issues](https://github.com/wttech/aet/issues).
Please notice we use the [ZenHub](https://www.zenhub.com) extension to manage issues.

## Documentation
* [AET Wiki](https://github.com/wttech/aet//wiki)

## Contact and Commercial Support

If you have any questions or require technical support please contact us at [aet@wundermanthompson.com](mailto:aet@wundermanthompson.com).
[Gitter Chat](https://gitter.im/aet-tool/Lobby) is a way for users to chat with the AET community. Feel free to leave a message, even if we’re not around, we will definitely respond to you when available.

[gitter]:https://gitter.im/aet-tool/Lobby
[gitter img]:https://badges.gitter.im/aet-tool/aet-tool.svg
