export default function (state = {}, action = null) {

  switch(action.type) {

    case "URL_ADDED": {
      let newState = [...state];
      let newUrls = null;
      if(newState.length === 0) {
        newUrls = [action.payload];
      } else {
        newUrls = [...newState, action.payload];
      }
      newState = [...newUrls];
      return newState;
    }

    case "URL_REMOVED": {
      let newState = [...state];
      const index = newState.indexOf(action.payload);
      newState = [...newState.slice(0, index), ...newState.slice(index + 1)];
      return newState;
    }

    case "URL_LIST_CLEARED": {
      return [];
    }

    case "URLS_LOADED": {
      const urls = action.payload;
      return urls;
    }

    default: {
      return state;
    }
  }
}