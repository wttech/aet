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
  }
)

export default reducer;