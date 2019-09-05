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

    case "TEST_NAME_CHANGED": {
      const newState = {
        name: action.payload,
        isVisible: true
      };
      return newState;
    }

    case "TEST_NAME_CLEARED": {
      const newState = {
        name: (action.payload === true ? "" : state.name),
        isVisible: false
      }
      return newState;
    }

    case "TEST_NAME_INITED": {
      let newState;
      if(action.payload) {
        newState = {
          name: "",
          isVisible: true
        }
      } else {
        newState = {
          name: (typeof state.name === "undefined" ? "" : state.name),
          isVisible: true
        }
      }
      return newState;
    }

    case "TEST_NAME_LOADED": {
      const newState = {
        name: action.payload.name,
        isVisible: false
      };
      return newState;
    }

    default: {
      return state;
    }
  }
}