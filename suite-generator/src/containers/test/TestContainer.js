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
import React, {Component} from "react";
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import NewProjectGenerator from "../main/NewProjectGenerator";
import NewTestGenerator from "../main/NewTestGenerator";
import {createNewProject} from "../../actions";
import AddTest from "../buttons/AddTestButton";
import UpdateTest from "../buttons/UpdateTestButton";
import DeleteTest from "../buttons/DeleteTestButton";
import ClearTest from "../buttons/ClearTestButton";
import ToggleTestOptions from "../buttons/ToggleBlocksExpandButton";

class TestContainer extends Component {

  componentDidMount() {
    if(localStorage.company && localStorage.project && localStorage.suite) {
        const project = localStorage.getItem("project");
        const company = localStorage.getItem("company");
        const suite = localStorage.getItem("suite");
        const domain = localStorage.getItem("domain");
        const tests = JSON.parse(localStorage.getItem("tests"));
        this.props.createNewProject({project: project, company: company, suite: suite, domain: domain, tests: tests});
    }
  }

  render () {
    if(Object.keys(this.props.project).length > 0) {
      return (
      <div className="middle-section-container">
          <NewTestGenerator/>
          <div className="buttons-container">
            <ToggleTestOptions />
            <AddTest />
            <UpdateTest />
            <DeleteTest />
            <ClearTest />
          </div>
      </div>
      )
    } else {
      return <NewProjectGenerator/>
    }
  }
}

function mapStateToProps(state)
{
  return {
    test: state.test,
    project: state.project,
  }
}

function matchDispatchToProps(dispatch)
{
  return bindActionCreators({createNewProject}, dispatch);
}

export default connect(mapStateToProps, matchDispatchToProps)(TestContainer)