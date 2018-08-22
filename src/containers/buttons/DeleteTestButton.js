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