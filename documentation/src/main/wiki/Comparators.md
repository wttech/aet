### Comparators

Comparator is module which main task is to consume data and compare it with pattern or against defined set of rules.

Each comparator presented in section below consists of three elements:

* consumed resource type,
* module name (comparator),
* parameters.

##### Consumed resource type

This is name of resource type consumed by defined comparator. This is always name of tag definition for comparator.

This name says the system which **resource type** should be consumed by defined comparator. When no comparator in system can consume defined resource type, system exception will occur and test will not be performed. This solution enables adding new features to the system without system downtime (just by installing new feature bundle).

Each comparator can consume only one type of resource.

##### Module name (comparator)

This is special parameter, unique name for comparator type treated as interpretation of given resource type. System will recognize which implementation of comparator should be called by this name. This parameter is required for each comparator but system will assume default comparator for each resource type when no `comparator` property is defined.

###### Default comparators for consumed resource names

* [[cookie|CookieCollector]] -> [[CookieComparator]],
* [[js errors|JSErrorsCollector]] -> [[JSErrorsComparator]],
* [[screen|ScreenCollector]] -> [[LayoutComparator]],
* [[source|SourceCollector]] -> [[SourceComparator]],
* [[status-codes|StatusCodesCollector]] -> [[StatusCodesComparator]].

Example of usage can be found in system for *source* comparison, where two comparators exists: [[W3C HTML5 Comparator|W3CHTML5Comparator]] and [[Source Comparator|SourceComparator]]. Example below shows sample usage:

```xml
...
<collect>
    <open/>
    <source/>
</collect>
<compare>
    <source comparator="source"/>
    <source comparator="w3c-html5"/>
</compare>
...
```

When test defined as above is executed, only one collection of page source is performed. But result of this collection is used twice during comparison phase. First by [[Source Comparator|SourceComparator]] and then by [[W3C HTML5 Comparator|W3C HTML5 Comparator]].

##### Parameters

This is set of key-value pairs using which user can pass some configuration and information to comparator. Parameters for comparators can be divided into two groups:

* mandatory - parameters without which comparison will be not possible,
* optional - passing this parameter is not obligatory, usually this is some comparator functionality extension.

###### collectorName

There exists special comparator property `collectorName` which is connected with collector's `name` property. By using `collectorName` property combined with collector's `name` property, user can control which comparator instance compares results collected by particular collector. See examples below:

```xml
...
<collect>
    <open/>
    <sleep duration="1000"/>
    <screen width="1280" height="1024" name="desktop"/>
    <screen width="768" height="1024" name="tablet"/>
    <screen width="320" height="480" name="mobile"/>
</collect>
<compare>
    <screen collectorName="mobile"/>
    <screen collectorName="tablet"/>
</compare>
...
```

Configuration above will trigger three screens collections (desktop, tablet and mobile) and two comparisons (mobile and tablet). Screenshot taken for *desktop* will be not compared.

```xml
...
<collect>
    <open/>
    <sleep duration="1000"/>
    <screen width="1280" height="1024" name="desktop"/>
    <screen width="768" height="1024" name="tablet"/>
    <screen width="320" height="480" name="mobile"/>
</collect>
<compare>
    <screen/>
</compare>
...
```

Configuration above will trigger three screens collections (desktop, tablet and mobile) and three comparisons (desktop, table, mobile).

```xml
...
<collect>
    <open/>
    <sleep duration="1000"/>
    <screen width="1280" height="1024" name="desktop"/>
    <screen width="768" height="1024" name="tablet"/>
    <screen width="320" height="480" name="mobile"/>
</collect>
<compare>
    <screen/>
    <screen collectorName="tablet"/>
</compare>
...
```

Configuration above will trigger three screens collections (desktop, tablet and mobile) and four comparisons (desktop, tablet, mobile and one additional for tablet).

##### Definitions illustration

Following picture presents definitions described earlier:

![Compare phase definitions](assets/diagrams/compare-phase-definitions.png)

where:

1. Consumed resource type,
2. Special property: collectorName,
3. Special property: comparator,
4. Module name (comparator).
