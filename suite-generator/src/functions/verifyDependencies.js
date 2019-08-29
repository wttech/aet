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
import generateID from "./generateID";

const verifyDependencies = (tests) => { 
  if(tests.length > 0) {
    tests.forEach((test, index) => {
      const deps = test.deps;
      const blocks = document.querySelectorAll("*[id^=" + deps + "]");
      if(blocks.length > 0) {
        let testID = generateID(test);
        if(test.type === "Source W3CHTML5") {
          testID = "source-comparators";
        } 
        handleFoundDependency(testID, deps, index)
      } else {
        let testID = generateID(test);
        if(test.type === "Source W3CHTML5") {
          testID = "source-comparators";
        } 
        handleNotFoundDependency(testID, test.depType, index);
      }
    });
  }
}

function handleNotFoundDependency(testID, depType, index) {
  document.querySelectorAll("#" + testID + "-" + index).forEach((block) => {
    if(depType === "Warning") {
      block.classList.add("block-warning");
    } else if(depType === "Error") {
      block.classList.add("block-error");
    }
  });
}

function handleFoundDependency(testID, depID, index) {
  const tests = document.querySelectorAll("#" + testID + "-" + index);
  const deps = document.querySelectorAll("#" + depID + "-" + index);
  const blocksArray = [...tests, ...deps];
  blocksArray.forEach((block) => {
    block.classList.remove("block-error");
    block.classList.remove("block-warning");
    block.classList.add("block-valid");
  });
}

export default verifyDependencies;