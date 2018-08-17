export default function (state = {}, action = null) {

  switch(action.type) {

    case "NEW_PROJECT_CREATED": {
      localStorage.setItem("project", action.payload.project);
      localStorage.setItem("company", action.payload.company);
      localStorage.setItem("suite", action.payload.suite);
      localStorage.setItem("domain", action.payload.domain);
      let newState = null;
      newState = {
          project: action.payload.project,
          company: action.payload.company,
          suite: action.payload.suite,
          domain: action.payload.domain,
          tests: action.payload.tests,
      }
      return [newState];
    }

    case "NEW_TEST_ADDED": {
      let newState = [...state];
      newState = newState.pop();
      const name = action.payload.name;
      let oldTests = newState.tests;
      const newTests = action.payload.tests;
      const urls = action.payload.urls;
      let addedTests = null;
      if(oldTests === null) {
        addedTests = [{tests: newTests, urls, name}]
      } else {
        oldTests = Object.values(oldTests);
        addedTests = [...oldTests, {tests: newTests, urls, name}];
      }
      localStorage.setItem("tests", JSON.stringify({...addedTests}));
      return [{...newState, tests: addedTests}]
    }

    case "TEST_DELETED": {
      let newState = [...state];
      const deletedTestName = action.payload.name;
      newState = newState.pop();
      let tests = newState.tests;
      tests = Object.values(tests);
      Object.values(newState.tests).forEach((test, index) => {
        if(deletedTestName === test.name.name) {
          tests = [...tests.slice(0, index), ...tests.slice(index + 1)]
        }
      });
      localStorage.setItem("tests", JSON.stringify({...tests}));
      return [{...newState, tests}];
    }

    case "TEST_UPDATED": {
      let newState = [...state];
      newState = newState.pop();
      let currentTests = state[0].tests;
      const changedTest = action.payload;
      if(currentTests) {
        Object.values(currentTests).forEach((test, index) => {
          if(test.name.name === changedTest.name.name) {
            currentTests[index] = changedTest;
            localStorage.setItem("tests", JSON.stringify({...currentTests}));
            return [{...newState, tests: currentTests}]
          } 
        });
      }
      return state;
    }
    default: {
      return state;
    }
  }
}