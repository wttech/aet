import React, {Component} from "react";
import {bindActionCreators} from "redux";
import {connect} from "react-redux";
import xmlbuilder from "xmlbuilder";
import {saveAs} from "file-saver";


class GenerateSuiteButton extends Component {

  handleSuiteGenerating() {
    const projectTests = this.props.project[0].tests;
    const suiteElement = xmlbuilder.create('suite', {encoding: "utf-8"});
    suiteElement.att('name', this.props.project[0].suite);
    suiteElement.att('company', this.props.project[0].company);
    suiteElement.att('domain', this.props.project[0].domain);
    suiteElement.att('project', this.props.project[0].project);
    if(Object.values(projectTests).length > 0) {
      Object.values(projectTests).forEach((testItem) => {
        const testElement = suiteElement.ele("test", {name: testItem.name.name})
        this.generateCollectorsGroup(testElement, testItem);
        this.generateComparatorsGroup(testElement, testItem);
        this.generateUrls(testElement, testItem);
      });
    }
    const xml = suiteElement.end({pretty: true});
    var file = new File([xml], "suite.xml", {type: "application/xml;charset=utf-8"});
    saveAs(file);
  }

  generateCollectorsGroup(testElement, projectTests) {
    const collectors = testElement.ele("collect");
    projectTests.tests.forEach((testItem) => {
      if(testItem.dropTo === "Modifiers") {
        const modifierItem = collectors.ele(testItem.tag);
        this.generateParameters(testItem, modifierItem);
      } 
    });
    collectors.ele("open");
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
          } else if(!this.checkIfParamExists(param.current) && this.checkIfParamExists(param.default)) {
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
      testElement.filters.forEach((filter) => {
        const filterItem = groupItem.ele(filter.tag);
        this.generateParameters(filter, filterItem);
      });
    }
  }

  generateUrls(testElement, projectTests) {
    const urls = testElement.ele("urls");
    if(projectTests.urls.length > 0 ) {
      projectTests.urls.forEach((url) => {
          urls.ele("url", {href: url});
      });
    }
  }

  checkIfTestsExist() {
    if(typeof this.props.project[0] !== "undefined") {
      if(typeof this.props.project[0].tests !== "undefined" && this.props.project[0].tests !== null) {
        if(Object.values(this.props.project[0].tests).length > 0) {
          return true;
        } 
      }
    }
    return false;
  }


  render () {
    if(this.checkIfTestsExist()) {
      return (
        <div className="generate-suite-btn" onClick={() => this.handleSuiteGenerating()}>
            GENERATE SUITE
        </div>
      )
    } else {
      return null;
    }
  }
}

function mapStateToProps(state) {
  return {
    test: state.test,
    project: state.project,
  }
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({}, dispatch)
}

export default connect(mapStateToProps, mapDispatchToProps)(GenerateSuiteButton);