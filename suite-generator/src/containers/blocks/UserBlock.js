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
import React, {Component} from 'react';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import generateID from '../../functions/generateID';
import {handleDragOver, handleDrop} from "../../functions/handleDragAndDrop";
import {addNewFilter, toggleOptionsBox} from "../../actions";
import DropContainer from "./DropBlock"

class UserBlockContainer extends Component {

  handleFilterDrop(ev, parentItem) {
    const droppedItem = handleDrop(ev);
    if(droppedItem) {
      this.props.addNewFilter(ev, droppedItem, parentItem);
    }
  }

  toggleOptionsBox(ev, test, ID, parentItem) {
    ev.stopPropagation();
    this.props.toggleOptionsBox(test, ID, parentItem);
  }

  generateListOfUserBlocks(type) {
    if(this.props.test.length > 0) {
      return this.props.test.map((component, index) => {
        if(typeof component !== 'undefined') {   
          if(component.dropTo.toLowerCase() === type) {
            const elemClass = `${type}-list-container ${this.props.testName.isVisible ? "block-hidden" : ""}`;
            let elemID = null;
            if(component.type === "Source W3CHTML5") {
              elemID = "source-comparators-" + index;
            } else {
              elemID = generateID(component) + "-" + index;
            }
            if(type === "comparators") {
              return (
                <div key={elemID} >
                  <div className={elemClass}>
                    <div 
                    className="block custom nested comparator" 
                    id={elemID} 
                    onClick={(ev) => this.toggleOptionsBox(ev, component, elemID)}
                    onDragOver={(ev) => handleDragOver(ev)}  
                    onDrop={(ev) => this.handleFilterDrop(ev, component)}>
                    <div className={`${component.group !== "Open" ? "block-type" : ""}`}>
                      <span>{component.type}</span>
                    </div>
                    {this.props.testOptions.blocksExpanded ? (
                      <div className="block-parameters-container">
                        {this.generateListOfBlockParameters(component)}
                      </div>
                    ) : null}
                    </div>
                    {this.generateListOfFilters(component, index)}
                  </div>
                  <DropContainer dropTo={"comparators"} />
                </div>
              )
            } else {
              return (
                <div className={elemClass}  key={elemID}>
                  <div 
                  className={`block custom nested  ${component.group === "Modifiers" ? "modifier" : "collector"}`}
                  onClick={(ev) => this.toggleOptionsBox(ev, component, elemID)}
                  key={elemID} 
                  id={elemID}>
                  <div className="block-type">
                    <span>{component.type}</span>
                  </div>
                  {component.group !== "Open" && this.props.testOptions.blocksExpanded ? (
                    <div className="block-parameters-container">
                      {this.generateListOfBlockParameters(component)}
                    </div>
                  ) : null}
                  </div>
                  <DropContainer dropTo="collectors" />
                </div>
              )
            } 
          } else {
            return null;
          }
        }
        return null;
      });
    }
  }

  generateListOfBlockParameters(component) {
    if(component.parameters !== null) {
      return Object.values(component.parameters).map((param, index) => {
        if(typeof param.current === "undefined") {
          param.current = null;
        }
        return (
          <div className={`block-parameter ${param.isMandatory && (param.current === null || param.current === "") && param.defaultValue === null  ? ("block-parameter-mandatory-missing") : ""}`} key={index}>
              <div className="block-parameter-wrapper">
                <span className="block-parameter-bold">{param.name}</span>
                {param.isMandatory && (param.current === null || param.current === "") ? (
                  `: ${param.defaultValue}`
                ) : (
                  `: ${param.current}`
                )}
              </div>
          </div>
        )
      });
    }
  }

  generateListOfFilters(test, parentIndex) {
    return (test.filters !== null && Object.values(test.filters).map((filter, index) => {
      if(filter !== null) {
        if(filter.dropTo === generateID(test)) {
          const filterID = generateID(filter) + "-" + index + "-" + parentIndex;
          return (
            <div 
            key={index}
            id={filterID}
            onClick={(ev) => this.toggleOptionsBox(ev, filter, filterID, test)}
            className="block nested-twice filter-block">
            <div className="block-type">
              <span>{filter.type}</span>
            </div>
            {this.props.testOptions.blocksExpanded ? (
              <div className="block-parameters-container">
                {this.generateListOfBlockParameters(filter)}
              </div>
            ) : null}
            </div>
          )
        }  
      }
      return null;
    }));
  }

  render() {
    return (
    <div className={`${this.props.type}-list ${this.props.staticBlocks[this.props.type] ? ("list-expanded") : ('list-hidden')}`}>
      {this.generateListOfUserBlocks(this.props.type)}
    </div>
    )
  }
}

function mapStateToProps(state) {
  return {
    test: state.test,
    optionsBox: state.optionsBox,
    staticBlocks: state.staticBlocks,
    testOptions: state.testOptions,
    testName: state.testName,
  }
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({addNewFilter, toggleOptionsBox}, dispatch)
}

export default connect(mapStateToProps, mapDispatchToProps)(UserBlockContainer)