### Client Application

**Important note**
**`aet-maven-plugin` is deprecated and will be no longer supported. Expect it will be removed soon.
Please use [[client script|ClientScripts]] instead or simply communicate with AET Web API to schedule your suite.**

The AET client application comes with a sort of a plugin to the *Maven* build automation tool -- `aet-maven-plugin`. This plugin connects to the AET application in order to run the specified test suite.

#### Usage

In order to run tests you must create a *Maven* project and include `aet-maven-plugin` in the `pom.xml` file in the following way:

```xml
<plugin>
    <groupId>com.cognifide.aet</groupId>
    <artifactId>aet-maven-plugin</artifactId>
    <version>${aet.version}</version>
</plugin>
```

The plugin version is here specified as the *Maven* property `aet.version`. This allows to define the AET plugin version from the command line using the `-Daet.version=x.y.z` option, where `x.y.z` is the version number.

`aet-maven-plugin` defines the `aet:run` *Maven* goal which needs to be executed in order to run the test suite.

So if you want to run AET tests you must execute the following command in the directory where the `pom.xml` file has been defined:
```
mvn aet:run -DtestSuite=FULL_PATH_TO_TEST_SUITE
```

##### Parameters

`aet-maven-plugin` has a few parameters that configure its behaviour. The `testSuite` parameter already mentioned defines the path to the xml suite configuration file. All the parameters are described below:

| Parameter | Description | Default Value | Mandatory |
| --------- | ----------- | ------------- | --------- |
| `testSuite` | The full path to the suite definition file (at least a file name with an extension, e.g. `testSuite.xml`).| suite.xml | no |
| `endpointDomain` | the URL to the main AET domain | http://localhost:8181 | no |
| `name` | Overrides the *name* parameter value from the test suite definition. | - | no |
| `domain` | Overrides the *domain* parameter value from the test suite definition. | - | no |
| `timeout` | Milliseconds to detect the timeout since the last status received from AET. This is useful to abort the test run if there is no activity for a long time. | 300000 (5 minutes) | no |
| `pattern` | Correlation ID of suite that will be used as patterns source. Identical structure of pattern and current suites is assumed. If you want to use latest patterns of given suite without specifying full correlation ID, use `patternSuite` parameter instead. | - | no |
| `patternSuite` | Name of the suite, whose latest version will be used as patterns source. Identical structure of pattern and current suites is assumed. This parameter is ignored if `pattern` parameter is already specified. | - | no |
| `xUnit` | The flag that indicates whether the xUnit report should be generated and downloaded or not.| false | no |

##### Test results

The result of successful command line execution of the AET test suite is the `redirect.html` file and, if the `xUnit` parameter is set to `true` (the suite is executed with the `-DxUnit=true` flag), also the `xunit-report.xml` file. Both files are generated in the `target` folder in the directory where the `pom.xml` file has been defined.

#### Examples

There are a few sample commands with a different parameter set below.

By default, `aet-maven-plugin` connects to the AET application using the port 8181 of localhost. If your AET application has been deployed to some other machine e.g. under the URL `http://aet.example.com`, you can tell the plugin to connect to that machine in the following way:
```
mvn aet:run -DtestSuite=suite.xml -DendpointDomain=http://aet.example.com
```

If all URLs in your test suite point to a single domain, you can specify it so it is no longer needed to do it again in the suite file. Let's assume you'd like to test some subpages of the English version of Wikipedia. Then you can specify the target domain in the following way:
```
mvn aet:run -DtestSuite=suite.xml -Ddomain=https://en.wikipedia.org
```


If the same suite is being used to test multiple environments, you can specify the name correlated with the execution output. When the patterns *are not being shared* across multiple environments but the suite definition is the same, you may override the name, that is typically placed in the suite XML. For example:
```
mvn aet:run -DtestSuite=suite.xml -Dname=env-integration-suite
```


If you want the test run to fail if its run takes too much time, you can specify the timeout (in milliseconds) in the following way:
```
mvn aet:run -DtestSuite=suite.xml -Dtimeout=2000
```

If you want to generate the xUnit report to have test results displayed in Jenkins, you can set it in the following way:
```
mvn aet:run -DtestSuite=suite.xml -DxUnit=true
```
