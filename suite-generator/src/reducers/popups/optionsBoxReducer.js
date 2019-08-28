export default function (state = {}, action = null) {

  switch(action.type) {

    case "OPTIONS_BOX_TOGGLED": {
      const item = action.payload.item;
      const itemID = action.payload.itemID
      const parentItem = action.payload.parentItem;
      let newState = null;
      if(state.isVisible === false) {
        newState = {
          isVisible: true,
          optionsBoxItem: item,
          optionsBoxItemID: itemID,
          optionsBoxParentItem: parentItem,
        }
      } else {
        if(state.optionsBoxItemID === itemID) {
          newState = {
            isVisible: false,
            optionsBoxItem: null,
            optionsBoxItemID: null,
            optionsBoxParentItem: null,
          }
        } else {
          newState = {
            isVisible: true,
            optionsBoxItem: item,
            optionsBoxItemID: itemID,
            optionsBoxParentItem: parentItem,
          }
        }
      }
      return newState;
    }

    case "OPTIONS_BOX_HIDDEN": {
      const newState = {
        isVisible: false,
        optionsBoxItem: null,
        optionsBoxItemID: null,
        optionsBoxParentItem: null,
      } 
      return newState;
    }

    default: {
      return state;
    }
  }
}