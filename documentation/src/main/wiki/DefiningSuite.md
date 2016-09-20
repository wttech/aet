## Defining Suite

### Test suite

In general the test suite is an XML document that defines tests performed over collection of web pages. This chapter covers test suite API, with description of each element.

### Example test suite

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

Root element of test suite definition is `suite` element.

### suite

| ! Important |
|:----------- |
| When defining a suite a user should think of three mandatory parameters properly: `name, company, project`. Those parameters are used by the AET System to identify the suite. <br/><br/> Any change in one of those parameters values in the future will occur in treating the suite as a completely new one, which will in effect gather all the patterns from scratch. |

Root element for xml definition, each test suite definition consists of exactly one `suite` tag.

| Attribute name | Description |Mandatory  |
| -------------- | ----------- | --------- |
| `name` | Name of the test suite. Should consist only of lowercase letters, digits and/or characters: `-`, `_`. | yes |
| `company` | Name of the company. Should consist only of lowercase letters, digits and/or characters: `-`.| yes |
| `project` | Name of the project. Should consist only of lowercase letters, digits and/or characters: `-`.| yes |
| `domain` | General domain name consistent for all considered urls. Every url link is built as a concatenation of *domain* name and *href* attribute of it. If `domain` property is not set, then `href` value in `url` definition should contain full valid url. See more in [[Urls|Urls]] section. | no |

`suite` element contains one or more **[[test|SuiteStructure#test]]** elements.
