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
import {connect} from "react-redux";
import {bindActionCreators} from 'redux';
import {blocksExpandToggle} from "../../actions"

class DeleteTest extends Component {

  expandBlocks() {
   this.props.blocksExpandToggle();
  }

  render () {
    return (
      <div className="expand-test test-button"
      onClick={() => this.expandBlocks()}>
      {this.props.testOptions.blocksExpanded ? (
        <span>HIDE PARAMS</span>
      ) : (
        <span>SHOW PARAMS</span>
      )}
      </div>
    )
  }
}

function mapStateToProps(state) {
  return {
    test: state.test,
    testOptions: state.testOptions,
  }
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({blocksExpandToggle}, dispatch);
}

export default connect(mapStateToProps, mapDispatchToProps)(DeleteTest);