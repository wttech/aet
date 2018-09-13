![Cognifide logo](http://cognifide.github.io/images/cognifide-logo.png)

# AET
<p align="center">
  <img src="https://github.com/Cognifide/aet/blob/master/misc/img/aet-logo-black.png?raw=true"
         alt="AET Logo"/>
</p>

## Important
**`aet-maven-plugin` is deprecated and will be no longer supported. Expect it will be removed soon.
Please use [`client script`](https://github.com/Cognifide/aet/wiki/ClientScripts) instead or simply communicate with AET Web API to schedule your suite.**

## AET Client
Contains AET client applications. Currently there is only one AET client implementation - maven plugin.

Client application sends the request containing the input XML Test Suite to the AET System and checks the processing status. After receiving a status telling that processing has finished from the AET System, client downloads the Report.

### client-core
Contains common part of each AET client application implementation, e.g. `TestSuiteRunner` which controls each suite run from client side.

### aet-maven-plugin
Maven plugin that is used as a client application. Usage: `mvn aet:run`. Test suite in XML form is required to run AET tests (should be passed in `testSuite` parameter to run command).
Please refer to documentation in order to find out more.
