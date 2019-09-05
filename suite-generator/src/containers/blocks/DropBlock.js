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
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {handleDragOver, handleDrop, handleDragLeave} from "../../functions/handleDragAndDrop"
import {addBlockToTest} from "../../actions";

class DropContainer extends Component {

  handleDropCase(ev) {
    const droppedItem = handleDrop(ev);
    if(droppedItem !== null) {
      this.props.addBlockToTest(droppedItem, ev.target);
    } else {
      console.log("Block couldn't be added properly.");
    }
  }

  render() {
    const dropContainerID = `${this.props.dropTo}-empty`
    return (
      <div 
        className="block nested empty" 
        id={dropContainerID}
        onDragOver={(ev) => handleDragOver(ev)}  
        onDrop={(ev) => this.handleDropCase(ev)}
        onDragLeave={(ev) => handleDragLeave(ev)}
        >
      </div>
    )
  }
}

function mapStateToProps(state) {
  return {
    test: state.test
  }
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({addBlockToTest}, dispatch)
}

export default connect(mapStateToProps, mapDispatchToProps)(DropContainer)