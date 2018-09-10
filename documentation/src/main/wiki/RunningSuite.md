## Running Suite

To schedule AET suite execution request the [[AET Web API endpoint `/suite`|TestExecutor#run-suite]]
with HTTP POST request that contains suite XML file content in the `suite` param.
This action will trigger AET suite processing and will return information on how to track suite progress.

AET provides 2 tools to schedule suite execution:
- [[Client Scripts|ClientScripts]]
- [[Client Maven Plugin|ClientApplication]]
however, you may use your own tool or just a simple `curl` to schedule the suite, e.g.:
```
curl -F "suite=@suite.xml" "http://aet-instance.com/suite"
```
Assuming your test executor instance is available at `aet-instance.com` domain.

### Running suite with bash script

#### Requirements
* Download the AET [shell script](https://github.com/Cognifide/aet/blob/master/client/client-scripts/aet.sh)
  (see required prerequisites in [README](https://github.com/Cognifide/aet/blob/master/client/client-scripts/README.md))
* Well-formed and valid xml test suite file available
(described with details in [[Defining Suite|DefiningSuite]] chapter)

#### Running from command line
Running the AET suite with aet bash script from the command line can be
done by invoking following command in the directory where the `aet.sh`
file has been saved:

```
./aet.sh http://localhost:8181 my-suite.xml
```

The first param is the [[Test Executor|TestExecutor]] endpoint address and the second is
the suite XML file. You can read more about the client script [[here|ClientScripts#usage]].

During test suite processing there will be information on its progress displayed
in the console. It reflects how many artifacts were currently collected, compared and reported.
When processing is finished the information about the processing status:

> Suite processing finished

Read more about tracking progress in the [[Tracking Progress|TrackingProgress]] section.

### Running suite with *aet-maven-plugin*

**Important note**
**`aet-maven-plugin` is deprecated and will be no longer supported. Expect it will be removed soon.
Please use [[client script|ClientScripts]] instead or simply communicate with AET Web API to schedule your suite.**

#### Requirements
* Maven installed (recommended version - 3.0.4).
* Well-formed and valid xml test suite file available (described with
details in [[Defining Suite|DefiningSuite]] chapter),
* [`pom.xml`](#pomxml) file with defined *aet-maven-plugin* configuration (described below).

#### Running from command line

Running the AET suite with *AET Maven plugin* from the command line can be
done by invoking a maven command in the directory where the `pom.xml`
file has been defined:

```
mvn aet:run -DtestSuite=FULL_PATH_TO_TEST_SUITE_XML_FILE
```

The `testSuite` parameter is the path to the xml suite configuration file.

During test suite processing there will be information on its progress displayed
in the console. It reflects how many artifacts were currently collected, compared and reported.
When processing is finished the information about the processing status
- `BUILD SUCCESS` or `BUILD FAILURE` - is displayed in the console.

When the test run completes, the resulting report files can be found in
the maven run `target` folder.

Check [[Client Application|ClientApplication]] for more details about `aet-maven-plugin`.

#### pom.xml

This file (`pom.xml`) is a *Maven* tool configuration file that contains
information about the project and configuration details used by *Maven* to build the project.

Running AET suite requires creating and configuring such a file. The File
presented below might be used as a template for setup AET suite runs:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>{PROJECT-GROUP}</groupId>
    <artifactId>{PROJECT-NAME}</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>

    <name>Tests</name>
    <url>http://www.example.com</url>

    <properties>
        <aet.version>{PLUGIN-VERSION}</aet.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <build>
        <plugins>
            <plugin>
                <groupId>com.cognifide.aet</groupId>
                <artifactId>aet-maven-plugin</artifactId>
                <version>${aet.version}</version>
            </plugin>
        </plugins>
    </build>
</project>
```

User should configure three variables before proceeding to the next steps:
* `{PROJECT-GROUP}` which is a group the project belongs to. It should
follow the package name rules, i.e. it is reversed domain name controlled
by project owner and consists of lowercase letters and dots,
    * example: `com.example.test`
* `{PROJECT-NAME}` which is this build identifier for *Maven* tool.
It should consist only of lowercase letters and `-` characters,
    * example: `aet-sanity-test`
* `{PLUGIN-VERSION}` which should be set to the *aet-maven-plugin* version currently used
    * example: `2.1.6`

Having the version as the maven property (`${aet.version}`) enables
defining this parameter from the command line later, e.g. `-Daet.version=2.1.6`.

### Tips and recommendations

Generally it is a good idea to create a separate **SCM repository**
(e.g. *GIT* or *SVN*) for AET suites. This will enable running AET suites using Jenkins easily.
