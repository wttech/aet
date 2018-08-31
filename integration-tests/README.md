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


Sample site can be uploaded to AET vagrant by running `mvn clean install -Pupload` from the 
`sample-site` directory level or from the root level of `aet` repository.
By default the sample site will be available at [http://aet-vagrant:9090/sample-site/](http://aet-vagrant:9090/sample-site/)

### test-suite

Prepares test suite and run tests on the pages provided by `sample-site` module.
Run `mvn clean install` to prepare `suite.xml` file.
Then run `mvn aet:run` to test sample pages and store results in AET database.

The result should be available at:

* [http://aet-vagrant/report.html?company=aet&project=aet&suite=main#/home](http://aet-vagrant/report.html?company=aet&project=aet&suite=main#/home)

There's a following naming convention for tests within the `test-suite`:
* `S-` prefix - tests are expected to be green on the report (passed cases)
* `F-` prefix - tests are expected to be red on the report (failed cases)
* `W-` prefix - tests are expected to be yellow on the report (warning cases)

*Note:* If you're running the suite for the first time, it needs to be executed at least twice to 
get expected results, because some of the test cases will always pass in the first run 
(e.g. screen comparison will always pass when there's no pattern yet).

### sanity-functional

Bobcat tests for AET reports web application.

Prerequisities:

* AET instance running
* Sample test suite: `test-suite` already executed (at least twice) against `sample-site` site.
Functional tests expect the report at URL specified by `report.url` property.
By default the URL is [http://aet-vagrant/report.html?company=aet&project=aet&suite=main](http://aet-vagrant/report.html?company=aet&project=aet&suite=main)
It may be changed it in `.../config/dev/instance.properties` file.
* Chrome browser installed
* Selenium [Chromedriver] available at *D:/Selenium/chromedriver.exe*.
This path can be changed at command-line with `-Dwebdriver.chrome.driver=<path>`
or in `.../config/common/webdriver.properties` file.

To start the Bobcat tests, run `mvn clean test` from the `sanity-functional` directory level

[Chromedriver]: https://sites.google.com/a/chromium.org/chromedriver/
