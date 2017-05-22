## AET Suite Migration to AET 2.1.0

### Updating the XML suite

With version 2.1.0 we are supporting regular expressions for following filters:

* [Accessibility Data Filter](https://github.com/Cognifide/aet/wiki/AccessibilityDataFilter)
* [JS Errors Data Filter](https://github.com/Cognifide/aet/wiki/JSErrorsDataFilter)
* [W3C HTML5 Issues Filter](https://github.com/Cognifide/aet/wiki/W3CHTML5IssuesFilter)

Please notice that the change introduced for [W3C HTML5 Issues Filter](https://github.com/Cognifide/aet/wiki/W3CHTML5IssuesFilter) is backward **incompatible**:

For version **2.0.x** we were expecting that `message` parameter contains a prefix of error message.

From **2.1.x** we need to use `messagePattern` parameter with JAVA regular expression. To match with a prefix we need to provide i.e.: `messagePattern="The first occurrence of.*"`. The `.*` is needed here to match all errors with given prefix.

### Using new version of maven plugin

With the new version we have refactored our maven plugin. Please update the AET plugin version in your `pom.xml`. Previous versions of maven plugin are not compatible with new AET version!
