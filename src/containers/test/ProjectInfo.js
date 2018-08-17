import React, {Component} from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux'; 

class ProjectInfo extends Component {

  createNewProject() {
    window.localStorage.clear();
    document.location.reload();
  }

  render() {
    return (
      <div className="project-info-container">
        <div className="project-info-block">
          <span>Project: </span> <h4>{this.props.project[0].project}</h4>
          <div className="new-proj-btn" onClick={() => this.createNewProject()}>NEW PROJECT</div>
        </div>
        <div className="project-info-block">
          <span>Company: </span> <h4>{this.props.project[0].company}</h4>
        </div>
        <div className="project-info-block">
          <span>Suite: </span> <h4>{this.props.project[0].suite}</h4>
        </div>
        <div className="project-info-block">
        <span>Domain: </span> <h4><a href={this.props.project[0].domain} target="_blank">{this.props.project[0].domain}</a></h4>
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
  return bindActionCreators({}, dispatch)
}

export default connect(mapStateToProps, mapDispatchToProps)(ProjectInfo);