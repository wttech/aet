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
      return this.props.test.map((test, index) => {
        if(typeof test !== 'undefined') {   
          if(test.dropTo.toLowerCase() === type) {
            const elemClass = type + "-list-container";
            let elemID = null;
            if(test.type === "Source W3CHTML5") {
              elemID = "source-comparators-" + index;
            } else {
              elemID = generateID(test) + "-" + index;
            }
            if(type === "comparators") {
              return (
              <div key={elemID} >
                <div className={elemClass}>
                  <div 
                  className="block custom nested" 
                  id={elemID} 
                  onClick={(ev) => this.toggleOptionsBox(ev, test, elemID)}
                  onDragOver={(ev) => handleDragOver(ev)}  
                  onDrop={(ev) => this.handleFilterDrop(ev, test)}>
                  <div className={`${test.group !== "Open" ? "block-type" : ""}`}>
                    <span>{test.type}</span>
                  </div>
                  {this.props.testOptions.blocksExpanded ? (
                    <div className="block-parameters-container">
                      {this.generateListOfBlockParameters(test)}
                    </div>
                  ) : null}
                  </div>
                  {this.generateListOfFilters(test, index)}
                </div>
                <DropContainer dropTo={"comparators"} />
              </div>
              )
            } else {
              return (
                <div className={elemClass}  key={elemID}>
                  <div 
                  className={`block custom nested  ${test.group === "Modifiers" ? "modifier" : ""} ${test.group === "Open" ? "static" : ""}`}
                  onClick={(ev) => this.toggleOptionsBox(ev, test, elemID)}
                  key={elemID} 
                  id={elemID}>
                  <div className={`${test.group !== "Open" ? "block-type" : ""}`}>
                    <span>{test.type}</span>
                  </div>
                  {test.group !== "Open" && this.props.testOptions.blocksExpanded ? (
                    <div className="block-parameters-container">
                      {this.generateListOfBlockParameters(test)}
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

  generateListOfBlockParameters(test) {
    if(test.parameters !== null) {
      return Object.values(test.parameters).map((param, index) => {
        return (
          <p className="block-parameter" key={index}>
            {param.isMandatory ? (
            <span className="block-parameter-mandatory">{param.name}</span>
            ) : (
            <span className="block-parameter-bold">{param.name}</span>
            )} 
            : {param.current === null || param.current === "" ? "null" : param.current}
          </p>
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
  }
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({addNewFilter, toggleOptionsBox}, dispatch)
}

export default connect(mapStateToProps, mapDispatchToProps)(UserBlockContainer)