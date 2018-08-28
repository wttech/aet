export const onSearchFeaturesChange = value => ({
  type: "SEARCH_FEATURES_CHANGED",
  payload: value
});

export const onSearchTestsChange = value => ({
  type: "SEARCH_TESTS_CHANGED",
  payload: value
});

export const createNewProject = project => ({
  type: "NEW_PROJECT_CREATED",
  payload: project
});

export const addBlockToTest = (item, dropContainer) => ({
  type: "NEW_BLOCK_ADDED",
  payload: {
    item: item,
    dropContainer: dropContainer
  }
});

export const clearTests = () => ({
  type: "TESTS_CLEARED",
  payload: null
});

export const addNewFilter = (event, item, parentItem) => ({
  type: "NEW_FILTER_ADDED",
  payload: {
    event: event,
    item: item,
    parentItem: parentItem
  }
});

export const deleteItemFromTest = (item, itemID, parentItem) => ({
  type: "BLOCK_DELETED",
  payload: {
    item: item,
    itemID: itemID,
    parentItem: parentItem
  }
});

export const toggleOptionsBox = (item, itemID, parentItem) => ({
  type: "OPTIONS_BOX_TOGGLED",
  payload: {
    item: item,
    itemID: itemID,
    parentItem: parentItem
  }
});

export const hideOptionsBox = () => ({
  type: "OPTIONS_BOX_HIDDEN",
  payload: null
});

export const toggleEditBox = (item, itemID) => ({
  type: "EDIT_BOX_TOGGLED",
  payload: {
    item: item,
    itemID: itemID
  }
});

export const hideEditBox = () => ({
  type: "EDIT_BOX_HIDDEN",
  payload: null,
});

export const updateFilterValue = (parent, param) => ({
  type: "FILTER_VALUE_CHANGED",
  payload: {
    parent: parent,
    param: param
  }
});

export const addTestToProject = (tests, urls, name) => ({
  type: "NEW_TEST_ADDED",
  payload: {
    tests: tests,
    urls: urls,
    name: name,
  }
});

export const updateTestInProject = (tests, urls, name, isValid) => ({
  type: "TEST_UPDATED",
  payload: {
    tests: tests,
    urls: urls,
    name: name,
    isValid: isValid
  }
});

export const addUrlToTest = (url) => ({
  type: "URL_ADDED",
  payload: url
});

export const hideUrlInput = () => ({
  type: "URL_INPUT_HIDDEN",
  payload: null
});

export const toggleUrlInput = () => ({
  type: "URL_INPUT_TOGGLED",
  payload: null,
});

export const removeUrlFromTest = (url) => ({
  type: "URL_REMOVED",
  payload: url
});

export const clearUrlsList = () => ({
  type: "URL_LIST_CLEARED",
  payload: null
});

export const handleTestNameChange = (value) => ({
  type: "TEST_NAME_CHANGED",
  payload: value
});

export const hideTestNameInput = (clear) => ({
  type: "TEST_NAME_CLEARED",
  payload: clear
});

export const initTestName = (clear) => ({
  type: "TEST_NAME_INITED",
  payload: clear
});

export const loadTest = (test) => ({
  type: "TEST_LOADED",
  payload: test
});

export const loadUrls = (urls) => ({
  type: "URLS_LOADED",
  payload: urls
});

export const loadTestName = (testName) => ({
  type: "TEST_NAME_LOADED",
  payload: testName
});

export const deleteTestFromProject = (name) => ({
  type: "TEST_DELETED",
  payload: name
});

export const deleteURLFromTest = (item, id) => ({
  type: "URL_DELETED",
  payload: {item, id}
})

export const toggleComparatorsList = () => ({
  type: "COMPARATORS_LIST_TOGGLE",
  payload: null
});

export const toggleCollectorsList = () => ({
  type: "COLLECTORS_LIST_TOGGLE",
  payload: null
});

export const toggleUrlsList = () => ({
  type: "URLS_LIST_TOGGLE",
  payload: null
});

export const initLists = () => ({
  type: "INIT_LISTS",
  payload: null,
});

export const setLoadedFileAsCurrentProject = (projectTree) => ({
  type: "SUITE_FILE_LOADED",
  payload: projectTree
});

export const testOptionsInited = () => ({
  type: "TEST_OPTIONS_INITED",
  payload: null
});

export const blocksExpandToggle = () => ({
  type: "BLOCKS_EXPAND_TOGGLED",
  payload: null
});

export const setTestAsInvalid = (test) => ({
  type: "TEST_SET_AS_INVALID",
  payload: test,
});