## Logs

### Overview

AET log files can be found on Apache Karaf directory, in `C:\content\karaf\data\log` folder. Logs are split into four files:

**karaf.log**

Dedicated for logging Apache Karaf activities such as starting Karaf, starting AET bundles, configurations binding, etc.

**runner.log**

Dedicated for logging AET runner activities, such as running new test, changes in test suite run lifecycle, communication between runner and workers, JMS messages management, e.t.c.

**worker.log**

Dedicated for logging AET workers activities, such as collecting, comparing, modifying data, saving data do Mongo DB, etc.

**cleaner.log**

Dedicated for logging *Cleaner* activities (removing old Artifacts from Mongo DB, etc.).

### Log structue

Each log record has common structure:

```
    1                       2       3                           4                               5       6
2015-03-19 15:22:26,307 | INFO  | worker                   | CollectorMessageListenerImpl       86 | CollectorJobData [Collector4] message arrived with 1 urls. CorrelationId: cognifide-project-test-suite-1426774946010 RequestMessageId: ID:FLANNEL-56766-1426771932253-1:1:1:1:2
2015-03-19 15:22:28,650 | INFO  | worker                   | CollectorDispatcherImpl            41 | Start collection from http://www.cognifide.com, with 4 steps to perform. Company: cognifide Project: project TestSuite: test-suite Test: test1
2015-03-19 15:22:28,651 | DEBUG | worker                   | CollectorDispatcherImpl            53 | Performing collection step 1/4: open named open with parameters: {} from http://www.cognifide.com. Company: cognifide Project: project TestSuite: test-suite Test: test1
2015-03-19 15:22:29,351 | DEBUG | worker                   | CollectorDispatcherImpl            53 | Performing collection step 2/4: sleep named sleep with parameters: {duration=1500} from http://www.cognifide.com. Company: cognifide Project: project TestSuite: test-suite Test: test1
2015-03-19 15:22:29,351 | DEBUG | job-common               | SleepModifier                      27 | Sleeping for 1500 milliseconds
2015-03-19 15:22:30,851 | DEBUG | worker                   | CollectorDispatcherImpl            53 | Performing collection step 3/4: screen named screen with parameters: {} from http://www.cognifide.com. Company: cognifide Project: project TestSuite: test-suite Test: test1
2015-03-19 15:22:31,804 | DEBUG | datastorage-gridfs-impl  | GridFsStorage                     600 | Saving node with key: PatternMetadata{company=cognifide, project=project, testSuiteName=test-suite, environment=win7-ff16, artifactType=PATTERNS, domain=null, version=null} and file name: result.json
2015-03-19 15:22:31,813 | INFO  | datastorage-gridfs-impl  | GridFsHelper                      428 | Index created; Database: cognifide; Collection: project.files
2015-03-19 15:22:31,814 | DEBUG | datastorage-gridfs-impl  | GridFsStorage                     600 | Saving node with key: PatternMetadata{company=cognifide, project=project, testSuiteName=test-suite, environment=win7-ff16, artifactType=PATTERNS, domain=null, version=null} and file name: screenshot.png
...
```

where:

1. Log record date and hour,
2. Log level (INFO, DEBUG or ERROR),
3. Name of the system module where information is logged,
4. Name of the class,
5. Line of code,
6. Log message.

### Logs configuration

AET logging can be configured in *org.ops4j.pax.logging.cfg* file.

This configuration file specifies among others log files destination folder, log level and  pattern, log file maximum size.
