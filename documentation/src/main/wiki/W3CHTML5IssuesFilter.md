#### W3C HTML5 Issues Filter

W3C HTML5 Issues Filter allows to exclude some W3C HTML5 issues from result. Excluded issues will appear at the bottom of issues table and won't be taken into account when calculating status.

Name: **w3c-filter**

Resource name: source

Comparators: **w3c-html5**

### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
| `message` | string | Prefix or all message text of issue to be filter out | At least one of params should be used and all of used params should be not empty. |
| `line` | integer | Line in source file where issue appear | |
| `column` | integer | Column in source file where issue appear | |

| ! Note |
|:------ |
| If there are some If some XML-specific charactes (e.g. `&`) are in parameter's value, then they have to be escaped. See example below. |

##### Example Usage for w3c-html5 comparator

```xml
<?xml version="1.0" encoding="UTF-8"?>
<suite name="test-suite" company="Cognifide" project="project" environment="win7-ff16">
    <test name="remove-nodes-test">
        <collect>
            ...
            <open/>
            ...
            <source/>
            ...
        </collect>
        <compare>
            ...
            <source comparator="w3c-html5" errors-only="false">
                <w3c-filter message = "The first occurrence of" />
                <w3c-filter message = "&#8220;&amp;&#8221; did not start a character reference"/>
                <w3c-filter line="1" column="119"/>
            </source>
            ...
        </compare>
        <urls>
            ...
        </urls>
    </test>
    <reports>
        ...
    </reports>
</suite>
```