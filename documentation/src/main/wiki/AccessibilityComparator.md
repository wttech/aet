#### Accessibility Comparator

| ! Beta Version |
|:------------   |
| This AET Plugin is currently in BETA version. |

Accessibility Comparator is responsible for processing of collected accessibility validation result. It uses [html CodeSniffer](http://squizlabs.github.io/HTML_CodeSniffer/) library.

Module name: **accessibility**

Resource name: accessibility

##### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
| `report-level` | ERROR (default)<br/><br/> WARN<br/><br/> NOTICE | Only violations of type ERROR are displayed on report.<br/><br/> Violations of type WARN and ERROR are displayed on report.<br/><br/> All violations are displayed on report. | no |
| ignore-notice | boolean<br/> (default: `true`) | If `ignore-notice=true` test status does not depend on the notices amount.<br/> If `ignore-notice=false` notices are treated as warnings in calculating test status. Enforces report-level = NOTICE. | no |
| `showExcluded` | boolean<br/> (default: `true`) | Flag that says if excluded issues (see [[Accessibility Data Filter|AccessibilityDataFilter]]) should be displayed in report. By default set to `true`. | no |

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
