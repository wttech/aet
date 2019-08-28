export default function (state = {}, action = null) {

  switch(action.type) {

    case "TEST_OPTIONS_INITED": {
      const newState = {
        blocksExpanded: true,
      }
      return newState;
    }

    case "BLOCKS_EXPAND_TOGGLED": {
      let newState = null;
      if(state.blocksExpanded === true) {
        newState = {
          blocksExpanded: false,
        }
      } else {
        newState = {
          blocksExpanded: true,
        }
      }
      return newState;
    }

    default: {
      return state;
    }
  }
}