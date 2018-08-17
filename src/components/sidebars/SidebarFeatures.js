import React, {Component} from "react";
import SearchFeatures from "../../containers/search/SearchFeatures";
import { createFeatureList } from "../../functions/createFeatureList";

class SidebarFeatures extends Component {

  render () {
    return (
      <div className="sidebar-features">
        <SearchFeatures/>
        {createFeatureList()}
      </div>
    )
  }
}

export default SidebarFeatures;