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

	./aet.sh <endpoint> [<suite_file_name>] [-i | --interval <POLL_INTERVAL>]

Options:
	-i --interval <POLL_INTERVAL>  - Set interval in seconds for polling suite status. Default interval : 1 sec
```

### Dependencies

Script utilizes following tools:

* `curl` - for making requests against AET REST API
* `xmllint` - for parsing xUnit report to retrieve number of failed tests to set proper exit code