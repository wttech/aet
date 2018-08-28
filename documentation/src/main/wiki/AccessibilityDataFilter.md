#### Accessibility Data Filter

Accessibility Data Filter filters out Accessibility issues - it removes the matched accessibility issues from reports.
This filter can be only applied to the `accessibility` comparator tag in the test case.
When more than one parameter is provided then only issues fully matched are filtered out.

Module name: **accessibility-filter**

Resource name: accessibility

##### Parameters

| Parameter | Value | Description | Mandatory |
| --------------- | ----- | ----------- | --------- |
| `error` | string error | The exact error message | At least one parameter is required, ignored if 'errorPattern' parameter is provided |
| `errorPattern` | regexp| Regular expression that matches message text of issue to be filter out | At least one parameter is required, |
| `principle` | string principle | The exact accessibility issue principle |
| `line` | integer line number |The line number in the file which the issue occurred in |
| `column` | integer column number | The column number in the file which the issue occurred is |
| `markupCss` | CSS selector | CSS selector matching the HTML fragment where the issue was detected |

*Note:*
- `error` will be overridden by `errorPattern` if set.
- If there are some If some XML-specific charactes (e.g. `&`) are in parameter's value, then they have to be escaped. Suite should be valid XML document.

##### Example Usage

In this example the exact match of the accessibility issue breaking principle "WCAG2A.Principle4.Guideline4_1.4_1_2.H91.Button.Name", at the line 21, the column 5 with the message "This button element does not have a name available to the accessibility API. Valid names are: title attribute, element content." will be totally ignored.

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
                    error="This button element does not have a name available to the accessibility API. Valid names are: title attribute, element content."
                    principle="WCAG2A.Principle4.Guideline4_1.4_1_2.H91.Button.Name"
                    line="21"
                    column="5" />
                <accessibility-filter errorPattern="[\w]* button element does not have a name available .*" />
                <accessibility-filter error="This select element does not have a name available to an accessibility API. Valid names are: label element, title attribute." />
                <accessibility-filter principle="WCAG2A.Principle1.Guideline1_3.1_3_1.F68" />
                <accessibility-filter line="252" />
                <accessibility-filter column="6" />
                <accessibility-filter line="317" column="50" />
                <accessibility-filter markupCss=".form-control" />
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
    <accessibility-filter errorPattern="^This button .* element content.$" />
</accessibility>
```
