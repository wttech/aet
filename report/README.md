![Cognifide logo](http://cognifide.github.io/images/cognifide-logo.png)

# Automated Exploratory Tests
<p align="center">
  <img src="misc/img/aet-logo-black.png" alt="AET Logo"/>
</p>

## Report Module
Client side application driven by AngularJS that renders html reports for the end users using AET REST API as the data source.

### Prerequisities:

The report web application requires AET Framework REST endpoint to be available.
Please setup AET environment and run some tests to have the data to show.

### Development environment
You will need following tools installed locally:

* [Node.js][node-js] with [npm][npm-install] package manager
* [Compass][compass] for building css files
* Grunt installed globally: `npm install grunt -g`

#### Running web application locally

1. update REST endpoint in `.../report/src/main/webapp/app/_old_js/services/configService`:

        var config = {
            'production': 'http://aet.server.com:8181/api/'
        };

2. got into `.../report/src/main/webapp` folder
3. run `npm install` to install required npm modules
4. run `grunt` to start your web application


#### Deploying report application to vagrant virtual machine

You could also upload report application to local virtual machine: 

    mvn clean install -Pupload-to-karaf

[node-js]: https://nodejs.org/en/
[npm-install]: https://docs.npmjs.com/getting-started/installing-node#updating-npm
[compass]: http://compass-style.org/install/
