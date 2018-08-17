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
      let newState;
      if(action.payload === true) {
        newState = {
          name: "",
          isVisible: false
        }
      } else {
        newState = {
          name: state.name,
          isVisible: false
        }
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
        if(typeof state.name !== "undefined") {
          newState = {
            name: state.name,
            isVisible: true
          }
        } else {
          newState = {
            name: "",
            isVisible: true
          }
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