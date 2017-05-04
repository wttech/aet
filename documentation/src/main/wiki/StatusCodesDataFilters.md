#### Status Codes Data Filters

##### Exclude Filter

Exclude Filter removes from reports Status Codes results that match specified parameters.  

Name: **exclude**

Resource name: status-codes

###### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
| `pattern` | String regex pattern| Regex pattern that urls should match to be removed from results. | At least one of parameter is required. |
| `url` | String url | Exact url to be removed from results. | At least one of parameter is required. Will be ignored if pattern parameter is provided |

If both parameters are provided then result is removed when it matches at least one of the parameters.

###### Example Usage

In this sample match results with url http://www.cognifide.com/_cog_opt_js_f359581ea4bd3379b4c25591838a5dd8.js or url that matches pattern **^.\*js$** will be ignored (not included in report).

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
                <exclude url="http://www.cognifide.com/_cog_opt_js_f359581ea4bd3379b4c25591838a5dd8.js" pattern="^.*js$"/>
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

There can be more than one `exclude` tags in `status-codes` comparator. They are processed in turns. Example below is equivalent to defined above:

```xml
<status-codes>
    <exclude url="http://www.cognifide.com/_cog_opt_js_f359581ea4bd3379b4c25591838a5dd8.js"/>
    <exclude pattern="^.*js$"/>
</status-codes>
```

In this case both results with url http://www.cognifide.com/_cog_opt_js_f359581ea4bd3379b4c25591838a5dd8.js and urls that match pattern **^.\*js$** (ending with js) will not be displayed on reports.

**Exclude** and **include** modifiers can be both applied to **status-codes comparator**. They are processed in turns. Example:

```xml
<status-codes>
    <include pattern="^.*js$"/>
    <exclude url="http://www.cognifide.com/_cog_opt_js_f359581ea4bd3379b4c25591838a5dd8.js"/>
</status-codes>
```

In this case, at first all urls that do not match **^.\*js$** pattern are removed. Then url http://www.cognifide.com/_cog_opt_js_f359581ea4bd3379b4c25591838a5dd8.js is removed. Therefore only urls ending with `js` except http://www.cognifide.com/_cog_opt_js_f359581ea4bd3379b4c25591838a5dd8.js will be included in reports.

##### Include Filter

Include Filter removes from reports Status Codes results that **do not** match specified parameters.  

Name: **include**

Resource name: status-codes

###### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
| `url` | String url | Exact url to be included in reports. Results that do not match will be removed. | At least one of parameter is required. |
| `pattern` | String regex pattern | Regex pattern that urls should match to be included in reports. Results that do not match will be removed. | |

If both parameters are provided then result is only included in the report when it matches both of the parameters.

###### Example Usage

In example below **only** result with url http://www.cognifide.com/_cog_opt_js_f359581ea4bd3379b4c25591838a5dd8.js will be included in report.

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
                <include url="http://www.cognifide.com/_cog_opt_js_f359581ea4bd3379b4c25591838a5dd8.js"/>
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
    <include url="http://www.cognifide.com/_cog_opt_js_f359581ea4bd3379b4c25591838a5dd8.js"/>
</status-codes>
```

In this case only http://www.cognifide.com/_cog_opt_js_f359581ea4bd3379b4c25591838a5dd8.js url will be included on reports: first all results that do not match **^.\*js$** pattern (ending with `js`) are removed. Then within that result all urls different that "http://www.cognifide.com/_cog_opt_js_f359581ea4bd3379b4c25591838a5dd8.js" are removed.

In example above, first `<include>` can be omitted and result will be the same.

**Include** and **exclude** modifiers can be both applied to **status-codes comparator**. They are processed in turns. Example:

```xml
<status-codes>
    <include pattern="^.*js$"/>
    <exclude url="http://www.cognifide.com/_cog_opt_js_f359581ea4bd3379b4c25591838a5dd8.js"/>
</status-codes>
```

In this case only first all urls that do not match **^.\*js$** pattern are removed. Then url http://www.cognifide.com/_cog_opt_js_f359581ea4bd3379b4c25591838a5dd8.js is removed. Therefore only urls ending with `js` except http://www.cognifide.com/_cog_opt_js_f359581ea4bd3379b4c25591838a5dd8.js will be included in reports.
