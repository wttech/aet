#### Cookie Comparator

Cookie Comparator is responsible for processing collected cookies. This can be simply listing of collected cookies, verifying if a cookie exists or comparing a collected cookie to the pattern.

The cookie feature allows to collect patterns and can be rebased from the report only in the compare action mode.

Module name: **cookie**

Resource name: cookie

##### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
| `action` | list<br/><br/> test<br/><br/> compare | Displays a list of cookies<br/><br/> Tests if a cookie with a given name and value exists<br/><br/> Compares the current data to the pattern (compares only cookie names, values are ignored) | no<br/><br/> If the `action` parameter is not provided, the default `list` action is performed |
| `cookie-name` |  | The name of the cookie to test, applicable only for the test action | yes, if `action` set to `test` |
| `cookie-value` |  | The value of a cookie to test, applicable only for the test action | no |
| `showMatched` | boolean<br/> (default: `true`) | **Works only in the compare mode.** The flag that says if matched cookies should be displayed in the report or not. By default set to `true`. | no |

##### Sample Usage

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
