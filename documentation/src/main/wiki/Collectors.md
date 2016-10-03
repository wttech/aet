### Collectors

The collector is a module which is responsible for collecting data from tested pages.

Each collector presented in the section below consists of the following two elements:

* module name (produced resource type),
* parameters.

##### Module name (produced resource type)

This name is a unique identifier of a system functionality. Each collector has its unique name which should also be unique for all modules in the *[[collect|SuiteStructure#collect]]* phase. This is always the name of a tag definition for the collector.

The AET System does not know what work will be performed by the collector when it reads the suite definition. The only thing that is known is **module name**. The system will recognize which collector should be called by matching a definition from the *[[collect|SuiteStructure#collect]]* phase with the name registered in the system. When no collector with a defined name is found in system, a system exception will occur and the test will be not performed. This solution enables adding new features to the system with no system downtime (just by installing a new feature bundle).

Each collector produces a resource of a defined type. This type can be later recognized by [[comparators |Comparators]] and [[data filters|DataFilters]]. Two collectors can't produce data with the same resource type. **A produced resource type is always equal to the collector module name.**

##### Parameters

This is a set of key-value pairs with the use of which the user can pass some configuration and information to the collector. Parameters for collectors are usually not mandatory - passing this parameter is not obligatory, usually this is the extension of some collector functionality. However, there is one special property: **name**. The collector with a name set can be treated in a special way by [[comparators|Comparators]] (some comparators may look only for collection results from the collectors with a specific name) e.g.:

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
</compare>
...
```

During the collect phase, three screenshot with different resolutions will be taken and saved in the database. However, only one of them (*mobile*) will be compared to the pattern during the comparison phase and presented in the report (under the "*Layout For Mobile*" section).

##### Definitions illustration

The following picture depicts the elements described before:

![Collect phase definitions](assets/diagrams/collect-phase-definitions.png)

where:

1. Module name (produced resource type),
2. Parameters,
3. Special collector property: name,
4. Special comparator property: collectorName.
