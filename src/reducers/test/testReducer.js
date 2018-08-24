export default function (state = {}, action = null) {

  switch(action.type) {

    case "NEW_BLOCK_ADDED": {
      const testObject = {
        type: action.payload.item.type,
        parameters: action.payload.item.parameters,
        deps: action.payload.item.deps,
        depType: action.payload.item.depType,
        dropTo: action.payload.item.dropTo,
        group: action.payload.item.group,
        wiki: action.payload.item.wiki,
        tag: action.payload.item.tag,
        proxy: action.payload.item.proxy,
        filters: null,
      }
      let newState = null;
      let offset = 0;
      let compOffset = 0;
      document.querySelectorAll(".empty").forEach((drop, index) => {
        if(drop === action.payload.dropContainer) {
          if(Object.keys(state).length !== 0 ) {
            state.forEach((test) => {
              if(test.dropTo === "Modifiers") {
                offset++;
              }
              testObject.dropTo === "Comparators" ? compOffset = 1 : compOffset = 0;
            });
            newState = [...state.slice(0, index - 1 - offset - compOffset), testObject, ...state.slice(index - 1 - offset - compOffset)];
          } else {
            newState = [...state, testObject];
          }
        }
      });
      return newState;
    }

    case "NEW_FILTER_ADDED": {
      const addedFilter = action.payload.item;
      let newState = [...state];
      newState.forEach((test, index) => {
        if(action.payload.parentItem === test) {
          if(test.filters === null) {
            test = {...test, filters: [addedFilter]};
            newState[index] = test;
          } else {
            const oldFilters = test.filters;
            const filters = [...oldFilters, addedFilter];
            test = {...test, filters: filters};
            newState[index] = test;
          }
        }
      });       
      return (newState);
    }

    case "BLOCK_DELETED": {
      if(action.payload.itemID.toString().includes("datafilters")) {
        let newState = null;
        let filterData = action.payload.itemID.split("-");
        const filterIndex = parseInt(filterData[filterData.length-2], 10);
        state.forEach((item) => {
          if(item === action.payload.parentItem) {
            const newFilters = [...item.filters.slice(0, filterIndex), 
                                ...item.filters.slice(filterIndex + 1)];
            item = {...item, filters: newFilters}
          }
          if(newState === null) {
            newState = [{...item}]
          } else {
            newState = [...newState, item];
          }
        });
        return newState;
      } else {
        let newState = state;
        newState.forEach((item) => {
          if(item === action.payload.item) {
            const index = newState.indexOf(item);
            newState = [...newState.slice(0, index),
                        ...newState.slice(index + 1)]
          }
        });
        return newState;
      }
    }

    case "TEST_LOADED": {
      let newState = null;
      const testInfo = Object.values(action.payload.tests);    
      testInfo.forEach((test) => {
        if(typeof test.type !== "undefined") {
          if(newState === null) {
            newState = [test];
          } else {
            newState = [...newState, test];
          }
        }
      });
      return newState;
    }

    case "TESTS_CLEARED": {
      return []
    }
    
    default: {
      return state;
    }
   
  }

}