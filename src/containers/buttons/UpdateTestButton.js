import React, {Component} from 'react';
import {connect} from "react-redux";
import {bindActionCreators} from 'redux';
import {updateTestInProject, clearTests, hideUrlInput, clearUrlsList, hideTestNameInput} from "../../actions"

class UpdateTest extends Component {

  updateTest() {
   if(this.props.test.length > 0) {
      this.props.updateTestInProject(this.props.test, this.props.urls, this.props.testName, true);
   }
  }

  render () {
      return (
        <div className={"update-test test-button "  + (this.props.testName.name && this.props.urls.length ? null : "btn-disabled")} onClick={() => this.updateTest()}>UPDATE TEST</div>
      )
  }
}

function mapStateToProps(state) {
  return {
    test: state.test,
    urls: state.urls,
    testName: state.testName
  }
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({updateTestInProject, clearTests, hideUrlInput, clearUrlsList, hideTestNameInput}, dispatch);
}

export default connect(mapStateToProps, mapDispatchToProps)(UpdateTest);