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
import React, {Component} from 'react'
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {handleDragStart, handleDragEnd} from "../../functions/handleDragAndDrop"
import {hideOptionsBox} from "../../actions"

class FeatureItem extends Component {

  onDragStart(ev, featureItem) {
    this.props.hideOptionsBox();
    handleDragStart(ev, featureItem);
  }

  render() {
    return (
      <li key={this.props.itemKey}
      className={`feature-${this.props.group.toLowerCase()}`}
      draggable
      onDragStart = {(ev) => this.onDragStart(ev, this.props.feature)}
      onDragEnd={(ev) => handleDragEnd(ev)}   
      >{this.props.value}</li>
    )
  }
}

function mapStateToProps(state)
{
    return {
        optionsBox: state.optionsBox,
    }
}

function matchDispatchToProps(dispatch)
{
    return bindActionCreators({hideOptionsBox}, dispatch);
}

export default connect(mapStateToProps, matchDispatchToProps)(FeatureItem)