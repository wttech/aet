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
import {fetchFeaturesPending, fetchFeaturesSuccess, fetchFeaturesError} from '../actions/features.actions'

function fetchFeatures() {
    return dispatch => {
        dispatch(fetchFeaturesPending())
        // TODO change url
        fetch('http://localhost:8181/configs/components')
        .then(res => res.json())
        .then(res => {
            if(res.error) {
                throw(res.error);
            }
            dispatch(fetchFeaturesSuccess(res));
            return res;
        })
        .catch(error => {
            dispatch(fetchFeaturesError(error));
        })
    }
}

export default fetchFeatures;