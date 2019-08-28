import React, {Component} from 'react';
import {connect} from "react-redux";
import {bindActionCreators} from 'redux';
import {handleTestNameChange, initTestName, addTestToProject, clearTests, clearUrlsList, hideTestNameInput, hideUrlInput} from "../../actions"
import {checkIfTestAlreadyExists} from "../../functions/checkIfTestExists"

class TestNameBlock extends Component {
  constructor(props) {
    super(props);

    this.state = {
      initCompleted: false
    }
  }

  componentDidMount() {
    this.props.initTestName(true);
    this.setState({
      initCompleted: true
    });
  }

  generateNameInput() {
    return (
      <div>
        <input 
        className="test-name-input" 
        id="test-name-input" 
        type="text" 
        value={this.props.testName.name} 
        onChange={(ev) => this.handleNameChange(ev.target.value)} 
        placeholder="Enter test name">
        </input>  
        <div className="test-name-add-test" onClick={() => this.handleNewTest()}>ADD</div>
      </div> 
    )  
  }

  handleNewTest() {
   if(this.props.test.length > 0 && this.props.testName.name && this.props.urls.length > 0) {
    if(!checkIfTestAlreadyExists(this.props.testName)) {
      this.props.addTestToProject(this.props.test, this.props.urls, this.props.testName);
      this.props.clearTests();
      this.props.hideUrlInput();
      this.props.clearUrlsList();
      this.props.hideTestNameInput(true);
      document.querySelectorAll(".block").forEach((block) => {
        block.classList.remove("block-hidden");
      });
    } else {
      alert("Test with that name already exists!");
    }
   }
  }

  handleNameChange(val) {
    this.props.handleTestNameChange(val);
  }

  render () {
    return (
      <div className="block test-name-container">
        {this.state.initCompleted ? (this.generateNameInput()) : null}
      </div>
    )
  }
}

function mapStateToProps(state) {
  return {
    test: state.test,
    testName: state.testName,
    urls: state.urls,
  }
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({handleTestNameChange, initTestName, addTestToProject, clearTests, clearUrlsList, hideTestNameInput, hideUrlInput}, dispatch);
}

export default connect(mapStateToProps, mapDispatchToProps)(TestNameBlock);