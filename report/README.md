![Cognifide logo](http://cognifide.github.io/images/cognifide-logo.png)

# AET
<p align="center">
  <img src="https://github.com/Cognifide/aet/blob/master/misc/img/aet-logo-black.png?raw=true"
         alt="AET Logo"/>
</p>

## Report Module
Client side application driven by AngularJS that renders html reports for the end users using AET REST API as the data source.

### Prerequisities:

The report web application requires AET Framework REST endpoint to be available.
Please setup AET environment and run some tests to have the data to show.

### Development environment
You will need following tools installed locally:

* [Node.js][node-js] with [npm][npm-install] package manager
* Gulp installed globally: `npm i gulp -g`

#### Running web application locally

1. update REST endpoint in `report/src/main/webapp/app/services/endpointConfiguration.service.js`:

        var config = {
            'getUrl': 'http://aet.server.com:8181/api/'
        };
   in order to fetch data displayed on the report from instance `http://aet.server.com:8181`.
   (for vagrant setup url example: 'http://aet-vagrant/api/')
2. go into `.../report/src/main/webapp` folder
3. run `npm install` to install required npm modules
4. if you are running the app for the first time use `gulp installLibs` or `gulp build`
4. run `gulp` to start the application


#### Deploying report application to vagrant virtual machine

You could also upload report application to local virtual machine: 

    mvn clean install -P upload

[node-js]: https://nodejs.org/en/
[npm-install]: https://docs.npmjs.com/getting-started/installing-node#updating-npm