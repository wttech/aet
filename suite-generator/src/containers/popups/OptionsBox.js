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
import {removeUrlFromTest, deleteItemFromTest, toggleEditBox, hideOptionsBox, hideEditBox} from "../../actions"

class OptionsBox extends Component {
  
  updateOptionsBox() {
    const parentID = this.props.optionsBox.optionsBoxItemID;
    if(document.getElementById(parentID) !== null) {
      const optionsBox = document.querySelector(".options-box");
      if(!optionsBox) { return }
      const boundingRect = document.getElementById(parentID).getBoundingClientRect();
      const currentScroll = document.getElementsByClassName("test-container")[0].scrollTop;
      const posX = boundingRect.x - 580;
      const posY = boundingRect.y - 2 + currentScroll;
      const offsetY = boundingRect.height / 2;
      const boxHeight = 40/2; // 40 is the height of the options box
      optionsBox.style.left = posX + "px";
      optionsBox.style.top = posY + offsetY - boxHeight + "px";
    } else {
      this.props.hideOptionsBox();
      this.props.hideEditBox();
    }
  }
  
  componentDidUpdate() {
    this.updateOptionsBox();
  }

  componentWillUnmount() {
    window.removeEventListener("scroll", (ev) => this.handleScrolling(ev), false);
    document.getElementsByClassName("tests-wrapper")[0].removeEventListener("scroll", (ev) => this.updateOptionsBox(), false);
  }

  componentDidMount() {
    window.removeEventListener("scroll", (ev) => this.handleScrolling(ev), false);
    document.getElementsByClassName("tests-wrapper")[0].addEventListener("scroll", (ev) => this.updateOptionsBox(), false);
    this.updateOptionsBox();
  }

  openWikiPage(item) {
    window.open(item.wiki, "_blank");
  }

  deleteItem(item, itemID, parentItem) {
    if(item.type === "URL") {
      this.props.removeUrlFromTest(item, parentItem);
    } else {
      this.props.deleteItemFromTest(item, itemID, parentItem);
    }
    this.props.hideOptionsBox()
  }

  editItem(ev, item, itemID) {
    ev.stopPropagation();
    this.props.toggleEditBox(item, itemID);
    this.props.hideOptionsBox()
  }

  render() {
    return (
      <div>
        <div className="options-box" id="option-box">
          <div className="option" onClick={(ev) => this.openWikiPage(this.props.optionsBox.optionsBoxItem)}>
            <i className="far fa-question-circle"></i>
          </div>
          <div className="option"  onClick={(ev) => this.editItem(ev, this.props.optionsBox.optionsBoxItem, this.props.optionsBox.optionsBoxItemID)}>
            <i className="fas fa-pencil-alt"></i>
          </div>
          <div className="option"  onClick={(ev) => this.deleteItem(this.props.optionsBox.optionsBoxItem, this.props.optionsBox.optionsBoxItemID, this.props.optionsBox.optionsBoxParentItem)}>
            <i className="fas fa-trash-alt"></i>
          </div>
        </div>
      </div>
    )
  }
}

function mapStateToProps(state) {
  return {
    test: state.test,
    optionsBox: state.optionsBox,
    editBox: state.editBox
  }
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({removeUrlFromTest, deleteItemFromTest, toggleEditBox, hideOptionsBox, hideEditBox}, dispatch)
}

export default connect(mapStateToProps, mapDispatchToProps)(OptionsBox)