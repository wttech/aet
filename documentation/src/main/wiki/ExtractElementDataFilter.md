#### Extranct Element Data Filter

Extract Element Data Filter allows to extract element from html source (collected by Screen Collector) by providing id attribute or class attribute. Found element's source is processed by comparator.

Module name: **extract-element**

Resource name: source

##### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
| `elementId` | HTML id | Id for element to extract | See note below |
| `class` | HTML class | Class name for element to extract | See note below |

| ! Note |
|:------ |
| One of these parameters is required. Only one parameter (either `elementId` attribute or `class` attribute) can be provided. |

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="extract-element-test">
        <collect>
            ...
            <open/>
            ...
            <source/>
            ...
        </collect>
        <compare>
            ...
            <source comparator="source">
                <extract-element elementId="login_form"/>
            <!-- OR -->
                <extract-element class="class_form"/>
            </source>
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
