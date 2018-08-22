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