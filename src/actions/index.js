export const onSearchFeaturesChange = value => {
    return {
      type: "SEARCH_FEATURES_CHANGED",
      payload: value
    }
}

export const onSearchTestsChange = value => {
  return {
    type: "SEARCH_TESTS_CHANGED",
    payload: value
  }
}

export const createNewProject = project => {
  return {
    type: "NEW_PROJECT_CREATED",
    payload: project
  }
}

export const addBlockToTest = (item, dropContainer) => {
  return {
    type: "NEW_BLOCK_ADDED",
    payload: {item: item, dropContainer: dropContainer}
  }
}

export const clearTests = () => {
  return {
    type: "TESTS_CLEARED",
    payload: null
  }
}

export const addNewFilter = (event, item, parentItem) => {
  return {
    type: "NEW_FILTER_ADDED",
    payload: {event: event, item: item, parentItem: parentItem}
  }
}

export const deleteItemFromTest = (item, itemID, parentItem) => {
  return {
    type: "BLOCK_DELETED",
    payload: {item: item, itemID: itemID, parentItem: parentItem}
  }
}

export const toggleOptionsBox = (item, itemID, parentItem) => {
  return {
    type: "OPTIONS_BOX_TOGGLED",
    payload: {item: item, itemID: itemID, parentItem: parentItem}
  }
}

export const hideOptionsBox = () => {
  return {
    type: "OPTIONS_BOX_HIDDEN",
    payload: null
  }
}

export const toggleEditBox = (item, itemID) => {
  return {
    type: "EDIT_BOX_TOGGLED",
    payload: {item: item, itemID: itemID}
  }
}

export const hideEditBox = () => {
  return {
    type: "EDIT_BOX_HIDDEN",
    payload: null,
  }
}

export const updateFilterValue = (parent, param) => {
  return {
    type: "FILTER_VALUE_CHANGED",
    payload: {parent: parent, param: param}
  }
}

export const addTestToProject = (tests, urls, name) => {
  return {
    type: "NEW_TEST_ADDED",
    payload: {tests: tests, urls: urls, name: name}
  }
}

export const updateTestInProject = (tests, urls, name, testID) => {
  return {
    type: "TEST_UPDATED",
    payload: {tests: tests, urls: urls, name: name, testID: testID}
  }
}

export const addUrlToTest = (url) => {
  return {
    type: "URL_ADDED",
    payload: url
  }
}

export const hideUrlInput = () => {
  return {
    type: "URL_INPUT_HIDDEN",
    payload: null
  }
}

export const toggleUrlInput = () => {
  return {
    type: "URL_INPUT_TOGGLED",
    payload: null,
  }
}

export const removeUrlFromTest = (url) => {
  return {
    type: "URL_REMOVED",
    payload: url
  }
}

export const clearUrlsList = () => {
  return {
    type: "URL_LIST_CLEARED",
    payload: null
  }
}

export const handleTestNameChange = (value) => {
  return {
    type: "TEST_NAME_CHANGED",
    payload: value
  }
}

export const hideTestNameInput = (clear) => {
  return {
    type: "TEST_NAME_CLEARED",
    payload: clear
  }
}

export const initTestName = (clear) => {
  return {
    type: "TEST_NAME_INITED",
    payload: clear
  }
}

export const loadTest = (test) => {
  return {
    type: "TEST_LOADED",
    payload: test
  }
}

export const loadUrls = (urls) => {
  return {
    type: "URLS_LOADED",
    payload: urls
  }
}

export const loadTestName = (testName) => {
  return {
    type: "TEST_NAME_LOADED",
    payload: testName
  }
}

export const deleteTestFromProject = (name) => {
  return { 
    type: "TEST_DELETED",
    payload: name
  }
}
