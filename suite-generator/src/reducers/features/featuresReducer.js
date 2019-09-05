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
import { FETCH_FEATURES_PENDING, FETCH_FEATURES_SUCCESS, FETCH_FEATURES_ERROR } from "../../actions/features.actions";

const initialState = {
    pending: false,
    features: {
        collectors: [],
        comparators: [],
        modifiers: [],
        dataFilters: []
    },
    error: null
}

export default function featuresReducer(state = initialState, action) {
    switch(action.type) {
        case FETCH_FEATURES_PENDING:
            return {
                ...state,
                pending: true
            }
        case FETCH_FEATURES_SUCCESS:
            return {
                ...state,
                pending: false,
                features: action.payload
            }
        case FETCH_FEATURES_ERROR:
            return {
                ...state,
                pending: false,
                error: action.payload
            }
        default:
            return state;
    }
}

export const getFeatures = state => state.features.features;
export const getFeaturesPending = state => state.features.pending;
export const getFeaturesError = state => state.features.error;