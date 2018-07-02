## Defining Suite

### Test suite

In general the test suite is an XML document that defines tests conducted on a collection of web pages. This chapter covers the test suite API, with a description of its each element.

### Sample test suite

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!-- Each test suite consists of one suite -->
<suite name="test-suite" company="cognifide" project="project">
    <!-- The First test of Test Suite -->
    <!-- The flow is [collect] [compare] [urls] -->
    <test name="first-test" useProxy="rest">
        <!-- Description of the collect phase -->
        <collect>
            <open/>
            <resolution width="800" height="600" />
            <!-- sleep 1500 ms before next steps - used on every url defined in urls -->
            <sleep duration="1500"/>
            <screen/>
            <source/>
            <status-codes/>
            <js-errors/>
        </collect>
        <!-- Description of compare phase, says what collected data should be compared to the patterns, can also define the exact comparator. If none chosen, the default one is taken. -->
        <compare xmlns="http://www.cognifide.com/aet/compare/">
            <screen comparator="layout"/>
            <source comparator="w3c-html5"/>
            <status-codes filterRange="400,600"/>
            <js-errors>
                <js-errors-filter source="http://w.iplsc.com/external/jquery/jquery-1.8.3.js" line="2" />
            </js-errors>
        </compare>
        <!-- List of urls which will be taken into tests -->
        <urls>
            <url href="http://www.cognifide.com"/>
        </urls>
    </test>
</suite>
```

The root element of the test suite definition is the `suite` element.

### suite

| ! Important |
|:----------- |
| When defining the suite the user should think of the following three mandatory parameters properly: `name, company, project`. These parameters are used by the AET System to identify the suite. <br/><br/> Any change in one of these parameter values in the future will occur in treating the suite as a completely new one which will in effect gather all patterns from scratch. |

The Root element for the xml definition, each test suite definition consists of exactly one `suite` tag.

| Attribute name | Description |Mandatory  |
| -------------- | ----------- | --------- |
| `name` | Name of the test suite. It should contain lowercase letters, digits and/or characters: `-`, `_` only. | yes |
| `company` | Name of the company. It should contain lowercase letters, digits and/or characters: `-` only.| yes |
| `project` | Name of the project. It should contain lowercase letters, digits and/or characters: `-` only.| yes |
| `domain` | General domain name consistent for all urls considered. Every url link is built as a concatenation of the *domain* name and the *href* attribute of it. If the `domain` property is not set, then the `href` value in the `url` definition should contain a full valid url. See more in the [[Urls]] section. | no |

The `suite` element contains one or more **[[test|SuiteStructure#test]]** elements.
