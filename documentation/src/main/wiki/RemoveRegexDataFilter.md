#### Replace Regex Data Filters

Replace Regex Data Filter allows to replace parts of source based on regex expressions from compared source (data and/or pattern).   
This may be helpful when we need to compare page sources with dynamic content. We can then remove these dynamic content markup.  
See also Remove Lines and Remove Nodes data Filters.

Module name: **replace-regexp**

Resource name: source

##### Parameters

| Parameter | Value | Mandatory |
| --------- | ----- | --------- |
| `dataRegExp` |RegExp that will replace matched parts of *data* sources  | At least one of -RegExp parameter is required. |
| `patternRegExp` | RegExp that will replace matched parts of *pattern* sources | At least one of -RegExp parameter is required. |
| `regExp` | RegExp | that will replace matched parts of  *pattern and data* sources | At least one of -RegExp parameter is required. |
| `value` | string | content of value will be used to replace matched parts| no, default: ""|

| ! Note |
|:------ |
| `regExp` value overrides `dataRegExp` and `patternRegExp` |

Tip:  
Use [http://www.regexplanet.com/advanced/java/index.html](http://www.regexplanet.com/advanced/java/index.html) to create  check your Regular Expression and when ready use 'as a Java string' value in your testsuite.

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="my-test-suite" company="cognifide" project="project">
    <test name="replace-regex-test">
        <collect>
            ...
            <source/>
            ...
        </collect>
        <compare>
            ...
            <source comparator="source">
                 <replace-regexp regExp='\"correlationId\": \".*\"' value="."/>
            </source>
            ...
        </compare>
        <urls>
            ...
        </urls>
    </test>
    ...
</suite>
```
