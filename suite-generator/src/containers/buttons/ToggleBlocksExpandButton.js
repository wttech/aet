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