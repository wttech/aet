import React, {Component} from "react";
import SearchFeatures from "../../containers/search/SearchFeatures";
import Features from "./Features";

class SidebarFeatures extends Component {

  render () {
    return (
      <div className="sidebar-features">
        <SearchFeatures/>
        <Features/>
      </div>
    )
  }
}

export default SidebarFeatures;