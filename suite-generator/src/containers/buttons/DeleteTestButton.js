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
import {deleteTestFromProject, clearTests, hideUrlInput, clearUrlsList, hideTestNameInput} from "../../actions"

class DeleteTest extends Component {

  deleteTest() {
   if(this.props.test.length > 0 && this.props.testName.name) {
    this.props.deleteTestFromProject(this.props.testName);
    this.props.clearTests();
    this.props.hideUrlInput();
    this.props.clearUrlsList();
    this.props.hideTestNameInput(true);
   }
  }

  render () {
    return (
      <div className={"delete-test test-button "  + (this.props.testName.name ? null : "btn-disabled")} onClick={() => this.deleteTest()}>DELETE TEST</div>
    )
  }
}

function mapStateToProps(state) {
  return {
    test: state.test,
    testName: state.testName
  }
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({clearTests, hideUrlInput, clearUrlsList, deleteTestFromProject, hideTestNameInput}, dispatch);
}

export default connect(mapStateToProps, mapDispatchToProps)(DeleteTest);