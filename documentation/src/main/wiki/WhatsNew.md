Here are notes that summarize the most important changes in AET from the user and admin perspective.
To see the full list of changes, please refer to [CHANGELOG](https://github.com/Cognifide/aet/blob/master/CHANGELOG.md).

### `master` branch version (not released yet)


### Version 3.0.0

#### Upgrade for our virtualization tool set

Changes that allows us to use newer versions of VirtualBox, Vagrant and Chef for AET instance setup:

* [VirtualBox 5.2.18](https://www.virtualbox.org/wiki/Downloads)
* [Vagrant 2.1.2](https://releases.hashicorp.com/vagrant/)
* [ChefDK 3.1.0](https://downloads.chef.io/chefdk/stable)

#### Suite XML re-use option
Added possibility to pass additional argument with suite XML file - `name`.
Thanks to that you may re-use the same XML file to run different suites
(e.g. have patterns collected separately for each environment but maintain only single suite XML file).

#### Dependencies upgrade
We have updated some of AET dependencies to the newer versions. If you are a developer and install `master` branch version on you AET instance
please make sure to update your `settings.xml` file according to [[this guide|AETIn10Minutes#upload-vagrant-prequisities.

### Version 2.1.6
#### Karaf upgrade
Karaf was upgraded to 4.2.0 version. Please remember to upgrade your Karaf instance if you upgrade to aet 2.1.6 or above.

#### Selenium Grid
Selenium Grid support is currently in beta test. You may use it already on your developer instances to check how it works.
It enables working with Chrome (please remember that not all AET features are supported there yet!). See more details in [PR-264](https://github.com/Cognifide/aet/pull/264).

### Version 2.1.5
#### Selenium and Guava has been upgraded to current versions
[Selenium](http://www.seleniumhq.org/download) has been upgraded to 3.8.1.
[Guava](https://github.com/google/guava) has been upgraded to 23.6-jre. 
We've kept compatibility with currently supported Firefox version - 38.6.0.

#### Instructions updated for new cookbook version
New Cookbook version 2.0.0 introduces changes in AET setup.
[[Linux and Windows Setup|LinuxAndWindowsSetup]] instructions were updated to reflect those changes.

#### Report UX
Multiple report UX fixes and improvements.

### Version 2.1.4
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
