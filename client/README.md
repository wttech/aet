![Cognifide logo](http://cognifide.github.io/images/cognifide-logo.png)

# Automated Exploratory Tests
<p align="center">
  <img src="https://github.com/Cognifide/aet/blob/master/misc/img/aet-logo-black.png?raw=true"
         alt="AET Logo"/>
</p>

## AET Client
Contains AET client applications. Currently there is only one AET client implementation - maven plugin.

Client application parses the input XML Test Suite and then sends request to the AET System and wait for results. After the Test Suite run message from the AET System, client downloads the Report.

### client-core
Contains common part of each AET client application implementation, e.g. models, xml parsers, and `TestSuiteRunner` which controls each suite run from client side.

### aet-maven-plugin
Maven plugin that is used as a client application. Usage: `mvn aet:run`. Test suite in XML form is required to run AET tests (should be passed in `testSuite` parameter to run command).
Please refer to documentation in order to find out more.
