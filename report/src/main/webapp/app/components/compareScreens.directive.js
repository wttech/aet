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
define(['angularAMD'], function (angularAMD) {
  'use strict';
  angularAMD.directive('aetCompareScreens', compareScreensDirective);


  function compareScreensDirective() {
    return {
      restrict: 'A',
      link: linkFunc
    };
  }

  function linkFunc(scope) {

    scope.advancedScreenComparison = function () {

      var tab = document.querySelector('.test-tabs > .tab-content > .tab-pane.ng-scope.active');
      var wrapper = tab.querySelector('.page-main .layout-compare');
      var initedClass = 'beta-active';
      var arrangeTimeout;

      function prepareMarkup(wrapper) {
        wrapper.innerHTML = '</div><div class="difs"></div><div class="customMasks"><div class="customMask maskAtop"></div><div class="customMask maskAbot"></div><div class="customMask maskBtop"></div><div class="customMask maskBbot"></div><div class="customLabel labelA"><div class="ruler"></div><div class="text"></div></div><div class="customLabel labelB"><div class="ruler"></div><div class="text"></div></div></div><div class="canvas-wrapper"></div>';
      }

      if (!wrapper) {
        console.warn('Can\'t find wrapper');
        return;
      } else if (wrapper.classList.contains(initedClass)) {
        console.warn('Beta already inited');
        return;
      } else {
        wrapper.classList.add(initedClass);
      }

      var items = wrapper.querySelectorAll('.layout-compare-item .img-responsive:not(.mask)');

      if (items.length !== 2) {
        return;
      }

      prepareMarkup(wrapper);

      var img = {
        imgA: items[0].src,
        imgB: items[1].src,
      };

      var canvas = {
        canvasWrapper: wrapper.querySelector('.canvas-wrapper'),
        canvasA: document.createElement('canvas'),
        canvasB: document.createElement('canvas'),
      };

      var label = {
        labelA: wrapper.querySelector('.labelA'),
        labelB: wrapper.querySelector('.labelB'),
      };

      var ruler = {
        rulerA: label.labelA.querySelector('.ruler'),
        rulerB: label.labelB.querySelector('.ruler'),
      };

      var mask = {
        maskA: {
          top: wrapper.querySelector('.maskAtop'),
          bot: wrapper.querySelector('.maskAbot'),
        },
        maskB: {
          top: wrapper.querySelector('.maskBtop'),
          bot: wrapper.querySelector('.maskBbot'),
        },
        customMask: wrapper.querySelector('.customMasks'),
        maskSize: null,
      };

      var context = {
        contextA: canvas.canvasA.getContext('2d'),
        contextB: canvas.canvasB.getContext('2d'),
      };

      var imgSize = {
        imgSizeA: null,
        imgSizeB: null,
      };

      var simple = {
        simpleA: null,
        simpleB: null,
      };

      var cursor = {
        cursorA: 0,
        cursorB: 0,
        cursorBstart: 0,
      };

      var difs = {
        main: wrapper.querySelector('.difs'),
        difsA: [],
        difsB: [],
      };

      var invertedGroups = {
        invertedGroupsA: [],
        invertedGroupsB: [],
      };

      var groups = [];
      var group = [];

      loadImage(img.imgA, imgSize.imgSizeA, canvas.canvasA, context.contextA, simple.simpleA, function (returnedSimple, returnedImgSize) {
        simple.simpleA = returnedSimple;
        imgSize.imgSizeA = returnedImgSize;
      });

      loadImage(img.imgB, imgSize.imgSizeB, canvas.canvasB, context.contextB, simple.simpleB, function (returnedSimple, returnedImgSize) {
        simple.simpleB = returnedSimple;
        imgSize.imgSizeB = returnedImgSize; // get rid of the callback here and move it outside of that func
        arrangeMasks(mask, difs, canvas, arrangeTimeout, callbackFunction);
      });

      function callbackFunction(params) {
        mask.maskSize = params;
        onImagesReady(mask, imgSize, simple, label, ruler, invertedGroups, difs, wrapper);
        processLines(cursor, simple,  group, groups);
        invertGroups(simple, groups, invertedGroups);
        drawDifferences(difs, imgSize, invertedGroups, simple, context);
      }
      
      canvas.canvasWrapper.appendChild(canvas.canvasA);
      canvas.canvasWrapper.appendChild(canvas.canvasB);
      };

    function drawDifferences(difs, imgSize, invertedGroups, simple, context) {
      var maxHeight = Math.max(imgSize.imgSizeA.height, imgSize.imgSizeB.height);
      var proc = 100 / maxHeight;

      for (var i = 0; i < invertedGroups.invertedGroupsA.length; i++) {
        var newDifferenceA = document.createElement('canvas');
        var newDifferenceContextA = newDifferenceA.getContext('2d');
        var newDifferenceB = document.createElement('canvas');
        var newDifferenceContextB = newDifferenceB.getContext('2d');
        var sizes = getGroupsSizes(i, invertedGroups, simple);
        if (sizes) {
          var heightA = sizes.aTo - sizes.aFrom;
          var heightB = sizes.bTo - sizes.bFrom;

          newDifferenceA.width = newDifferenceB.width = newDifferenceContextA.width = newDifferenceContextB.width = imgSize.imgSizeA.width;
          newDifferenceA.height = newDifferenceContextA.height = heightA;
          newDifferenceB.height = newDifferenceContextB.height = heightB;
          newDifferenceB.style.left = '52%';

          difs.main.appendChild(newDifferenceA);
          difs.main.appendChild(newDifferenceB);

          newDifferenceA.style.top = sizes.aFrom * proc + '%';
          newDifferenceB.style.top = sizes.bFrom * proc + '%';

          newDifferenceA.style.height = (sizes.aTo - sizes.aFrom) * proc + '%';
          newDifferenceB.style.height = (sizes.bTo - sizes.bFrom) * proc + '%';

          var dataA = context.contextA.getImageData(0, sizes.aFrom, imgSize.imgSizeA.width, Math.max(heightA, 1));
          var dataB = context.contextB.getImageData(0, sizes.bFrom, imgSize.imgSizeA.width, Math.max(heightB, 1));
          var imageData;

          if (dataA.height > dataB.height) {
            imageData = convertDifferenceData(dataA, dataB);
          } else {
            imageData = convertDifferenceData(dataB, dataA);
          }

          newDifferenceContextA.putImageData(imageData, 0, 0, 0, 0, imgSize.imgSizeA.width, heightA);
          if (heightB > 0) {
            newDifferenceContextB.putImageData(imageData, 0, 0, 0, 0, imgSize.imgSizeA.width, heightB);
          }

          difs.difsA.push(newDifferenceA);
          difs.difsB.push(newDifferenceB);
        }
      }
    }

    function convertDifferenceData(firstImgData, secondImgData) {
      for (var index = 0; index < firstImgData.data.length; index += 4) {
        if (typeof secondImgData.data[index] !== 'undefined') {
          if (firstImgData.data[index] !== secondImgData.data[index] || firstImgData.data[index + 1] !== secondImgData.data[index + 1] || firstImgData.data[index + 2] !== secondImgData.data[index + 2]) {
            firstImgData = dataToRGB(firstImgData, index, 255, 0, 0, 127);
          } else {
            firstImgData = dataToRGB(firstImgData, index, 0, 0, 0, 0);
          }
        } else {
          firstImgData = dataToRGB(firstImgData, index, 255, 0, 0, 127);
        }
      }
      return firstImgData;
    }

    function dataToRGB(imgData, index, red, green, blue, alpha) {
      imgData.data[index] = red;
      imgData.data[index + 1] = green;
      imgData.data[index + 2] = blue;
      imgData.data[index + 3] = alpha;
      return imgData;
    }

    function loadImage(url, imgSize, canvas, context, simple, callbackOnSuccess) {
      var image = new Image();
      image.setAttribute('crossOrigin', '');
      image.src = url;

      image.onload = function () {
        imgSize = {
          width: image.naturalWidth,
          height: image.naturalHeight,
        };
        canvas.width = context.width = imgSize.width;
        canvas.height = context.height = imgSize.height;
        context.drawImage(image, 0, 0);
        simple = simplifyImage(context.getImageData(0, 0, imgSize.width, imgSize.height));
        callbackOnSuccess(simple, imgSize);
      };

      image.onerror = function () {
        return false;
      };
    }

    function rgb2hex(r, g, b) {
      return ('0' + r.toString(16)).slice(-2) + ('0' + g.toString(16)).slice(-2) + ('0' + b.toString(16)).slice(-2);
    }

    function simplifyImage(imageData) {
      var imageWidth = imageData.width;
      var offset = 0;
      var data = imageData.data;
      var simplifiedList = [];
      var previousLine;
      var valuesPerColor = 4;

      for (var y = 0; y < data.length / (imageWidth * valuesPerColor); y++) {
        var previousElement = simplifiedList.length ? simplifiedList[simplifiedList.length - 1] : {};
        var previousColor = '';
        var simplifiedLine = '';
        var encounters = 0;
        for (var x = 0; x < imageWidth; x++) {
          var index = y * imageWidth * valuesPerColor + x * valuesPerColor;
          var currentColor = rgb2hex(data[index + 0], data[index + 1], data[index + 2]);
          if (currentColor !== previousColor) {
            if (encounters > 0) {
              simplifiedLine += ',' + encounters + '|';
            }
            simplifiedLine += currentColor;
            previousColor = currentColor;
          }
          encounters++;
        }

        if (encounters > 0) {
          simplifiedLine += ',' + encounters;
        }

        if (previousLine) {
          if (previousLine === simplifiedLine) {
            if (previousElement && previousElement.type === 'space' && previousElement.value === previousLine) {
              simplifiedList[simplifiedList.length - 1].size++;
            } else {
              addNewSpace(simplifiedLine, simplifiedList, offset);
            }
            offset++;
          } else {
            if (previousElement && previousElement.type === 'block') {
              previousElement.size++;
              previousElement.content.push(simplifiedLine);
            } else {
              addNewBlock(simplifiedLine, simplifiedList, offset);
            }
            offset++;
          }
        }
        previousLine = simplifiedLine;
      }

      simplifiedList.push({
        type: 'block',
        content: [],
        size: 0,
        offset: offset + 1
      });

      return simplifiedList;
    }

    function addNewSpace(simplifiedLine, simplifiedList, offset) {
      var newElement = {
        type: 'space',
        value: simplifiedLine,
        size: 2,
        offset: offset
      };
      simplifiedList.push(newElement);
    }

    function addNewBlock(simplifiedLine, simplifiedList, offset) {
      var newElement = {
        type: 'block',
        content: [simplifiedLine],
        size: 1,
        offset: offset ? offset + 1 : 0
      };
      simplifiedList.push(newElement);
    }

    function processLines(cursor, simple, group, groups) {
      if (cursor.cursorA === (simple.simpleA.length - 1)) {
        pushGroups();
        return;
      } else if (cursor.cursorB === (simple.simpleB.length - 1)) {
        cursor.cursorB = cursor.cursorBstart;
        cursor.cursorA++;
        pushGroups();
      } else {
        var lastBlockMatchA;
        var lastBlockMatchB;
        if (simple.simpleA[cursor.cursorA].type === simple.simpleB[cursor.cursorB].type) {
          if (simple.simpleA[cursor.cursorA].type === 'space') {
            handleSpaceLineProcessing();
          } else {
            handleBlockLineProcessing();
          }
        } else {
          cursor.cursorB++;
        }
      }
      processLines(cursor, simple, group, groups);

      function handleSpaceLineProcessing() {
        if (simple.simpleA[cursor.cursorA].color === simple.simpleB[cursor.cursorB].color) {
          if (simple.simpleA[cursor.cursorA].size === simple.simpleB[cursor.cursorB].size) {
            if (simple.simpleA[cursor.cursorA].match !== cursor.cursorB) {
              changeCursor('space');
            } else {
              cursor.cursorB = cursor.cursorBstart;
              cursor.cursorA++;
              pushGroups();
            }
          } else {
            cursor.cursorB = cursor.cursorBstart;
            cursor.cursorA++;
            pushGroups();
          }
        } else {
          cursor.cursorB++;
        }
      }

      function handleBlockLineProcessing() {
        if (simple.simpleA[cursor.cursorA].size === simple.simpleB[cursor.cursorB].size) {
          if (simple.simpleA[cursor.cursorA].content.join(',') === simple.simpleB[cursor.cursorB].content.join(',')) {
            if (simple.simpleA[cursor.cursorA].match !== cursor.cursorB) {
              changeCursor('block');
            } else {
              cursor.cursorB++;
            }
          } else {
            cursor.cursorB++;
          }
        } else {
          cursor.cursorB++;
        }
      }

      function changeCursor(lineType) {
        simple.simpleA[cursor.cursorA].match = cursor.cursorB;
        simple.simpleB[cursor.cursorB].match = cursor.cursorA;
        if (lineType === 'space') {
          lastBlockMatchA = cursor.cursorA;
          lastBlockMatchB = cursor.cursorB;
        }
        group.push(cursor.cursorA);
        cursor.cursorB++;
        cursor.cursorBstart = cursor.cursorB;
        cursor.cursorA++;
      }

      function pushGroups() {
        if (group.length) {
          groups.push(group.slice());
          group = [];
        }
      }
    }

    function invertGroups(simple, groups, invertedGroups) {
      var lastGroup = groups[groups.length - 1];
      var lastElementA = lastGroup[lastGroup.length - 1];
      var lastElementB = lastElementA.match;
      var firstGroup = [];
      var secondGroup = [];
      if (groups[0][0] > 0) {
        for (var i = 0; i < groups[0][0]; i++) {
          firstGroup.push(i);
        }
        invertedGroups.invertedGroupsA.push(firstGroup);
      }
      if (simple.simpleA[groups[0][0]].match > 0) {
        var firstGroup = [];
        for (var i = 0; i < simpleA[groups[0][0]].match; i++) {
          firstGroup.push(i);
        }
        invertedGroups.invertedGroupsB.push(firstGroup);
      }

      firstGroup = [];

      for (var i = 0; i < (groups.length - 1); i++) {
        firstGroup = groups[i];
        secondGroup = groups[i + 1];

        var firstGroupLastElementA = firstGroup[firstGroup.length - 1];
        var secondGroupFirstElementA = secondGroup[0];

        var firstGroupLastElementB = simple.simpleA[firstGroupLastElementA].match;
        var secondGroupFirstElementB = simple.simpleA[secondGroupFirstElementA].match;

        var newGroupA = [];
        var newGroupB = [];

        for (var j = firstGroupLastElementA + 1; j < secondGroupFirstElementA; j++) {
          newGroupA.push(j);
        }

        for (var j = firstGroupLastElementB + 1; j < secondGroupFirstElementB; j++) {
          newGroupB.push(j);
        }

        invertedGroups.invertedGroupsA.push(newGroupA);
        invertedGroups.invertedGroupsB.push(newGroupB);
      }

      pushGroupsIntoInverted(lastElementA, simple.simpleA);
      pushGroupsIntoInverted(lastElementB, simple.simpleB);

      function pushGroupsIntoInverted(lastElement, simpleParam) {
        if (lastElement < simpleParam.length) {
          var lastGroup = [];
          for (var i = lastElement + 1; i < simpleParam.length; i++) {
            lastGroup.push(i);
          }
          invertedGroups.invertedGroupsB.push(lastGroup);
        }
      }
    }

    function arrangeMasks(masks, difs, canvas, arrangeTimeout, maskCallback) {
      masks.customMask.style.display = difs.main.style.display = 'none';
      clearTimeout(arrangeTimeout);
      arrangeTimeout = setTimeout(function () {
        masks.customMask.style.display = difs.main.style.display = 'block';
        var maskSize = {
          width: canvas.canvasA.offsetWidth * 2 + canvas.canvasWrapper.offsetWidth * 0.04,
          height: canvas.canvasWrapper.offsetHeight
        };
        difs.main.style.width = masks.customMask.style.width = maskSize.width + 'px';
        difs.main.style.height = masks.customMask.style.height = maskSize.height + 'px';
        difs.main.style.top = masks.customMask.style.top = canvas.canvasA.offsettop + 'px';
        maskCallback(maskSize);
      }, 500);
    }

    function getElementByPosition(list, position) {
      for (var i = 0; i < list.length; i++) {
        if (list[i].offset < position && list[i].offset + list[i].size > position) {
          return i;
        }
      }
    }

    function getInvertedGroupByElement(index, isA, invertedGroups) {
      if (isA) {
        for (var i = 0; i < invertedGroups.invertedGroupsA.length; i++) {
          var group = invertedGroups.invertedGroupsA[i];
          if (group.indexOf(index) !== -1) {
            return i;
          }
        }
      } else {
        for (var i = 0; i < invertedGroups.invertedGroupsB.length; i++) {
          var group = invertedGroups.invertedGroupsB[i];
          if (group.indexOf(index) !== -1) {
            return i;
          }
        }
      }
    }

    function lightMask(selectFrom, selectTo, imgSize, maskParam, maskSizeParam, labelParam, rulerParam) {
      var maxHeight = Math.max(imgSize.imgSizeA.height, imgSize.imgSizeB.height);
      var proc = 100 / maxHeight;
      var scale = maskSizeParam.height / maxHeight;
      var height = selectTo - selectFrom;

      maskParam.top.style.height = selectFrom * proc + '%';
      maskParam.bot.style.top = selectTo * proc + '%';
      maskParam.bot.style.height = (imgSize.imgSizeB.height - selectTo) * proc + '%';

      labelParam.style.top = (selectFrom + height / 2) * proc + '%';
      labelParam.querySelector('.text').innerText = (selectTo - selectFrom) + 'px';
      rulerParam.style.height = height * scale + 'px';
      labelParam.style.display = 'block';
    }

    function getGroupsSizes(index, invertedGroups, simple) {
      var groupA = invertedGroups.invertedGroupsA[index];
      var indexFromA = groupA[0];
      var indexToA = groupA[groupA.length - 1];

        if (typeof invertedGroups.invertedGroupsB[index] !== 'undefined' && invertedGroups.invertedGroupsB[index].length > 0) {
          var groupB = invertedGroups.invertedGroupsB[index];
          var indexFromB = groupB[0];
          var indexToB = groupB[groupB.length - 1];
          return {
            aFrom: simple.simpleA[indexFromA].offset,
            aTo: simple.simpleA[indexToA].offset + simple.simpleA[indexToA].size,
            bFrom: simple.simpleB[indexFromB].offset,
            bTo: simple.simpleB[indexToB].offset + simple.simpleB[indexToB].size
          };
        } else {
          var bPosition = 0;
          if (simple.simpleA[indexFromA - 1]) {
            bPosition = simple.simpleB[simple.simpleA[indexFromA - 1].match].offset + simple.simpleB[simple.simpleA[indexFromA - 1].match].size;
          } else if (simple.simpleA[indexToA + 1]) {
            bPosition = simple.simpleB[simple.simpleA[indexToA + 1].match].offset;
          }
          return {
            aFrom: simple.simpleA[indexFromA].offset,
            aTo: simple.simpleA[indexToA].offset + simple.simpleA[indexToA].size,
            bFrom: bPosition,
            bTo: bPosition
          };
        }
    }

    function showAllDifs(wrapper) {
      var hoverElements = wrapper.querySelectorAll('.hover');
      for (var i = 0; i < hoverElements.length; i++) {
        hoverElements[i].classList.remove('hover');
      }
    }

    function selectGroup(index, isA, difs, ruler, wrapper) {
      ruler.rulerA.style.display = 'block';
      ruler.rulerB.style.display = 'block';
      showAllDifs(wrapper);
      if (isA) {
        if (difs.difsA[index]) {
          difs.difsA[index].classList.add('hover');
        }
      } else {
        if (difs.difsB[index]) {
          difs.difsB[index].classList.add('hover');
        }
      }
    }

    function deselectGroups(label, ruler, wrapper) {
      label.labelA.style.display = 'none';
      label.labelB.style.display = 'none';
      ruler.rulerA.style.display = 'none';
      ruler.rulerB.style.display = 'none';
      showAllDifs(wrapper);
    }

    function getAbsoluteOffset(element) {
      var offset = {
        x: element.offsetLeft,
        y: element.offsetTop
      };
      var parentElement = element.offsetParent;
      for (var i = 0; i < 100 && parentElement; i++) {
        offset.x += parentElement.offsetLeft;
        offset.y += parentElement.offsetTop;
        parentElement = parentElement.offsetParent;
      }

      return offset;
    }

    function onImagesReady(mask, imgSize, simple, label, ruler, invertedGroups, difs, wrapper) {
      mask.customMask.onmousemove = function (e) {
        var masksOffset = getAbsoluteOffset(mask.customMask);
        var relativePosition = {
          x: e.clientX - masksOffset.x,
          y: e.clientY - masksOffset.y
        };
        var isA = relativePosition.x < 0;
        var ratio = Math.max(imgSize.imgSizeA.height, imgSize.imgSizeB.height) / mask.maskSize.height;
        var element;
        if (isA) {
          element = getElementByPosition(simple.simpleA, (relativePosition.y + window.pageYOffset) * ratio);
        } else {
          element = getElementByPosition(simple.simpleB, (relativePosition.y + window.pageYOffset) * ratio);
        }

        if (element !== undefined) {
          var selectedGroup = getInvertedGroupByElement(element, isA, invertedGroups);
          if (selectedGroup >= 0 && typeof selectedGroup != 'undefined') {
            var sizes = getGroupsSizes(selectedGroup, invertedGroups, simple);
            lightMask(sizes.aFrom, sizes.aTo, imgSize, mask.maskA, mask.maskSize, label.labelA, ruler.rulerA);
            lightMask(sizes.aFrom, sizes.aTo, imgSize, mask.maskB, mask.maskSize, label.labelB, ruler.rulerB);
            selectGroup(selectedGroup, isA, difs, ruler, wrapper);
          }
        }
      };
      mask.customMask.onmouseleave = function () {
        lightMask(0, imgSize.imgSizeA.height, imgSize, mask.maskA, mask.maskSize, label.labelA, ruler.rulerA);
        lightMask(0, imgSize.imgSizeB.height, imgSize, mask.maskB, mask.maskSize, label.labelB, ruler.rulerB);
        deselectGroups(label, ruler, wrapper);
      };
    }
  }
});