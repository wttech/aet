export default function (state = {}, action = null) {

  switch (action.type) {

    case "EDIT_BOX_HIDDEN":
      {
        const newState = {
          isVisible: false,
          editBoxItem: null,
          editBoxItemID: null,
        }
        return newState;
      }

    case "EDIT_BOX_TOGGLED":
      {
        const parentItem = action.payload.item;
        const parentID = action.payload.itemID;
        let newState = null;
        if (state.isVisible === false) {
          newState = {
            isVisible: true,
            editBoxItem: parentItem,
            editBoxItemID: parentID,
          }
        } else {
          if (state.editBoxItemID === parentID) {
            newState = {
              isVisible: false,
              editBoxItem: null,
              editBoxItemID: null,
            }
          } else {
            newState = {
              isVisible: true,
              editBoxItem: parentItem,
              editBoxItemID: parentID,
            }
          }
        }
        return newState;
      }

    case "FILTER_VALUE_CHANGED":
      {
        let newState = state;
        const parent = action.payload.parent;
        const value = action.payload.param;
        const newParams = {
          ...newState.editBoxItem.parameters
        }
        Object.values(newParams).forEach((param) => {
          if(parent.name === param.name) {
            param.current = value;
          }
        });
        if (value !== null) {
          newState = {
            ...newState,
            editBoxItem: {
              ...newState.editBoxItem,
              parameters: {
                newParams,
              }
            }
          }
          return state;
        }
        break;
      }
    default: {
        return state;
    }
  }
}