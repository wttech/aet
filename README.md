![Cognifide logo](http://cognifide.github.io/images/cognifide-logo.png)

[![Build Status](https://travis-ci.org/Cognifide/aet.svg?branch=master)](https://travis-ci.org/Cognifide/aet)
[![Apache License, Version 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/)
[![][gitter img]][gitter]

# AET
<p align="center">
  <img src="https://github.com/Cognifide/aet/blob/master/misc/img/aet-logo-black.png?raw=true"
         alt="AET Logo"/>
</p>

AET is a system that detects changes on web sites and performs basic page health check (like w3c 
compliance, accessibility check and other).
AET is designed as a flexible system that can be adapted and tailored to the regression requirements of a given project.
The tool has been developed to aid front end client side layout regression testing of websites or portfolios. 
In essence assessing the impact or change of a website from one snapshot to the next.

## What's the philosophy behind AET?
AET helps testers to ensure that a change in one part of the software has not introduced new defects in other parts of the software.

#### AET is suited for
* monitoring regression across large digital web platforms,
* managing visual changes across digital platform after changing common component (e.g. footer),
* regression tests at the end of an Hourly/Daily/Weekly/Per Sprint Completion,
* as part of an upgrade or migration process of a platform.

#### A typical scenario of use
1. The AET user (Developer or QA) baselines a set of components or pages with URLs as an input to the tool.
2. The CMS user changes the page component or content.
3. The ‘current baseline’ is used to compare with the ‘new version’ and the change is assessed for one of the 3 possibilities:
  * There are no changes - no involvement required.
  * There is a change but the user accepts it, which means she/he re-baselines.
  * There is a change and the user does not accept it, so she/he has to fix it.
4. AET produces a report.

#### AET is not about
* functional testing - checkout another Cognifide's testing framework: [Bobcat](https://github.com/Cognifide/bobcat) for functional testing,
* cross-browser testing,
* usability testing,
* security or server-side performance.

## What's inside?
*AET* uses several tools and frameworks that are used to check page quality in following areas:

* Full page **screenshots comparison** using Firefox browser to render page and [Selenium](http://www.seleniumhq.org/projects/webdriver/) to capture screenshots.
    * Hiding Page Items by xpath,
    * Changing screen resolution (width/height setup),
* Compare page **sources**.
* Compare page source **W3C compliance** using [nu.validator](https://validator.w3.org/nu/).
* Check **js errors** with [JSErrorCollector](https://github.com/mguillem/JSErrorCollector).
* Check **status codes** using [BrowserMob Proxy](https://bmp.lightbody.net/).
* Check page **accessibility** with [HTML_CodeSniffer](http://squizlabs.github.io/HTML_CodeSniffer/).
* Check and modify page **cookies**.
* Check page **client side performance** using [YSlow](http://yslow.org/).

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

## How to start
Please see our [AET in 10 minutes](https://github.com/Cognifide/aet/wiki/AETIn10Minutes) guide to start using AET.

To run **AET** tests the following tools are required:

* [Maven](https://maven.apache.org/download.cgi) (at least version 3.0.4)
* [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Chrome browser](https://www.google.com/chrome/browser/desktop/) to preview reports

## Setup
Please refer to the [Setup Guide](https://github.com/Cognifide/aet/wiki/BasicSetup) in the documentation for an overview on how to configure AET.

## License
**AET** is licensed under [Apache License, Version 2.0 (the "License")](https://www.apache.org/licenses/LICENSE-2.0.txt)


## Bugs and Feedback

For bugs, questions and discussions please use the [Github Issues](https://github.com/Cognifide/aet/issues).
Please notice we use [ZenHub](https://www.zenhub.com/) extension to manage issues.

## Documentation
* [AET Wiki](https://github.com/Cognifide/aet/wiki)

## Contact and Commercial Support

If you have any questions or require technical support please contact us at [aet@cognifide.com](mailto:aet@cognifide.com).
[Gitter Chat](https://gitter.im/aet-tool/Lobby) is a way for users to chat with the AET community. Feel free to leave a message, even if we’re not around, we will definitely respond to you when available.

[gitter]:https://gitter.im/aet-tool/Lobby
[gitter img]:https://badges.gitter.im/aet-tool/aet-tool.svg
