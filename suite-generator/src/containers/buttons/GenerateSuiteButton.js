import React, {Component} from "react";
import {bindActionCreators} from "redux";
import {connect} from "react-redux";
import xmlbuilder from "xmlbuilder";
import {saveAs} from "file-saver";
import {setTestAsInvalid} from "../../actions";


class GenerateSuiteButton extends Component {
  handleSuiteGenerating() {
    const projectTests = this.props.project[0].tests;
    const invalidParams = this.validateParams(projectTests);
   if(invalidParams.length === 0 ) {
      const suiteElement = xmlbuilder.create('suite', {encoding: "utf-8"});
      suiteElement.att('name', this.props.project[0].suite)
        .att('company', this.props.project[0].company)
        .att('domain', this.props.project[0].domain)
        .att('project', this.props.project[0].project);
      if(Object.values(projectTests).length > 0) {
        Object.values(projectTests).forEach((testItem) => {
          const isProxyRequired = this.checkForProxy(testItem);
          let testElement = null;
          if(isProxyRequired) {
            testElement = suiteElement.ele("test", {name: testItem.name.name, useProxy: "rest"})
          } else {
            testElement = suiteElement.ele("test", {name: testItem.name.name})
          }      
          this.generateCollectorsGroup(testElement, testItem);
          this.generateComparatorsGroup(testElement, testItem);
          this.generateUrls(testElement, testItem);
        });
      }
      const xml = suiteElement.end({pretty: true});
      var file = new File([xml], "suite.xml", {type: "application/xml;charset=utf-8"});
      saveAs(file);
    } else {
      Object.values(invalidParams).forEach((test, index) => {
      const testInProject = Object.values(this.props.project[0].tests).find((obj) => {
        return obj.name.name === test.name;
      });
      this.props.setTestAsInvalid(testInProject);
      document.querySelectorAll(".test-item").forEach((block) => {
        if(testInProject.name.name === block.children[0].innerHTML) {
          const scrollToValue = block.getBoundingClientRect().top;
          const scrollCurrent = document.querySelector(".tests-container").scrollTop;
          document.querySelector(".tests-container").scrollTo(0, scrollToValue + scrollCurrent - 200);
        }
      });
    });
   }
  }

  validateParams(suite) {
    let invalidParams = [];
    let invalidBlock = {};
    let listOfMissingParams = [];
    let testName = null;
    Object.values(suite).forEach((testObj) => {
      listOfMissingParams = [];
      Object.values(testObj.tests).forEach((test) => {
        if(test.parameters && Object.values(test.parameters).forEach((param) => {
          if((param.current === null || param.current === "") && param.isMandatory) {
            testName = testObj.name.name;
            listOfMissingParams.push(
              param.name
            ); 
          }
        }));
        invalidBlock = {
          name: testName,
          params: listOfMissingParams,
        };
      });
      if(invalidBlock.name) {
        invalidParams.push(invalidBlock);
      }
    });
    return invalidParams;
  }

  generateCollectorsGroup(testElement, projectTests) {
    const collectors = testElement.ele("collect");
    projectTests.tests.forEach((testItem) => {
      if(testItem.dropTo === "Modifiers") {
        const modifierItem = collectors.ele(testItem.tag);
        this.generateParameters(testItem, modifierItem);
      } 
    });
    projectTests.tests.forEach((testItem) => {
      if(testItem.dropTo === "Collectors") {
        const collectorItem = collectors.ele(testItem.tag);
        this.generateParameters(testItem, collectorItem);
      }
    });
  }

  generateComparatorsGroup(testElement, projectTests) {
    const comparators = testElement.ele("compare");
    projectTests.tests.forEach((testItem) => {
      if(testItem.dropTo === "Comparators") {
        const collectorItem = comparators.ele(testItem.tag);
        this.generateParameters(testItem, collectorItem);
        this.generateFilters(testItem, collectorItem);
      }
    });
  }

  generateParameters(testItem, groupItem) {
    if(testItem.parameters !== null) {
      Object.values(testItem.parameters).forEach((param) => {
        if(param.isMandatory === false) {
          if(this.checkIfParamExists(param.current)) {
            groupItem.att(param.tag, param.current)
          }
        } else {
          if(this.checkIfParamExists(param.current)) {
            groupItem.att(param.tag, param.current);
          } else if(this.checkIfParamExists(param.default)) {
            groupItem.att(param.tag, param.default);
          } else if(!this.checkIfParamExists(param.current) && !this.checkIfParamExists(param.default)) {
            console.error("Mandatory parameter " + param.tag + " is missing!");
            groupItem.att(param.tag, "MISSING_PARAM");
          }
        }
      });
    }
  }

  checkIfParamExists(param) {
    return param !== null && typeof param !== "undefined" ? true : false
  }

  generateFilters(testElement, groupItem) {
    if(testElement.filters !== null) {
      Object.values(testElement.filters).forEach((filter) => {
        const filterItem = groupItem.ele(filter.tag);
        this.generateParameters(filter, filterItem);
      });
    }
  }

  generateUrls(testElement, projectTests) {
    const urls = testElement.ele("urls");
    if(projectTests.urls.length > 0 ) {
      projectTests.urls.forEach((url) => {
          if(url.parameters.name.current) {
            urls.ele("url", {href: url.parameters.href.current, name: url.parameters.name.current});
          } else {
            urls.ele("url", {href: url.parameters.href.current});
          }

      });
    }
  }

  checkForProxy(testItem) {
    let isProxyRequired = false;
    Object.values(testItem.tests).forEach((test) => {
      if(test.proxy === "true") {
        isProxyRequired = true;
      }
    });
    return isProxyRequired;
  }

  checkIfTestsExist() {
    if(typeof this.props.project[0] !== "undefined"
    && typeof this.props.project[0].tests !== "undefined"
    && this.props.project[0].tests !== null
    ) {
        return true;
    }
    return false;
  }


  render () {
    return this.checkIfTestsExist() ? (
      (
        <div className="sidebar-btn" onClick={() => this.handleSuiteGenerating()}>
            GENERATE SUITE
        </div>
      )
    ) : null 
  }
}

function mapStateToProps(state) {
  return {
    test: state.test,
    project: state.project,
  }
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({setTestAsInvalid}, dispatch)
}

export default connect(mapStateToProps, mapDispatchToProps)(GenerateSuiteButton);