## AET In 10 Minutes

This is a quick guide showing how to setup AET environment and run an example test.

### Prerequisites
Before start make sure that you have enough memory on your machine (8 GB is minimum, 16 GB recommended though).

You need to download and install following software:
* [VirtualBox 5.0](https://www.virtualbox.org/wiki/Downloads)
* [Vagrant 1.8.1](https://www.vagrantup.com/downloads.html)
* [ChefDK 0.11.0](https://downloads.chef.io/chef-dk/)
* [Maven](https://maven.apache.org/download.cgi) (at least version 3.0.4)

### Vagrant setup

Open command prompt as an administrator and execute the following commands:
* `vagrant plugin install vagrant-omnibus`
* `vagrant plugin install vagrant-berkshelf`
* `vagrant plugin install vagrant-hostmanager`

Navigate to the `vagrant` module directory. Run `berks install` and then `vagrant up` to start virtual machine. This process may take a few minutes.

### Test setup

Create file named `suite.xml` with following content:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="company" project="project">
    <test name="first-test" useProxy="rest">
        <collect>
            <open/>
            <resolution width="800" height="600" />
            <sleep duration="1500"/>
            <screen/>
            <source/>
            <status-codes/>
            <js-errors/>
        </collect>
        <compare xmlns="http://www.cognifide.com/aet/compare/">
            <screen comparator="layout"/>
            <source comparator="w3c-html5"/>
            <status-codes filterRange="400,600"/>
            <js-errors>
                <js-errors-filter source="http://w.iplsc.com/external/jquery/jquery-1.8.3.js" line="2" />
            </js-errors>
        </compare>
        <urls>
            <url href="https://en.wikipedia.org/wiki/Main_Page"/>
        </urls>
    </test>
</suite>
```

Then create another file named `pom.xml` with following content:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>test-group</groupId>
    <artifactId>test-project</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>

    <name>Test project</name>
    <url>http://www.example.com</url>

    <properties>
        <aet.version>1.4.3</aet.version>
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

It does not need to be in the same directory as `suite.xml` file.

### Run test

Once you have created both `suite.xml` and `pom.xml` files open command prompt in the directory which contains `pom.xml` file and execute following command:

```
mvn aet:run -DtestSuite=full/path/to/suite.xml
```

Remember to provide path to your `suite.xml` file.

### Check results

Once the test run finishes there should be `target` directory created inside a dicrectory containing the `pom.xml` file. Inside the `target` directory you should find `redirect.html` file. Open this file and the test report will show up in your web browser.

Congratulations! You have successfully created and run your first AET test.
