#!/bin/bash

POLL_INTERVAL=1
SUITE_ENDPOINT="/suite"
function usage {
    echo
    echo "AET Test executor"
    echo
    echo "Usage:"
    echo
    echo -e "\t$0 <endpoint> [<suite_file_name>] [-i | --interval <POLL_INTERVAL>]"
    echo
    echo "Options:"
    echo -e "\t-i --interval <POLL_INTERVAL>  - Set interval in seconds for polling suite status. Default interval : 1 sec"
    echo
    exit 1
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
    test_failures=$(xmllint --xpath 'string(//testsuites/@failures)' xUnit.xml)
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

[[ $# -eq 0 ]] && usage

endpoint=$1
suite_file_name=${2-suite.xml}

while [[ $# -gt 0 ]]; do
    case "$1" in
        -i | --interval )
            POLL_INTERVAL="$2"
            shift 2
            ;;
        * )
            shift
            ;;
    esac
done

# request /suite endpoint
run_response=$(curl -sw "%{http_code}" -F "suite=@$suite_file_name" "$endpoint$SUITE_ENDPOINT")
extract_code_and_body "$run_response"

if [ $code -eq 200 ]; then
    correlation_id=$(echo $body | jq -r ".correlationId")
    status_url=$(echo $body | jq -r ".statusUrl")
    html_report_url=$(echo $body | jq -r ".htmlReportUrl")
    xunit_url=$(echo $body | jq -r ".xunitReportUrl")
    echo "Suite started with correlation id: $correlation_id"
else
    echo "Unsuccessful Request to \"$endpoint$SUITE_ENDPOINT\", status: $code
    $body"
    exit 1
fi

# request /status/<correlationId> endpoint
process_status=true
last_progress_message=""
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
                echo "Test failures: $test_failures"
                exit $test_failures
                ;;
            "PROGRESS")
                last_progress_message=$message
                echo "$message"
                ;;
            "ERROR"|"FATAL_ERROR")
                echo "$message"
                exit 1
                ;;
            *)
                echo "$last_progress_message"
                ;;
        esac
        sleep $POLL_INTERVAL
    else
        echo "Unsuccessful Request to \"$endpoint$status_url\", status: $code
        $body"
        exit 1
    fi
done
