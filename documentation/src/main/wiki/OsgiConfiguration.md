#### OSGi Configuration

This section describes how to configure the AET OSGi services so that they could connect to the appropriate system components.

The services are configured through the Karaf Web Console which is hosted on Windows machine. Assuming that this machine's IP address is `192.168.0.2`, the Karaf console is available under following address: http://192.168.0.2:8181/system/console/configMgr.

##### Assumptions

The example configuration assumes the following:
* The IP address of Linux machine is `192.168.0.1`
* The IP address of Windows machine is `192.168.0.2`
* The Apache HTTP server serves Reports application under domain `http:\\aet-report`

##### Connections configuration

The diagram below shows which AET OSGi service should connect to which system component on the appropriate machine. On the diagram the arrows point from the AET services to the system components. The notes on the arrows contain the properties of each service which should be set and example values according to assumptions stated above.

![aet-osgi-configuration](assets/diagrams/aet-osgi-configuration.png)


There are more services that require configuration which are not present on the diagram above. These are described below.

##### Collectors and comparators configuration

 The services are **AET Collector Message Listener** and **AET Comparator Message Listener**. There must be at least one of each of those services configured. Below there are listed the properties of each of above mentioned services with required values.

###### AET Collector Message Listener

| Property name | Value |
| ------------- | ----- |
| Collector name | Has to be unique within Collector Message Listeners. |
| Consumer queue name | Fixed value `AET.collectorJobs` |
| Producer queue name | Fixed value `AET.collectorResults` |
| Embedded Proxy Server Port | Has to be unique within Collector Message Listeners. |

###### AET Comparator Message Listener
| Property name | Value |
| ------------- | ----- |
| Comparator name | Has to be unique within Comparator Message Listeners. |
| Consumer queue name | Fixed value `AET.comparatorJobs` |
| Producer queue name | Fixed value `AET.comparatorResults` |


##### Web Driver Factories configuration

The service is **Firefox Web Driver Factory**. Below are listed it's properties with required values.

###### Firefox Web Driver Factory

| Property name | Value |
| ------------- | ----- |
| Name | Fixed value `ff` |
| Path | Path to Firefox binary |
| Log file path | Path to Firefox log file |
| Selenium Grid URL | Url to Selenium Gid Hub. If blank tests are handled in a legacy way - without Selenium Grid proxy |

