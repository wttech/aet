### Worker
The Worker is a single processing unit that can perform a specific task e.g. collect a screenshot using the Firefox browser, 
collect a page source, compare two screenshots, check if the source of a page is W3C-compliant and many others. 
Worker uses `jobs` to perform the tests. This module communicates with the browser (Firefox).

The Worker is responsible for executing a single piece of work and returning it to the [[Runner|Runner]]. See the following diagram that shows the sequence of suite processing:

![aet-test-lifecycle](assets/diagrams/aet-test-lifecycle.png)