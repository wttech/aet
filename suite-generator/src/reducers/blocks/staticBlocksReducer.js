export default function (state = {}, action = null) {

  switch (action.type) {

    case "INIT_LISTS": {
      const newState = {
        comparators: true,
        modifiers: true,
        collectors: true,
        urls: true,
      }
      return newState;
    }

    case "COMPARATORS_LIST_TOGGLE": {
      const newState = {
        ...state,
        comparators: !state.comparators,
      }
      return newState;
    }

    case "COLLECTORS_LIST_TOGGLE": {
      const newState = {
        ...state,
        modifiers: !state.modifiers,
        collectors: !state.collectors
      }
      return newState;
    }

    case "URLS_LIST_TOGGLE": {
      const newState = {
        ...state,
        urls: !state.urls
      }
      return newState;
    }

    default: {
        return state;
    }
  }
}