#### JS Errors

##### Description
This case displays success status when there were no JS errors found.

| ! Important information |
|:----------------------- |
|All errors filtered with [[JS Errors Data Filter|JSErrorsDataFilter]] are ommited. |

![JS errors success](assets/suiteReport/jserrors-success.png)

Otherwise the report is marked as risk (red) when at least one error has been found.

![JS errors failure](assets/suiteReport/jserrors-failure.png)

##### What vulnerabilities it discovers
* JS Errors can cause improper behaviour of a page (e.g. dynamic components may not work properly in some (or even all) browsers,
* JS Error can also occur when good practices are not followed in the javascript code.