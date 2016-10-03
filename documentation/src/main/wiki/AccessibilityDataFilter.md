#### Accessibility Data Filter

Accessibility Data Filter filters out Accessibility issues - it removes the matched accessibility issues from reports.  
This filter can be only applied to the `accessibility` comparator tag in the test case.  
When more than one parameter is provided then only issues fully matched are filtered out.

Module name: **accessibility-filter**

Resource name: accessibility

##### Parameters

| Parameter | Value | Description | Mandatory |
| --------------- | ----- | ----------- | --------- |
| `error` | string error | The exact error message | At least one of parameters is required |
| `principle` | string principle | The exact accessibility issue principle |
| `line` | integer line number |The line number in the file which the issue occurred in |
| `column` | integer column number | The column number in the file which the issue occurred is |

##### Example Usage

In this example the exact match of the accessibility issue breaking principle "WCAG2A.Principle4.Guideline4_1.4_1_2.H91.Button.Name", at the line 21, the column 5 with the message "This button element does not have a name available to an accessibility API. Valid names are: title attribute, element content." will be totally ignored.

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="accessibility-filter-test">
        <collect>
            ...
            <open/>
            ...
            <accessibility/>
            ...
        </collect>
        <compare>
            ...
            <accessibility>
                <accessibility-filter
                    error="This button element does not have a name available to an accessibility API. Valid names are: title attribute, element content."
                    principle="WCAG2A.Principle4.Guideline4_1.4_1_2.H91.Button.Name"
                    line="21"
                    column="5" />
            </accessibility>
            ...
        </compare>
        <urls>
            ...
        </urls>
    </test>
    ...
    <reports>
        ...
    </reports>
</suite>
```

There can be more than one `accessibility-filter` tag in the `accessibility` comparator e.g.:

```xml
<accessibility>
    <accessibility-filter principle="WCAG2A.Principle1.Guideline1_3.1_3_1.F68" />
    <accessibility-filter error="This select element does not have a name available to an accessibility API. Valid names are: label element, title attribute." />
    <accessibility-filter line="270" />
    <accessibility-filter line="314" />
    <accessibility-filter column="5" />
</accessibility>
```
