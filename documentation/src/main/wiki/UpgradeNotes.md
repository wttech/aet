# AET Upgrade Notes

If you are upgrading AET from the previous version, here are notes that will help you do all 
necessary configuration changes that were introduced in comparison to previously released AET version.

You may see all changes in the [Changelog](https://github.com/Cognifide/aet/blob/master/CHANGELOG.md).

## Unreleased

### BrowserMob Proxy server connection  
  The address of BrowserMob Proxy server is set to `localhost:8080` by default (in Karaf config) - 
  it's used by workers to connect to the proxy server. Previously, when tests were being run on local Firefox instances
  running on the same machine as the proxy server, there was no need to change the default config.
  Currently, when tests are executed on Selenium Grid nodes on different machines, the address of proxy 
  server must be configured  to an IP/host name which is accessible for the nodes. 
  It can be configured in `com.cognifide.aet.proxy.RestProxyManager.cfg` file - default config for Vagrant is:
  ```
  server=192.168.123.100
  port=8080
  ```
  