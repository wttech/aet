#!/bin/bash
#
# AET
#
# Copyright (C) 2013 Cognifide Limited
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


POLL_INTERVAL=1
UNLOCK_TIMEOUT=0
SUITE_ENDPOINT="/suite"
DOMAIN_BODY=""
SUITE_NAME_BODY=""
CORRELATION_ID_BODY=""
PATTERN_SUITE_BODY=""
function usage {
    echo
    echo "AET Test executor"
    echo
    echo "Usage:"
    echo
    echo -e "\t$0 <endpoint> [<suite_file_name>] [options]"
    echo
    echo "Options:"
    echo -e "\t-d --domain <DOMAIN>                 - Override domain attribute defined in suite file"
    echo -e "\t-n --name <SUITE_NAME>               - Override name attribute defined in suite file"
    echo -e "\t-c --correlationId <CORRELATION_ID>  - Set id of patterns to run test against."
    echo -e "\t-p --patternSuite <SUITE_NAME>       - Set the suite name to run test against its latest pattern (only used if -c is not set)"
    echo -e "\t-i --interval <POLL_INTERVAL>        - Set interval in seconds for polling suite status. Default interval : 1 sec."
    echo -e "\t-w --waitForUnlock <TIMEOUT>         - Set timeout for the script to wait for unlocked suite. Default timeout: 0 sec."
    echo -e "\t-v --verbose                         - Make it more descriptive"
    echo
    exit 1
}
function get_json {
	curl -s "$1"
}

# extracts http status code and response body from curl response string
function extract_code_and_body {
    local response=$1
    code="${response:${#response}-3}"
    body="${response:0:${#response}-3}"
}

# download xUnit report
function parse_xunit {
    curl $1 --output xUnit.xml --silent
    all_run_test=$(xmllint --xpath 'string(//testsuites/@tests)' xUnit.xml)
    if ((all_run_test > 0)); then
        test_failures=$(xmllint --xpath 'string(//testsuites/@failures)' xUnit.xml)
    fi
}

# prepare html file with redirect to AET results report
function prepare_redirect {
  echo "<!DOCTYPE html>
<html>
<head>
  <title>AET Test results - redirect</title>
  <script>window.location.href = \"$1\"</script>
</head>
<body>
  <a href=\"$1\">AET Report</a>
</body>
</html>" > redirect.html
}

function process_locked_suite {
  if [[ $code -eq 500 && UNLOCK_TIMEOUT -gt 0 && "$body" =~ "Suite is currently locked" ]]; then
      echo "Waiting for suite to be unlocked for $UNLOCK_TIMEOUT second(s)"
      sleep $(( 5 > $UNLOCK_TIMEOUT ? $UNLOCK_TIMEOUT : 5))
      ((UNLOCK_TIMEOUT-=5))
      start_suite
  else
    echo "Unsuccessful Request to \"$endpoint$SUITE_ENDPOINT\", status: $code
    $body"
    exit 1
  fi
}

# request /suite endpoint
function start_suite {
  run_response=$(curl -sw "%{http_code}" -F "projectChecksSum=$(get_json $endpoint_sum_contr)" -F "suite=@$suite_file_name"$DOMAIN_BODY$SUITE_NAME_BODY$CORRELATION_ID_BODY$PATTERN_SUITE_BODY "$endpoint$SUITE_ENDPOINT")
  extract_code_and_body "$run_response"

  if [ $code -eq 200 ]; then
      correlation_id=$(echo $body | jq -r ".correlationId")
      status_url=$(echo $body | jq -r ".statusUrl")
      html_report_url=$(echo $body | jq -r ".htmlReportUrl")
      xunit_url=$(echo $body | jq -r ".xunitReportUrl")
      echo "Suite started with correlation id: $correlation_id"
  else
      process_locked_suite
  fi
}

[[ $# -eq 0 ]] && usage

endpoint=$1
suite_file_name=${2-suite.xml}
endpoint_sum_contr=$3

while [[ $# -gt 0 ]]; do
    case "$1" in
        -i | --interval )
            POLL_INTERVAL="$2"
            shift 2
            ;;
        -w | --waitForUnlock )
            UNLOCK_TIMEOUT="$2"
            shift 2
            ;;
        -d | --domain )
            DOMAIN="$2"
            DOMAIN_BODY=" -F domain=$DOMAIN"
            shift 2
            ;;
        -n | --name )
            SUITE_NAME="$2"
            SUITE_NAME_BODY=" -F name=$SUITE_NAME"
            shift 2
            ;;
        -c | --correlationId )
            CORRELATION_ID=$2
            CORRELATION_ID_BODY=" -F pattern=$CORRELATION_ID"
            shift 2
            ;;
        -p | --patternSuite )
            PATTERN_SUITE=$2
            PATTERN_SUITE_BODY=" -F patternSuite=$PATTERN_SUITE"
            shift 2
            ;;
        -v | --verbose )
            VERBOSE=1
            shift 1
            ;;
        * )
            shift
            ;;
    esac
done

if [[ $VERBOSE -eq 1 ]]; then
  echo -e "Test parameters:"
  echo -e "\tAET endpoint:          $endpoint$SUITE_ENDPOINT"
  echo -e "\tSuite:                 $suite_file_name"
  echo -e "\tOverridden domain:     ${DOMAIN-not set}"
  echo -e "\tOverridden suite name: ${SUITE_NAME-not set}"
  echo -e "\tPattern id:            ${CORRELATION_ID-not set}"
  echo -e "\tPattern suite:         ${PATTERN_SUITE-not set}"
  echo ""
fi

start_suite

# request /status/<correlationId> endpoint
process_status=true
while $process_status; do
    status_response=$(curl -sw "%{http_code}" $endpoint$status_url)
    extract_code_and_body "$status_response"

    if [ $code -eq 200 ]; then
        status=$(echo $body | jq -r ".status")
        message=$(echo $body | jq -r ".message")

        case "$status" in
            "FINISHED")
                process_status=false
                parse_xunit "$endpoint$xunit_url"
                prepare_redirect "$html_report_url"
                echo -e "$message\nReport url:\n$html_report_url"
                echo "Test failures: ${test_failures:=1}"
                exit $test_failures
                ;;
            "ERROR"|"PROGRESS")
                echo "$message"
                ;;
            "FATAL_ERROR")
                echo "$message"
                exit 1
                ;;
            *)
                ;;
        esac
        sleep $POLL_INTERVAL
    else
        echo "Unsuccessful status request to \"$endpoint$status_url\", status: $code
        $body"
        exit 1
    fi
done
