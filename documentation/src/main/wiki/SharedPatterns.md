## Shared Patterns

When running AET suite it is possible to use patterns from different suite. Example use case of this feature is when
you have a `stable` environment where you collect the patterns and then check on `develop` environments what changes were done with new features/fixes.
There is very simple and important assumption when using shared pattern feature:

* your suite and `master` suite must have the same structure.

This mean that your suite have the same tests [[Suite Structure|SuiteStructure#test]] (`name` parameter is important here).
It is also possible to share patterns only within same project and company (this mean, that `company` and `project` parameters
should have the same value as in `master` suite).

## Using shared patterns
Let's consider following suite as `master` suite:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="master" company="company" project="project">
    <test name="first-test">
        <collect>
            <open/>
            <resolution width="800" height="600" />
            <sleep duration="1500"/>
            <screen/>
        </collect>
        <compare>
            <screen comparator="layout"/>
        </compare>
        <urls>
            <url href="http://www.cognifide.com/"/>
        </urls>
    </test>
    <test name="second-test">
        <collect>
            <open/>
            <resolution width="800" height="600" />
            <sleep duration="1500"/>
            <screen/>
        </collect>
        <compare>
            <screen comparator="layout"/>
        </compare>
        <urls>
            <url href="https://www.google.com/"/>
        </urls>
    </test>
</suite>
```

When you define your own suite, you should consider the same structure (order is not important). However,
you may use different set of [[Modifiers|Modifiers]]:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="green" company="company" project="project">
    <test name="first-test">
        <collect>
            <open/>
            <resolution width="800" height="600" />
            <executejavascript cmd="document.body.style.background = 'green';"/>
            <sleep duration="1500"/>
            <screen/>
        </collect>
        <compare>
            <screen comparator="layout"/>
        </compare>
        <urls>
            <url href="http://www.cognifide.com/"/>
        </urls>
    </test>
    <test name="second-test">
        <collect>
            <open/>
            <resolution width="800" height="600" />
            <executejavascript cmd="document.body.style.background = 'green';"/>
            <sleep duration="1500"/>
            <screen/>
        </collect>
        <compare>
            <screen comparator="layout"/>
        </compare>
        <urls>
            <url href="https://www.google.com/"/>
        </urls>
    </test>
</suite>
```

When you run `green` suite use [[following command|ClientApplication#parameters]] to use suite pattern from **master** suite execution.

`mvn aet:run -DtestSuite=green.xml -DpatternSuite=master`

This option will enforce AET to use patterns from latest version of `master` suite. Alternatively, if you want to use patterns from a specific version (i.e. correlation ID) of `master` suite, use:

`mvn aet:run -DtestSuite=green.xml -Dpattern=company-project-master-1495191612345`

**Remember that `master` suite must be run before running `green` suite with `pattern` or `patternSuite` option.**
 In other case, running `green` suite will be treated as running it for the first time.
