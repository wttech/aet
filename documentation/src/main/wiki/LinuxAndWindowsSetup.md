#### Operating Systems setup
![aet-setup-advanced](assets/diagrams/aet-setup-advanced.png)

This section describes advanced setup of AET using Linux and Windows. The main advantage of this approach is ability to run tests on Firefox on Windows, which is more reliable than Firefox on Linux.

Please note that full list of required tools and its versions can be found in [System Components](SystemComponents) section.

##### Linux Setup
1. Turn off Firewall. This may be achieved differently on various linux distribution, for example on CentOS `selinux` and `iptables` should be disabled.
2. Install MongoDB in version 3.2.3
3. Install JDK (1.7) from Oracle or OpenJDK
4. Install ActiveMQ in version 5.13.1
    * Enable JMX for ActiveMQ with connector under port `11199`
    * Switch Persistence for ActiveMQ
    * Enable cleaning unused topic for ActiveMQ
5. Install Apache Server
    * Configure site for the following path: `/opt/aet/apache/aet_reports/current`
    
##### Windows Setup
Windows Setup.
1. [Follow to link](https://github.com/Cognifide/aet/releases) and download all packages to your local instance.
2. Turn off Windows Firewall (both, private and public network location settings).
3. Install JDK 8 and set JAVA_HOME variable: C:\Program Files\Java\jdkYouVersion.
4. Create dir under C:\ for karaf files. Preferably C:\aet\.
5. Unpack [Apache Karaf 4.1.4](https://archive.apache.org/dist/karaf/4.1.4/apache-karaf-4.1.4.zip) to C:\aet\karaf.
7. Unpack “configs.zip” to PATH C:\aet\aet_configs\current.
8. Unpack “features.zip” to PATH C:\aet\aet_features\current.
9. Unpack “bundles.zip” to PATH C:\aet\aet_bundles\current.
10. Provide *org.apache.felix.fileinstall-configs.cfg* and *org.apache.felix.fileinstall-features.cfg* files for Karaf. The content of these files should be like `felix.fileinstall.dir=./../aet_configs/current` and `felix.fileinstall.dir=./../aet_features/current`.
11. Open CMD as Administrator and Run C:\aet\karaf\bin\shell.bat.
    * Execute “wrapper:install” - it should generate new files under `/bin` dir
12. Edit C:\aet\karaf\etc\karaf-wrapper.conf.
    * set set.default.KARAF_DATA to C:\aet\karaf\data
    * modify and uncomment wrapper.java.initmemory= (set to 1024)
    * modify wrapper.java.maxmemory (set to 4096)
13. Unpack nssm-2.24 under C:\.
    * run C:\nssm-2.24\win64\nssm.exe install “Apache Karaf”
    * NSSM prompt should display. Add “C:\aet\karaf\bin\karaf.bat” as Application Path. Startup dir should be filled up automaticly to “C:\aet\karaf\bin”
14. Unpack “browsermob-proxy-2.0.0-bin” to PATH “C:\aet”.
    * run C:\nssm-2.24\win64\nssm.exe install “BrowserMob Proxy”
    * NSSM prompt should display. Add “C:\aet\browsermob-proxy-2.0.0\bin\browsermob-proxy.bat” as Application Path. Startup dir should be filled up automaticly to “C:\aet\browsermob-proxy-2.0.0\bin”
15. Install Firefox Setup 38.6.0esr.
    * Choose 'Custom Install' and do not install maintenance service - this is very important
    * Open Firefox and navigate to Options => Advanced => Update => Select “Never check for updates”
16. Reboot machine -> CMD -> shutdown –r /t 0.
17. Configure your Karaf machine.
    * Open Karaf connection. I.e  localhost:8181/system/console/configMgr
    * Edit " AET Default JMS Connection” 
         * Set connection to JMS 
         * failover:tcp:// <IP-of_ActiveMQ>:61616. Paste this to brokerURL and exchange localhost for proper IP address
    * Set username and password, i.e. admin/admin
    * Edit “AET Messages Manager”
    * Set “ActiveMQ JMX” 
         * service:jmx:rmi:///jndi/rmi:// <IP-of_ActiveMQ>:11199/jmxrmi. Paste this to ActiveMQ JMX endpoint URL and exchange localhost for proper IP address
    * Edit “AET MongoDB Client”.
         * Set “MongoURI”
              * mongodb:// <IP-of_MongoDB>. Paste this to MongoURI and exchange localhost for proper IP address where mongoDB was installed
    * Edit “AET Report Application Configuration”
         * Set “Report application domain”
              * https://aet-example.cognifide.com/report. 
18. Run example/test job.



##### AET deployment
Here's a description where to deploy all the artifacts.

| Artifact     | Environment     | Default folder                      |
| ------------ | --------------- | ----------------------------------- |
| apache-karaf-4.1.4.zip  | Windows - Karaf | C:\aet\karaf             |
| browsermob-proxy-2.0.0-bin.zip | Windows - Karaf | C:\aet\browsermob-proxy-2.0.0        |
| bundles.zip  | Windows - Karaf | C:\aet\aet_bundles\current                             |
| configs.zip  | Windows - Karaf | C:\aet\aet_configs\current                                |
| features.zip | Windows - Karaf | C:\aet\aet_features\current                             |
| Firefox Setup 38.6.0esr | Windows - Karaf | C:\Program Files (x86)\Mozilla Firefox      |
| jdk-8u162-windows-x64 | Windows - Karaf | C:\Program Files\Java\jdk1.8.0_162              |
| nssm-2.24.zip | Windows - Karaf | C:\nssm-2.24                       |
| report.zip   | Linux - Apache  | /opt/aet/apache/aet_reports/current |

##### Optional Software

You can also install [Baretail](https://www.baremetalsoft.com/baretail/) and [Notepad++](https://notepad-plus-plus.org/download/) for viewing logs and configuration files.
