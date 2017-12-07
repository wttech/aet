#### W3C HTML5 Comparator

W3C HTML5 Comparator is responsible for validating the collected page source against w3c standards using [validator.nu](https://validator.nu/). HTML5 is supported by this library.

The W3C HTML5 feature does not allow to collect patterns, so it does not compare results with any patterns either - the rebase action is also not available.

Module name: **w3c-html5**

Resource name: source

##### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
| `ignore-warnings` | boolean (default: true) | If `ignore-warnings="true"` the test status does not depend on the warnings amount. Otherwise warnings count as w3c errors when computing the testcase status. | no |
| `errors-only` | boolean(default: true) | **This parameter will be removed from the 1.5 AET Version!**<br/> It has the same result as the `ignore-warnings` parameter. | no |

| ! Mandatory parameter |
|:--------------------- |
| Please remember, that using the parameter `comparator="w3c-html5"` is mandatory while defining this comparator. More information about this parameter can be found in the [[Comparators]] section. |

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="w3c-html5-test">
        <collect>
            ...
            <open />          
            ...
            <source />
            ...
        </collect>
        <compare>
            ...
            <source comparator="w3c-html5" />
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
