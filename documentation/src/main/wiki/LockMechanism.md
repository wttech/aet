## Lock Mechanism

Lock feature was implemented to block concurent modification of suite. This prevents overriding user changes (eg rebase, comments) by test suite run.

When suite is locked there is no way to update it by any operation in REST API.  

### Test suite run flow

When client (e.g. aet-maven-plugin) starts test suite then it tries to set lock by sending request to REST API.

* If given suite is already in locked state, REST API returns status 409 and then client is throwing exception and finishes execution (test suite doesn't get into runner in Queue).
* If suite isn't locked, lock is set and 'lock-heart-beat' is started. On each heartbeat lock duration is extended. Heartbeat is working until client finishes its execution.  
