#### Accessibility Comparator

| ! Beta Version |
|:------------   |
| This AET Plugin is currently in a BETA version. |

Accessibility Comparator is responsible for processing collected accessibility validation results. It makes use of the [html CodeSniffer](http://squizlabs.github.io/HTML_CodeSniffer/) library.

Module name: **accessibility**

Resource name: accessibility

##### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
| `report-level` | ERROR (default)<br/><br/> WARN<br/><br/> NOTICE | Violations of the ERROR type are only displayed in the report.<br/><br/> Violations of WARN and ERROR types are displayed in the report.<br/><br/> All violation types are displayed in the report. | no |
| ignore-notice | boolean<br/> (default: `true`) | If the `ignore-notice=true` test status does not depend on the number of notices.<br/> If `ignore-notice=false` notices are treated as warnings in calculating the test status. Enforces the report-level = NOTICE. | no |
| `showExcluded` | boolean<br/> (default: `true`) | The flag that indicates if excluded issues (see [[Accessibility Data Filter|AccessibilityDataFilter]]) should be displayed in the report. By default set to `true`. | no |

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="accessibility-test">
        <collect>
            ...
            <accessibility />
            ...
        </collect>
        <compare>
            ...
            <accessibility report-level="WARN" />
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
