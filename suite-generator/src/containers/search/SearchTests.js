/*
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import React, {Component} from "react";
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {onSearchTestsChange} from "../../actions"

class SearchTests extends Component {

  constructor(props) {
    super(props);
    this.state = {searchTestsValue: ''};

    this.handleChange = this.handleChange.bind(this);
  }

  handleChange(event) {
    this.setState({searchTestsValue: event.target.value});
  }

  render () {
    return (
      <div className="search-bar">
        {<input type="text" placeholder="Search..." onChange={(e) => this.props.onSearchTestsChange(e.target.value)}/>}
      </div>
    )
  }
}

function mapStateToProps(state)
{
  return {
    searchTests: state.searchTests
  }
}

function matchDispatchToProps(dispatch)
{
  return bindActionCreators({onSearchTestsChange}, dispatch);
}

export default connect(mapStateToProps, matchDispatchToProps)(SearchTests)