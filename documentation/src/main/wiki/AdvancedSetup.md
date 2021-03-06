### Advanced Setup

This section describes advanced setup of AET using Linux and Windows. 

Please note that full list of required tools and its versions can be found in [[System Components|SystemComponents]] section.

![aet-setup-advanced](assets/diagrams/aet-setup-advanced-grid-scaled.png)

#### Linux Setup

For Linux environment setup, use the [AET Cookbook](https://github.com/wttech/aet-cookbook) which provides following services:
* Karaf with AET service running
* MongoDB as the database
* ActiveMQ as the message broker
* BrowserMob as the proxy server
* Selenium Grid Hub (that later Windows nodes will be attached to)
* Apache Server with the Report app

#### Windows Setup

**Prerequisites**
 * [Chrome browser](https://www.google.com/chrome/browser/desktop/) 
 * [ChromeDriver](https://sites.google.com/a/chromium.org/chromedriver/downloads) (version 2.40 or newer)
 * [Selenium Standalone Server](http://www.seleniumhq.org/download/) (version 3.41 or newer)

**Start Selenium Grid node**

Run following command to start selenium grid node process on windows machine:
```
java -Dwebdriver.chrome.driver="<path/to/chromedriver>" -jar <path/to/selenium-server-standalone.jar> -role node -hub http://<linux-ip>:4444/grid/register -browser "browserName=chrome,maxInstances=20" -maxSession 20
```
> Running the selenium grid node process as a Windows service is not recommended - it's causing some 
issues for driver which runs Chrome in headless mode. However this issue does not occur if you 
configure the node process as a Windows startup program.

**Optional Software**

On Windows you can also install [Baretail](https://www.baremetalsoft.com/baretail/) and [Notepad++](https://notepad-plus-plus.org/download/) for viewing logs and configuration files.

#### OSGi Configuration

This section describes how to configure the AET OSGi services so that they could connect to the appropriate system components.
The services are configured through the Karaf Web Console which is hosted on Linux machine. Assuming that this machine's IP address is `192.168.0.1`, 
the Karaf console is available under following address: http://192.168.0.1:8181/system/console/configMgr.

##### Connections configuration

Assuming that most of the system components are running on a single machine 
(e.g. provisioned by [AET Cookbook](https://github.com/wttech/aet-cookbook)) then those
components can work correctly either with default `localhost` configurations,
or with a configuration that points to Linux machine IP.
Those configurations are:
* ActiveMQ broker URL in `AET JMS Connection`
* ActiveMQ JMX endpoint UR in `Messages Manager Configuration`
* Mongo URI property in `AET MongoDB Client`
* Selenium Grid URL property in `AET Chrome WebDriver Factory`

OSGi configurations which need to be configured with environment-specific values are:
* `AET Rest Proxy Manager` - the `server` property needs to be set to IP address of the Linux machine. This address is used
by the Selenium Grid nodes and needs to be accessible for the Windows machine(s).
* `AET Report Application Configuration` - the `reportDomain` property should be set to a domain 
of Apache server which hosts the AET Report application - e.g. `http://aet-report`

##### Collectors and comparators configuration

The service is **AET Workers Listeners Service**.  
To enable proper working of AET instance, you should configure at least 1 collector and 1 comparator.

| Property name | Default value | Comment |
| ------------- | ----- | ----- |
| Number of collector instances | `5` | Might be overwritten by env variable `COLLECTORS_NO` |
| Collectors prefetch size | `1` | Read more [here](http://activemq.apache.org/what-is-the-prefetch-limit-for.html) |
| Number of comparator instances | `5` | Might be overwritten by env variable `COMPARATORS_NO` |
| Comparators prefetch size | `1` | Read more [here](http://activemq.apache.org/what-is-the-prefetch-limit-for.html) |

> **Important note**
> Number of collector instances should be the number of browsers available through all Selenium Grid Nodes.

##### Chrome options configuration

The `AET Chrome WebDriver Factory` component configuration (`chromeOptions` property) allows you to configure a list of options/arguments 
which will be passed to the Chrome browser binary. The default list of Chrome options is: `--disable-plugins`, `--headless`, `--hide-scrollbars`, `--disable-gpu`.
