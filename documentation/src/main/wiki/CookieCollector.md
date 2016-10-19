#### Cookie Collector

Cookie collector is responsible for collecting cookies.

Module name: **cookie**

##### Parameters

No parameters.

##### Sample Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="cookie-test">
        <collect>
            ...
            <cookie/>
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
