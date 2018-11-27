#### Accessibility Collector

Accessibility Collector is responsible for collecting validation results containing violations of the defined coding standard found on the page. The plugin is based on [AccessSniff](https://github.com/yargalot/AccessSniff/) v. 3.2.0, which uses the [HTML_CodeSniffer](http://squizlabs.github.io/HTML_CodeSniffer/) tool to find violations.

Module name: **accessibility**

##### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
| `standard` |WCAG2A<br/> WCAG2AA (default)<br/> WCAG2AAA | The parameter specifies the standard which the page is validated against. More information on the standards is available at: [WCAG2](http://squizlabs.github.io/HTML_CodeSniffer/Standards/WCAG2/) | no |

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
