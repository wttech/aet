/*
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import React, {Component} from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux'; 
import { handleDomainChange } from '../../actions';


class ProjectInfo extends Component {
  constructor(props) {
    super(props);

    this.state = {
      domainInputValue: this.props.project[0].domain,
      domainInputPlaceholder: this.props.project[0].domain,
      isEditMode: false,
    };
  }

  createNewProject() {
    if (confirm('Are you sure you want to create a new project?')) { // eslint-disable-line
      window.localStorage.clear();
      document.location.reload();
    } 
  }

  handleDomainSave() {
    this.setState({domainInputPlaceholder: this.state.domainInputValue});
    this.props.handleDomainChange(this.state.domainInputValue);
    this.toggleDomainEdit();
  }

  handleDomainCancel() {
    this.setState({domainInputValue: this.state.domainInputPlaceholder});
    this.toggleDomainEdit();
  }

  toggleDomainEdit() {
    if (this.state.isEditMode) {
      this.setState({isEditMode: false});
    } else {
      this.setState({isEditMode: true});
    }
  }

  handleDomainInputChange(ev) {
    this.setState({domainInputValue: ev.target.value});
  }

  render() {
    return (
      <div className="project-info-container">
        <div className="project-info-block">
          <span>Project: </span> <h4>{this.props.project[0].project}</h4>
          <div className="new-proj-btn" onClick={() => this.createNewProject()}>+</div>
        </div>
        <div className="project-info-block">
          <span>Company: </span> <h4>{this.props.project[0].company}</h4>
        </div>
        <div className="project-info-block">
          <span>Suite: </span> <h4>{this.props.project[0].suite}</h4>
        </div>
        <div className="project-info-block">
        <span>Domain: </span> <h4 className={`${!this.state.isEditMode ? ("edit-domain-visible") : ("edit-domain-hidden")}`}><a href={this.state.domainInputPlaceholder} target="_blank" rel="noopener noreferrer">{this.state.domainInputPlaceholder}</a></h4>
        <textarea type="text" className={`edit-domain-input ${this.state.isEditMode ? ("edit-domain-visible") : ("edit-domain-hidden")}`} value={this.state.domainInputValue} onChange={(ev) => this.handleDomainInputChange(ev)}/>
        <div onClick={() => this.toggleDomainEdit()} className={`domain-icon ${!this.state.isEditMode ? ("edit-domain-visible") : ("edit-domain-hidden")}`}>
          <i className={`fas fa-pencil-alt edit-domain`}></i>
        </div>

        <div className="domain-icons-container">
          <div onClick={() => this.handleDomainSave()} className={`domain-icon-grouped ${this.state.isEditMode ? ("edit-domain-visible") : ("edit-domain-hidden")}`}>
            <i className={`fas fa-check-circle edit-domain-save`}></i>
          </div>
          <div onClick={() => this.handleDomainCancel()} className={`domain-icon-grouped ${this.state.isEditMode ? ("edit-domain-visible") : ("edit-domain-hidden")}`}>
            <i className={`fas fa-times-circle edit-domain-save`}></i>
          </div>
        </div>
        </div>
      </div>  
    )
  }
}

function mapStateToProps(state) {
  return {
    project: state.project
  }
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({handleDomainChange}, dispatch)
}

export default connect(mapStateToProps, mapDispatchToProps)(ProjectInfo);