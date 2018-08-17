import React, {Component} from 'react';
import {connect} from "react-redux";
import {bindActionCreators} from 'redux';
import {clearTests, hideUrlInput, clearUrlsList, hideTestNameInput} from "../../actions"

class ClearTest extends Component {

  clearTest() {
    this.props.clearTests();
    this.props.hideUrlInput();
    this.props.clearUrlsList();
    this.props.hideTestNameInput(true);
  }

  render () {
    if(this.props.test.length > 0) {
      return (
        <div className="clear-test test-button" onClick={() => this.clearTest()}>CLEAR TEST</div>
      )
    } else {
      return (
        <div className="clear-test test-button btn-disabled">CLEAR TEST</div>
      )
    }
  }
}

function mapStateToProps(state) {
  return {
    test: state.test,
  }
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({clearTests, hideUrlInput, clearUrlsList, hideTestNameInput}, dispatch);
}

export default connect(mapStateToProps, mapDispatchToProps)(ClearTest);