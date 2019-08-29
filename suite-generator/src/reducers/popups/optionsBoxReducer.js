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

  switch(action.type) {

    case "OPTIONS_BOX_TOGGLED": {
      const item = action.payload.item;
      const itemID = action.payload.itemID
      const parentItem = action.payload.parentItem;
      let newState = null;
      if(state.isVisible === false) {
        newState = {
          isVisible: true,
          optionsBoxItem: item,
          optionsBoxItemID: itemID,
          optionsBoxParentItem: parentItem,
        }
      } else {
        if(state.optionsBoxItemID === itemID) {
          newState = {
            isVisible: false,
            optionsBoxItem: null,
            optionsBoxItemID: null,
            optionsBoxParentItem: null,
          }
        } else {
          newState = {
            isVisible: true,
            optionsBoxItem: item,
            optionsBoxItemID: itemID,
            optionsBoxParentItem: parentItem,
          }
        }
      }
      return newState;
    }

    case "OPTIONS_BOX_HIDDEN": {
      const newState = {
        isVisible: false,
        optionsBoxItem: null,
        optionsBoxItemID: null,
        optionsBoxParentItem: null,
      } 
      return newState;
    }

    default: {
      return state;
    }
  }
}