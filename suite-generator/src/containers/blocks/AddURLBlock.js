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
import React, {Component} from 'react';
import {connect} from "react-redux";
import {bindActionCreators} from 'redux';
import {addUrlToTest, hideUrlInput, toggleUrlInput, removeUrlFromTest, toggleOptionsBox} from "../../actions";
import urlBlock from "../../constants/urlBlock";

class AddURLBlock extends Component {
  constructor(props){
    super(props);

    this.state = {
      urlValue: ""
    }

    this.handleInputChange = this.handleInputChange.bind(this);
  }

  componentDidMount() {
    this.props.hideUrlInput();
  }

  handleInputChange(value) {
    this.setState({
      ...this.state,
      urlValue: value
    });
  }

  handleUrlAdded() {
    if(this.state.urlValue.length > 0) {
      const urlItem = {
        ...urlBlock,
        parameters: {
          ...urlBlock.parameters,
          href: {
            ...urlBlock.parameters.href,
            current: this.state.urlValue
          }
        }
      };
      this.props.addUrlToTest(urlItem);
      this.setState({
        ...this.state,
        urlValue: ""
      });
      this.props.toggleUrlInput();
    }
  }

  handleURLClick(url, urlID) {
    this.props.toggleOptionsBox(url, urlID, null);
  }

// return (
//   <div key={index} className="block nested added-url" id={urlID} onClick={(ev) => this.handleURLClick(ev, url, urlID, this)}><span>{url}</span></div>
// )

  generateListOfUrls() {
    return Object.values(this.props.urls).map((url, index) => {
      const urlID = `${url.tag}-${index}`;
      return (
        <div key={index} className="block nested added-url" id={urlID} onClick={(ev) => this.handleURLClick(url, urlID)}><span>{url.parameters.href.current}</span></div>
      )
    });
  }

  render () {
    return (
      <div className={`urls-list ${this.props.staticBlocks["urls"] ? ("list-expanded") : ("list-hidden")} ${this.props.testName.isVisible ? "block-hidden" : ""}`}>
        {this.props.urlInput.isUrlInputVisible ? (
          <div className="block nested add-url-input-container" id="addurl-empty-input">
            <input className="add-url-input" value={this.state.urlValue} onChange={(ev) => this.handleInputChange(ev.target.value)} placeholder="Enter URL here"></input>
            <div className="add-url-button" onClick={() => this.handleUrlAdded()}>+</div>
          </div>
        ) : (
          <div className="block nested add-url" id="addurl-empty" onClick={() => this.props.toggleUrlInput()}>ADD URL</div>
        )}
        {this.generateListOfUrls()}
      </div>
    )
  }
}

function mapStateToProps(state) {
  return {
    urls: state.urls,
    urlInput: state.urlInput,
    staticBlocks: state.staticBlocks,  
    testName: state.testName,
  }
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({addUrlToTest, hideUrlInput, toggleUrlInput, removeUrlFromTest, toggleOptionsBox}, dispatch);
}

export default connect(mapStateToProps, mapDispatchToProps)(AddURLBlock);