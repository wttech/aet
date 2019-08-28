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