### REST API

Representational State Transfer API for accessing and modifying data stored in AET Database. REST API is part of AET System and is the interface between system database, user and application.

Its methods are used by *AET Maven Plugin* to download reports, *HTML Report* uses it to load images and to perform rebase action.  

Rebase (switching artifacts id of pattern(s)) and adding comments should be done on client side. REST API only consumes whole json representation of suite.  

#### REST API HTTP methods

Base api path:

`http://<Domain_or_IP_Address>:<PORT>/api`

|method|URL|HTTP Method|Parameters|Example|Description|
|------|---|-----------|----------|-------|-----------|
|get artifact by artifact Id|artifact|GET|company<br/> project<br/> id|http://aet.example.com/api/artifact?company=cognifide&project=example&id=56fa80c1ab21c61f14bfef45|Returns file from DB as "application/octet-stream" (even for json files), if not found or error occured json message with "application/json" content type is returned .|
|get metadata by correlationId|metadata|GET|company<br/> project<br/> correlationId | http://aet.example.com/api/metadata?company=cognifide&project=example&correlationId=cognifide-example-1459257500567 | Returns newest version of metadata identified by provided correlationId. |
|get metadata by suite name|metadata|GET|company<br/> project<br/> suite |http://aet.example.com/api/metadata?company=cognifide&project=example&suite=mysimplesuite | Returns newest version of latest run (identified by latest correlationId) of metadata by with provided suite name.|
|upade suite metadata|metadata|POST|raw JSON in POST body|http://aet.example.com/api/metadata [raw json in post body] | This method increments version number before saving to DB and returns updated suite object in json format.<br/><br/> Returns status 409 if given suite is locked.|
|get lock for suite|lock|GET|"company-project-name" as last part of path |http://aet.example.com/api/lock/cognifide-example-mysimplesuite | Returns lock status for given suite (true if it's locked or false in json)|
|try to set lock|lock|POST|`value` - additional info for lock (currently it's correlationId only)<br/><br/> "company-project-name" as last part of path | http://aet.example.com/api/lock/cognifide-example-mysimplesuite <br/><br/> [value=cognifide-example-mysimplesuite-12312454]|This methods sets lock only if there is no lock already set for given suite. Returns status 409 if given suite is already locked.|
|update heart beat|lock|PUT|`value` - additional info for lock (currently it's correlationId only)<br/><br/> "company-project-name" as last part of path | http://aet.example.com/api/lock/cognifide-example-mysimplesuite <br/><br/> [value=cognifide-example-mysimplesuite-12312454] | This method extends the duration of a lock for given suite. |
|gets list of all suites in system|config/list|GET| |http://aet.example.com/api/config/list | Returns all suites for all projects in all companies as html list of links to reports and metadatas<br> (this method will change or will be removed in near future- for now it stays only for devs and testing purposes). |
|get all locked suites|config/locks|GET| |http://aet.example.com/api/config/locks | Returns list of current locks. |
|get communication settings|config/communicationSettings|GET| | http://aet.example.com/api/config/communicationSettings | Returns current JMS broker settings and report app domain. This method is used by maven client. |
