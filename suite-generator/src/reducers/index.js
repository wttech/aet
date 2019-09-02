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
import {combineReducers } from 'redux'
import test from './test/testReducer';
import project from "./project/projectReducer";
import searchFeatures from "./search/searchFeaturesReducer";
import searchTests from "./search/searchTestsReducer";
import optionsBox from "./popups/optionsBoxReducer";
import editBox from "./popups/editBoxReducer";
import urls from "./urls/urlsReducer";
import urlInput from "./urls/urlInputReducer";
import testName from "./test/testNameReducer";
import staticBlocks from "./blocks/staticBlocksReducer";
import testOptions from "./test/testOptionsReducer";
import features from "./features/featuresReducer";

const reducer = combineReducers(
  {
    test: test,
    project: project,
    searchFeatures: searchFeatures,
    searchTests: searchTests,
    optionsBox: optionsBox,
    editBox: editBox,
    urls: urls,
    urlInput: urlInput,
    testName: testName,
    staticBlocks: staticBlocks,
    testOptions: testOptions,
    features: features,
  }
)

export default reducer;