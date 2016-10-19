#### Source Collector

Source Collector is responsible for collecting source of the page under given URL. Unlike others collectors source collector doesn't use web driver, it connects directly to web server.

Module name: **source**

| ! Note    |
| :-------- |
| System is waiting up to 20 seconds before request is timed out. This parameter is configurable via OSGi configuration (`AET Source Collector Factory`). |

##### Parameters

No parameters.

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="source-test">
        <collect>
            ...
            <source />
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
