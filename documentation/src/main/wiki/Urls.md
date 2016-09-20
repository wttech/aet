#### Urls

`<urls>` element lists all urls which will be processed within current test. Contains one or more **[url](#url)** elements.

##### url
A single url which will be processed by particular test.

###### Parameters

| Attribute name | Description | Mandatory |
| -------------- | ----------- | --------- |
| `href` | Page address (also see note under name attribute) | yes |
| `name` | Identifier for url. It is used to identify data for url. If provided should be unique for each test in test suite. If not provided is set to encoded `href` value. Should consists only of letters, digits and/or characters: `-`, `-`. Note that if `href=""` with provided url `name` attribute and suite `domain` attribute is also valid | no |
| ~~`description`~~ | ~~Additional description for url that will be shown in html report~~ | no longer supported |

###### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="urls-test">
        <collect>
            ...
        </collect>
        <compare>
            ...
        </compare>
        <urls>
            <url href="http://www.example.com"/>
            ...
        </urls>
    </test>
    ...
    <reports>
        ...
    </reports>
</suite>
```
