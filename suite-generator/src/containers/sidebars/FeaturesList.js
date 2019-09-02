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
import loadAndDisplayItems from "../../functions/loadAndDisplayItems";
import toggleItemsVisibility from "../../functions/toggleItemsVisibility";

class FeaturesList extends Component {
  render () {

    return (
        <div className={'feature ' + this.props.type}>
        <div  onClick={() => toggleItemsVisibility(this.props.type)}>
          <h3 className="feature-title">{this.props.name} </h3>
          <i className="fas fa-angle-right arrow-before-title"></i>
        </div>
        <ul className="list-of-features">
          {loadAndDisplayItems(this.props.data, this.props.searchFeatures[0])}
        </ul>
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
    return bindActionCreators({}, dispatch);
}

export default connect(mapStateToProps, matchDispatchToProps)(FeaturesList)