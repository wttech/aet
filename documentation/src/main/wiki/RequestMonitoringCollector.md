#### Request Monitoring Collector

Request Monitoring Collector is responsible for collecting responses size for the requests that match the given regexp.

Module name: **request-monitoring**

##### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
| `urlPattern` |`^.*\.(jpe?g)$`  | The parameter specifies the url pattern that will be used to filter the requests | yes |


##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="request-monitoring-test" useProxy="rest">
        <collect>
            ...
            <request-monitoring urlPattern="^.*\.(jpe?g|png|svg|gif|ico|ics)$"/>
            ...
        </collect>
        <compare>
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
