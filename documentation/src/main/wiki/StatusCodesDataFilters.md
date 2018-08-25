#### Status Codes Data Filters

There might be a need to ignore some of the collected status codes. We can exclude them so that they no longer affect our test case.

Data filters will be applied only for codes contained in logic sum of the `filterRange` and the `filterCodes`. If the `filterRange` isn't provided, default range will be used.

##### Exclude Filter

Exclude Filter removes from reports Status Codes results that match specified parameters.  

Name: **exclude**

Resource name: status-codes

###### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
| `url` | String url | Exact url to be excluded from results. | At least one of parameter is required. |
| `pattern` | String regex pattern| Regex pattern that urls should match to be excluded from results. | |

If both parameters are provided then result is removed when it matches at least one of the parameters.

###### Example Usage

In this sample result with url http://www.external.com/_optional.js or urls that match pattern **^.\*js$** will be excluded.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<suite name="test-suite" company="Cognifide" project="project">
    <test name="exclude-test" useProxy="rest">
        <collect>
            ...
            <open/>
            ...
            <status-codes/>
            ...
        </collect>
        <compare>
            ...
            <status-codes filterRange="200,999">
                <exclude url="http://www.external.com/_optional.js" pattern="^.*js$"/>
            </status-codes>
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

There can be more than one `exclude` tags in `status-codes` comparator. They are processed in turns. Example below is equivalent to previous one:

```xml
<status-codes>
    <exclude url="http://www.external.com/_optional.js"/>
    <exclude pattern="^.*js$"/>
</status-codes>
```

In this case both results with url http://www.external.com/_optional.js and urls that match pattern **^.\*js$** (ending with `js`) will not be displayed on reports.

##### Include Filter

Include Filter excludes from reports Status Codes results that **do not** match specified parameters.  

Name: **include**

Resource name: status-codes

###### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
| `url` | String url | Exact url to be included in reports. Results that do not match will be excluded. | At least one of parameter is required. |
| `pattern` | String regex pattern | Regex pattern that urls should match to be included in reports. Results that do not match will be excluded. | |

If both parameters are provided then result is only included in the report when it matches both of the parameters.

###### Example Usage

In example below **only** result with url http://www.cognifide.com/main.js will be included in report.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<suite name="test-suite" company="Cognifide" project="project" environment="win7-ff16">
    <test name="include-test" useProxy="rest">
        <collect>
            ...
            <open/>
            ...
            <status-codes/>
            ...
        </collect>
        <compare>
            ...
            <status-codes filterRange="200,999">
                <include url="http://www.cognifide.com/main.js"/>
            </status-codes>
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

There can be more than one `include` tags in `status-codes` comparator. They are processed in turns. Example:

```xml
<status-codes>
    <include pattern="^.*js$"/>
    <include url="http://www.cognifide.com/main.js"/>
</status-codes>
```

In this case only http://www.cognifide.com/main.js url will be included on reports: first all results that do not match **^.\*js$** pattern (ending with `js`) are excluded. Then within that result all urls different from "http://www.cognifide.com/main.js" are excluded.

In example above, first `<include>` can be omitted and result will be the same.

##### Include and Exclude

**Include** and **exclude** modifiers can be both applied to **status-codes comparator**. They are processed in turns. Example:

```xml
<status-codes>
    <include pattern="^.*js$"/>
    <exclude url="http://www.external.com/_optional.js"/>
</status-codes>
```

In example above we include URLs that match **^.\*js$**, so any other URL will be excluded. Then we exclude http://www.external.com/_optional.js. Therefore only urls ending with `js` except http://www.external.com/_optional.js will be included in reports.
