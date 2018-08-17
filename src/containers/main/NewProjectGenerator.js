import React, {Component} from "react";
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {createNewProject} from "../../actions"

class NewProjectGenerator extends Component {
  constructor(props) {
    super(props);

    this.state = {
      company: '',
      project: '',
      suite: '',
      domain: '',
      tests: null
    }
    this.handleChange = this.handleChange.bind(this);
  }

  componentDidMount() {
    document.getElementsByClassName("test-container")[0].style.overflow = "hidden";
  }

  handleChange(inputValue, inputType) {
    this.setState({
      [inputType]: inputValue
    });
  }

  handleNewProject(ev) {
    ev.preventDefault();
    if(this.state.project && this.state.company && this.state.suite) {
      this.props.createNewProject({project: this.state.project, company: this.state.company, suite: this.state.suite, domain: this.state.domain, tests: null}); 
    }
  }

  render () {
    return (
      <div className="test-container">
        <div className="new-project-form">
          <form onSubmit={(ev) => this.handleNewProject(ev)}>
              <div className="input-container">
                <label>Company name: </label>
                <input type="text" name="company-name" placeholder="Company name" onChange={(ev) => this.handleChange(ev.target.value, "company")}/>
              </div>
              <div className="input-container">
                <label>Project name: </label>
                <input type="text" name="project-name" placeholder="Project name" onChange={(ev) => this.handleChange(ev.target.value, "project")}/>
              </div> 
              <div className="input-container">
                <label>Domain link (can be left blank): </label>
                <input type="text" name="domain" placeholder="Default domain" onChange={(ev) => this.handleChange(ev.target.value, "domain")}/>
              </div>
              <div className="input-container">
                <label>Suite name: </label>
                <input type="text" name="suite-name" placeholder="Suite name" onChange={(ev) => this.handleChange(ev.target.value, "suite")}/>
              </div>
              <div className="input-container">
                <input className="submit-button" type="submit" value="SUBMIT" name="submit"/>
              </div>
          </form>
        </div>
      </div>
    )
  }
}

function mapStateToProps(state)
{
    return {
        project: state.project,
    }
}

function matchDispatchToProps(dispatch)
{
    return bindActionCreators({createNewProject}, dispatch);
}

export default connect(mapStateToProps, matchDispatchToProps)(NewProjectGenerator)