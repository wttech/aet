#### Accessibility Collector BETA

| ! Beta Version |
|:------------   |
| This AET Plugin is currently in BETA version. |

Accessibility Collector is responsible for collecting validation result containing violations of a defined coding standard found on a page. It uses [HTML_CodeSniffer](http://squizlabs.github.io/HTML_CodeSniffer/) tool to find violations.

Module name: **accessibility**

##### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
| `standard` |WCAG2A<br/> WCAG2AA (default)<br/> WCAG2AAA | Parameter specifies a standard against which the page is validated. More information on standards: [WCAG2](http://squizlabs.github.io/HTML_CodeSniffer/Standards/WCAG2/) | no |

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="source-test">
        <collect>
            ...
            <accessibility standard="WCAG2AAA" />
            ...
        </collect>
        <compare>
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
