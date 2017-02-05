#### Client Side Performance Collector BETA

| ! Beta Version |
|:------------   |
| This AET Plugin is currently in a BETA version. |

Client Side Performance Collector is responsible for collecting performance analysis results. The collector makes use of the [YSlow](http://yslow.org/) tool to perform analysis.

Module name: **client-side-performance**

| ! Important information |
| :---------------------- |
| In order to use this collector *[[proxy|SuiteStructure#proxy]]* must be used. |

##### Parameters
No parameters.

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project" environment="win7-ff16">
    <test name="source-test" useProxy="rest">
        <collect>
            ...
            <client-side-performance />
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
