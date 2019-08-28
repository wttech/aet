import React, { Component } from 'react';
import SidebarFeatures from "../sidebars/SidebarFeatures";
import SidebarTests from "../sidebars/SidebarTests";
import TestContainer from "../../containers/test/TestContainer";

class SuiteGenerator extends Component {

  render() {
    return (
      <div className="suite-generator">
        <SidebarFeatures/>
        <TestContainer />
        <SidebarTests />
      </div>
    )
  }
}

export default SuiteGenerator;