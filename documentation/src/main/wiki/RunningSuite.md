## Running Suite

Currently, running an AET suite requires using *aet-maven-plugin* which is an AET client application.

### Requirements
* Maven installed (recommended version - 3.0.4).
* Proper version of AET Maven plugin installed.
* Well-formed and valid xml test suite file available (described with details in [[Defining Suite|DefiningSuite]] chapter),
* `pom.xml` file with defined *aet-maven-plugin* configuration (described below).

#### pom.xml

This file (`pom.xml`) is a *Maven* tool configuration file that contains information about the project and configuration details used by *Maven* to build the project.

Running AET suite requires creating and configuring such a file. The File presented below might be used as a template for setup AET suite runs:

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
* `{PROJECT-GROUP}` which is a group the project belongs to. It should follow the package name rules, i.e. it is reversed domain name controlled by project owner and consists of lowercase letters and dots,
    * example: `com.example.test`
* `{PROJECT-NAME}` which is this build identifier for *Maven* tool. It should consist only of lowercase letters and `-` characters,
    * example: `aet-sanity-test`
* `{PLUGIN-VERSION}` which should be set to the *aet-maven-plugin* version currently used
    * example: `1.0.0`

Having the version as the maven property (`${aet.version}`) enables defining this parameter from the command line later, e.g. `-Daet.version=1.1.0`.

### Running suite from command line
Running the AET suite with *AET Maven plugin* from the command line can be done by invoking a maven command in the directory where the `pom.xml` file has been defined:
```
mvn aet:run -DtestSuite=FULL_PATH_TO_TEST_SUITE
```

The `testSuite` parameter is the path to the xml suite configuration file.

During test suite processing there will be information on its progress displayed in the console. It reflects how many artifacts were currently collected, compared and reported. When processing is finished the information about the processing status - `BUILD SUCCESS` or `BUILD FAILURE` - is displayed in the console.

When the test run completes, the resulting report files can be found in the maven run `target` folder.

Check [[Client Application|ClientApplication]] for more details about `aet-maven-plugin`.

### Tips and recommendations

Generally it is a good idea to create a separate **SCM repository** (e.g. *GIT* or *SVN*) for AET suites. This will enable running AET suites using Jenkins easily.
