#### JS Errors Collector

JS Errors Collector is responsible for collecting javascript errors occuring on given page.

Module name: **js-errors**

##### Parameters

No parameters.

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="js-errors-test">
        <collect>
            ...
            <js-errors/>
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
