**Jasmine documentation:** [DOCS](https://jasmine.github.io/index.html)
**Chakram documentation:** [DOCS](http://dareid.github.io/chakram/jsdoc/index.html)

## Setup on local machine
- Download and install Node.js runtime from [here](https://nodejs.org/en/)
- Check if Node.js (at least v6.9.2) is available by typing in console: `node -v`
  - alternatively use [Node Version Manager (NVM)](https://github.com/creationix/nvm/blob/master/README.markdown) to install proper version of Node.js for this project
  - `nvm install`
  - `nvm use`
- Clone the  repository to your local machine
- Run `npm install` in `integration-tests\api-tests`

## Running the tests on local machine
- To run API tests you can simply run `npm test`. This will run tests, added to jasmine.json configuration file, on local instance
```
npm test
```

- To run a single test, use jasmine directly:

```
jasmine specs/file-name.js
```

## Troubleshooting
* make sure that you are using Node.js in a correct version
* if you are using NVM make sure to execute `nvm use` to switch to correct version of Node.js
* In case of issues with running the job, try to remove `node_modules` folder and install application again.

## Useful plugins
* JS-CSS-HTML Formatter
* JShint
* ESlint
