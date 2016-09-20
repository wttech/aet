#### Layout Comparator

Layout Comparator is responsible for comparing collected screenshot of page with pattern. This is default comparator for `screen` resource.

Can be rebased from report.

Module name: **layout**

Resource name: screen

##### Parameters

No parameters

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
            <screen comparator="layout" />
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
