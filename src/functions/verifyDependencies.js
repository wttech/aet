import generateID from "./generateID";

const verifyDependencies = (tests) => { 
  if(tests.length > 0) {
    tests.forEach((test, index) => {
      const deps = test.deps;
      const blocks = document.querySelectorAll("*[id^=" + deps + "]");
      if(blocks.length > 0) {
        if(test.type === "Source W3CHTML5") {
          handleFoundDependency("source-comparators", deps, index)
        } else {
          handleFoundDependency(generateID(test), deps, index)
        }
      } else {
        handleNotFoundDependency(generateID(test), test.depType, index);
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