/*
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
          <FeatureItem itemKey={item.type.trim()} group={item.group} key={item.type.trim()} value={item.type} id={featureID} feature={item}/>
        )
      }
      return false;
    });
}

export default loadListOfItems;