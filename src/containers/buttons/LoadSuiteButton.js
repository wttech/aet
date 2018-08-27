import React, { Component } from 'react';
import { connect } from "react-redux";
import { bindActionCreators } from 'redux';
import { setLoadedFileAsCurrentProject, clearTests, hideUrlInput, clearUrlsList, hideTestNameInput} from "../../actions"
import listOfComparators from "../../constants/listOfComparators";
import listOfCollectors from "../../constants/listOfCollectors";
import listOfModifiers from "../../constants/listOfModifiers";
import listOfDataFilters from "../../constants/listOfDataFilters";

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
          const testUrls = vm.getTestUrls(urlsChildren, projectData.domain);
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

  getTestUrls(urlsChildren, domain) {
    let urls = [];
    if(urlsChildren && Object.values(urlsChildren).forEach((child, index) => {
      const validChild = Object.values(child.attributes).find((attr) => {
        return attr.name === "href";
      });
      if(domain) {
        if(typeof validChild.value.split(domain)[1] !== "undefined") {
          urls.push(validChild.value.split(domain)[1]);
        } else {
          urls.push(validChild.value.split(domain)[0]);
        }
      } else {
        urls.push(validChild.value)
      }
      if(typeof urls[index] === "undefined") {
        urls[index] = "BAD_URL_OR_DOMAIN";
      }
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
          listOfModifiers.forEach((modifier) => {
            if (modifier.tag === tagName) {
              block = modifier;
            }
          });
          break;
        }
      case "collectors":
        {
          listOfCollectors.forEach((collector) => {
            if (collector.tag === tagName) {
              block = collector;
            }
          });
          break;
        }
      case "comparators":
        {
          listOfComparators.forEach((comparator) => {
            if (comparator.tag === tagName) {
              block = comparator;
            }
          });
          break;
        }
      case "data-filters":
        {
          listOfDataFilters.forEach((datafilter) => {
            if (datafilter.tag === tagName) {
              block = datafilter;
            }
          });
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
    testName: state.testName
  }
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({setLoadedFileAsCurrentProject, clearTests, hideUrlInput, clearUrlsList, hideTestNameInput}, dispatch);
}

export default connect(mapStateToProps, mapDispatchToProps)(UpdateTest);