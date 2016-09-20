#### Accessibility Data Filter

Accessibility Data Filter filters Accessibility issues - it removes matched accessibility issues from reports.  
This filter can be only applied to `accessibility` comparator tag in test case.  
When more than one parameter is provided then only fully matched issues are filtered.

Module name: **accessibility-filter**

Resource name: accessibility

##### Parameters

| Parameter | Value | Description | Mandatory |
| --------------- | ----- | ----------- | --------- |
| `error` | string error | Exact error message | At least one of parameter is required |
| `principle` | string principle | Exact accessibility issue principle |
| `line` | integer line number | Line number in file in which issue occurred |
| `column` | integer column number | Column number in file in which issue occurred |

##### Example Usage

In this sample exact match of accessibility issue breaking principle "WCAG2A.Principle4.Guideline4_1.4_1_2.H91.Button.Name", at line 21, column 5 with message "This button element does not have a name available to an accessibility API. Valid names are: title attribute, element content." will be totally ignored.

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

There can be more than one `accessibility-filter` tag in `accessibility` comparator eg:

```xml
<accessibility>
    <accessibility-filter principle="WCAG2A.Principle1.Guideline1_3.1_3_1.F68" />
    <accessibility-filter error="This select element does not have a name available to an accessibility API. Valid names are: label element, title attribute." />
    <accessibility-filter line="270" />
    <accessibility-filter line="314" />
    <accessibility-filter column="5" />
</accessibility>
```
