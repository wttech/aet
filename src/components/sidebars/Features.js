import React, {Component} from "react";
import FeaturesList from "../../containers/sidebars/FeaturesList";

class Features extends Component {

  render() {
    return (
      <div className="features-container">
        <FeaturesList type="collectors"/>
        <FeaturesList type="comparators"/>
        <FeaturesList type="modifiers"/>
        <FeaturesList type="data-filters"/>
      </div>
    )
  }
}

export default Features;