![Cognifide logo](http://cognifide.github.io/images/cognifide-logo.png)

# AET
<p align="center">
  <img src="https://github.com/Cognifide/aet/blob/master/misc/img/aet-logo-black.png?raw=true"
         alt="AET Logo"/>
</p>

## AET Client - shell script

Script that allows executing AET tests.

### Usage

```
AET Test executor

Usage:

	./aet.sh <endpoint> [<suite_file_name>] [options]

Options:
	-d --domain <DOMAIN>                 - Override domain attribute defined in suite file
	-c --correlationId <CORRELATION_ID>  - Set id of patterns to run test against
	-p --patternSuite <SUITE_NAME>       - Set the suite name to run test against its latest pattern (only used if -c is not set)
	-i --interval <POLL_INTERVAL>        - Set interval in seconds for polling suite status. Default interval : 1 sec
	-w --waitForUnlock <TIMEOUT>         - Set timeout for the script to wait for unlocked suite. Default timeout: 0 sec
	-v --verbose                         - Make it more descriptive
```

### Prerequisites

In order to run properly, following commands need to be available on PATH of the environment the script is executed on:
* `curl` - used to make actual request against [[Test Executor API|TestExecutor]] and also to download `xUnit.xml` file after test completion. Comes preinstalled on most Unix systems. [Download link](https://curl.haxx.se/download.html)
* `jq` - used to parse JSON responses from [[Test Executor API|TestExecutor]]. [Download link](https://stedolan.github.io/jq/download/)
* `xmllint` - used to retirieve failure information from downloaded `xUnit.xml`. Comes preinstalled on most Unix systems. For Windows installation instructions, refer to [Jo Jordan's post](http://flowingmotion.jojordan.org/2011/10/08/3-steps-to-download-xmllint/).
