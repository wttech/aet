#### Sleep Modifier

Sleep Modifier is responsible for temporarily ceasing execution, causes current thread to sleep. It is useful in situations when page resources have a long loading time - it suspends next collectors for some time.

Module name: **sleep**

##### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
| `duration` | int (1 to 30000) | Sleep time, in milliseconds | yes |

| ! Important information |
|:----------------------- |
| One sleep duration cannot be longer than 30000 milliseconds (30 seconds).<br/><br/> Two consecutive sleep modifiers are not allowed.<br/><br/> Total sleep duration (sum of all sleeps) in test collection phase cannot be longer than 120000 milliseconds (2 minutes). |

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="sleep-test">
        <collect>
            ...
            <open />
            ...
            <sleep duration="3000" />
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
