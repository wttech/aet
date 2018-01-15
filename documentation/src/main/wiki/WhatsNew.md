### `master` branch version (not released yet)

#### Virtualbox version
We have observed some issues with VirtualBox (used for [AET in 10 minutes|AETIn10Minutes] setup).
Required version is now [VirtualBox 5.1.30](https://www.virtualbox.org/wiki/Download_Old_Builds_5_1), especially for Windows 10.

#### Proxy configuration in suite
`useProxy` configuration has been updated. Only named proxy (e.g. `rest`) types will be respected from now 
(removed `true` and `false` options). If you don't want to use proxy, simply skip `useProxy` property in the `test` definition.
See more in [[Suite Structure|SuiteStructure]] documentation.

### Version 2.1.3
#### Two new modifiers have been added:
- [wait for element being visible](https://github.com/Cognifide/aet/wiki/WaitForElementToBeVisibleModifier),
- [wait for image being loaded](https://github.com/Cognifide/aet/wiki/WaitForImageCompletionModifier).

#### A new (recommended!) way to run the AET suite
There is a new (recommended!) way to run the AET suite with a bash script, read more [here](https://github.com/Cognifide/aet/wiki/ClientScripts).

#### Improved ExecuteJavaScriptModifier
Since this version [ExecuteJavaScriptModifier](https://github.com/Cognifide/aet/wiki/ExecuteJavaScriptModifier) has supported the external snippets source with basic auth.

### Version 2.1.2
Besides technical improvements within this version we've updated the W3C validator to the most recent version (15.6.29).

### Version 2.1.1
This version provides mostly technical changes that improve logging and the automation of the release process.

### Version 2.1.0
#### Comparing suites patterns
Consider having more than one AET environment. What if one of them is returning errors on your page? Now it is possible to compare all patterns collected in different environments to check whether the page is really broken or maybe it is just a broken pattern. It is easier to maintain, because all environments may use the same suite, only patterns may be different.

#### Regular expressions in filters
We've added the possibility to use regular expressions in filters. Thanks to that AET users are now able to filter all the messages using the same message pattern. It is useful i.e. in filtering W3C errors which may be parametrized and thanks to this they may differ in small details. Regular expressions help to handle such cases.

### Version 2.0.0
AET 2.0 brings new reporting capabilities and improved performance. We have decided to label the new features as BETA and 
improve them based your feedback as it means a lot to us. Stay tuned!

#### Architecture performance improvements
We keep evolving the AET architecture and optimizing reporting performance. We have introduced new web application reports, 
which are now generated on the client-side. Also, we have optimized database model queries and decreased number of calls by a factor of 10. 
If you’ve ever been working on a release and discovered that the AET database has become overloaded, we have removed data storage duplications so you don’t need to worry any longer!

#### New reporting interface BETA
The reporting interface has been turned into a modern and responsive client-side application. 
Now it’s easy to search and filter results from your test case. If you are tired of re-running your tests after 
accepting patterns don’t worry, now all changes are applied on the fly. Additionally you can share a link to a test result 
that you are concerned about and share with your team just by copying the test URL and pasting to JIRA.

#### Accessibility report BETA
AET 2.0 introduces an accessibility report that displays validation output of page accessibility analysis using the 
[HTML_CodeSniffer](http://squizlabs.github.io/HTML_CodeSniffer/) library. Results show the total count of errors, warnings and notice type violations.

#### Client-side performance BETA
Now it’s possible to test client-side performance in an agile and incremental fashion. Brand new reports show overall scores along with a 
list of rules with individual grades. The performance analysis is based on the [YSlow](http://yslow.org/) tool.
