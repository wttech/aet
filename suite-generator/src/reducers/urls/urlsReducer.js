export default function (state = {}, action = null) {

  switch(action.type) {

    case "URL_ADDED": {
      const newURL = action.payload;
      const newState = [
        ...state,
        newURL
      ];
      return newState;
    }

    case "URL_REMOVED": {
      let newState = [...state];
      newState.forEach((url, index) => {
        if(url === action.payload) {
          newState = [
            ...newState.splice(0, index),
            ...newState.splice(index + 1)
          ]        
        }
      });
      return newState;
    }

    case "URL_LIST_CLEARED": {
      return [];
    }

    case "URLS_LOADED": {
      return action.payload;
    }

    default: {
      return state;
    }
  }
}