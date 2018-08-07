#### Status Codes Comparator

Status Codes Comparator is responsible for processing collected status codes. In this case it is simply displaying the list of collected status codes from given page.

Status Codes feature do not allow to collect patterns, so it does not compare results with any patterns - rebase action is also not available.

Module name: **status-codes**

Resource name: status-codes

##### Parameters

| Parameter | Value | Example | Description | Mandatory |
| --------- | ----- | ------- | ----------- | --------- |
| `filterRange` | x,y (default: `400,600`) | 400,500 | Defines **range** of status codes that should be processed | no |
| `filterCodes` | x,y,z | 400,401,404 | **List** of status codes that should be processed | no |
| `showExcluded` | boolean (default: `true`) | true | Used to show excluded status codes on report (see *Status Codes Data Filters*). | no |

If you provide both `filterRange` and `filterCodes`, it will be used as logical sum. It means that:
 - `<status-codes filterRange="400,500" filterCodes="501,502" />` is equivalent to `<status-codes filterRange="400,502" />`
 - `<status-codes filterRange="300,400" filterCodes="404" />` won't check `401-403` codes.

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="status-codes-test" useProxy="rest">
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
