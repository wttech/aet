**Protractor documentation:** [tutorial](http://www.protractortest.org/#/tutorial), [API](http://www.protractortest.org/#/api)

## Protractor setup on local machine
- Download and install Node.js runtime from [here](https://nodejs.org/en/)
- Check if Node.js (at least v6.9.2) is available by typing in console: `node -v`
  - alternatively use [Node Version Manager (NVM)](https://github.com/creationix/nvm/blob/master/README.markdown) to install proper version of Node.js for this project
  - `nvm install`
  - `nvm use`
- Clone the  repository to your local machine
- Run `npm install` in `/integration-tests/sanity-functional-protractor`
- Create new branch and start automating the tests :)

## Running the tests on local machine
- To run Protractor tests you can simply run `npm test`. This will run all available tests in '/specs' directory and on default environment which is live instance
- To run specific tests suite or few suites just add the parameter `--suite [suite-name]`. The suite names can be found in the `protractor-conf.js` file. Example:
```
npm test -- --suite reportTests
```

- To run a single test, use the `--specs` parameter

```
npm test -- --specs specs/file-name.js
```

## Troubleshooting
* make sure that you are using Node.js in a correct version
* if you are using NVM make sure to execute `nvm use` to switch to correct version of Node.js
* In case of issues with running the job, try to remove `node_modules` folder and install application again.

## Useful plugins
* JS-CSS-HTML Formatter
* Protractor Snippets
* JShint
* ESlint
