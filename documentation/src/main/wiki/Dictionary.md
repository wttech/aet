## Dictionary

*Active MQ*  
a *JMS (Java Message Service)* Server which is a basic communication channel between AET System components.

*AET*  
an open source testing tool developed by Cognifide.

*AET Core*  
a set of system modules that are crucial for the whole system to work. The AET system will not work properly without all core modules configured and running properly.

*AET Jobs*  
implementations of jobs that can perform a particular task (e.g. collect screenshots, compare sources, validate a page against *W3C HTML5*).

*AET Maven Plugin*  
a default client application for the AET system that is used to trigger the execution of the *Test Suite*.

*Amazon Web Services*  
cloud computing services where the AET environment can be deployed to.

*Apache Karaf*  
see *Karaf*.

*Artifact*  
usually used in the context of a small piece of data, the result of some operation (e.g. a collected screenshot or a list of *W3C HTML5* validation errors).

*AWS*  
see *Amazon Web Services*.

*Baseline*  
a snapshot of the page, stored in a file, against which future versions will be compared. It is called also a **Pattern**.

*Browsermob*  
a proxy server used by AET to collect some kinds of data from tested pages.

*Cleaner*  
a module responsible for removing old and unused artifacts from the database.

*Collector*  
a module responsible for gathering data necessary for its further processing (e.g. validation, comparison).

*Collection*  
the first phase of the AET service during which all specified data is collected (e.g. screenshots, page source, js errors). Once they are collected successfully, all collection results are saved in the database.

*Comparator*  
a module responsible for comparing data currently collected to its existing pattern or validating it against a set of defined rules.

*Comparison*  
the second phase of the AET service that performs the operation on the data. In some cases the data collected during the first phase is compared to patterns, in others special validation is performed (e.g. *W3C HTML5*). The second phase starts before the collection finishes - just the moment when required artifacts are collected and become ready to be compared (e.g. to compare two screenshots system does not have to wait until the source of a page is collected).

*Cookie Collector*  
a collector responsible for collecting cookies.

*Cookie Comparator*  
a comparator responsible for processing collected cookies.

*Cookie Modifier*  
a modifier that allows to modify cookies for a given page, i.e. to add or remove cookies.

*Data Filter*  
a module responsible for filtering the collected data before performing comparison e.g. filtering uninteresting js errors before the js errors check takes place.

*Data Storage*  
a database abstraction layer which contains versioned data (data grid).

*Extract Element Modifier*  
a modifier that allows to extract an element from the html source (collected by the Screen Collector) by providing the id attribute or the class attribute.

*Feature*  
a part of the AET system which covers a full testing case e.g. layout - this feature consists of the Screen Collector, the screen comparator and the layout reporter module.

*Firefox*  
a browser the AET tool makes use of, currently the version that is used is 38.6.0 ESR (en-US).

*Header Modifier*  
a modifier responsible for adding additional headers to a page.

*Hide Modifiers*  
a modifier responsible for hiding an element on a page that is unnecessary for a given test.

*Html-report*  
a basic report in a form of a HTML file.

*Java*  
a programming language that is used to develop the AET tool.

*Java Development Kit*  
see *JDK*.

*Java Management Extensions*  
see *JMX*.

*Java Message Service*  
see *JMS*.

*JavaScript*  
see *JS*.

*JDK*  
the *Java Development Kit* is a program development environment for developing Java applications.

*Jenkins*  
a continuous Integration (CI) server which is used as the user interface wrapper for the *AET Maven Plugin*.

*Jetty*  
a simple Http Server, used as a container for web applications.

*JMS*  
an acronym for the *Java Message Service*, simple message standard that allows application components to communicate with one another.

*JMX*  
*Java Management Extensions* (JMX) is a technology that is used to manage and monitor advanced interfaces of Java applications. In the AET tool it is used to manage *ActiveMQ*.

*JS*  
a dynamic programming language.

*JS Error*  
a JavaScript error that occurs in a script during its execution.

*JS Errors Collector*  
a collector responsible for collecting JavaScript errors occurring on a given page.

*JS Errors Comparator*  
a comparator responsible for processing the collected JavaScript error resource.

*JS Errors Filter*  
a filter that filters the results returned by the JS Errors Collector. It removes matched JavaScript errors from reports.

*JUnit*  
a simple framework allowing to develop repeatable tests. It is an instance of the xUnit architecture for unit testing frameworks. More information about it can be found at:  http://junit.org/.

*Karaf*  
in fact *Apache Karaf* is an OSGi container that provides a basic configuration for existing OSGi implementations (e.g. Apache Felix).

*Layout Comparator*  
a comparator responsible for comparing a collected screenshot of page to its pattern.

*Login Modifier*  
a modifier that allows to log in into the application and access secured sites.

*Maven*  
a software project management and comprehension tool. It used as a base for the *AET Maven Plugin*.

*Modifier*  
a module responsible for converting the target before the data collection process is performed e.g. modifying a requested header, adding a new cookie, hiding a visible element.

*MongoDB*  
an open-source cross-platform document-oriented database that the AET tool makes use of for data storage and management. MongoDB is developed by MongoDB Inc.

*Open*  
A module that is a special operand for the Collect Phase.

*OSGi*  
a modular system and services platform for Java. It is used as an application environment for AET Java components.

*Pattern*  
a sample model of data. Collection results are compared to their patterns to discover potential differences.

*pom.xml*  
a Maven tool configuration file that contains information about the project and configuration details used by Maven to build the project.

*Rebasing*  
an operation changing the existing pattern to the current result.

*Regression testing*  
this is a type of software testing that seeks to uncover new software bugs, or regressions, in existing functional and non-functional areas of a system. It is especially useful after changes such as enhancements, patches or configuration changes, have been made.

*Remove Lines Data Modifier*  
a modifier that allows to remove lines from the source (data or pattern) that a given page is compared to.

*Remove Nodes Data Modifier*  
a modifier that allows to delete some node(s) from a html tree. Node(s) are defined by the xpath selector.

*Report (Web application)*  
a web application for viewing / browsing AET test results. (At the moment the Google Chrome browser is supported).

*Representational State Transfer API*  
see *Rest API*.

*Resolution Modifier*  
a modifier responsible for changing the size of the browser screen.

*Resource type*  
a unique name for the resource produced by the collector and consumed by the comparator.

*Rest API*  
a Representational State Transfer API for the data stored in the AET Database. It enables the user to browse the data and artifacts stored after a run of the *Test Suite* was completed.

*Runner*  
a unit responsible for the communication with the client and dispatching processing among workers.

*SCM repository*  
a data structure storing metadata for a set of files that is managed by a source control management (SCM) system responsible for managing changes in files. The most popular examples of SCM systems are Git (http://git-scm.com/) and SVN (https://subversion.apache.org/).

*Screen Collector*  
a collector responsible for collecting a screenshot of the page under a given URL.

*Selenium*  
a portable software testing framework for web applications. At the moment AET makes use of Selenium 3.8.1.

*Selenium Driver*  
a test tool that allows to perform specific actions in a browser environment (e.g. take a screenshot of a page).

*Sleep Modifier*  
a modifier responsible for ceasing the execution of a given test temporarily. It causes a current thread to sleep.

*Source Collector*  
a collector responsible for collecting the source of a page under a given URL. Unlike other collectors the *Source Collector* does not use *Web Driver*. It connects directly to a web server.

*Source Comparator*  
a comparator responsible for comparing a collected page source to its pattern.

*Status Code*  
a response code for the resource request. For a detailed list of codes please refer to the Hypertext Transfer Protocol documentation at: http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html.

*Status Codes Collector*  
a collector responsible for collecting status codes for links to resources on a page under a given URL.

*Status Codes Comparator*  
a comparator responsible for processing collected *Status Codes*.

*Step*  
a single operation performed on the url defined in the `<collect>` phase of the suite.

*Test*  
a definition of logical set of *Test Cases* performed on a set of URLs.

*Test Suite*  
a set of *Tests* (at least one) finished with the *Report*.

*Test Case*  
a single URL *Test* against a feature, e.g. a *W3C HTML* page test, a screenshot for the resolution 800x600 test.

*Thresholds*  
a feature allowing to declare a Jenkins build as ‘success’, ‘unstable’ or ‘failed’ depending on the number of *Tests* that failed or were skipped.

*Wait For Page Loaded Modifier*  
a modifier that waits until a page is loaded or a fixed amount of time is up.

*Web Console*  
the OSGi console installed on Apache Karaf. By default it is accessible via a browser: http://localhost:8181/system/console/configMgr. The default user/password are: karaf/karaf.

*Worker*  
a single processing unit that can perform a defined amount of tasks (e.g. collect a screenshot, compare a source).

*W3C HTML5 Comparator*  
a comparator responsible for validating a collected page source against *W3C HTML5* standards.

*xunit-report*  
a *Report* that visualizes risks on the Jenkins job board and that contains information about the number of performed tests and the number of failures (potential threats).
