import React, {Component} from 'react';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {deleteItemFromTest, toggleEditBox, hideOptionsBox, hideEditBox} from "../../actions"

class OptionsBox extends Component {

  updateOptionsBox() {
    const parentID = this.props.optionsBox.optionsBoxItemID;
    if(document.getElementById(parentID) !== null) {
      const boundingRect = document.getElementById(parentID).getBoundingClientRect();
      const currentScroll = document.getElementsByClassName("test-container")[0].scrollTop;
      const posX = boundingRect.x - 628;
      const posY = boundingRect.y - 2 + currentScroll;
      const optionsBox = document.querySelector(".options-box");
      optionsBox.style.left = posX + "px";
      optionsBox.style.top = posY + "px";
    } else {
      this.props.hideOptionsBox();
      this.props.hideEditBox();
    }
  }
  
  componentDidUpdate() {
    this.updateOptionsBox();
  }

  componentDidMount() {
    this.updateOptionsBox();
  }

  openWikiPage(item) {
    window.open(item.wiki, "_blank");
  }

  deleteItem(item, itemID, parentItem) {
    this.props.deleteItemFromTest(item, itemID, parentItem);
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
  return bindActionCreators({deleteItemFromTest, toggleEditBox, hideOptionsBox, hideEditBox}, dispatch)
}

export default connect(mapStateToProps, mapDispatchToProps)(OptionsBox)