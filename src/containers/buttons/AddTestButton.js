import React, {Component} from 'react';
import {connect} from "react-redux";
import {bindActionCreators} from 'redux';
import {addTestToProject, clearTests, hideUrlInput, clearUrlsList, hideTestNameInput, initTestName} from "../../actions"

class AddTest extends Component {

  addTestToProject() {
    if(this.props.test.length > 0 && this.props.urls.length > 0) {
      document.querySelectorAll(".block").forEach((block) => {
        block.classList.add("block-hidden");
      });
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
    testName: state.testName
  }
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({addTestToProject, clearTests, hideUrlInput, clearUrlsList, hideTestNameInput, initTestName}, dispatch);
}

export default connect(mapStateToProps, mapDispatchToProps)(AddTest);