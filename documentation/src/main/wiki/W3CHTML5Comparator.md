#### W3C HTML5 Comparator

W3C HTML5 Comparator is responsible for validating collected page source against w3c standards using [validator.nu](https://validator.nu/). HTML5 is supported by this library.

W3C HTML5 feature do not allow to collect patterns, so it does not compare results with any patterns - rebase action is also not available.

Module name: **w3c-html5**

Resource name: source

##### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
| `ignore-warnings` | boolean (default: true) | If `ignore-warnings="true"` test status does not depend on the warnings amount, otherwise warnings counts as w3c errors when computing testcase status. | no |
| `errors-only` | boolean(default: true) | **This parameter will be removed from 1.5 AET Version!**<br/> Has the same result as `ignore-warnings` parameter. | no |

| ! Mandatory parameter |
|:--------------------- |
| Please remember, that using parameter `comparator="w3c-html5"` is mandatory while defining this comparator. More information about this parameter can be found in [[Comparators]] section. |

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
