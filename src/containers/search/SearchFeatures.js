import React, {Component} from "react";
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {onSearchFeaturesChange} from "../../actions"

class SearchFeatures extends Component {

  render () {
    return (
      <div className="search-bar">
        {<input type="text" placeholder="Search..." onChange={(e) => this.props.onSearchFeaturesChange(e.target.value)} value={this.props.searchFeaturesValue}/>}
      </div>
    )
  }
}

function mapStateToProps(state)
{
  return {
    searchFeatures: state.searchFeatures
  }
}

function matchDispatchToProps(dispatch)
{
  return bindActionCreators({onSearchFeaturesChange}, dispatch);
}

export default connect(mapStateToProps, matchDispatchToProps)(SearchFeatures)