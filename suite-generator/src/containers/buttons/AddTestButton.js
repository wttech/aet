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
import {addTestToProject, clearTests, hideUrlInput, clearUrlsList, hideTestNameInput, initTestName} from "../../actions"

class AddTest extends Component {

  addTestToProject() {
    if(this.props.test.length > 0 && this.props.urls.length > 0) {
      this.props.initTestName();
    }
  }

  render () {
    return (
      <div className={"add-test test-button " + (this.props.test.length && this.props.urls.length ? null : "btn-disabled") } onClick={() => this.addTestToProject()}>ADD TEST</div>
    )
  }
}

function mapStateToProps(state) {
  return {
    test: state.test,
    project: state.project,
    urls: state.urls,
    urlInput: state.urlInput,
  }
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({addTestToProject, clearTests, hideUrlInput, clearUrlsList, hideTestNameInput, initTestName}, dispatch);
}

export default connect(mapStateToProps, mapDispatchToProps)(AddTest);