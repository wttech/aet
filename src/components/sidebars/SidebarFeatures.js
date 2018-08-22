import React, {Component} from "react";
import SearchFeatures from "../../containers/search/SearchFeatures";
import { CreateFeatureList } from "../../functions/createFeatureList";

class SidebarFeatures extends Component {

  render () {
    return (
      <div className="sidebar-features">
        <SearchFeatures/>
        <CreateFeatureList />
      </div>
    )
  }
}

export default SidebarFeatures;