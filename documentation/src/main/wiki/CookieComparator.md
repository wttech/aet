#### Cookie Comparator

Cookie Comparator is responsible for processing of collected cookies. This can be simply listing of collected cookies, verifying if cookie exists or comparing collected cookie with pattern.

Cookie feature allows to collect patterns and can be rebased from report only in compare action mode*.*

Module name: **cookie**

Resource name: cookie

##### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
| `action` | list<br/><br/> test<br/><br/> compare | Displays the list of cookies<br/><br/> Tests if cookie with the given name and value exists<br/><br/> Compares the current data with the pattern (compares only cookie names, values are ignored) | no<br/><br/> If `action` parameter is not provided, default `list` action is performed |
| `cookie-name` |  | Name of the cookie to test, applicable only for test action | yes, if `action` set to `test` |
| `cookie-value` |   | Value of the cookie to test, applicable only for test action | no |
| `showMatched` | boolean<br/> (default: `true`) | **Works only in compare mode.** Flag that says if matched cookies should be displayed in report. By default set to `true`. | no |

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="cookie-test">
        <collect>
            ...
            <cookie />
            ...
        </collect>
        <compare>
            ...
            <cookie />
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
