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
export default function (state = {}, action = null) {

  switch (action.type) {

    case "EDIT_BOX_HIDDEN":
      {
        const newState = {
          isVisible: false,
          editBoxItem: null,
          editBoxItemID: null,
        }
        return newState;
      }

    case "EDIT_BOX_TOGGLED":
      {
        const parentItem = action.payload.item;
        const parentID = action.payload.itemID;
        let newState = null;
        if (state.isVisible === false) {
          newState = {
            isVisible: true,
            editBoxItem: parentItem,
            editBoxItemID: parentID,
          }
        } else {
          if (state.editBoxItemID === parentID) {
            newState = {
              isVisible: false,
              editBoxItem: null,
              editBoxItemID: null,
            }
          } else {
            newState = {
              isVisible: true,
              editBoxItem: parentItem,
              editBoxItemID: parentID,
            }
          }
        }
        return newState;
      }

    case "FILTER_VALUE_CHANGED":
      {
        let newState = state;
        const parent = action.payload.parent;
        const value = action.payload.param;
        const newParams = {
          ...newState.editBoxItem.parameters
        }
        Object.values(newParams).forEach((param) => {
          if(parent.name === param.name) {
            param.current = value;
          }
        });
        if (value !== null) {
          newState = {
            ...newState,
            editBoxItem: {
              ...newState.editBoxItem,
              parameters: {
                newParams,
              }
            }
          }
          return state;
        }
        break;
      }
    default: {
        return state;
    }
  }
}