import React, {Component} from "react";
import SearchTests from "../../containers/search/SearchTests";
import TestsList from "../../containers/test/TestsList";
import GenerateSuiteButton from "../../containers/buttons/GenerateSuiteButton"

class SidebarTests extends Component {

  render () {
    return (
      <div className="sidebar-tests">
        <SearchTests />
        <TestsList />
        <GenerateSuiteButton />
      </div>
    )
  }
}

export default SidebarTests;