#### Remove Nodes Data Filter

Remove Nodes Data Filter allows to delete some node(s) from html tree. Node(s) are defined by xpath selector.

| ! Important information |
|:----------------------- |
| Html source has to be valid xml document |

Name: **remove-nodes**

Resource name: source

##### Parameters

| Parameter | Value | Description | Mandatory |
| --------- | ----- | ----------- | --------- |
| `xpath` | xpath_to_node| Xpath selector for nodes to remove | yes |

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8"?>
<suite name="test-suite" company="Cognifide" project="project">
    <test name="remove-nodes-test">
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
                <remove-nodes xpath="//*[@id='blueBarNAXAnchor']/div/div/div/a/i"/>
            </source>
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
