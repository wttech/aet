#### Urls

`<urls>` element lists all urls which will be processed within the current test. It contains one or more **[url](#url)** elements.

##### url
A single url which will be processed by a particular test.

###### Parameters

| Attribute name | Description | Mandatory |
| -------------- | ----------- | --------- |
| `href` | A page address (also see note under the name attribute) | yes |
| `name` | An identifier for the url. It is used to identify the data for the url. If provided it should be unique for each test in the test suite. If not provided it is set to the encoded `href` value. It should consists of letters, digits and/or characters: `-`, `_` only. Note that if `href=""` with the provided url `name` attribute and the suite `domain` attribute is also valid. | no |
| ~~`description`~~ | ~~An additional description for the url that will be shown in the html report~~ | no longer supported |

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
