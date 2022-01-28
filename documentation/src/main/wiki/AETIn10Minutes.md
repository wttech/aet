## AET in 10 Minutes

This is a quick guide showing how to setup the AET environment and run a sample test.

### Environment setup

See [[Environment Setup|EnvironmentSetup]] guide.

### Set Up the Test

Create the file named `suite.xml` (in any location) with the following content:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="company" project="project">
    <test name="first-test" useProxy="rest">
        <collect>
            <open/>
            <resolution width="800" height="600" />
            <sleep duration="1500"/>
            <screen/>
            <source/>
            <status-codes/>
            <js-errors/>
        </collect>
        <compare xmlns="http://www.cognifide.com/aet/compare/">
            <screen comparator="layout"/>
            <source comparator="w3c-html5"/>
            <status-codes filterRange="400,600"/>
            <js-errors>
                <js-errors-filter source="http://w.iplsc.com/external/jquery/jquery-1.8.3.js" line="2" />
            </js-errors>
        </compare>
        <urls>
            <url href="https://en.wikipedia.org/wiki/Main_Page"/>
        </urls>
    </test>
</suite>
```

Then download the AET [shell script](https://github.com/wttech/aet/blob/master/client/client-scripts/aet.sh)
(see required prerequisites in [README](https://github.com/wttech/aet/blob/master/client/client-scripts/README.md)).

### Run the Test

Assuming you have the woring instance of AET under https://my-aet-domain and both `suite.xml` and `aet.sh` files in the same directory, 
open a command prompt in that directory and execute following command:
```
./aet.sh https://my-aet-domain
```
If your suite file is not named `suite.xml` or it's in a different directory than `aet.sh`, you need to specify the suite path:
```
./aet.sh https://my-aet-domain path/to/my-suite.xml
```

### Check Results

At the end of script output, there's a generated report URL, e.g.:
```
Report url:
https://my-aet-domain/report.html?company=company&project=project&correlationId=company-project-test-suite-1535375408860
```
Open the report URL in your browser to see the test results.

Congratulations! You have successfully created and run your first AET test.

### AET Development
See the [Contributing guide](https://github.com/wttech/aet/blob/master/CONTRIBUTING.md)
