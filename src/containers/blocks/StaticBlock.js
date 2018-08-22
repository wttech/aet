import React, {Component} from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import {initLists, toggleCollectorsList, toggleComparatorsList, toggleUrlsList} from "../../actions";

class StaticBlock extends Component {

  componentDidMount() {
    this.props.initLists();
  }

  handleExpanding() {
    if(this.props.expandable) {
      const category = this.props.value.toString().toLowerCase();
      if(category === "collectors") {
        this.props.toggleCollectorsList();
      } else if (category === "comparators") {
        this.props.toggleComparatorsList();
      } else {
        this.props.toggleUrlsList();
      }
    }
  }

  render() {
    const isListExpanded = this.props.staticBlocks[this.props.value.toString().toLowerCase()];
    if(this.props.type === "start") {
      return (
        <div className="block static start" id={ `${this.props.value}-start` } onClick={() => this.handleExpanding()}>
          {this.props.value}
          <div className={`arrow-block ${isListExpanded ? ("arrow-expanded") : ("arrow-hidden")}`}>
            <i className={`fas fa-angle-right block-arrow`}></i>
          </div> 
        </div>
      )
    } else if (this.props.type === "end") {
      return (
        <div className="block static end">
          {this.props.value}
        </div>
      )
    } else if(this.props.type === "open") {
      return (
        <div className="block static nested">{this.props.value}</div>
      )
    } else {
      return null;
    }
  }
}

function mapStateToProps(state) {
  return {
    staticBlocks: state.staticBlocks,
  }
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({initLists, toggleCollectorsList, toggleComparatorsList, toggleUrlsList}, dispatch);
}

export default connect(mapStateToProps, mapDispatchToProps)(StaticBlock);