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