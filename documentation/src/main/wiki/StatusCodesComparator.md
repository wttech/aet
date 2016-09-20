#### Status Codes Comparator

Status Codes Comparator is responsible for processing collected status codes. In this case it is simply displaying the list of collected status codes from given page.

Status Codes feature do not allow to collect patterns, so it does not compare results with any patterns - rebase action is also not available.

Module name: **status-codes**

Resource name: status-codes

##### Parameters

| Parameter | Value | Example | Description | Mandatory |
| --------- | ----- | ------- | ----------- | --------- |
| `filterRange` | x,y | 400,500 | Defines range of status codes that should be processed | yes, if `filterCodes` is not present |
| `filterCodes` | x,y,z | 400,401,404 | List of status codes that should be processed | yes, if `filterRange` is not present |
| `showExcluded` | boolean (default: `true`) | true | Flag that says if excluded codes (see [[Status Codes Data Filters | StatusCodesDataFilters]]) should be displayed in report. By default set to `true`. | no |

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="status-codes-test">
        <collect>
            ...
            <open />          
            ...
            <status-codes />
            ...
        </collect>
        <compare>
            ...
            <status-codes filterRange="400,404" />
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
