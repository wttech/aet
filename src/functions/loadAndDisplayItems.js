import React from 'react';
import FeatureItem from "../containers/sidebars/FeatureItem";
import generateID from "../functions/generateID";

const loadListOfItems = (array, searchString) => {
    return array.map((item) => {
      const featureID = generateID(item);
      if(typeof searchString === "undefined") {
        searchString = "";
      }
       if(item.type.toLowerCase().search(searchString.toLowerCase()) !== -1) {
        return (
          <FeatureItem itemKey={item.type.trim()} key={item.type.trim()} value={item.type} id={featureID} feature={item}/>
        )
      }
      return false;
    });
}

export default loadListOfItems;