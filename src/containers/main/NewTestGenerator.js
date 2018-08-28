import React, {Component} from "react";
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import StaticBlock from "../../containers/blocks/StaticBlock";
import DropContainer from "../../containers/blocks/DropBlock";
import UserBlocksContainer from "../../containers/blocks/UserBlock";
import verifyDependencies from "../../functions/verifyDependencies";
import OptionsBox from "../../containers/popups/OptionsBox";
import EditBox from "../../containers/popups/EditBox";
import AddURLBlock from "../../containers/blocks/AddURLBlock";
import TestNameBlock from "../../containers/popups/TestName"
import {checkIfTestAlreadyExists} from "../../functions/checkIfTestExists"
import ProjectInfo from "../../containers/test/ProjectInfo";
import {testOptionsInited, hideEditBox, hideOptionsBox, hideUrlInput, hideTestNameInput, addTestToProject, clearTests, clearUrlsList} from "../../actions/";

class NewTestGenerator extends Component {

  componentDidMount() {
    this.props.testOptionsInited();
    this.props.hideOptionsBox();
    this.props.hideEditBox();
  }

  componentDidUpdate() {
    verifyDependencies(this.props.test);
  }

  hideBoxes(ev) {
    if(ev.target.classList.contains("tests-wrapper")
    || ev.target.classList.contains("test-container") 
    || ev.target.classList.contains("comparators-list-container") 
    || ev.target.classList.contains("modifiers-list-container") 
    || ev.target.classList.contains("collectors-list-container")
    || ev.target.classList.contains("tests")
    || ev.target.classList.contains("urls-list")
    ) {
      this.props.hideEditBox();
      this.props.hideOptionsBox();
      this.props.hideUrlInput();
      this.props.hideTestNameInput();
      document.querySelectorAll(".block").forEach((block) => {
        block.classList.remove("block-hidden");
      });
    }
  }

  handleNewTest() {
    if(this.props.test.length > 0 && this.props.testName.name && this.props.urls.length > 0) {
      if(!checkIfTestAlreadyExists(this.props.testName)) {
        this.props.addTestToProject(this.props.test, this.props.urls, this.props.testName);
        this.props.clearTests();
        this.props.hideUrlInput();
        this.props.clearUrlsList();
        this.props.hideTestNameInput();
        document.querySelectorAll(".block").forEach((block) => {
          block.classList.remove("block-hidden");
        });
      } else {
        alert("Test with that name already exists!");
      }
    }
  }

  handleKeyboardShortcuts(ev) {
    if(ev.key === "Enter") {
      if(this.props.editBox.isVisible) {
        this.props.hideEditBox(); 
      } else if(this.props.testName.isVisible) {
        this.handleNewTest();
      }   
    } else if(ev.key === "Escape") {
      if(this.props.editBox.isVisible) {
        this.props.hideEditBox(); 
      } else if(this.props.testName.isVisible) {
        this.props.hideTestNameInput(true);
        document.querySelectorAll(".block").forEach((block) => {
          block.classList.remove("block-hidden");
        });
      } 
    }
  }

  render () {
    return (
      <div className="test-container" onClick={(ev) => this.hideBoxes(ev)} tabIndex="0" onKeyUp={(ev) => this.handleKeyboardShortcuts(ev)}>
        {this.props.optionsBox.isVisible ? <OptionsBox /> : null}
        {this.props.editBox.isVisible ? <EditBox /> : null}
        {this.props.testName.isVisible ? <TestNameBlock /> : null}
        <ProjectInfo />
        <div className="tests-wrapper">
          <div className="tests">
            {this.props.testName.name && !this.props.testName.isVisible ? (<span className="test-name">Current test: {this.props.testName.name}</span>) : null}
            <StaticBlock type="start" value="Collectors" expandable={true} />
            <DropContainer dropTo="modifiers" />
            <UserBlocksContainer type="modifiers" />
            <StaticBlock type="open" value="Open"  expandable={false}/>
            <DropContainer dropTo="collectors" />
            <UserBlocksContainer type="collectors" />
            <StaticBlock type="end" value="Collectors" expandable={false}/>
            <StaticBlock type="start" value="Comparators"  expandable={true}/>
            <DropContainer dropTo="comparators" />
            <UserBlocksContainer type="comparators" />
            <StaticBlock type="end" value="Comparators" expandable={false}/>
            <StaticBlock type="start" value="URLs" expandable={true}/>
            <AddURLBlock type="open" value="Add URL" />
            <StaticBlock type="end" value="URLs"/>
          </div>
        </div>
      </div>
    )
  }
}

function mapStateToProps(state)
{
  return {
      test: state.test,
      project: state.project,
      optionsBox: state.optionsBox,
      editBox: state.editBox,
      urlInput: state.urlInput,
      testName: state.testName,
      urls: state.urls,
  }
}

function matchDispatchToProps(dispatch)
{
  return bindActionCreators({testOptionsInited, hideEditBox, hideOptionsBox, hideUrlInput, hideTestNameInput,  addTestToProject, clearTests, clearUrlsList}, dispatch);
}

export default connect(mapStateToProps, matchDispatchToProps)(NewTestGenerator)