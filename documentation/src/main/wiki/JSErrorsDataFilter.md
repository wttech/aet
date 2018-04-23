#### JS Errors Data Filter

Js Errors Data Filter filters JS Errors Collector result - it marks filtered errors as ignored
and attaches data about applied filters.

This filter can be only applied to `js-errors` comparator tag in test case.  
When more than one parameter is provided then only fully matched errors are filtered.  
If some XML-specific charactes (e.g. `&`) are in parameter's value, then they must be escaped.

Module name: **js-errors-filter**

Resource name: js-errors

##### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
|`error`|string error|Exact error message|At least one of parameter is required|
|`source`|JavaScript filename suffix (see notes) below|Source file name in which error occurred|At least one of parameter is required|
|`errorPattern` | pattern error text | Regular expression that matches message text of issue to be filter out|At least one parameter is required|
|`line`  integer line number|Line number in file in which error occurred| |At least one of parameter is required|

*Note:*
- filter will check if value of `source` param is a suffix of JS error source. So we can use `"/jquery-1.8.3.js"` to filter errors from all JQuery files regardless of path or even filter errors from all JavaScript files with `".js"`.
- `error` param will be overridden by `errorPattern` if set
- If some XML-specific charactes (e.g. `&`) are in parameter's value, then they have to be escaped. Suite should be valid XML document:

```xml
<js-errors-filter source="http://www.example.com/jquery-1.8.3.js?q=1&amp;p=2" />
```

##### Example Usage

In this sample exact match of js error from file  "[http://w.iplsc.com/external/jquery/jquery-1.8.3.js](http://w.iplsc.com/external/jquery/jquery-1.8.3.js)", line 2 with message "Error: Syntax error, unrecognized expression: .iwa_block=pasek-ding" will be ignored. That is to say, it will be marked in report as ignored and it will have associated information about the applied filter. In this case, about filter specified in first `js-errors-filter` node below. 

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="js-errors-filter-test">
        <collect>
            ...
            <open/>
            ...
            <js-errors/>
            ...
        </collect>
        <compare>
            ...
            <js-errors>
                <js-errors-filter
                    error="Error: Syntax error, unrecognized expression: .iwa_block=pasek-ding"
                    line="2"
                    source="http://w.iplsc.com/external/jquery/jquery-1.8.3.js" />
                <js-errors-filter
                    source="/some/path/to/custom.js" />
                <js-errors-filter
                    errorPattern="^.*Syntax error, unrecognized expression.*$" />                    
            </js-errors>
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

There can be more than one `js-errors-filter` tag in `js-errors` comparator eg:

```xml
<js-errors>
    <js-errors-filter error="Error: Syntax error, unrecognized expression: .iwa_block=pasek-ding" />
    <js-errors-filter source="http://w.iplsc.com/external/jquery/jquery-1.8.3.js"
                      line="2" />
</js-errors>
```
