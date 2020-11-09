#### Request Monitoring Comparator

Request Monitoring Comparator is responsible for validating the amount of the retrieved data for the filtered requests.

If the amount of the transferred data is bigger than the *maxSize* parameter, the test will fail.

Request Monitoring feature does not allow to collect patterns, so it does not compare results with any patterns - rebase action is also not available.

Module name: **request-monitoring**

Resource name: request-monitoring

##### Parameters

| Parameter | Value | Example | Description | Mandatory |
| --------- | ----- | ------- | ----------- | --------- |
| `maxSize` | x | 300| Defines the maximum size (kB) of the transferred data for the filtered requests  | no |

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="request-monitoring-test" useProxy="rest">
        <collect>
            ...
            <open />          
            ...
            <request-monitoring urlPattern="^.*\.(jpe?g|png|svg|gif|ico|ics)$"/>
            ...
        </collect>
        <compare>
            ...
            <request-monitoring maxSize="1024" />
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
