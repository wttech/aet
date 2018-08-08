window.initNewComparison = function (window, document, undefined) { return function(event) {
  var debug = false;
  var tab = document.querySelector('.test-tabs > .tab-content > .tab-pane.ng-scope.active');
  var wrapper = tab.querySelector('.page-main .layout-compare');
  var initedClass = 'beta-active';
  var arrangeTimeout;

  debug && console.log('tab: ', tab);
  debug && console.log('wrapper: ', wrapper);

  function prepareMarkup(wrapper) {
    wrapper.innerHTML = '<div class="difs"></div><div class="customMasks"><div class="customMask maskAtop"></div><div class="customMask maskAbot"></div><div class="customMask maskBtop"></div><div class="customMask maskBbot"></div><div class="customLabel labelA"><div class="ruller"></div><div class="text"></div></div><div class="customLabel labelB"><div class="ruller"></div><div class="text"></div></div></div><div class="canvas-wrapper"></div>';
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

	var imgA = items[0].src;
	var imgB = items[1].src;

	prepareMarkup(wrapper);

	var canvasA = document.createElement('canvas');
	var canvasB = document.createElement('canvas');

	var masks = wrapper.querySelector('.customMasks');
	var difs = wrapper.querySelector('.difs');

	var labelA = wrapper.querySelector('.labelA');
	var labelB = wrapper.querySelector('.labelB');

	var rullerA = labelA.querySelector('.ruller');
	var rullerB = labelB.querySelector('.ruller');

	var maskSize;
	var maskA = {
		top: wrapper.querySelector('.maskAtop'),
		bot: wrapper.querySelector('.maskAbot')
	};
	var maskB = {
		top: wrapper.querySelector('.maskBtop'),
		bot: wrapper.querySelector('.maskBbot')
	};

	var canvasWrapper = wrapper.querySelector('.canvas-wrapper');
	var canvasA = document.createElement('canvas');
	var canvasB = document.createElement('canvas');

	var contextA = canvasA.getContext('2d');
	var contextB = canvasB.getContext('2d');

	var imgSizeA;
	var imgSizeB;

	var simpleA;
	var simpleB;

	var cursorA = 0;
	var cursorB = 0;
	var result = [];
	var cursorBstart = 0;
	var maxIndex;
	var progressInterval;
	var colorIndex = 0;
	var colors = ['#ffdfba', '#ffffba', '#baffc9', '#bae1ff'];
	var groups = [];
	var group = [];
	var invertedGroups = [];
	var invertedGroupsB = [];
	var difsA = [];
	var difsB = [];

	function drawDifferences() {
		var maxHeight = Math.max(imgSizeA.height, imgSizeB.height);
		var proc = 100 / maxHeight;

		for (var i=0; i<invertedGroups.length; i++) {
			var groupA = invertedGroups[i];
			var groupB = invertedGroupsB[i];

			var newDifferenceA = document.createElement('canvas');
			var newDifferenceContextA = newDifferenceA.getContext('2d');
			var newDifferenceB = document.createElement('canvas');
			var newDifferenceContextB = newDifferenceB.getContext('2d');

			var sizes = getGroupsSizes(i);

			if (sizes) {
				var heightA = sizes.aTo - sizes.aFrom;
				var heightB = sizes.bTo - sizes.bFrom;

				newDifferenceA.width = newDifferenceB.width = newDifferenceContextA.width = newDifferenceContextB.width = imgSizeA.width;
				newDifferenceA.height = newDifferenceContextA.height = heightA;
				newDifferenceB.height = newDifferenceContextB.height = heightB;

				newDifferenceB.style.left = '52%';

				difs.appendChild(newDifferenceA);
				difs.appendChild(newDifferenceB);

				newDifferenceA.style.top = sizes.aFrom * proc + '%';
				newDifferenceB.style.top = sizes.bFrom * proc + '%';

				newDifferenceA.style.height = (sizes.aTo - sizes.aFrom) * proc + '%';
				newDifferenceB.style.height = (sizes.bTo - sizes.bFrom) * proc + '%';

				var dataA = contextA.getImageData(0, sizes.aFrom, imgSizeA.width, heightA);
				var dataB = contextB.getImageData(0, sizes.bFrom, imgSizeA.width, Math.max(heightB, 1));
				var imageData;

				if (dataA.height > dataB.height) {
					imageData = convertDifferenceData(dataA, dataB);
				} else {
					imageData = convertDifferenceData(dataB, dataA);
				}

				newDifferenceContextA.putImageData(imageData, 0, 0, 0, 0, imgSizeA.width, heightA);
				if (heightB > 0) {
					newDifferenceContextB.putImageData(imageData, 0, 0, 0, 0, imgSizeA.width, heightB);
				}

				difsA.push(newDifferenceA);
				difsB.push(newDifferenceB);
			}
		}
	}

	function convertDifferenceData(data1, data2) {
		for (var index = 0; index < data1.data.length; index += 4) {
			if (typeof data2.data[index] !== 'undefined') {
				if (data1.data[index] !== data2.data[index] || data1.data[index + 1] !== data2.data[index + 1] || data1.data[index + 2] !== data2.data[index + 2]) {
					data1.data[index] = 255;
					data1.data[index + 1] = 0;
					data1.data[index + 2] = 0;
					data1.data[index + 3] = 127;
				} else {
					data1.data[index] = 0;
					data1.data[index + 1] = 0;
					data1.data[index + 2] = 0;
					data1.data[index + 3] = 0;
				}
			} else {
				data1.data[index] = 255;
				data1.data[index + 1] = 0;
				data1.data[index + 2] = 0;
				data1.data[index + 3] = 127;
			}
		}

		return data1;
	}

	function loadImage(url) {
    debug && console.log('load image: ', url);
		return new Promise(function(resolve, reject) {
			var image = new Image();

			image.onload = function() {
        debug && console.log('onload');
				resolve(image);
			}
			image.onerror = function() {
        debug && console.log('onerror');
				reject();
			}

			image.src = url;
		});
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

    for (var y = 0; y < data.length / imageWidth / 4; y ++) {
      var previousElement = simplifiedList.length ? simplifiedList[simplifiedList.length - 1] : {};
      var previousColor = '';
      var simplifiedLine = '';
      var encounters = 0;
      for (var x = 0; x < imageWidth; x ++) {
        var index = y * imageWidth * 4 + x * 4;
        var currentColor = rgb2hex(data[index + 0], data[index + 1], data[index + 2]);
        if (currentColor !== previousColor) {
          if (encounters > 0) {
            simplifiedLine += ',' + encounters + '|';
          }
          simplifiedLine += currentColor;
          previousColor = currentColor;
          encounters ++;
        } else {
          encounters ++;
        }
      }

      if (encounters > 0) {
        simplifiedLine += ',' + encounters;
      }

      if (previousLine) {
        if (previousLine === simplifiedLine) {
          if (previousElement && previousElement.type === 'space' && previousElement.value === previousLine) {
            simplifiedList[simplifiedList.length - 1].size ++;
            offset ++;
          } else {
            var newElement = {
              type: 'space',
              value: simplifiedLine,
              size: 2,
              offset: offset
            };
            simplifiedList.push(newElement);
            offset ++;
          }
        } else {
          if (previousElement && previousElement.type === 'block') {
            previousElement.size ++;
            previousElement.content.push(simplifiedLine);
            offset ++;
          } else {
            var newElement = {
              type: 'block',
              content: [simplifiedLine],
              size: 1,
              offset: offset ? offset + 1 : 0
            };
            simplifiedList.push(newElement);
            offset ++;
          }
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

	loadImage(imgA).then(function (image) {
    debug && console.log('image A loaded');
		imgSizeA = {
			width: image.naturalWidth,
			height: image.naturalHeight
		};
		canvasA.width = contextA.width = imgSizeA.width;
		canvasA.height = contextA.height = imgSizeA.height;
		contextA.drawImage(image, 0, 0);

    debug && console.log('imgSizeA: ', imgSizeA);
		simpleA = simplifyImage(contextA.getImageData(0, 0, imgSizeA.width, imgSizeA.height));
    debug && console.log('simpleA: ', simpleA);

		if (imgSizeB) {
			onImagesReady();
		}
	});

	loadImage(imgB).then(function (image) {
    debug && console.log('image B loaded');
		imgSizeB = {
			width: image.naturalWidth,
			height: image.naturalHeight
		};
		canvasB.width = contextB.width = imgSizeB.width;
		canvasB.height = contextB.height = imgSizeB.height;
		contextB.drawImage(image, 0, 0);

    debug && console.log('imgSizeB: ', imgSizeB);
		simpleB = simplifyImage(contextB.getImageData(0, 0, imgSizeB.width, imgSizeB.height));
    debug && console.log('simpleB: ', simpleB);

		if (imgSizeA) {
			onImagesReady();
		}
	});

	function getColor() {
		var color = colors[colorIndex];
		colorIndex = (colorIndex + 1) % colors.length;
		return color;
	}

	function processLines() {
		if (cursorA === (simpleA.length - 1)) {
			if (group.length) {
				groups.push(group.slice());
				group = [];
			}
			return;
		} else if (cursorB === (simpleB.length - 1)) {
			/*
			contextA.fillStyle = 'red';
			contextA.fillRect(0, simpleA[cursorA].offset, 5, simpleA[cursorA].size);
			*/
			cursorB = cursorBstart;
			cursorA ++;
			if (group.length) {
				groups.push(group.slice());
				group = [];
			}
		} else {
			var lastBlockMatchA;
			var lastBlockMatchB;
			if (simpleA[cursorA].type === simpleB[cursorB].type) {
				if (simpleA[cursorA].type === 'space') {
					if (simpleA[cursorA].color === simpleB[cursorB].color) {
						if (simpleA[cursorA].size === simpleB[cursorB].size) {
							if (simpleA[cursorA].match !== cursorB) {
								if (debug) {
									var color = getColor();
									contextA.fillStyle = color;
									contextA.fillRect(0, simpleA[cursorA].offset, 20, simpleA[cursorA].size);

									contextB.fillStyle = color;
									contextB.fillRect(0, simpleB[cursorB].offset, 20, simpleB[cursorB].size);
								}

								simpleA[cursorA].match = cursorB;
								simpleB[cursorB].match = cursorA;

								group.push(cursorA);

								cursorB ++;
								cursorBstart = cursorB;
								cursorA ++;
							} else {
								cursorB = cursorBstart;
								cursorA ++;

								if (group.length) {
									groups.push(group.slice());
									group = [];
								}
							}
						} else {
							cursorB = cursorBstart;
							cursorA ++;

							if (group.length) {
								groups.push(group.slice());
								group = [];
							}
						}
					} else {
						cursorB ++;
					}
				} else {
					if (simpleA[cursorA].size === simpleB[cursorB].size) {
						if (simpleA[cursorA].content.join(',') === simpleB[cursorB].content.join(',')) {
							if (simpleA[cursorA].match !== cursorB) {
								if (debug) {
									var color = getColor();
									contextA.fillStyle = color;
									contextA.fillRect(0, simpleA[cursorA].offset, 20, simpleA[cursorA].size);

									contextB.fillStyle = color;
									contextB.fillRect(0, simpleB[cursorB].offset, 20, simpleB[cursorB].size);
								}

								simpleA[cursorA].match = cursorB;
								simpleB[cursorB].match = cursorA;

								lastBlockMatchA = cursorA;
								lastBlockMatchB = cursorB;

								group.push(cursorA);

								cursorB ++;
								cursorBstart = cursorB;
								cursorA ++;
							} else {
								cursorB ++;
							}
						} else {
							cursorB ++;
						}
					} else {
						cursorB ++;
					}
				}
			} else {
				cursorB ++;
			}
		}
		processLines();
	}

	function invertGroups() {
		var lastGroup = groups[groups.length - 1];
		var lastElement = lastGroup[lastGroup.length - 1];
		var lastElementB = lastElement.match;
		var maxIndex = Math.max(simpleA.length, simpleB.length);
		if (groups[0][0] > 0) {
			var firstGroup = [];
			for (var i=0;i<groups[0][0]; i++) {
				firstGroup.push(i);
			}
			invertedGroups.push(firstGroup);
		}
		if (simpleA[groups[0][0]].match > 0) {
			var firstGroup = [];
			for (var i=0;i<simpleA[groups[0][0]].match; i++) {
				firstGroup.push(i);
			}
			invertedGroupsB.push(firstGroup);
		}

		for (var i = 0; i < (groups.length - 1); i ++) {
			var group1 = groups[i];
			var group2 = groups[i + 1];

			var group1LastElement = group1[group1.length - 1];
			var group2FirstElement = group2[0];

			var group1LastElementB = simpleA[group1LastElement].match;
			var group2FirstElementB = simpleA[group2FirstElement].match;

			var newGroup = [];
			var newGroupB = [];

			for (var j=group1LastElement + 1; j < group2FirstElement; j++) {
				newGroup.push(j);
			}

			for (var j=group1LastElementB + 1; j < group2FirstElementB; j++) {
				newGroupB.push(j);
			}

			invertedGroups.push(newGroup);
			invertedGroupsB.push(newGroupB);
		}

		if (lastElement < simpleA.length) {
			var lastGroup = [];
			for (var i = lastElement + 1; i < simpleA.length; i++) {
				lastGroup.push(i);
			}
			invertedGroups.push(lastGroup);
		}

		if (lastElementB < simpleB.length) {
			var lastGroup = [];
			for (var i = lastElement + 1; i < simpleB.length; i++) {
				lastGroup.push(i);
			}
			invertedGroupsB.push(lastGroup);
		}
	}

	function compareBoth() {
		arrangeMasks();
		processLines();
		invertGroups();
		drawDifferences();
	}

  function arrangeMasks() {
    masks.style.display = difs.style.display = 'none';
    clearTimeout(arrangeTimeout);
    arrangeTimeout = setTimeout(function () {
      masks.style.display = difs.style.display = 'block';
      maskSize = {
        width: canvasA.offsetWidth * 2 + canvasWrapper.offsetWidth * 0.04,
        height: canvasWrapper.offsetHeight
      };
      difs.style.width = masks.style.width = maskSize.width + 'px';
      difs.style.height = masks.style.height = maskSize.height + 'px';
      difs.style.top = masks.style.top = canvasA.offsettop + 'px';
    }, 500);
  }

  function bindListeners() {
    window.addEventListener('resize', arrangeMasks);
  }

	function getElementByPosition(list, position) {
		for (var i=0; i<list.length; i++) {
			if (list[i].offset < position && list[i].offset + list[i].size > position) {
				return i;
			}
		}
	}

	function getGroupByElement(index, isA) {
		if (isA) {
			for (var i=0; i<groups.length; i++) {
				var group = groups[i];

				if (group.indexOf(index) !== -1) {
					return i;
				}
			}
		} else {
			for (var i=0; i<groups.length; i++) {
				var group = groups[i];

				for (var j=0; j<group.length; j++) {
					var idA = group[j];
					var idB = simpleA[idA].match;

					if (idB === index) {
						return i;
					}
				}
			}
		}
	}

	function getInvertedGroupByElement(index, isA) {
		if (isA) {
			for (var i=0; i<invertedGroups.length; i++) {
				var group = invertedGroups[i];

				if (group.indexOf(index) !== -1) {
					return i;
				}
			}
		} else {
			for (var i=0; i<invertedGroupsB.length; i++) {
				var group = invertedGroupsB[i];

				if (group.indexOf(index) !== -1) {
					return i;
				}
			}
		}
	}

	function lightMaskA(selectFrom, selectTo) {
		var maxHeight = Math.max(imgSizeA.height, imgSizeB.height);
		var scale = maskSize.height / maxHeight;
		var proc = 100 / maxHeight;
		var height = selectTo - selectFrom;
		maskA.top.style.height = selectFrom * proc + '%';
		maskA.bot.style.top = selectTo * proc + '%';
		maskA.bot.style.height = (imgSizeA.height - selectTo) * proc + '%';

		labelA.style.top = (selectFrom + height / 2) * proc + '%';
		labelA.querySelector('.text').innerText = (selectTo - selectFrom) + 'px';
		rullerA.style.height = height * scale + 'px';
		labelA.style.display = 'block';
	}

	function lightMaskB(selectFrom, selectTo) {
		var maxHeight = Math.max(imgSizeA.height, imgSizeB.height);
		var proc = 100 / maxHeight;
		var scale = maskSize.height / maxHeight;
		var height = selectTo - selectFrom;

		maskB.top.style.height = selectFrom * proc + '%';
		maskB.bot.style.top = selectTo * proc + '%';
		maskB.bot.style.height = (imgSizeB.height - selectTo) * proc + '%';

		labelB.style.top = (selectFrom + height / 2) * proc + '%';
		labelB.querySelector('.text').innerText = (selectTo - selectFrom) + 'px';
		rullerB.style.height = height * scale + 'px';
		labelB.style.display = 'block';
	}
	function getGroupsSizes(index) {
		var groupA = invertedGroups[index];
		var indexFromA = groupA[0];
		var indexToA = groupA[groupA.length - 1];

		if (typeof invertedGroupsB[index] !== 'undefined' && invertedGroupsB[index].length > 0) {
			var groupB = invertedGroupsB[index];
			var indexFromB = groupB[0];
			var indexToB = groupB[groupB.length - 1];
			return {
				aFrom: simpleA[indexFromA].offset,
				aTo: simpleA[indexToA].offset + simpleA[indexToA].size,
				bFrom: simpleB[indexFromB].offset,
				bTo: simpleB[indexToB].offset + simpleB[indexToB].size
			};
		} else {
			var bPosition = 0;
			if (simpleA[indexFromA - 1]) {
				bPosition = simpleB[simpleA[indexFromA - 1].match].offset + simpleB[simpleA[indexFromA - 1].match].size;
			} else if (simpleA[indexToA + 1]) {
				bPosition = simpleB[simpleA[indexToA + 1].match].offset;
			}
			return {
				aFrom: simpleA[indexFromA].offset,
				aTo: simpleA[indexToA].offset + simpleA[indexToA].size,
				bFrom: bPosition,
				bTo: bPosition
			};
		}
	}

	function showAllDifs() {
		var hoverElements = wrapper.querySelectorAll('.hover');
		for (var i=0; i<hoverElements.length;i++) {
			hoverElements[i].classList.remove('hover');
		}
	}

	function selectGroup(index, isA) {
		var sizes = getGroupsSizes(index);

		lightMaskA(sizes.aFrom, sizes.aTo);
		lightMaskB(sizes.bFrom, sizes.bTo);

		showAllDifs();

		if (isA) {
			if (difsA[index]) {
				difsA[index].classList.add('hover');
			}
		} else {
			if (difsB[index]) {
				difsB[index].classList.add('hover');
			}
		}
	}

	function deselectGroups() {
		lightMaskA(0, imgSizeA.height);
		lightMaskB(0, imgSizeB.height);

		labelA.style.display = 'none';
		labelB.style.display = 'none';

		showAllDifs();
	}

  function getAbsoluteOffset(element) {
    var offset = {
      x: element.offsetLeft,
      y: element.offsetTop
    };
    var parentElement = element.offsetParent;
    for (var i=0; i<100 && parentElement; i++) {
      offset.x += parentElement.offsetLeft;
      offset.y += parentElement.offsetTop;
      parentElement = parentElement.offsetParent;
    }

    return offset;
  }

	function onImagesReady() {
		masks.onmousemove = function(e) {
      var masksOffset = getAbsoluteOffset(masks);
      var relativePosition = {
        x: e.clientX - masksOffset.x,
        y: e.clientY - masksOffset.y
      };
			var isA  = relativePosition.x < 0;
			var ratio = Math.max(imgSizeA.height, imgSizeB.height) / maskSize.height;
      var element;
			if (isA) {
				element = getElementByPosition(simpleA, (relativePosition.y + window.pageYOffset) * ratio);
			} else {
				element = getElementByPosition(simpleB, (relativePosition.y + window.pageYOffset) * ratio);
			}

      if (element !== undefined) {
        var selectedGroup = getInvertedGroupByElement(element, isA);
        if (selectedGroup >= 0) {
          selectGroup(selectedGroup, isA);
        }
      }
		}
		masks.onmouseleave = deselectGroups;
		totalLength = Math.max(simpleA.length, simpleB.length);
		compareBoth(simpleA, simpleB);
	}

	canvasWrapper.appendChild(canvasA);
	canvasWrapper.appendChild(canvasB);

  bindListeners();
}}(window, document);
