#### Layout Comparator

Layout Comparator is responsible for comparing collected screenshot of page with pattern. This is default comparator for `screen` resource.

Can be rebased from report.

Module name: **layout**

Resource name: screen

##### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
| `pixelThreshold` | int (equal or greater than 0) | The value to set the error threshold in pixels e.g if difference between photos is smaller or equal to `pixelThreshold`, the test will pass. In case of difference is bigger than `pixelThreshold`, the test will fail. | no |
| `percentageThreshold` | double (between 0 and 100) | It works as `pixelThreshold` but values are in percentages | no |

When you provide `pixelThreshold` and `percentageThreshold` test will pass only if pixel difference is smaller or equal than `pixelThreshold` and percentage difference is smaller or equal than `percentageThreshold`.

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="layout-compare-test">
        <collect>
            ...
            <screen />
            ...
        </collect>
        <compare>
            ...
            <screen comparator="layout" pixelThreshold="2500" percentageThreshold="0.5" />
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

##### Fast pre-comparison

Since AET 1.3 fast comparison of screenshots will be implemented. Taken screenshot MD5 will be matched against current pattern. If hashes will be the same, screenshot will be treated as one without differences and no further comparison will be performed.
