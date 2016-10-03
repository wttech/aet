#### Client Side Performance Comparator

| ! Beta Version |
|:------------   |
| This AET Plugin is currently in a BETA version. |

Client Side Performance Comparator is responsible for processing collected client side performance analysis results. The comparator makes use of the [YSlow](http://yslow.org/) tool in order to perform the comparison phase on collected results.

Module name: **client-side-performance**

Resource name: client-side-performance

##### Parameters

No parameters  

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="client-side-performance-test">
        <collect>
            ...
            <client-side-performance />
            ...
        </collect>
        <compare>
            ...
            <client-side-performance />
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
