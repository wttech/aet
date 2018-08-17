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
  
  checkIfTestAlreadyExists(testName) {
    let exists = false;
    document.querySelectorAll(".tests-container>.test-item>.test-name").forEach((test) => {
      if(testName.name === test.innerHTML) {
        exists = true;
      }
    });
    return exists;
  }

  render () {
    if(this.props.test.length > 0 && this.props.urls.length > 0) {
      return (
        <div className="add-test test-button" onClick={() => this.addTestToProject()}>ADD TEST</div>
      )
    } else {
      return (
        <div className="add-test test-button btn-disabled">ADD TEST</div>
      )
    }

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