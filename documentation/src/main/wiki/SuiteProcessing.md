## Suite Processing

Each AET test processing consists of two phases:

* collection,
* comparison,

After report is generated, client is notified about the finished test run.

The following diagram shows the life cycle of single test:

![aet-test-lifecycle](assets/diagrams/aet-test-lifecycle.png)

### Collection

This is the first phase during which all specified data will be collected (e.g. screenshots, page source, js errors). All collection results are saved in database after successful collection.

### Comparison

The second phase is operation on collected data. In some cases collected data is compared to patterns, in other special validation is performed (e.g. w3c). The second phase starts before collection finishes - just at the moment when required artefacts are collected and ready to compare (e.g. to compare two screenshots, system does not have to wait until source of page is collected).

The following diagram shows the life cycle of test suite:

![aet-test-suite-lifecycle](assets/diagrams/aet-test-suite-lifecycle.png)

### Run and rerun part of the suite

Suite Executor provides us methods to run suite from XML format or in case of rerun - suite model from the database. In case of rerun a test or an URL we are providing the model from database enriched with information necessary to process by runners and workers e.g `Correlation ID`, `Company`, `Project`. In the next step, the model is taken by the runner. It has knowledge about the type of model - is it a suite, a test or an URL and uses it for correct extraction URLs for workers and save or replace information in a database after comparison part.