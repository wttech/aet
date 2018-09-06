### Web API

Web API for accessing and modifying data stored in AET Database. This API is part of AET System and is the interface between system database, user and application.

Its methods are used by *AET Maven Plugin* to download reports, *HTML Report* uses it to load images and to perform rebase action.

Rebase (switching artifacts id of pattern(s)) and adding comments should be done on client side. Web API only consumes whole json representation of suite.

#### Executor API
The [[Test Executor|TestExecutor]] module is a part of AET Web API and an
entry point of the test suite processing. You may read more about it [[here|TestExecutor]]


#### Web API HTTP methods

##### Get artifact by artifact Id
* **URL**: `/api/artifact`
* **HTTP Method**: GET
* **Parameters**: `company`, `project`, `id`
* **Example**: http://aet.example.com/api/artifact?company=cognifide&project=example&id=56fa80c1ab21c61f14bfef45
* **Description**: Returns file from DB as `application/octet-stream` (even for json files), if not found or error occurred json message with `application/json` content type is returned.

--------
##### Get metadata by correlationId
* **URL**: `/api/metadata`
* **HTTP Method**: GET
* **Parameters**: `company`, `project`, `correlationId` 
* **Example**: http://aet.example.com/api/metadata?company=cognifide&project=example&correlationId=cognifide-example-1459257500567 
* **Description**: Returns newest version of metadata identified by provided correlationId.

--------
##### Get metadata by suite name
* **URL**: `/api/metadata`
* **HTTP Method**: GET
* **Parameters**: `company`, `project`, `suite` 
* **Example**: http://aet.example.com/api/metadata?company=cognifide&project=example&suite=mysimplesuite 
* **Description**: Returns newest version of latest run (identified by latest correlationId) of metadata by with provided suite name.

--------
##### Get metadata by suite name and version
* **URL**: `/api/metadata`
* **HTTP Method**: GET
* **Parameters**: `company`, `project`, `suite`, `version` 
* **Example**: http://aet.example.com/api/metadata?company=cognifide&project=example&suite=mysimplesuite&version=7
* **Description**: Returns specified version of metadata identified by provided suite and version.

--------
##### Update suite metadata
* **URL**: `/api/metadata`
* **HTTP Method**: POST
* **Parameters**: raw JSON in POST body
* **Example**: http://aet.example.com/api/metadata `[raw json in post body]`
* **Description**: This method increments version number before saving to DB and returns updated suite object in json format. Returns status 409 if given suite is locked.

--------
##### Get correlationIds and versions by suite name
* **URL**: `/api/history`
* **HTTP Method**: GET
* **Parameters**: `company`, `project`, `suite`
* **Example**: http://aet.example.com/api/history?company=cognifide&project=example&suite=mysimplesuite
* **Description**: Returns all found correlationIds and versions by suite name

--------
##### Get lock for suite
* **URL**: `/lock`
* **HTTP Method**: GET
* **Parameters**: `company-project-name` as last part of path 
* **Example**: http://aet.example.com/lock/cognifide-example-mysimplesuite 
* **Description**: Returns lock status for given suite (true if it's locked or false in json)

--------
##### Try to set lock
* **URL**: `/lock`
* **HTTP Method**: POST
* **Parameters**: `value` - additional info for lock (currently it's correlationId only) "company-project-name" as last part of path 
* **Example**: http://aet.example.com/lock/cognifide-example-mysimplesuite `[value=cognifide-example-mysimplesuite-12312454]`
* **Description**: This methods sets lock only if there is no lock already set for given suite. Returns status 409 if given suite is already locked.

--------
##### Update heart beat
* **URL**: `/lock`
* **HTTP Method**: PUT
* **Parameters**: `value` - additional info for lock (currently it's correlationId only) "company-project-name" as last part of path 
* **Example**: http://aet.example.com/lock/cognifide-example-mysimplesuite `[value=cognifide-example-mysimplesuite-12312454]` 
* **Description**: This method extends the duration of a lock for given suite.
 
--------
##### Gets list of all suites in system
* **URL**: `/configs/list`
* **HTTP Method**: GET
* **Example**: http://aet.example.com/config/list 
* **Description**: Returns all suites for all projects in all companies as html list of links to reports and metadata (this method will change or will be removed in near future- for now it stays only for devs and testing purposes).
 
--------
##### Get all locked suites
* **URL**: `/configs/locks`
* **HTTP Method**: GET
* **Example**: http://aet.example.com/config/locks 
* **Description**: Returns list of current locks.
 
--------
##### Get communication settings
* **URL**: `/configs/communicationSettings`
* **HTTP Method**: GET
* **Example**: http://aet.example.com/config/communicationSettings 
* **Description**: Returns current report app domain. This method is used by maven client.

--------
##### Get suite xUnit output
* **URL**: `/xunit`
* **HTTP Method**: GET
* **Parameters**: `company`, `project`, `correlationId` or `suite` (name)
* **Example**: http://aet.example.com/xunit?company=cognifide&project=example&correlationId=56fa80c1ab21c61f14bfef45
* **Description**: Returns xUnit file with the output of the run of suite identified by `correlationId`.
