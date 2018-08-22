import React, {Component} from "react";
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import listOfModifiers from "../../constants/listOfModifiers";
import listOfCollectors from "../../constants/listOfCollectors";
import listOfDataFilters from "../../constants/listOfDataFilters";
import listOfComparators from "../../constants/listOfComparators";
import loadAndDisplayItems from "../../functions/loadAndDisplayItems";
import toggleItemsVisibility from "../../functions/toggleItemsVisibility";

class FeaturesList extends Component {
  constructor(props) {
    super(props);

    this.state = {
      componentClass: 'feature ' + this.props.type,
      data: {
        "collectors": listOfCollectors,
        "comparators": listOfComparators,
        "modifiers": listOfModifiers,
        "data-filters": listOfDataFilters,
      }
    };
  }

  render () {
    return (
        <div className={this.state.componentClass}>
        <div  onClick={() => toggleItemsVisibility(this.props.type)}>
          <i className="fas fa-angle-right arrow-before-title"></i>
          <h3 className="feature-title">{this.props.type.toUpperCase()}: </h3>
        </div>
        <ul className="list-of-features">
          {loadAndDisplayItems(this.state.data[this.props.type], this.props.searchFeatures[0])}
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