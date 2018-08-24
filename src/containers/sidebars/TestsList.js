import React, {Component} from "react";
import {bindActionCreators} from "redux";
import {connect} from "react-redux";
import {hideOptionsBox, hideEditBox, loadTest, loadUrls, loadTestName} from "../../actions"

class TestsList extends Component {

  generateTests() {
    const project = this.props.project;
    let listOfTests = project;
    if (Object.keys(listOfTests).lenght !== 0) {
      listOfTests = project[0];
    } 
    listOfTests = {...listOfTests}.tests;
    if (listOfTests) {
      return Object.values(listOfTests).map((test, index) => {
        let searchValue = null;
        Object.keys(this.props.searchTests).length ? searchValue = this.props.searchTests : searchValue = "";
        if(test.name.name.includes(searchValue)) {
          return (
            <div key={index} className="test-item" onClick={() => this.handleTestLoading(test)}>
              <h2 className="test-name">{test.name.name}</h2>
            </div> 
          )
        } else {
          return null;
        }
      });
    }
  }

  handleTestDelete(ev, test) {
    ev.preventDefault();
    ev.stopPropagation();
    this.props.deleteTestFromProject(test);
  }

  handleTestLoading(test) {
    this.props.hideOptionsBox();
    this.props.hideEditBox();
    this.props.loadTest(test);
    this.props.loadUrls(test.urls);
    this.props.loadTestName(test.name);
  }


  render () {
    return (
      <div className="tests-container">
        <h2 className="tests-main">TESTS: </h2>
        {this.generateTests()}
      </div>
    )
  }
}

function mapStateToProps(state) {
  return {
    test: state.test,
    project: state.project,
    searchTests: state.searchTests
  }
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({hideOptionsBox, hideEditBox, loadTest, loadUrls, loadTestName}, dispatch)
}

export default connect(mapStateToProps, mapDispatchToProps)(TestsList);