/*
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import React, { Component } from 'react';
import { connect } from "react-redux";
import { bindActionCreators } from 'redux';
import { setLoadedFileAsCurrentProject, clearTests, hideUrlInput, clearUrlsList, hideTestNameInput} from "../../actions";
import { getFeatures } from "../../reducers/features/featuresReducer";
import urlBlock from "../../constants/urlBlock";

class UpdateTest extends Component {
  
  loadSuite(ev) {
    if (window.File && window.FileReader && window.FileList && window.Blob) {
      const vm = this;
      this.readLoadedFile(ev, (fileData) => {
        const parser = new DOMParser();
        const parsedFileData = this.removeSpecialCharacters(fileData);
        const xml = parser.parseFromString(parsedFileData, "text/xml");
        let attributes;
        if(xml.getElementsByTagName("suite")[0]) {
          attributes = xml.getElementsByTagName("suite")[0].attributes;
        }
        const projectData = vm.getProjectData(attributes);
        let projectTree = {
          company: projectData.company,
          domain: projectData.domain,
          project: projectData.project,
          suite: projectData.name,
          tests: {}
        };
        const testsInProject = xml.getElementsByTagName("test");
        if(!testsInProject.length > 0) {
          return;
        }
        Object.values(testsInProject).forEach((test, index) => {
          const testName = test.attributes[0].value;
          const collectorsChildren = xml.getElementsByTagName("collect")[index].children;
          const comparatorsChildren = xml.getElementsByTagName("compare")[index].children;
          const urlsChildren = xml.getElementsByTagName("urls")[index].children;
          const collectorsBlocks = vm.getTestCollectors(vm, collectorsChildren);
          const comparatorsBlocks = vm.getTestComparators(vm, comparatorsChildren);
          const testUrls = vm.getTestUrls(vm, urlsChildren, projectData.domain);
          const testObject = {
            name: {name: testName, isVisible: false},
            tests: [
              ...collectorsBlocks,
              ...comparatorsBlocks,
            ],
            urls: testUrls,
            isValid: true,
          }
          projectTree = {
            ...projectTree,
            tests: {
              ...projectTree.tests,
              [index]: testObject
            }
          }
        });
        this.clearTestTree();
        this.props.setLoadedFileAsCurrentProject(projectTree);
      });
    } else {
      alert('The File APIs are not fully supported in this browser.');
    }
  }

  removeSpecialCharacters(fileData) {
    const parse = fileData.replace(/&/g, '&amp;');
    return parse;
  }

  clearTestTree() {
    this.props.clearTests();
    this.props.hideUrlInput();
    this.props.clearUrlsList();
    this.props.hideTestNameInput(true);
  }

  getTestUrls(vm, urlsChildren, domain) {
    let urls = [];
    if(urlsChildren && Object.values(urlsChildren).forEach((child) => {
      let block = vm.getMatchingBlock("url", "urls");
      block = vm.setBlockParameters(block, child.attributes);
      urls.push(block)
    }));
    return urls;
  }

  getTestComparators(vm, comparatorsChildren) {
    let comparators = [];
    if(comparatorsChildren && Object.values(comparatorsChildren).forEach((child) => {
      const tag = child.nodeName;
      let block = vm.getMatchingBlock(tag, "comparators");
      block = vm.setBlockParameters(block, child.attributes);
      block = vm.setBlockFilters(vm, block, child.children);
      comparators.push(block);
    }));
    return comparators;
  }

  setBlockFilters(vm, block, filters) {
    let fil = [];
    Object.values(filters).forEach((filter) => {
      const tag = filter.nodeName;
      const filterBlock = vm.getMatchingBlock(tag, "data-filters");
      const filterTemp = vm.setBlockParameters(filterBlock, filter.attributes);
      fil.push(filterTemp);
    });
    block = {
      ...block,
      filters: {
        ...fil,
      }
    }
    return block;
  }

  getTestCollectors(vm, collectorsChildren) {
    let collectors = [];
    if(collectorsChildren && Object.values(collectorsChildren).forEach((child) => {
      const tag = child.nodeName;
      let block = vm.getMatchingBlock(tag, "collectors");
      if(block === null) {
        block = vm.getMatchingBlock(tag, "modifiers");
      }
      block = vm.setBlockParameters(block, child.attributes);
      collectors.push(block);
    }));

    return collectors;
  }

  setBlockParameters(block, params) {
    let newParam = null;
    if(block === null) {
      console.log(block)
      console.log(params)
    }
    if(block !== null && typeof block.parameters !== "undefined" && block.parameters !== null) {
      Object.values(block.parameters).forEach((blockParam) => {
        Object.values(params).forEach((param) => {
          if(param.name === blockParam.tag) {
            const tempParamName = param.name.replace(/-|_/g, '').toString().toLowerCase();
            newParam = {
              ...blockParam,
              current: param.value,
            };
            block = {
              ...block,
              parameters: {
                ...block.parameters,
                [tempParamName]: newParam,
              }
            };
          }
        });
      });
    }
    return block;
  }


  getMatchingBlock(tagName, type) {
    let block = null;
    switch (type) {
      case "modifiers":
        {
          block = this.props.features.modifiers.find((modifier) => {
            return modifier.tag === tagName;
          });
          break;
        }
      case "collectors":
        {
          block = this.props.features.collectors.find((collector) => {
            return collector.tag === tagName;
          });
          break;
        }
      case "comparators":
        {
          block = this.props.features.comparators.find((comparator) => { 
            return comparator.tag === tagName; 
          });
          break;
        }
      case "data-filters":
        {
          block = this.props.features.dataFilters.find((dataFilter) => {
            return dataFilter.tag === tagName;
          });
          break;
        }
      case "urls": {
        block = urlBlock;
        break;
      }
      default:
        {
          break;
        }
    }
    return block;
  }

  getProjectData(attributes) {
    let projectData = {};
    if(attributes && Object.values(attributes).forEach((att) => {
      projectData = {
        ...projectData,
        [att.name]: att.value,
      };
    }));
    return projectData;
  }

  readLoadedFile(ev, onFileLoadedCallback) {
    const input = ev.target;
    let data = null;
    const reader = new FileReader();
    reader.onload = function () {
      data = reader.result;
      onFileLoadedCallback(data)
    };
    if(typeof input.files[0] !== "undefined") {
      reader.readAsText(input.files[0]);
    }
  }

  render() {
    return (
      <div>
        <label htmlFor="fileLoader" className="sidebar-btn">LOAD SUITE</label>
        <input id="fileLoader" style={{display: "none"}} type="file" accept=".xml" onChange={(ev) => this.loadSuite(ev)}/>
      </div>
    )
  }
}

function mapStateToProps(state) {
  return {
    test: state.test,
    urls: state.urls,
    testName: state.testName,
    features: getFeatures(state)
  }
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({setLoadedFileAsCurrentProject, clearTests, hideUrlInput, clearUrlsList, hideTestNameInput}, dispatch);
}

export default connect(mapStateToProps, mapDispatchToProps)(UpdateTest);