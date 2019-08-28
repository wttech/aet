import React, {Component} from "react";
import SearchTests from "../../containers/search/SearchTests";
import TestsList from "../../containers/sidebars/TestsList";
import GenerateSuiteButton from "../../containers/buttons/GenerateSuiteButton";
import LoadSuiteButton from "../../containers/buttons/LoadSuiteButton";

class SidebarTests extends Component {

  render () {
    return (
      <div className="sidebar-tests">
        <SearchTests />
        <TestsList />
        <GenerateSuiteButton />
        <LoadSuiteButton />
      </div>
    )
  }
}

export default SidebarTests;