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
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import {initLists, toggleCollectorsList, toggleComparatorsList, toggleUrlsList, hideOptionsBox} from "../../actions";

class StaticBlock extends Component {

  componentDidMount() {
    this.props.initLists();
  }

  handleExpanding() {
    if(this.props.expandable) {
      const category = this.props.value.toString().toLowerCase();
      if(category === "collectors") {
        this.props.toggleCollectorsList();
      } else if (category === "comparators") {
        this.props.toggleComparatorsList();
      } else {
        this.props.toggleUrlsList();
      }

      this.props.hideOptionsBox();
    }
  }

  render() {
    const isListExpanded = this.props.staticBlocks[this.props.value.toString().toLowerCase()];
    if(this.props.type === "start") {
      return (
        <div className={`block static start ${this.props.testName.isVisible ? "block-hidden" : ""}`} id={ `${this.props.value}-start` } onClick={() => this.handleExpanding()}>
          {this.props.value}
          <div className={`arrow-block ${isListExpanded ? ("arrow-expanded") : ("arrow-hidden")}`}>
            <i className={`fas fa-angle-right block-arrow`}></i>
          </div> 
        </div>
      )
    } else if (this.props.type === "end") {
      return (
        <div className={`block static start ${this.props.testName.isVisible ? "block-hidden" : ""}`}>
          {this.props.value}
        </div>
      )
    } else {
      return null;
    }
  }
}

function mapStateToProps(state) {
  return {
    staticBlocks: state.staticBlocks,
    testName: state.testName,
  }
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({initLists, toggleCollectorsList, toggleComparatorsList, toggleUrlsList, hideOptionsBox}, dispatch);
}

export default connect(mapStateToProps, mapDispatchToProps)(StaticBlock);