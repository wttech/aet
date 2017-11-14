![Cognifide logo](http://cognifide.github.io/images/cognifide-logo.png)

# AET
<p align="center">
  <img src="https://github.com/Cognifide/aet/blob/master/misc/img/aet-logo-black.png?raw=true"
         alt="AET Logo"/>
</p>

## Core Module
Contains core AET System elements, such as Runner, Worker and storage implementation.

### cleaner
The cleaner is a mechanism that removes obsolete data from the AET Database.
The cleaning is a scheduled task (using the Quartz Scheduler library with a Cron expresion).

### communication
Contains JMS implementation of `communication-api` module that AET System uses (e.g. `JmsConnection`).

### environment
Contains libraries that are used in the System, wrapped into OSGi bundles:

* proxy - proxy used by the System, 
* selenium - wrapped Selenium driver (excluded from build, latest build artifact
is kept in `static-files` module).
* w3chtml5validator - wraped nu.validator w3c library that checks source against w3c validation
(excluded from build, latest build artifact is kept in `static-files` module).

### jobs
Contains implementations of jobs that can perform a particular task
(e.g. collect screenshots, compare sources, validate a page against W3C).

### runner
Runner is the heart of the system. It is responsible for consuming Client's request
and dispatching it to Workers. It works similar to the Map-Reduce algorithm.
During the execution of the suite, the Runner checks if the next phase
can begin and when all the phases are finished the Runner informs the client about it.

### validation
Module responsible for suite validation on server side.

### versionstorage
Abstract layer over datastorage system.

### worker
The Worker is a single processing unit that can perform a specific task
e.g. collect a screenshot using the Firefox browser,
collect a page source, compare two screenshots,
check if the source of a page is W3C-complaint and many others.
Worker uses `jobs` to perform the tests.
This module communicates with browser (Firefox).
