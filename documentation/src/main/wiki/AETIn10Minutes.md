## AET in 10 Minutes

This is a quick guide showing how to setup the AET environment and run a sample test.

### Prerequisites
Before start make sure that you have enough memory on your machine (8 GB is minimum, 16 GB is recommended though).

You need to download and install the following software:
   * [VirtualBox 5.1.30](https://www.virtualbox.org/wiki/Download_Old_Builds_5_1)
   * [Vagrant 1.9.2](https://releases.hashicorp.com/vagrant/)
   * [ChefDK 1.1.16](https://downloads.chef.io/chef-dk/)
   * [Maven](https://maven.apache.org/download.cgi) (at least version 3.0.4)
   * [JDK 8](http://www.oracle.com/technetwork/pt/java/javase/downloads/jdk8-downloads-2133151.html)
   * [Chrome browser](https://www.google.com/chrome/browser/desktop/) to view reports

### Set Up Vagrant

Open a command prompt as the administrator and execute the following commands:
* `vagrant plugin install vagrant-omnibus`
* `vagrant plugin install vagrant-berkshelf`
* `vagrant plugin install vagrant-hostmanager`

Navigate to the `vagrant` module directory. Run `berks install` and then `vagrant up` to start the virtual machine. This process may take a few minutes.

### Set Up the Test

Create the file named `suite.xml` with the following content:

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

Then create another file named `pom.xml` with the following content:

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
        <aet.version>2.1.2</aet.version>
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

It does not need to be in the same directory as the `suite.xml` file.

### Run the Test

Once you have created both `suite.xml` and `pom.xml` files open a command prompt in the directory which contains the `pom.xml` file and execute the following command:

```
mvn aet:run -DtestSuite=full/path/to/suite.xml
```

Remember to provide the path to your `suite.xml` file.

### Check Results

Once the test run finishes there should be the `target` directory created inside the directory containing the `pom.xml` file. Inside the `target` directory you should find the `redirect.html` file. Open this file and the test report will show up in your web browser.

Congratulations! You have successfully created and run your first AET test.

### Build and Upload the Application
You need JDK 8 and Maven 3.3.1 or newer to build the AET application.
To build and upload the application use the following command in the application root:
```
mvn clean install -P upload
```

#### Upload Vagrant Prequisities:
In order to be able to deploy bundles to the Karaf instance define the location of the vagrant vm in your `setting.xml` file (`$USER_HOME/m2`):
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
