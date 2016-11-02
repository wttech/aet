![Cognifide logo](http://cognifide.github.io/images/cognifide-logo.png)

# Automated Exploratory Tests
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
* sample test suite: `test-suite` executed against `sample-site` site.
Report should be available at `report.url`
(please configured it in `src/main/config/instance.properties` file).


### json-test

Uses AETs to compare AET's results.

This suite reads the test result JSON that is available at:

* [/api/metadata?company=aet&project=aet&suite=main&formatted=true](http://127.0.0.1:8181/api/metadata?company=aet&project=aet&suite=main&formatted=true)

and compares it to stored pattern. The JSON comparison result is available at:

* [/html/index.html?company=aet&project=aet&suite=aet-json-test#/home](http://localhost:8181/html/index.html?company=aet&project=aet&suite=aet-json-test#/home)

