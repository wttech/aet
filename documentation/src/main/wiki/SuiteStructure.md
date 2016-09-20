### Suite Structure

#### test

This tag is definition of the single test in test suite. Test suite can contain many tests.

| Attribute name | Description | Mandatory |
| -------------- | ----------- | --------- |
| `name` | Name of the test. Should consists only of letters, digits and/or characters: `-`, `_`. This value is also presented on report (more details in [[Suite Report|SuiteReport]] section). | yes |
| `useProxy` | Defines which (if any) *Proxy* should be used during collection phase. If not provided, empty or set with `"false"`, proxy won't be used. If set to `"true"`, default *Proxy Manager* will be used. Otherwise *Proxy Manager* with provided name will be used (see [Proxy](#proxy)). Proxy is needed by Status Codes Collector and Header Modifier. | no |
| `zIndex` | Specifies order of tests on *HTML Report*. A test with greater `zIndex` is always before test with lower value. Default value is `0`. This attribute accepts integers in range `<-2147483648; 2147483647>`. | no |

Each **test** element contains:

* **one [collect](#collect) and one [compare](#compare) element** - test execution phases,
* **one [urls](#urls) element** - list of urls to process.

##### Proxy

Proxy is provided by two separated implementations: *embedded* and *rest*.

###### *embedded*

*Embedded* proxy does not need standalone *[[Browsermob Server|WindowsSetup#browsermob-proxy-setup]]*, but does not support SSL. *Embedded* proxy is used as default when `useProxy` is setted to "true" (which is equivalent to setting `useProxy="embedded"`*)*.

**Example usage**

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="header-modify-test" useProxy="embedded">
      ...
    </test>
    ...
</suite>
```

###### rest

*Rest* proxy requires standalone *[[Browsermob Server|WindowsSetup#browsermob-proxy-setup]]*.

**Example usage**

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="header-modify-test" useProxy="rest">
      ...
    </test>
    ...
</suite>
```

#### collect

This tag contain list of collectors and modifiers which will be run. It specifies what pages' data should be collected and it allows for some data modification before collection step. All collect steps are processed in defined order.

Each collector provides some specific result of gathering current data (i.e. png, html files) and a common metadata file - `result.json`.

Following elements are available in `collect` element:
* **[[Open|Open]]**
* **[[Collectors|Collectors]]**
* **[[Modifiers|Modifiers]]**

#### compare

This tag contain list of **[[Comparators]]**. Each comparator takes collected resource of defined type and runs it against comparator. It provides some specific result files illustrating found differences (i.e png, html files) and a common metadata file - `result.json`.

Each resource type has default comparator, user can use other comparators for each type by providing attribute `comparator` with comparator name, e.g.:
```xml
<source comparator="my_source_comparator"/>
```

runs `my_source_comparator` against each source collected during collection phase. Each comparator can contain list of **[[Data Filters|DataFilters]]** which will be performed before each compare phase.

Data filters are used to modify gathered data before these data are passed to comparator. For example you may remove some node from html tree. Data filters are defined in test suite xml as subnodes of `comparator` node.

Each Data Filter has predefined type of data on which it operates.

#### urls

See [[Urls]].
