import React, {Component} from "react";
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {onSearchTestsChange} from "../../actions"

class SearchTests extends Component {

  constructor(props) {
    super(props);
    this.state = {searchTestsValue: ''};

    this.handleChange = this.handleChange.bind(this);
  }

  handleChange(event) {
    this.setState({searchTestsValue: event.target.value});
  }

  render () {
    return (
      <div className="search-bar">
        {<input type="text" placeholder="Search..." onChange={(e) => this.props.onSearchTestsChange(e.target.value)}/>}
      </div>
    )
  }
}

function mapStateToProps(state)
{
  return {
    searchTests: state.searchTests
  }
}

function matchDispatchToProps(dispatch)
{
  return bindActionCreators({onSearchTestsChange}, dispatch);
}

export default connect(mapStateToProps, matchDispatchToProps)(SearchTests)