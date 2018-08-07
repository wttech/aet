# Troubleshooting
This section contains tips and ways to repair AET instances. If all the tips from the list failed
or you have discovered a new one, please raise a ticket using [Issues Tool](https://github.com/Cognifide/aet/issues)
or create a Pull Request with change to [this file](https://github.com/Cognifide/aet/blob/master/documentation/src/main/wiki/Troubleshooting.md).

- [Karaf](#karaf)
  - [Karaf can't find some dependencies or configurations - clearing the cache](#karaf-cant-find-some-dependencies-or-configurations---clearing-the-cache)
- [ActiveMQ](#activemq)
  - [Messages serialization issue](#messages-serialization-issue)
- [MongoDB](#mongodb)
  - [Make sure that you have indexed `metadata` collection in the project db.](#make-sure-that-you-have-indexed-metadata-collection-in-the-project-db)
- [Report app](#report-app)
  - [Failed to load report data](#failed-to-load-report-data)

## Karaf

### Karaf can't find some dependencies or configurations - clearing the cache
This may help in multiple situations, e.g. new version of AET was deployed
or Karaf can't find some dependencies or configurations.
To clear bundles cache, stop Karaf, remove following directories from `$KARAF_HOME/data` and start Karaf:
- `cache`
- `generated-bundles`
- `tmp`

## ActiveMQ

### Messages serialization issue
Make sure that no unfinished tasks are available on
the ActiveMQ (you may check it using console `<active-mq-host>:8161/admin/queues.jsp`).
Column `Number Of Pending Messages` should display `0` in all `AET.` queues.
If there is a positive number of messages pending, use `Purge` option.
Run tests again.

## MongoDB

### Make sure that you have indexed `metadata` collection in the project db.
Manually created databases require manually created indexes. To create
indexes run [this script](https://github.com/Cognifide/aet/blob/master/misc/mongodb/create-indexes.js)
in your MongoDB database. You may update all existing databases at once using
[this script](https://github.com/Cognifide/aet/blob/master/misc/mongodb/create-indexes-for-all-dbs.js).

## Report app

### Failed to load report data
When you see the alert `Failed to load report data!` it may mean several things.
First one is a problem with connectivity between report app and AET Web Services.
Open browser developer's console and check the status of a request `<aet-web-api-endpoint>/api/metadata?...`.
Make sure that your report instance is not trying to do Cross-Origin
resource call which is blocked by most of popular browsers.
Configuration of an endpoint for AET reports can be found in
[`/webapp/app/services/endpointConfiguration.service.js`](https://github.com/Cognifide/aet/blob/master/report/src/main/webapp/app/services/endpointConfiguration.service.js) file.