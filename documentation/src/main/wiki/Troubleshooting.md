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
To clear bundles cache, stop Karaf, clear `$KARAF_HOME/data` and start Karaf.
If you want to backup existing logs, copy `$KARAF_HOME/data/logs`
to backup location before clearing `$KARAF_HOME/data`.
Example command to clear the cache (without logs backup):
```
sudo service karaf stop
sudo rm -fr /opt/aet/karaf/current/data/*
sudo service karaf start
```

## ActiveMQ

### Messages serialization issue
Make sure that no unfinished tasks are available on
the ActiveMQ. You may check it using console `http://<active-mq-host>:8161/admin/queues.jsp` (default credentials are `admin/admin`).
Column `Number Of Pending Messages` should display `0` in all `AET` queues.
If there is a positive number of messages pending, use `Purge` option.
Run tests again.

## MongoDB

### Check if Mongo is running
Navigate to `http://<mongo-db-host>:27017` with your browser. Mongo is OK if you see message about trying to access Mongo with HTTP protocol.

### Make sure that you have indexed `metadata` collection in the project db.
Manually created databases require manually created indexes. To create
indexes run [this script](https://github.com/Cognifide/aet/blob/master/misc/mongodb/create-indexes.js)
in your MongoDB database. You may update all existing databases at once using
[this script](https://github.com/Cognifide/aet/blob/master/misc/mongodb/create-indexes-for-all-dbs.js).

## Report app

### Failed to load report data
When you see the alert `Failed to load report data!` it may mean several things.
First one is a problem with connectivity between report app and AET Web Services.
Open browser developer's console and check the status of a request to `<aet-web-api-endpoint>/api/metadata?...`.
Make sure that your report instance is not trying to do Cross-Origin
resource call which is blocked by most of popular browsers.
Configuration of an endpoint for AET reports can be found in
[`/webapp/app/services/endpointConfiguration.service.js`](https://github.com/Cognifide/aet/blob/master/report/src/main/webapp/app/services/endpointConfiguration.service.js) file.
