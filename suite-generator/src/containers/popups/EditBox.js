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
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {updateFilterValue} from "../../actions"

class EditBox extends Component {
  constructor(props) {
    super(props);

    this.state = {
      inputVal: "",
      inputItem: null,
    };

    this.handleInputChange = this.handleInputChange.bind(this);
  }

  displayParameters(item) {
    if(item.parameters) {
      const parameters = Object.values(item.parameters);
      return parameters.map((param, index) => {
        let currentValue = param.current;
        if(currentValue === null) {
          currentValue = "";
        }
        return (
        <div key={index} className="param-wrapper" >
          <h3 className="title center">Type: {param.name}</h3>
          {param.isMandatory ? (<h5 className="mandatory center">This parameter is mandatory</h5>) : null}
          <p className="desc center">{param.description}</p>
          {param.values ? <div className="parameters center"><h3 className="values center">Choose a value:</h3><ul className="param-list center">{this.generateListOfParamValues(param, param.values)}</ul></div> : null}
          {param.default ? (<p className="default center">Default value: {param.default} </p>) : null}
          {param.current ? (<p className="current center">Current value: {param.current} </p>) : (<p className="current center">No current value has been set. Will use default or none on export, depending if the parameter is mandatory.</p>)}
          {!param.values ? (
            <div className="input-wrapper">
              <span className="input-text">Set value:</span>
              <div className="input-container">
                <input className="input" type="text" name={"param-" + index} onChange={(ev) => this.handleInputChange(param, ev.target.value)} placeholder="Input value" value={currentValue}></input>
              </div>
              </div>) : null}
            </div>
          )
      });
    } else {
      return (
        <p className="no-params center">This block has no parameters</p>
      )
    }
  }

  handleInputChange(param, value) {
    this.setState({
      inputVal: value,
      inputItem: param,
    });
    this.props.updateFilterValue(param, value);
  }

  generateListOfParamValues(param, values) {
    return values.map((value, index) => {
      return <li className="param center" key={index} onClick={() => this.setValueToCurrent(param, value)}>{value}</li>
    });
  }

  setValueToCurrent(param, value) {
    if(value) {
      param.current = value;
      this.props.updateFilterValue(param, value);
      this.setState({
        inputVal: ""
      });
    }
  }

  render() {
    const item = this.props.editBox.editBoxItem;
    return (
      <div className="edit-box-container">
        <div className="edit-box">       
          <h2 className="type center">Parameter: {item.type}</h2>
          <h2 className="group center">Group: {item.group}</h2>
          {this.displayParameters(item)}
        </div>
      </div>
    )
  }


}

function mapStateToProps(state) {
  return {
    test: state.test,
    editBox: state.editBox
  }
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({updateFilterValue}, dispatch)
}

export default connect(mapStateToProps, mapDispatchToProps)(EditBox)