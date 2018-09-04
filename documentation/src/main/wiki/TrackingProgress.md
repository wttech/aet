### Tracking Progress

#### How to read AET Reports and real time progress

AET execution progress is updated on real time basis and can be viewed in the console
(also e.g. in the preview of the any CI tool that schedules running AET suite).

During test processing detailed information about the actual progress is displayed as in the following example:

```
...
[14:24:29.010]: COLLECTED: [success: 0, total: 4] ::: COMPARED: [success: 0, total: 0]
[14:24:46.036]: COLLECTED: [success: 1, total: 4] ::: COMPARED: [success: 1, total: 1]
[14:24:50.039]: COLLECTED: [success: 2, total: 4] ::: COMPARED: [success: 1, total: 2]
[14:24:51.042]: COLLECTED: [success: 2, total: 4] ::: COMPARED: [success: 2, total: 2]
[14:24:53.059]: COLLECTED: [success: 3, total: 4] ::: COMPARED: [success: 2, total: 3]
[14:24:54.047]: COLLECTED: [success: 3, total: 4] ::: COMPARED: [success: 3, total: 3]
[14:24:55.050]: COLLECTED: [success: 4, total: 4] ::: COMPARED: [success: 3, total: 4]
...
```

where:

**COLLECTED** - shows results of collectors' work - how many artifacts have been successfully collected and what is the total number of all artifacts to be collected,

**COMPARED** -  shows results of comparators' work - how many artifacts have been successfully compared and what is the total number of all artifacts to be compared. The total number of artifacts to be compared depends on collectors' work progress - increases when the number of successfully collected artifacts increases.

If there are problems during processing, warning information with some description of processing step and its parameters is displayed:
```
CollectionStep: source named source with parameters: {} thrown exception. TestName: comparator-Source-Long-Response-FAILED UrlName: comparators/source/failed_long_response.jsp Url: http://192.168.123.100:9090/sample-site/sanity/comparators/source/failed_long_response.jsp
```

In this example the source collector failed to collect necessary artifacts. This information is subsequently reflected in the progress log:
```
...
[06:36:44.832]: COLLECTED: [success: 46, failed: 1, total: 72] ::: COMPARED: [success: 46, total: 46]
[06:36:50.837]: COLLECTED: [success: 47, failed: 1, total: 72] ::: COMPARED: [success: 47, total: 47]
[06:36:52.840]: COLLECTED: [success: 48, failed: 1, total: 72] ::: COMPARED: [success: 47, total: 48]
...
```

In the example above one artifact has failed during the collection phase.

#### When tests successfully finish - command line

When the AET test processing completes the information about available reports and processing status is shown in the console - as shown below:

```
Suite processing finished
Report url:
http://aet-report/report.html?company=cognifide&project=docker&correlationId=cognifide-docker-browsers-1535552618224
Test failures: 2
```

`Test failures`  - the number of tests that ended with `FAILED` status

#### xUnit support

You may want to use the xUnit report format to validate suite processing status
immediately in your CI tool (e.g. with [Jenkins xUnit plugin](https://wiki.jenkins.io/display/JENKINS/xUnit+Plugin)).
AET supports this format and you may obtain xUnit report file (in the XML form) after suite processing is finished with [[AET WebAPI|WebAPI]].
[[Aet client script|ClientScripts#exit-status]] supports downloading xUnit after suite processing is finished out of the box.

