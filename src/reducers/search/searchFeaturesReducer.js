export default function (state = {}, action = null) {

  switch(action.type) {

    case "SEARCH_FEATURES_CHANGED": {
      return [action.payload];
    }

    default: {
      return state;
    }
  }
}