![Automated Exploratory Tests](misc/img/logo.png)

[![Build Status](https://travis-ci.org/Cognifide/aet.svg?branch=master)](https://travis-ci.org/Cognifide/aet)

# Automated Exploratory Tests
AET (acronym formed from Automated Exploratory Testing) is a system that detects changes on web sites.
AET is designed as a flexible system that can be adapted and tailored to the regression requirements of a given project.
The tool has been developed to aid front end client side layout regression testing of websites or portfolios. In essence assessing the impact or change of a website from one snapshot to the next.

## Features

* Layout (full page screenshots) Comparison, including:
    * Hiding Page Items by xpath,
    * Changing screen resolution (width/height setup),
* W3C Compliance,
* JS Errors check,
* Status Codes check (e.g. 404 references for non existing resources),
* Source Code Comparison,
* Accessibility check,
* Cookies check,
* Integration with CI Tools (Jenkins, Bamboo).

## Modules
Please see details in README files for each module individually.

##Setup
Please refer to the [Setup Guide](https://github.com/Cognifide/aet/wiki/BasicSetup) in the documentation for an overview on how to configure AET.

### Maven
#### Prequisities:

In order to be able to deploy bundles
to Karaf instance define vagrant vm location
in your setting.xml file (`$USER_HOME/m2`):
```
<server>
  <id>aet-vagrant-instance</id>
  <username>developer</username>
  <password>developer</password>
  <configuration>
    <sshExecutable>plink</sshExecutable>
    <scpExecutable>pscp</scpExecutable>
  </configuration>
</server>
```

#### Command for upload
```
mvn clean install -P upload
```

## Non-application directories:
###sanity-tests

Separate sub-project used for sanity/functional tests.

## References
* [AET Documentation and User Guide](https://github.com/Cognifide/aet/wiki)
