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

    case "INIT_LISTS": {
      const newState = {
        comparators: true,
        modifiers: true,
        collectors: true,
        urls: true,
      }
      return newState;
    }

    case "COMPARATORS_LIST_TOGGLE": {
      const newState = {
        ...state,
        comparators: !state.comparators,
      }
      return newState;
    }

    case "COLLECTORS_LIST_TOGGLE": {
      const newState = {
        ...state,
        modifiers: !state.modifiers,
        collectors: !state.collectors
      }
      return newState;
    }

    case "URLS_LIST_TOGGLE": {
      const newState = {
        ...state,
        urls: !state.urls
      }
      return newState;
    }

    default: {
        return state;
    }
  }
}