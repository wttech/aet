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
      <div>
          <NewTestGenerator/>
          <div className="buttons-container">
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