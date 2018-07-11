### Comparators

The comparator is a module which is responsible for consuming data and comparing it to the pattern or against a defined set of rules.

Each comparator presented in the section below consists of the following three elements:

* consumed resource type,
* module name (comparator),
* parameters.

##### Consumed resource type

This is a name of the resource type consumed by the defined comparator. This is always a name of the tag definition for the comparator.

This name tells the system which **resource type** should be consumed by the defined comparator. When no comparator in the system can consume the defined resource type, a system exception will occur and the test will not be performed. This solution enables to add new features to the system with no system downtime (just by installing a new feature bundle).

Each comparator can consume only one resource type.

##### Module name (comparator)

This is a special parameter, a unique name for the comparator type treated as the interpretation of a given resource type. The system will recognize which implementation of the comparator should be called by this name. This parameter is required for each comparator but the system will assume a default comparator for each resource type when no `comparator` property is defined.

###### Default comparators for the consumed resource names

* [[cookie|CookieCollector]] -> [[CookieComparator]],
* [[js errors|JSErrorsCollector]] -> [[JSErrorsComparator]],
* [[screen|ScreenCollector]] -> [[LayoutComparator]],
* [[source|SourceCollector]] -> [[SourceComparator]],
* [[status-codes|StatusCodesCollector]] -> [[StatusCodesComparator]].

Sample usage can be found in the system for the *source* comparison where two comparators exist: [[W3C HTML5 Comparator|W3CHTML5Comparator]] and [[Source Comparator|SourceComparator]]. The example below shows its sample usage:

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

When the test defined as shown above is executed, collection of a page source is performed only once but the result of this collection is used twice during the comparison phase. First by [[Source Comparator|SourceComparator]] and then by [[W3C HTML5 Comparator|W3C HTML5 Comparator]].

##### Parameters

This is a set of key-value pairs allowing the user to pass some configuration and information to the comparator. Parameters for comparators can be divided into the following two groups:

* mandatory - parameters which comparison will be not possible without,
* optional - passing this parameter is not obligatory, usually this is the extension of some comparator functionality.

###### collectorName

There exists the special comparator property `collectorName` which is connected to the collector `name` property. By using the `collectorName` property combined with the collector `name` property the user can control which comparator instance compares results collected by a particular collector. See examples below:

```xml
...
<collect>
    <open/>
    <sleep duration="1000"/>
    <resolution width="1280" height="1024" name="desktop"/>
    <screen name="desktop"/>
    <resolution width="768" height="1024" name="tablet"/>
    <screen name="tablet"/>
    <resolution width="320" height="480" name="mobile"/>
    <screen name="mobile"/>
</collect>
<compare>
    <screen collectorName="mobile"/>
    <screen collectorName="tablet"/>
</compare>
...
```

The configuration above will trigger three screens collections (desktop, tablet and mobile) and two comparisons (mobile and tablet). The screenshot taken for *desktop* will not be compared.

```xml
...
<collect>
    <open/>
    <sleep duration="1000"/>
     <resolution width="1280" height="1024" name="desktop"/>
     <screen name="desktop"/>
     <resolution width="768" height="1024" name="tablet"/>
     <screen name="tablet"/>
     <resolution width="320" height="480" name="mobile"/>
     <screen name="mobile"/>
</collect>
<compare>
    <screen/>
</compare>
...
```

The configuration above will trigger three screens collections (desktop, tablet and mobile) and three comparisons (desktop, table, mobile).

```xml
...
<collect>
    <open/>
    <sleep duration="1000"/>
    <resolution width="1280" height="1024" name="desktop"/>
    <screen name="desktop"/>
    <resolution width="768" height="1024" name="tablet"/>
    <screen name="tablet"/>
    <resolution width="320" height="480" name="mobile"/>
    <screen name="mobile"/>
</collect>
<compare>
    <screen/>
    <screen collectorName="tablet"/>
</compare>
...
```

The configuration above will trigger three screens collections (desktop, tablet and mobile) and four comparisons (desktop, tablet, mobile and one additional for the tablet).

##### Definitions illustration

The following picture depicts definitions described earlier:

![Compare phase definitions](assets/diagrams/compare-phase-definitions.png)

where:

1. Consumed resource type,
2. Special property: collectorName,
3. Special property: comparator,
4. Module name (comparator).
