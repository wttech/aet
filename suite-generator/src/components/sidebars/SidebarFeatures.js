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
import { connect } from 'react-redux';
import { bindActionCreators } from "redux";

import SearchFeatures from "../../containers/search/SearchFeatures";
import AppName from "../main/AppName";
import { CreateFeatureList } from "../../functions/createFeatureList";
import Spinner from 'react-bootstrap/Spinner'

import fetchFeaturesAction from "../../functions/fetchFeatures";
import {getFeatures, getFeaturesPending, getFeaturesError} from "../../reducers/features/featuresReducer";



class SidebarFeatures extends Component {
  constructor(props) {
    super(props);

    this.shouldComponentRender = this.shouldComponentRender.bind(this);
  }

  componentWillMount() {
    const {fetchFeatures} = this.props;
    fetchFeatures();
  }

  shouldComponentRender() {
    const {pending} = this.props;
    if (pending === false) return true;

    return false;
  }

  render () {

    const {features, error} = this.props;

    let content = null;

    if (this.shouldComponentRender()) {
      content = (
        <React.Fragment>
          <SearchFeatures/>
          <CreateFeatureList features={features} error={error}/>
        </React.Fragment>
      )
    } else {
      content = (
        <div className="features-spinner-container">
          <Spinner className="features-spinner" animation="grow" variant="light" role="status"/>
        </div>
      )
    }

    return (
      <div className="sidebar-features">
        <AppName />
        {content}
      </div>
    )
  }
}

const mapStateToProps = state => ({
  error: getFeaturesError(state),
  features: getFeatures(state),
  pending: getFeaturesPending(state)
}) 

const mapDispatchToProps = dispatch => bindActionCreators({
  fetchFeatures: fetchFeaturesAction
}, dispatch)

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(SidebarFeatures);