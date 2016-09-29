#### Operating Systems setup
![aet-setup-advanced](assets/diagrams/aet-setup-advanced.png)

This section describes advanced setup of AET using Linux and Windows. The main advantage of this approach is ability to run tests on Firefox on Windows, which is more reliable than Firefox on Linux.

Please note that full list of required tools and its versions can be found in [System Components](SystemComponents) section.

##### Linux Setup
1. Turn off Firewall. This may be achieved differently on various linux distribution, for example on CentOS `selinux` and `iptables` should be disabled.
2. Install MongoDB in version 3.2.3
3. Install JDK from Oracle (1.7)
4. Install ActiveMQ in version 5.13.1
    * Enable JMX for ActiveMQ with connector under port `11199`
    * Switch Persistence for ActiveMQ
    * Enable cleaning unused topic for ActiveMQ
5. Install Apache Server
    * Configure site for the following path: `/opt/aet/apache/aet_reports/current`
    
##### Windows Setup
1. Install Java 7 JDK and update JAVA_HOME environment variable.
2. Change default console resolution - install VNC server (e.g. http://www.tightvnc.com/), connect by VNC client to console and change resolution (min. 1024x768).
3. Turn off Windows Firewall (both, private and public network location settings).
4. Install Karaf in version 2.3.9
    * Update Apache Felix Framework to version 4.2.1
    * Install Karaf as a Windows service
    * Check if it's working under http://localhost:8181/system/console/ and credentials karaf/karaf.
5. Install Browsermob in version 2.0.0
    * Install Browsermob as a Windows service
    * Check if it's working under http://localhost:9272/proxy.
6. Install Firefox 38.6.0 ESR
    * Turn off automatic updates
7. Check the following connections between Windows and Linux:
    * MongoDB: `telnet ${LINUX_MACHINE_PRIVATE_IP} 27017`
    * ActiveMQ: `telnet ${LINUX_MACHINE_PRIVATE_IP} 61616`
    * ActiveMQ's JMX: `jconsole.exe ${LINUX_MACHINE_PRIVATE_IP}:11199`

##### AET deployment
Here's a description where to deploy all the artifacts.

| Artifact     | Environment     | Default folder                      |
| ------------ | --------------- | ----------------------------------- |
| bundles.zip  | Windows - Karaf | /deploy                             |
| features.zip | Windows - Karaf | /deploy                             |
| configs.zip  | Windows - Karaf | /etc                                |
| report.zip   | Linux - Apache  | /opt/aet/apache/aet_reports/current |