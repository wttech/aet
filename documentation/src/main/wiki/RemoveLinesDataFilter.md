#### Remove Lines Data Filter

Remove Lines Data Filter allows to remove lines from compared source (data or pattern). This may be helpful when we need to compare page sources with dynamic content. We can then remove these dynamic content markup.

Line number in reports represents lines state after modification, so have in mind that marked lines have different lines number in real source.

Module name: **remove-lines**

Resource name: source

##### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
|`dataRanges`|ranges of lines to remove from data|Ranges should be provided in form **a,b**, this will be interpreted as closed interval of integers [a,b].Particular ranges should be separated by semicolons: **a,b;c,d;e,f** a>0, b>0|At least one of parameters is required.|
|`patternRanges`|ranges of lines to remove from pattern|Ranges should be provided in form **a,b**, this will be interpreted as closed interval of integers [a,b].Particular ranges should be separated by semicolons: **a,b;c,d;e,f** a>0, b>0||

Examples:

Suppose we want to remove line 10: `10,10`

Suppose we want to remove lines from 10 to 15: `10,15`

Suppose we want to remove lines from 10 to 15, line 27 and lines from 30 to 38: `10,15;27,27;30,38`

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="my-test-suite" company="cognifide" project="project">
    <test name="remove-lines-test">
        <collect>
            ...
            <source/>
            ...
        </collect>
        <compare>
            ...
            <source comparator="source">
                <remove-lines dataRanges="10,15;27,27" patternRanges="10,14;27,28"/>
            </source>
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
