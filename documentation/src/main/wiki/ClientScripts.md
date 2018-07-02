### Client Scripts

Apart from the [[client application|ClientApplication]] in the form of a maven plugin, AET offers a [shell script](https://github.com/Cognifide/aet/blob/master/client/client-scripts/aet.sh)
to execute tests. In order to trigger tests, the shell script provided utilizes the exposed [[Test Executor API|TestExecutor]].

### Usage

```
AET Test executor

Usage:

	./aet.sh <endpoint> [<suite_file_name>] [options]

Options:
	-d --domain <DOMAIN>                 - Override domain attribute defined in suite file
	-n --name <SUITE_NAME>               - Override name attribute defined in suite file
	-c --correlationId <CORRELATION_ID>  - Set id of patterns to run test against
	-i --interval <POLL_INTERVAL>        - Set interval in seconds for polling suite status. Default interval : 1 sec
	-w --waitForUnlock <TIMEOUT>         - Set timeout for the script to wait for unlocked suite. Default timeout: 0 sec
	-v --verbose                         - Make it more descriptive
```

The shell script needs one parameter to trigger tests. Set the `endpoint` parameter to point the script to a domain which exposes the [[Test Executor API|TestExecutor]].

#### Parameters

The shell script can be run with parameters which are described below:

| Parameter | Description | Default Value | Mandatory |
| --------- | ----------- | ------------- | --------- |
| `endpointDomain` | an AET domain which exposes the [[Test Executor API\|TestExecutor]] | - | yes |
| `suite_file_name` | a full path to the suite definition file (at least a file name with an extension is required, e.g. `testSuite.xml`). | suite.xml | no |
| `-d \| --domain <DOMAIN>` | overrides the _domain_ parameter value from the test suite definition. | - | no |
| `-n \| --name <SUITE_NAME>` | overrides the _name_ parameter value from the test suite definition. | - | no |
| `-c \| --correlationId <CORRELATION_ID>` | Id of suite that will be used as patterns source. Identical structure of pattern and current suites is assumed. | - | no |
| `-i \| --interval <POLL_INTERVAL>` | an interval of polling the status of the test currently executed. (Unit: seconds) | 1 | no |
| `-w \| --waitForUnlock <TIMEOUT>` | specify the time that the script will wait for the suite to be unlocked.  (Unit: seconds) | 0 | no |
| `-v \| --verbose` | if provided additional information about execution is printed out | - | no |

#### Exit status

After the test is completed the script downloads the xUnit report to extract the number of test failures. The number of failures is then set as the exit status.

#### Artifacts

During test execution the script creates two files:
* `xUnit.xml` - this file is downloaded after the test is processed, to extract information on any failures.
* `redirect.html` - this file is created to help reach the test result by redirecting to test results URL after opening it.

#### Prerequisites

In order to run properly, following commands need to be available on the PATH of the environment the script is executed on:
* `curl` - used to make an actual request against the [[Test Executor API|TestExecutor]] and also to download the `xUnit.xml` file after the test has reached completion. It comes preinstalled on most Unix systems. [Download link](https://curl.haxx.se/download.html)
* `jq` - used to parse JSON responses from the [[Test Executor API|TestExecutor]]. [Download link](https://stedolan.github.io/jq/download/)
* `xmllint` - used to retrieve failure information from the downloaded `xUnit.xml`. It comes preinstalled on most Unix systems. For Windows installation instructions, refer to [Jo Jordan's post](http://flowingmotion.jojordan.org/2011/10/08/3-steps-to-download-xmllint/).

#### Example usages

Execute a test on an AET instance that exposes the [[Test Executor API|TestExecutor]] on `http://localhost:8181`.
```
./aet.sh http://localhost:8181
```

Execute a test as above, suite configuration is available in the `my-suite.xml` file.
```
./aet.sh http://localhost:8181 my-suite.xml
```

Execute a test as above, poll the status of running the test every 5 seconds.
```
./aet.sh http://localhost:8181 my-suite.xml -i 5
```

Execute a test as above, override domain and wait for suite to be unlocked for 5 minutes at most
```
./aet.sh http://localhost:8181 my-suite.xml -i 5 -d http://my.overriden.domain -w 300
```
