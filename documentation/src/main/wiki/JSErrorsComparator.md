#### JS Errors Comparator

JS Errors Comparator is responsible for processing of collected javascript errors resource. In this case it is simply displaying list of javascript errors.

JS Errors feature do not allow to collect patterns, so it does not compare results with any patterns - rebase action is also not avaliable.

Module name: **js-errors**

Resource name: js-errors

##### Parameters

No parameters

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="js-errors-test">
        <collect>
            ...
            <js-errors />
            ...
        </collect>
        <compare>
            ...
            <js-errors />
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

| ! Important information |
|:----------------------- |
| [[JS Errors Data Filter|JSErrorsDataFilter]] can be applied to collected javascript errors result before comparison to modify data that is to be processed. |
