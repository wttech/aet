### Client Scripts

Next to [[client application|ClientApplication]] in form of a maven plugin, AET offers shell script to execute tests. In order to trigger test execution, provided shell script utilizes exposed [[Test Executor API|TestExecutor]].

### Usage

```
AET Test executor

Usage:

	./aet.sh <endpoint> [<suite_file_name>] [-i | --interval <POLL_INTERVAL>]

Options:
	-i --interval <POLL_INTERVAL>  - Set interval in seconds for polling suite status. Default interval : 1 sec
```

Shell script needs one parameter to trigger test execution. Set `endpoint` parameter to point script to domain which exposes [[Test Executor API|TestExecutor]].

#### Parameters

Shell script can be run with parameters which are described below:

| Parameter | Description | Default Value | Mandatory |
| --------- | ----------- | ------------- | --------- |
| `endpointDomain` | AET domain which exposes [[Test Executor API\|TestExecutor]] | - | yes |
| `suite_file_name` | The full path to the suite definition file (at least a file name with an extension, e.g. `testSuite.xml`). | suite.xml | no |
| `-i \| --interval <POLL_INTERVAL>` | Interval of polling the status of currently executed test. (Unit: seconds) | 1 | no |

#### Exit status

After test is completed the script downloads xUnit report to extract the test failures number. The number of failures is then set as an exit status.

#### Artifacts

During test execution script creates two files:
* `xUnit.xml` - this file is downloaded after test is processed, to extract information on any failures.
* `redirect.html` - this file is created to help reach the test result by redirecting to test results URL after opening it.

#### Dependencies

In order to run properly, following commands need to be available on PATH of the environment the script is executed on:
* `curl` - used to make actual request against [[Test Executor API|TestExecutor]] and also to download `xUnit.xml` file after test completion. Comes preinstalled on most Unix systems. [Download link](https://curl.haxx.se/download.html)
* `jq` - used to parse JSON responses from [[Test Executor API|TestExecutor]]. [Download link](https://stedolan.github.io/jq/download/)
* `xmllint` - used to retirieve failure information from downloaded `xUnit.xml`. Comes preinstalled on most Unix systems. For Windows installation instructions, refer to [Jo Jordan's post](http://flowingmotion.jojordan.org/2011/10/08/3-steps-to-download-xmllint/).

#### Example usages

Execute test on AET instance that exposes [[Test Executor API|TestExecutor]] on `http://localhost:8181`.
```
./aet.sh http://localhost:8181
```

Execute test as above, suite configuration is available in `my-suite.xml` file.
```
./aet.sh http://localhost:8181 my-suite.xml
```

Execute test as above, poll status of running test every 5 seconds.
```
./aet.sh http://localhost:8181 my-suite.xml 5
```
