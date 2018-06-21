### Runner
Runner is the heart of the system. It is responsible for consuming client's request and dispatching it to Workers. 
It works similar to the Map-Reduce algorithm. During the execution of the suite, the Runner checks if the next phase can 
begin and when all the phases are finished the Runner informs the client about it.

| ! Important information |
|:----------------------- |
| It is recommended to configure only one instance of Runner in the whole system. It is possible to have more than one Runner configured but it may lead to unexpected suites starvation and timeouts. |


Runner starts whole suite processing and is responsible for merging all results. See following diagram that shows sequence of suite processing:

![aet-test-lifecycle](assets/diagrams/aet-test-lifecycle.png)

#### Configuration
There are couple of important parameters that could be used to adjust Runner configuration for optimal work with your infrastructure.
Use OSGi configuration console and edit `RunnerConfiguration` to change Runner settings.

##### Parameters
| Parameter | Default value | Description |
| --------- | ------------- | ----------- |
| Failure timeout `ft` | `120 seconds` | Time in seconds, after which suite processing will be interrupted if no notification was received in duration of this parameter. That means if Runner will be not updated by any collection or comparison result in that time it will decide to force stop suite processing. |
| Message ttl `mttl` | `300 seconds` | Time in seconds after which messages will be thrown out of queues. |
| URL Package Size `urlPackageSize` | `5` | Defines how many urls are being sent in one message. Each message is being processed by single CollectorListener. |
| Max Messages in Collector Queue `maxMessagesInCollectorQueue` | `20` | Defines the maximum amount of messages in the collector queue. This is defined for each runner instance separately, that means if you have more than one Runner instance in the system configured, the global number of messages in the queue is the sum of all Runners `maxMessagesInCollectorQueue` values. |
| Max Concurrent Suites Count `maxConcurrentSuitesCount` | `5` | Defines the maximum number of suites processed concurrently byt the Runner. If more suites will come to the system, they will be scheduled for later processing. |
