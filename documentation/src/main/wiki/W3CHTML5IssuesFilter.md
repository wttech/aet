#### W3C HTML5 Issues Filter

W3C HTML5 Issues Filter allows to exclude some W3C HTML5 issues from the result. The issues excluded will appear at the bottom of a table with issues and won't be taken into account when calculating the status.

Name: **w3c-filter**

Resource name: source

Comparators: **w3c-html5**

### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
| `message` | string | Exact message text of the issue to be filtered out. *see notes below | At least one of params should be used and all the params used should be not empty. |
| `messagePattern` | regexp | A regular expression that matches message text of the issue to be filtered out. *see notes below | At least one of params should be used and all of the params used should be not empty. |
| `line` | integer | A line in the source file where the issue appears. | |
| `column` | integer | A column in the source file where the issue appears. | |

*Note:*
- `message` will be overridden by `messagePattern` if set.
- If there are some XML-specific characters (e.g. `&`) in the parameter value, they have to be escaped. The suite should be a valid XML document.

##### Sample usage of w3c-html5 comparator

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
                <w3c-filter messagePattern = "The first occurrence of.*" />
                <w3c-filter message="A slash was not immediately followed by “&gt;”."/>
                <w3c-filter message="Element “img” is missing required attribute “src”."/>
                <w3c-filter line="1" column="119"/>
                <w3c-filter line="390" message="End tag for  “html” seen, but there were unclosed elements."/>
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
