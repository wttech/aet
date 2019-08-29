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
export const handleDragOver = (ev) => {
  ev.preventDefault();
  if(ev.target.classList.contains("valid")) {
    ev.target.classList.add("valid-onhover");
  }
}

export const handleDragEnd = (ev) => {
  ev.preventDefault();
  document.querySelectorAll(".empty").forEach((emptyBlock) => {
    emptyBlock.classList.remove("valid");
    emptyBlock.classList.remove("valid-onhover");
  });
  document.querySelectorAll(".data-filter-valid").forEach((filterBlock) => {
    filterBlock.classList.remove("data-filter-valid");
  });
}

export const handleDrop = (ev) => {
  ev.preventDefault();
  const recievedItem = ev.dataTransfer.getData("block");
  if(recievedItem) {
    const parsedItem = JSON.parse(recievedItem);
    if(parsedItem) {
      return parsedItem;
    } else {
      return null;
    }
  }
}

export const handleDragLeave = (ev) => {
  document.querySelectorAll(".empty").forEach((emptyBlock) => {
    emptyBlock.classList.remove("valid-onhover");
  });
}

export const handleDragStart = (ev, item) => {
  if(typeof document.getElementsByClassName("tests-wrapper")[0] !== "undefined") {
    if(item.dropTo === "Collectors" || item.dropTo === "Comparators" || item.dropTo === "Modifiers") {
      const itemGroup = item.dropTo.toString().toLowerCase() + "-empty";
      document.querySelectorAll("#" + itemGroup).forEach((emptyBlock) => {
        emptyBlock.classList.add("valid");
      });
      const offsetTop = document.querySelector("#" + itemGroup).offsetTop;
      smoothScrollTo(document.getElementsByClassName("tests-wrapper")[0], offsetTop, 400);
      const itemJSON = JSON.stringify(item);
      ev.dataTransfer.setData("block", itemJSON);
    } else {
      document.querySelectorAll("*[id^=" + item.dropTo + "]").forEach((block) => {
        block.classList.add("data-filter-valid");
      });
      const itemJSON = JSON.stringify(item);
      ev.dataTransfer.setData("block", itemJSON);   
    }
  }
}

export const handleFilterDragEnd = (ev) => {
  ev.preventDefault();
  document.querySelectorAll(".empty").forEach((emptyBlock) => {
    emptyBlock.classList.remove("data-filter-valid");
  });
}

const smoothScrollTo = (parent, offset, duration) => {
  const startingPoint = parent.scrollTop;
  let difference = offset - parent.scrollTop;
  let start;

  window.requestAnimationFrame(function step(timestamp) {
    if(!start) {
      start = timestamp;
    }
    let time = timestamp - start;
    let percent = Math.min(time/duration, 1);
    parent.scrollTo(0, startingPoint + difference * percent)

    if (time < duration) {
      window.requestAnimationFrame(step);
    }
  });
}