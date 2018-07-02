![Cognifide logo](http://cognifide.github.io/images/cognifide-logo.png)

# AET
<p align="center">
  <img src="https://github.com/Cognifide/aet/blob/master/misc/img/aet-logo-black.png?raw=true"
         alt="AET Logo"/>
</p>

## Integration Tests

This project build is managed by maven and is not part of the AET System.

### sample-site

Sample web application for tests. Builds *war* file containing pages for various test cases.

Pages are written in a way that allows us to provide repeatable test results
(i.e. failed test cases).
Even if someone would try to update the patterns.


Sample site may be uploaded to AET vagrant and will be available at  
[http://aet-vagrant:9090/sample-site/](http://aet-vagrant:9090/sample-site/)

### test-suite

Prepares test suite and run tests on the pages provided by `sample-site` module.
Run `mvn clean install` to prepare `suite.xml` file.
Then run `mvn aet:run` to test sample pages and store results in AET database.

The result should be available at:

* [http://aet-vagrant/report.html?company=aet&project=aet&suite=main#/home](http://aet-vagrant/report.html?company=aet&project=aet&suite=main#/home)


### sanity-functional

Bobcat tests for AET reports web application.

Prerequisities:

* AET instance running
* Sample test suite: `test-suite` already executed against `sample-site` site.
Functional tests expect the report at URL specified by `report.url` property.
By default the URL is [http://aet-vagrant/report.html?company=aet&project=aet&suite=main](http://aet-vagrant/report.html?company=aet&project=aet&suite=main)
It may be changed it in `.../config/dev/instance.properties` file.
* Chrome browser installed
* Selenium [Chromedriver] available at *D:/Selenium/chromedriver.exe*.
This path can be changed at command-line with `-Dwebdriver.chrome.driver=<path>`
or in `.../config/common/webdriver.properties` file.


[Chromedriver]: https://sites.google.com/a/chromium.org/chromedriver/
