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
      className="feature"
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