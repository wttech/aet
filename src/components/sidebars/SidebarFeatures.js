import React, {Component} from "react";
import SearchFeatures from "../../containers/search/SearchFeatures";
import AppName from "../main/AppName";
import { CreateFeatureList } from "../../functions/createFeatureList";

class SidebarFeatures extends Component {

  render () {
    return (
      <div className="sidebar-features">
        <AppName />
        <SearchFeatures/>
        <CreateFeatureList />
      </div>
    )
  }
}

export default SidebarFeatures;