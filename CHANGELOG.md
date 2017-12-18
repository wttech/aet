![Cognifide logo](http://cognifide.github.io/images/cognifide-logo.png)
<p align="center">
  <img src="https://github.com/Cognifide/aet/blob/master/misc/img/aet-logo-black.png?raw=true"
         alt="AET Logo"/>
</p>

# AET Releases
All notable changes to AET will be documented in this file.

## Unreleased
**List of changes that are finished but not yet released in any final version.**

## Version 2.1.3-RC1

- [PR-166](https://github.com/Cognifide/aet/pull/166) Added two modifiers: [wait for element being visible](https://github.com/Cognifide/aet/wiki/WaitForElementToBeVisibleModifier) and [wait for image being loaded](https://github.com/Cognifide/aet/wiki/WaitForImageCompletionModifier)
- [PR-172](https://github.com/Cognifide/aet/pull/172) Added [example AET Shell script](https://github.com/Cognifide/aet/wiki/ClientScripts) to run suite.
- [PR-164](https://github.com/Cognifide/aet/pull/164) [ExecuteJavaScriptModifier](https://github.com/Cognifide/aet/wiki/ExecuteJavaScriptModifier) enhancement: support for external snippets source and basic auth
- [PR-153](https://github.com/Cognifide/aet/pull/153) Fixed SSL error in Source Collector
- [PR-145](https://github.com/Cognifide/aet/pull/145) Update for schema location in `web.xml` file (in sample site for AET integration tests)
- [PR-144](https://github.com/Cognifide/aet/pull/144) Logging and error messages for suite run exceptions, status progress formatting

## Version 2.1.2

- [PR-135](https://github.com/Cognifide/aet/pull/135) Fixed Cleaner removing still valid patterns
- [PR-133](https://github.com/Cognifide/aet/pull/133) Update W3C validator version (nu.validator 15.6.29)

## Version 2.1.1

- [PR-128](https://github.com/Cognifide/aet/pull/128) Documentation update for advanced (Linux + Windows) AET setup
- [PR-125](https://github.com/Cognifide/aet/pull/125), [PR-130](https://github.com/Cognifide/aet/pull/130) improvements for release process automation
- [PR-127](https://github.com/Cognifide/aet/pull/127), [PR-129](https://github.com/Cognifide/aet/pull/129) updates for AET suite processing error logging

## Version 2.1.0

- [PR-110](https://github.com/Cognifide/aet/pull/110) Bobcat version upgrade to [version 1.2.1](https://github.com/Cognifide/bobcat/releases/tag/1.2.1)
- [aet-106](https://github.com/Cognifide/aet/issues/106) Compare against another suite patterns
- [aet-67](https://github.com/Cognifide/aet/issues/67) Support for regular expressions in filters

## Version 2.0.3-rc1

- UI improvements: [PR-72](https://github.com/Cognifide/aet/pull/72), [PR-74](https://github.com/Cognifide/aet/pull/74), [PR-83](https://github.com/Cognifide/aet/pull/83), [PR-84](https://github.com/Cognifide/aet/pull/84)
- [PR-86](https://github.com/Cognifide/aet/pull/86) New modifier: [Execute Java Script](https://github.com/Cognifide/aet/wiki/ExecuteJavaScriptModifier).
- [PR-85](https://github.com/Cognifide/aet/pull/85) Displaying suite execution time
- [PR-81](https://github.com/Cognifide/aet/pull/81) Wiki FAQ page
- [PR-77](https://github.com/Cognifide/aet/pull/77) Upgrade for used AET cookbook to [version 1.4.13](https://supermarket.chef.io/cookbooks/aet/versions/1.4.13)
- [PR-57](https://github.com/Cognifide/aet/pull/57) Part of Maven Client application moved to the new Test-executor module.
- [PR-54](https://github.com/Cognifide/aet/pull/54) Bobcat version upgrade to [version 1.1.3](https://github.com/Cognifide/bobcat/releases/tag/1.1.3)

## Version 2.0.2
- [PR-44](https://github.com/Cognifide/aet/pull/44) [ScreenCollector](https://github.com/Cognifide/aet/wiki/ScreenCollector) enhancement: added ability to take screenshots of parts of the page by using css or xpath element selectors
- [PR-41](https://github.com/Cognifide/aet/pull/41) Added new [Replace Text](https://github.com/Cognifide/aet/wiki/ReplaceTextModifier) modifier and  [Click](https://github.com/Cognifide/aet/wiki/ClickModifier)
 and [Hide](https://github.com/Cognifide/aet/wiki/HideModifier) Modifiers allows to use css selectors as parameters. 
- [PR-39](https://github.com/Cognifide/aet/pull/39) Fixed filtering w3cHtml issues and update nuvalidator to the newest version
- [PR-31](https://github.com/Cognifide/aet/pull/31) Fixed source report artifact error
- [PR-30](https://github.com/Cognifide/aet/pull/30) Fixed cookie report display in 'test' action mode

## Version 2.0.1
- [PR-28](https://github.com/Cognifide/aet/pull/28) Fixed [Login Modifier](https://github.com/Cognifide/aet/wiki/LoginModifier) problems.
- [PR-17](https://github.com/Cognifide/aet/pull/17) Processing error will be displayed at the report and improved [reports app](https://github.com/Cognifide/aet/wiki/SuiteReport) performance.
- [PR-6](https://github.com/Cognifide/aet/pull/6) Fixed: 'canvas difference area' is not included in mask.

## Version 2.0.0
- Initial open source release.
