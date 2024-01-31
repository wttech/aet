![Cognifide logo](http://cognifide.github.io/images/cognifide-logo.png)
<p align="center">
  <img src="https://github.com/wttech/aet/blob/master/misc/img/aet-logo-blue.png?raw=true"
         alt="AET Logo"/>
</p>

# AET Releases
All notable changes to AET will be documented in this file.

## Unreleased

**List of changes that are finished but not yet released in any final version.**
- [PR-427](https://github.com/Cognifide/aet/pull/427) Added .htaccess file to prevent directory listing ([#152](https://github.com/Cognifide/aet/issues/152))

- [PR-576](https://github.com/wttech/aet/pull/576) Unique screen name validation
- [PR-617](https://github.com/wttech/aet/pull/617) Add gradle builds.
- [PR-601](https://github.com/wttech/aet/pull/601) Reports new design. ([#595](https://github.com/wttech/aet/issues/595))
- [PR-528](https://github.com/wttech/aet/pull/528) Added source pattern date and source collected date. ([#500](https://github.com/wttech/aet/issues/500))
- [PR-589](https://github.com/wttech/aet/pull/589) Fixed bug with 'Discard all changes' button not properly displayed. ([#587](https://github.com/wttech/aet/issues/587))
- [PR-586](https://github.com/wttech/aet/pull/586) Removed duplicate of "Edit url note" button. ([#585](https://github.com/wttech/aet/issues/585))
- [PR-502](https://github.com/wttech/aet/pull/502) Implement a feature that will allow monitoring the size of responses for the given requests ([#501](https://github.com/wttech/aet/issues/501))
- [PR-525](https://github.com/wttech/aet/pull/525) Added tests ordering. ([#509](https://github.com/wttech/aet/issues/509))
- [PR-524](https://github.com/wttech/aet/pull/524) Remove handling unescaped URLs in suites
- [PR-526](https://github.com/wttech/aet/pull/526) Added sending urls to collectors in packets. ([#431](https://github.com/wttech/aet/issues/431))
- [PR-565](https://github.com/wttech/aet/pull/565) Fixing styles of note-editor icon ([#371](https://github.com/wttech/aet/issues/371))
- [PR-567](https://github.com/wttech/aet/pull/567) Added missing tooltips in the report - "Previous url" and "Next url" ([#566](https://github.com/wttech/aet/issues/566))
- [PR-563](https://github.com/wttech/aet/pull/563) Fixed bug with tooltips for "Re-run" buttons. ([#476](https://github.com/wttech/aet/issues/476))
- [PR-522](https://github.com/wttech/aet/pull/522) Improve performance of generating /configs/list. ([#519](https://github.com/wttech/aet/issues/519))
- [PR-531](https://github.com/wttech/aet/pull/531) Login modifier can be used with partial url of login page and combined with suite `domain`.
- [PR-529](https://github.com/wttech/aet/pull/529) Remove client side performance test module ([#481](https://github.com/wttech/aet/issues/481))
- [PR-530](https://github.com/wttech/aet/pull/530) Remove max timeouts limitation for login and WebElements wait ([#99](https://github.com/wttech/aet/issues/99))
- [PR-554](https://github.com/wttech/aet/pull/554) Fix race condition under non-native bash environments ([#551](https://github.com/wttech/aet/issues/551))
- [PR-564](https://github.com/wttech/aet/pull/564) Accessibility Issues Provider ([#557](https://github.com/wttech/aet/issues/557))

## Version 3.3.0

- [PR-479](https://github.com/wttech/aet/pull/479) Added Secure and HttpOnly flags for cookies. ([#477](https://github.com/wttech/aet/issues/477))
- [PR-506](https://github.com/wttech/aet/pull/506) About tab ([#475](https://github.com/wttech/aet/issues/475))
- [PR-462](https://github.com/wttech/aet/pull/462) Popup window unification([#367](https://github.com/wttech/aet/issues/367))
- [PR-489](https://github.com/wttech/aet/pull/489) Cleaner integration tests
- [PR-511](https://github.com/wttech/aet/pull/511) Added daily log rotation with 7 days retention
- [PR-480](https://github.com/wttech/aet/pull/480) Test summary stats on the main report page. ([#474](https://github.com/wttech/aet/issues/474))
- [PR-459](https://github.com/wttech/aet/pull/459) Print more meaningful error messages when suite.xml is malformed ([#436](https://github.com/wttech/aet/issues/436))
- [PR-517](https://github.com/wttech/aet/pull/517) Single URL rerun fixed for named URLs ([#487](https://github.com/wttech/aet/issues/487))
- [PR-516](https://github.com/wttech/aet/pull/516) Added option to change headers added by Chrome instance. ([#515](https://github.com/wttech/aet/issues/515))
- [PR-520](https://github.com/wttech/aet/pull/520) Fix displaying test tiles
- [PR-518](https://github.com/wttech/aet/pull/518) Minimum site height ([#384](https://github.com/wttech/aet/issues/384))

## Version 3.2.2

- [PR-507](https://github.com/wttech/aet/pull/507) Add option to match colors that are close to the target color in a grayscale space when comparing pixels with the LayoutComparator

## Version 3.2.1

- [PR-469](https://github.com/wttech/aet/pull/469) Fix WebAPI issues([#425](https://github.com/wttech/aet/issues/425))
- [PR-439](https://github.com/wttech/aet/pull/439) Fixed duplicating line and column numbers in accessibility tab
- [PR-360](https://github.com/wttech/aet/pull/360) Keyboard shortcuts for 'Accept/Revert url/test' buttons ([#317](https://github.com/wttech/aet/issues/317))
- [PR-432](https://github.com/wttech/aet/pull/432) Fixed issue with lack of clear message for erroneous suite definition.
- [PR-472](https://github.com/wttech/aet/pull/472) Return message when database does not exists and set suite status to MISSING_DATABASE ([#341](https://github.com/wttech/aet/issues/341))
- [PR-363](https://github.com/wttech/aet/pull/363) Fixed saving case-level notes
- [PR-470](https://github.com/wttech/aet/pull/470) Security vulnerabilities fix ([#407](https://github.com/wttech/aet/issues/407))
- [PR-468](https://github.com/wttech/aet/pull/468) Add loggers in collector/modifier ([#446](https://github.com/wttech/aet/issues/446))
- [PR-461](https://github.com/wttech/aet/pull/461) Generating correct correlationId when suite name is overridden([#440](https://github.com/wttech/aet/issues/440))
- [PR-413](https://github.com/wttech/aet/pull/413) Added ability to show full page source when there has been no difference discovered ([#369](https://github.com/wttech/aet/issues/369))
- [PR-463](https://github.com/wttech/aet/pull/463) Removed dependency to Joda time library
- [PR-488](https://github.com/wttech/aet/pull/488) Fix SuiteRemoveCondition unit test
- [PR-490](https://github.com/wttech/aet/pull/490) Fix removing too many artifacts by cleaner job by terminating processing in case of timeout

## Version 3.2.0

- [PR-451](https://github.com/wttech/aet/pull/451) Collectors and comparators configured by single config number
- [PR-449](https://github.com/wttech/aet/pull/449) Improvements to the Winter Edition Theme
- [PR-354](https://github.com/wttech/aet/pull/354) Remove jmsEndpointConfig information from communication settings endpoint ([#352](https://github.com/wttech/aet/issues/352))
- [PR-412](https://github.com/wttech/aet/pull/412) ([PR-336](https://github.com/wttech/aet/pull/336), [PR-337](https://github.com/wttech/aet/pull/337), [PR-395](https://github.com/wttech/aet/pull/395)) - Added rerun functionality for suite, test and url
- [PR-429](https://github.com/wttech/aet/pull/429) - `aet-cookbook` version updated to [`v5.1.1`](https://github.com/wttech/aet-cookbook/blob/master/CHANGELOG.md#511) in Vagrant. **Important**: please follow the instructions from [PR-43 description](https://github.com/wttech/aet-cookbook/pull/43) in order to keep the MongoDB data on your local environment.
- [PR-422](https://github.com/wttech/aet/pull/422) RunnerConfiguration's urlPackageSize property got set to 1
- [PR-430](https://github.com/wttech/aet/pull/430) Upgraded HTML codesniffer to latest release (3.2.0)
- [PR-385](https://github.com/wttech/aet/pull/385) Fixed ChefDK and vagrant-berkshelf versions
- [PR-404](https://github.com/wttech/aet/pull/404) Added missing tooltip for conditional tests
- [PR-408](https://github.com/wttech/aet/pull/408) Advanced Screen Comparision button layout fix
- [PR-410](https://github.com/wttech/aet/pull/410) Notification that displays when exclude-elements are not found on page now shows what specific elements were not found([#372](https://github.com/wttech/aet/issues/372))

## Version 3.1.0

- [PR-409](https://github.com/wttech/aet/pull/409) Added sources link in "view source" url
- [PR-394](https://github.com/wttech/aet/pull/394) Added scroll modifier
- [PR-399](https://github.com/wttech/aet/pull/399) Bug fix on side panel - conditional tests ([#398](https://github.com/wttech/aet/issues/398))
- [PR-396](https://github.com/wttech/aet/pull/396) Added horizontal scrollbar for wide pages ([#393](https://github.com/wttech/aet/issues/393))
- [PR-403](https://github.com/wttech/aet/pull/403) Conditionally passed tests can be accepted ([#400](https://github.com/wttech/aet/issues/400))
- [PR-387](https://github.com/wttech/aet/pull/387) Set max allowed page screenshot height to 35k pixels.
- [PR-401](https://github.com/wttech/aet/pull/401) Added new regex param `sourcePattern` to [JS Errors Data Filter](https://github.com/wttech/aet/wiki/JSErrorsDataFilter)
- [PR-397](https://github.com/wttech/aet/pull/397) Add algorithm to enable taking long screenshots without resolution-sleep-resolution workaround

## Version 3.0.1

- [PR-380](https://github.com/wttech/aet/pull/380) Exclude elements position calculated with partial screenshot offset ([#379](https://github.com/wttech/aet/issues/379))
- [PR-378](https://github.com/wttech/aet/pull/378) OSGI-configurable Chrome options.

## Version 3.0.0

- [PR-359](https://github.com/wttech/aet/pull/359) ([#351](https://github.com/wttech/aet/issues/351)) Disabled nu.validator language detection to fix random NPE
- [PR-328](https://github.com/wttech/aet/pull/328) Added suite's history
- [PR-303](https://github.com/wttech/aet/pull/303) Added `exclude-elements` parameter to [ScreenCollector](https://github.com/wttech/aet/wiki/ScreenCollector)
- [PR-327](https://github.com/wttech/aet/pull/327) Default web driver changed from Firefox to Chrome
- [PR-294](https://github.com/wttech/aet/pull/294) Added support for full page screenshot in chrome
- [PR-326](https://github.com/wttech/aet/pull/326) ([PR-308](https://github.com/wttech/aet/pull/308), [PR-310](https://github.com/wttech/aet/pull/310), [PR-311](https://github.com/wttech/aet/pull/311), [PR-312](https://github.com/wttech/aet/pull/312), [PR-313](https://github.com/wttech/aet/pull/313), [PR-314](https://github.com/wttech/aet/pull/314), [PR-315](https://github.com/wttech/aet/pull/315), [PR-316](https://github.com/wttech/aet/pull/316), [PR-322](https://github.com/wttech/aet/pull/322)) - updated OSGi annotations to 6.0.0 OSGi standard
- [PR-293](https://github.com/wttech/aet/pull/293) Added error treshold in pixels and percentages for screen comparator
- [PR-300](https://github.com/wttech/aet/pull/300) Added creating indexes for collection
- [PR-289](https://github.com/wttech/aet/pull/289) User now stays on the same tab while navigating between URLs
- [PR-271](https://github.com/wttech/aet/pull/271) Added possibility to override name parameter from the aet client
- [PR-268](https://github.com/wttech/aet/pull/268) Bobcat upgrade to version 1.4.0
- [PR-279](https://github.com/wttech/aet/pull/279) Upgrade for some of libraries used by AET
- [PR-281](https://github.com/wttech/aet/pull/281) No version in Bundle names (simpler deployment)
- [PR-286](https://github.com/wttech/aet/pull/286) Shared Patterns - use latest patterns of given suite name ([#121](https://github.com/wttech/aet/issues/121))
- [PR-291](https://github.com/wttech/aet/pull/291) Updated nu.validator version from 15.6.29 to 17.11.1
- [PR-298](https://github.com/wttech/aet/pull/298) Filtering accessibility errors by markup CSS ([#214](https://github.com/wttech/aet/issues/214))
- [PR-296](https://github.com/wttech/aet/pull/296) Status Code Comparator now checks range 400-600 by default, parameters validation added
- [PR-297](https://github.com/wttech/aet/pull/297) Advanced screen comparison
- [PR-325](https://github.com/wttech/aet/pull/325) Support env variables in report manager
- [PR-331](https://github.com/wttech/aet/pull/325) Support env variables in MongoDB config
- [PR-338](https://github.com/wttech/aet/pull/338) Enable creating new dbs by default
- [PR-345](https://github.com/wttech/aet/pull/345) Deprecate AET maven plugin
- [PR-301](https://github.com/wttech/aet/pull/301) New versions of virtualization tools used with new AET cookbook

## Version 2.1.6

- [PR-260](https://github.com/wttech/aet/pull/260) **Upgrade to Karaf 4.2.0**
- [PR-261](https://github.com/wttech/aet/pull/261) AET artifacts folders watched for new files
- [PR-264](https://github.com/wttech/aet/pull/264) Few changes for Selenium grid support
- [PR-265](https://github.com/wttech/aet/pull/265) Runner simplification refactor
- [PR-267](https://github.com/wttech/aet/pull/267) Guice removed from project dependencies

## Version 2.1.5

- [PR-252](https://github.com/wttech/aet/pull/252) Bug fix for JS Error table
- [PR-244](https://github.com/wttech/aet/pull/244) Side panel scrolled automatically when navigating under specific test/url on first application load.
- [PR-239](https://github.com/wttech/aet/pull/239) Switch off default require.js timeouts.
- [PR-238](https://github.com/wttech/aet/pull/238) AET Winter Edition introduced
- [PR-233](https://github.com/wttech/aet/pull/233) Font Awesome introduced to AET
- [PR-228](https://github.com/wttech/aet/pull/228) Remove 'jump to' button from whole suite view.
- [PR-230](https://github.com/wttech/aet/pull/230) Bug Fixed: Side panel items not accessible when 'Save/Discard Changes' buttons are visible.
- [PR-229](https://github.com/wttech/aet/pull/229) Improved notes (new icons, buttons order)
- [PR-226](https://github.com/wttech/aet/pull/226) Side panel follows the currently opened report while navigating them using keyboard shortcuts. The unused mCustomScrollbar plugin was removed.
- [PR-225](https://github.com/wttech/aet/pull/225) Added sidebar resize functionality for report app
- [PR-221](https://github.com/wttech/aet/pull/221) Added crosshair buttons for scrolling Side Panel to currently opened url/test.
- [PR-209](https://github.com/wttech/aet/pull/209) Selenium upgraded to 3.8.1. Guava upgraded to 23.6-jre

## Version 2.1.4
- [PR-184](https://github.com/wttech/aet/pull/184) Fixed xUnit generation NPE when stepResult is missing
- [PR-183](https://github.com/wttech/aet/pull/183) Fixed ambiguous messages from AET API ([#161](https://github.com/wttech/aet/issues/161) and [#165](https://github.com/wttech/aet/issues/161))
- [PR-202](https://github.com/wttech/aet/pull/202) VirtualBox version update required for Windows 10
- [PR-204](https://github.com/wttech/aet/pull/204) AET with JAVA 8, using new Karaf (from new [AET cookbook][aet-cookbook])

## Version 2.1.3
- [PR-166](https://github.com/wttech/aet/pull/166) Added two modifiers: [wait for element being visible](https://github.com/wttech/aet/wiki/WaitForElementToBeVisibleModifier) and [wait for image being loaded](https://github.com/wttech/aet/wiki/WaitForImageCompletionModifier)
- [PR-172](https://github.com/wttech/aet/pull/172) Added [example AET Shell script](https://github.com/wttech/aet/wiki/ClientScripts) to run suite.
- [PR-164](https://github.com/wttech/aet/pull/164) [ExecuteJavaScriptModifier](https://github.com/wttech/aet/wiki/ExecuteJavaScriptModifier) enhancement: support for external snippets source and basic auth
- [PR-153](https://github.com/wttech/aet/pull/153) Fixed SSL error in Source Collector
- [PR-145](https://github.com/wttech/aet/pull/145) Update for schema location in `web.xml` file (in sample site for AET integration tests)
- [PR-144](https://github.com/wttech/aet/pull/144) Logging and error messages for suite run exceptions, status progress formatting

## Version 2.1.2

- [PR-135](https://github.com/wttech/aet/pull/135) Fixed Cleaner removing still valid patterns
- [PR-133](https://github.com/wttech/aet/pull/133) Update W3C validator version (nu.validator 15.6.29)

## Version 2.1.1

- [PR-128](https://github.com/wttech/aet/pull/128) Documentation update for advanced (Linux + Windows) AET setup
- [PR-125](https://github.com/wttech/aet/pull/125), [PR-130](https://github.com/wttech/aet/pull/130) improvements for release process automation
- [PR-127](https://github.com/wttech/aet/pull/127), [PR-129](https://github.com/wttech/aet/pull/129) updates for AET suite processing error logging

## Version 2.1.0

- [PR-110](https://github.com/wttech/aet/pull/110) Bobcat version upgrade to [version 1.2.1](https://github.com/wttech/bobcat/releases/tag/1.2.1)
- [aet-106](https://github.com/wttech/aet/issues/106) Compare against another suite patterns
- [aet-67](https://github.com/wttech/aet/issues/67) Support for regular expressions in filters

## Version 2.0.3-rc1

- UI improvements: [PR-72](https://github.com/wttech/aet/pull/72), [PR-74](https://github.com/wttech/aet/pull/74), [PR-83](https://github.com/wttech/aet/pull/83), [PR-84](https://github.com/wttech/aet/pull/84)
- [PR-86](https://github.com/wttech/aet/pull/86) New modifier: [Execute Java Script](https://github.com/wttech/aet/wiki/ExecuteJavaScriptModifier).
- [PR-85](https://github.com/wttech/aet/pull/85) Displaying suite execution time
- [PR-81](https://github.com/wttech/aet/pull/81) Wiki FAQ page
- [PR-77](https://github.com/wttech/aet/pull/77) Upgrade for used AET cookbook to [version 1.4.13](https://supermarket.chef.io/cookbooks/aet/versions/1.4.13)
- [PR-57](https://github.com/wttech/aet/pull/57) Part of Maven Client application moved to the new Test-executor module.
- [PR-54](https://github.com/wttech/aet/pull/54) Bobcat version upgrade to [version 1.1.3](https://github.com/wttech/bobcat/releases/tag/1.1.3)

## Version 2.0.2
- [PR-44](https://github.com/wttech/aet/pull/44) [ScreenCollector](https://github.com/wttech/aet/wiki/ScreenCollector) enhancement: added ability to take screenshots of parts of the page by using css or xpath element selectors
- [PR-41](https://github.com/wttech/aet/pull/41) Added new [Replace Text](https://github.com/wttech/aet/wiki/ReplaceTextModifier) modifier and  [Click](https://github.com/wttech/aet/wiki/ClickModifier)
 and [Hide](https://github.com/wttech/aet/wiki/HideModifier) Modifiers allows to use css selectors as parameters.
- [PR-39](https://github.com/wttech/aet/pull/39) Fixed filtering w3cHtml issues and update nuvalidator to the newest version
- [PR-31](https://github.com/wttech/aet/pull/31) Fixed source report artifact error
- [PR-30](https://github.com/wttech/aet/pull/30) Fixed cookie report display in 'test' action mode

## Version 2.0.1
- [PR-28](https://github.com/wttech/aet/pull/28) Fixed [Login Modifier](https://github.com/wttech/aet/wiki/LoginModifier) problems.
- [PR-17](https://github.com/wttech/aet/pull/17) Processing error will be displayed at the report and improved [reports app](https://github.com/wttech/aet/wiki/SuiteReport) performance.
- [PR-6](https://github.com/wttech/aet/pull/6) Fixed: 'canvas difference area' is not included in mask.

## Version 2.0.0
- Initial open source release.

[aet-cookbook]: https://supermarket.chef.io/cookbooks/aet
