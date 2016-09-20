### Runner
Runner is the heart of the system. It is responsible for consuming client's request and dispatching it to Workers. 
It works similar to the Map-Reduce algorithm. During the execution of the suite, the Runner checks if the next phase can 
begin and when all the phases are finished the Runner informs the client about it.

Runner starts whole suite processing and is responsible for merging all results. See following diagram that shows sequence of suite processing:

![aet-test-lifecycle](assets/diagrams/aet-test-lifecycle.png)