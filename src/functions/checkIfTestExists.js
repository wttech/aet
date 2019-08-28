export const checkIfTestAlreadyExists = (testName) => {
  let exists = false;
  document.querySelectorAll(".tests-container>.test-item>.test-name").forEach((test) => {
    if(testName.name === test.innerHTML) {
      exists = true;
    }
  });
  return exists;
}