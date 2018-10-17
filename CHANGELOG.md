![Cognifide logo](http://cognifide.github.io/images/cognifide-logo.png)
<p align="center">
  <img src="https://github.com/Cognifide/aet/blob/master/misc/img/aet-logo-black.png?raw=true"
         alt="AET Logo"/>
</p>

# AET Releases
All notable changes to AET will be documented in this file.

## Unreleased
**List of changes that are finished but not yet released in any final version.**
- [PR-394](https://github.com/Cognifide/aet/pull/394) ([#386](https://github.com/Cognifide/aet/issues/386)) Added scroll modifier
- [PR-387](https://github.com/Cognifide/aet/pull/387) Set max allowed page screenshot height to 35k pixels.

## Version 3.0.1

- [PR-380](https://github.com/Cognifide/aet/pull/380) Exclude elements position calculated with partial screenshot offset ([#379](https://github.com/Cognifide/aet/issues/379))
- [PR-378](https://github.com/Cognifide/aet/pull/378) OSGI-configurable Chrome options.

## Version 3.0.0

- [PR-359](https://github.com/Cognifide/aet/pull/359) ([#351](https://github.com/Cognifide/aet/issues/351)) Disabled nu.validator language detection to fix random NPE
- [PR-328](https://github.com/Cognifide/aet/pull/328) Added suite's history
- [PR-303](https://github.com/Cognifide/aet/pull/303) Added `exclude-elements` parameter to [ScreenCollector](https://github.com/Cognifide/aet/wiki/ScreenCollector)
- [PR-327](https://github.com/Cognifide/aet/pull/327) Default web driver changed from Firefox to Chrome
- [PR-294](https://github.com/Cognifide/aet/pull/294) Added support for full page screenshot in chrome
- [PR-326](https://github.com/Cognifide/aet/pull/326) ([PR-308](https://github.com/Cognifide/aet/pull/308), [PR-310](https://github.com/Cognifide/aet/pull/310), [PR-311](https://github.com/Cognifide/aet/pull/311), [PR-312](https://github.com/Cognifide/aet/pull/312), [PR-313](https://github.com/Cognifide/aet/pull/313), [PR-314](https://github.com/Cognifide/aet/pull/314), [PR-315](https://github.com/Cognifide/aet/pull/315), [PR-316](https://github.com/Cognifide/aet/pull/316), [PR-322](https://github.com/Cognifide/aet/pull/322)) - updated OSGi annotations to 6.0.0 OSGi standard
- [PR-293](https://github.com/Cognifide/aet/pull/293) Added error treshold in pixels and percentages for screen comparator
- [PR-300](https://github.com/Cognifide/aet/pull/300) Added creating indexes for collection
- [PR-289](https://github.com/Cognifide/aet/pull/289) User now stays on the same tab while navigating between URLs
- [PR-271](https://github.com/Cognifide/aet/pull/271) Added possibility to override name parameter from the aet client
- [PR-268](https://github.com/Cognifide/aet/pull/268) Bobcat upgrade to version 1.4.0
- [PR-279](https://github.com/Cognifide/aet/pull/279) Upgrade for some of libraries used by AET
- [PR-281](https://github.com/Cognifide/aet/pull/281) No version in Bundle names (simpler deployment)
- [PR-286](https://github.com/Cognifide/aet/pull/286) Shared Patterns - use latest patterns of given suite name ([#121](https://github.com/Cognifide/aet/issues/121))
- [PR-291](https://github.com/Cognifide/aet/pull/291) Updated nu.validator version from 15.6.29 to 17.11.1
- [PR-298](https://github.com/Cognifide/aet/pull/298) Filtering accessibility errors by markup CSS ([#214](https://github.com/Cognifide/aet/issues/214))
- [PR-296](https://github.com/Cognifide/aet/pull/296) Status Code Comparator now checks range 400-600 by default, parameters validation added
- [PR-297](https://github.com/Cognifide/aet/pull/297) Advanced screen comparison
- [PR-325](https://github.com/Cognifide/aet/pull/325) Support env variables in report manager
- [PR-331](https://github.com/Cognifide/aet/pull/325) Support env variables in MongoDB config
- [PR-338](https://github.com/Cognifide/aet/pull/338) Enable creating new dbs by default
- [PR-345](https://github.com/Cognifide/aet/pull/345) Deprecate AET maven plugin
- [PR-301](https://github.com/Cognifide/aet/pull/301) New versions of virtualization tools used with new AET cookbook

## Version 2.1.6

- [PR-260](https://github.com/Cognifide/aet/pull/260) **Upgrade to Karaf 4.2.0**
- [PR-261](https://github.com/Cognifide/aet/pull/261) AET artifacts folders watched for new files
- [PR-264](https://github.com/Cognifide/aet/pull/264) Few changes for Selenium grid support
- [PR-265](https://github.com/Cognifide/aet/pull/265) Runner simplification refactor
- [PR-267](https://github.com/Cognifide/aet/pull/267) Guice removed from project dependencies

## Version 2.1.5

- [PR-252](https://github.com/Cognifide/aet/pull/252) Bug fix for JS Error table
- [PR-244](https://github.com/Cognifide/aet/pull/244) Side panel scrolled automatically when navigating under specific test/url on first application load.
- [PR-239](https://github.com/Cognifide/aet/pull/239) Switch off default require.js timeouts.
- [PR-238](https://github.com/Cognifide/aet/pull/238) AET Winter Edition introduced
- [PR-233](https://github.com/Cognifide/aet/pull/233) Font Awesome introduced to AET
- [PR-228](https://github.com/Cognifide/aet/pull/228) Remove 'jump to' button from whole suite view.
- [PR-230](https://github.com/Cognifide/aet/pull/230) Bug Fixed: Side panel items not accessible when 'Save/Discard Changes' buttons are visible.
- [PR-229](https://github.com/Cognifide/aet/pull/229) Improved notes (new icons, buttons order)
- [PR-226](https://github.com/Cognifide/aet/pull/226) Side panel follows the currently opened report while navigating them using keyboard shortcuts. The unused mCustomScrollbar plugin was removed.
- [PR-225](https://github.com/Cognifide/aet/pull/225) Added sidebar resize functionality for report app
- [PR-221](https://github.com/Cognifide/aet/pull/221) Added crosshair buttons for scrolling Side Panel to currently opened url/test.
- [PR-209](https://github.com/Cognifide/aet/pull/209) Selenium upgraded to 3.8.1. Guava upgraded to 23.6-jre

## Version 2.1.4
- [PR-184](https://github.com/Cognifide/aet/pull/184) Fixed xUnit generation NPE when stepResult is missing
- [PR-183](https://github.com/Cognifide/aet/pull/183) Fixed ambiguous messages from AET API ([#161](https://github.com/Cognifide/aet/issues/161) and [#165](https://github.com/Cognifide/aet/issues/161))
- [PR-202](https://github.com/Cognifide/aet/pull/202) VirtualBox version update required for Windows 10
- [PR-204](https://github.com/Cognifide/aet/pull/204) AET with JAVA 8, using new Karaf (from new [AET cookbook][aet-cookbook])

## Version 2.1.3
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

[aet-cookbook]: https://supermarket.chef.io/cookbooks/aet
