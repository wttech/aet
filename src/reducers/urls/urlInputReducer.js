export default function (state = {}, action = null) {

  switch(action.type) {

    case "URL_INPUT_HIDDEN": {
      const newState = {
        isUrlInputVisible: false
      }
      return newState;
    }

    case "URL_INPUT_TOGGLED": {
      let newState = null;
      if(state.isUrlInputVisible) {
        newState = {
          isUrlInputVisible: false
        }
      } else {
        newState = {
          isUrlInputVisible: true
        }
      }
      return newState;
    }

    default: {
      return state;
    }
  }
}