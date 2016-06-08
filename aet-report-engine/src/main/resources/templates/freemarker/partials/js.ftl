<#--

    Cognifide AET :: Report Engine

    Copyright (C) 2013 Cognifide Limited

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

-->
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js"></script>
<script
	src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.3/jquery-ui.min.js"></script>

<script type="text/javascript">

var reportParameters = ${utils.toJson(reportsProperties.rebasePatternsUrl)};

var UrlBuilder = function () {
	var api = {},
			arr = [],
			self = this;

	api.add = function (value) {
		if (value !== undefined && value !== null) {
			arr.push(value);
		}
		return this;
	},

	api.build = function () {
		return arr.join('/');
	};

	return api;
};

var imageLazyLoadService = (function ($, document) {
	"use strict";

	var api = {},
			selectors = {
				lazyLoadImg: "img[data-src]"
			},

			flags = {
				loaded: 'loaded',
				loading: 'loading'
			},

			onImageLoad = function (event) {
				$(event.target).addClass(flags.loaded).removeClass(flags.loading);
			},

			imageLoad = function ($elem) {
				var imgSrc = $elem.data('src');
				if (!$elem.hasClass(flags.loaded) && imgSrc != undefined) {
					$elem.addClass(flags.loading).on('load', onImageLoad).attr('src', imgSrc);
					$elem.bind('click', function () {
						event.preventDefault();
						event.stopPropagation();
						window.open(this.currentSrc, '_blank');
					});
				}
			};

	api.loadInScope = function ($scope) {
		var $elems = $scope.find(selectors.lazyLoadImg);
		$elems.each(function () {
			imageLoad($(this));
		});
	};

	return api;
})($, document);

var flashMessageService = (function ($, document, config) {
	"use strict";

	var api = {},

			$target = null;

	api.removeAll = function () {
		$target.empty();
	};

	api.remove = function ($elem) {
		$elem.alert('close', function () {
			$elem.remove();
		});
		return $elem;
	};

	api.add = function (msg, type) {
		var $elem = $('<div role="alert" class="fade in alert alert-' + type + '"><button type="button" class="close" data-dismiss="alert">&times;</button>' + msg + '</div>');

		$target.append($elem);
		$elem.alert();
		return $elem;
	};

	$(document).ready(function () {
		$target = $(config.target);
	});

	return api;
})($, document, {
	target: '#flash-message-container'
});

var baselineService = (function ($, document, flashMessageService, reportParameters) {
	"use strict";

	var api = {},

			baseLineHistory = [],

			baseLineCall =  function (url, data, id) {
				$.ajax({
					url: url,
					data: data,
					id: id,
					type: 'POST',
					timeout: 5000,
					success: onSuccess,
					error: onError
				});
			},

			onSuccess = function (data, url, sendData) {
				addHistoryItem(this.id, data.success, data.finished);
				flashMessageService.removeAll();
				var $elem = flashMessageService.add(data.message, (data.success) ? 'success' : (data.finished) ? 'danger' : 'warning');

				setTimeout(function () {
					flashMessageService.remove($elem)
				}, 3000);

				if (!data.success && !data.finished) {
					setTimeout(baseLineCall, 3000, this.url, this.data, this.id);
				}
			},

			onError = function (XMLHttpRequest, textStatus, errorThrown) {
				addHistoryItem(this.id, false);
				flashMessageService.removeAll();
				var $elem = flashMessageService.add("Could not connect to the server, cause: " + textStatus, 'danger');
				setTimeout(function () {
					flashMessageService.remove($elem)
				}, 3000);
			},

			addHistoryItem = function (id, success, finished) {
				baseLineHistory.push(id);
				historyChange(id, success, finished);
			},

			historyChange = function (id, success, finished) {
				$.event.trigger({
					type: "historyChange",
					message: id,
					success: success,
					finished: finished,
					time: new Date()
				});
			};

	api.clearBaseLineHistory = function () {
		baseLineHistory = [];
	};

	api.getBaseLineHistory = function () {
		return baseLineHistory;
	};

	api.setBaseLine = function (params, id) {
		var pageToRebase,
				params = $.extend({}, reportParameters, params),
				data = {
					rebaseCorrelationId: params.rebaseCorrelationId
				},
				builder;

		builder = new UrlBuilder()
				.add(reportParameters.servletUrl)
				.add(reportParameters.rebasePath)
				.add(params.testName);

		if (params.urlName !== undefined) {
			builder.add("urls").add(encodeURIComponent(params.urlName))
		}
		if (params.collectorModule !== undefined && params.collectorModuleName !== undefined) {
			builder.add("artifactTypes")
					.add("data")
					.add(params.collectorModule)
					.add(params.collectorModuleName);
		}

		baseLineCall(builder.build(), data, id);

	};

	return api;
})($, document, flashMessageService, reportParameters);

var reportSorter = (function ($, document) {
	"use strict";

	var api = {},

			selectors = {
				testsContainer: '#tests-container',
				testSection: '.test-section',
				testResults: '.test-results',
				panel: 'div.panel',
				panelHeading: '.panel-heading',
				panelHeadingTitle: '.panel-title > a'
			},

			statusValueAdapter = function (status) {
				if (status === 'success') {
					return 0;
				} else if (status === 'warning') {
					return 1;
				} else {
					return 2;
				}
			},

			getZIndex = function (elem) {
				return $(elem).data('z-index');
			},

			getStatus = function (elem) {
				return $(elem).data('status');
			},

			getName = function (elem) {
				return $(elem).children(selectors.panelHeading).find(selectors.panelHeadingTitle).text().trim();
			},

			comparators = {
				zIndexComparator: function (a, b) {
					if (a === b) {
						return 0;
					}
					return (a === undefined || a > b) ? -1 : 1;
				},

				nameComparator: function (a, b) {
					if (a === b) {
						return 0;
					}
					return a > b ? 1 : -1;
				},

				statusComparator: function (a, b) {
					var aStatusValue = statusValueAdapter(a),
							bStatusValue = statusValueAdapter(b);

					if (aStatusValue === bStatusValue) {
						return 0;
					}
					return aStatusValue > bStatusValue ? -1 : 1;
				},

				testComparator: function (a, b) {
					var  aZIndex = getZIndex(a),
							bZIndex = getZIndex(b);

					return comparators.zIndexComparator(aZIndex, bZIndex);
				},

				statusNameComparator: function (a, b) {
					var aStatus = getStatus(a),
							bStatus = getStatus(b),
							aName = getName(a),
							bName = getName(b);

					var result = comparators.statusComparator(aStatus, bStatus);

					if (result === 0) {
						result = comparators.nameComparator(aName, bName);
					}
					return result;
				}
			};

	api.sort = function () {

		var $testSection = $(selectors.testSection);
		$testSection.sort(comparators.testComparator);
		$('body').find(selectors.testsContainer).append($testSection.detach());

		var $container = $(selectors.testResults);

		$container.each(function () {
			var $panels = $(this).children(selectors.panel);

			$panels.sort(comparators.statusNameComparator);
			$panels.each(function () {
				var $subPanels = $(this).find(selectors.panel);
				$subPanels.sort(comparators.statusNameComparator);
				$(this).find('.panel-body:first').append($subPanels.detach());
			});

			$(this).append($panels.detach());
		});
	};

	return api;

})($, document);

var buttonGroupToggle = (function ($, document, config) {
	"use strict";

	var selectors = {
		btnGroup: '.btn-group',
		btn: 'button'
	};

	$(document).ready(function () {
		var $elements = $(selectors.btnGroup);

		$elements.children(selectors.btn).click(function (event) {
			var $elem = $(this),
					$parent = $elem.parent();
			if ($elem.hasClass(config.disabledClass)) {
				$parent.children(selectors.btn).removeClass(config.activeClass).addClass(config.disabledClass);
				$elem.removeClass(config.disabledClass).addClass(config.activeClass);
			} else {
				event.stopImmediatePropagation();
			}
		});
	});
})($, document, {
	activeClass: 'btn-primary',
	disabledClass: 'btn-default'
});

var toggleVisibility = (function ($, document, config) {
	"use strict";

	var selectors = {
		btn: '.toggle-visibility-button'
	};

	$(document).ready(function () {
		var $elements = $(selectors.btn);

		$elements.click(function () {
			var $elem = $(this),
					$target = $elem.closest(".row").find('.mask');
			$target.each(function () {
				var $elem = $(this);
				if ($elem.hasClass(config.toggleClass)) {
					$elem.removeClass(config.toggleClass);
				} else {
					$elem.addClass(config.toggleClass);
				}
			});
		});
	});
})($, document, {
	toggleClass: 'hide'
});

var toggleVisibility = (function ($, document, config) {
	"use strict";

	var selectors = {
		btn: '.toggle-source-visibility-button'
	};

	$(document).ready(function () {
		var $elements = $(selectors.btn);

		$elements.click(function () {
			var $elem = $(this),
			$target = $($elem.data('target'));
			$target.each(function () {
				var $elem = $(this);
				if ($elem.hasClass(config.toggleClass)) {
					$elem.removeClass(config.toggleClass);
				} else {
					$elem.addClass(config.toggleClass);
				}
			});
		});
	});
})($, document, {
	toggleClass: 'hide'
});

var cssClassSwitcher = (function ($, document) {
	"use strict";

	var api = {},

			selectors = {
				classChangeTrigger: '.class-change-button'
			};

	api.switch = function ($elem) {
		var cssClass = $elem.data('class'),
				$target = $($elem.data('target'));

		if (cssClass != undefined && $target != undefined) {
			$target.removeClass().addClass(cssClass);
		}
	};

	$(document).ready(function () {
		$(selectors.classChangeTrigger).click(function (event) {
			var $elem = $(this);
			api.switch($elem);
		});
	});

	return api;
})($, document);

var filterElements = (function ($, document) {
	"use strict";

	var api = {},

			selectors = {
				filterTrigger: '.filter-button'
			};

	api.filter = function ($elem) {
		var targetAttr = $elem.data('target'),
				filterElementsBy = $elem.data('filter-selector'),
				$target = $(targetAttr);

		if (filterElementsBy === undefined) {
			$target.removeClass('hidden');
		} else {
			$target.not(filterElementsBy).addClass('hidden');
		}
	};

	$(document).ready(function () {
		$(selectors.filterTrigger).click(function (event) {
			var $elem = $(this);
			api.filter($elem);
		});
	});

	return api;
})($, document);

var diffElement = (function ($, document) {
	"use strict";

	var api = {},

			selectors = {
				diffReprot: '.diff-report',
				scrollableElement: 'tbody'
			};

	api.scroll = function ($target, offsetX, offsetY) {
		if ($target != undefined) {
			if (offsetX != undefined) {
				$target.scrollLeft(offsetX);
			}
			if (offsetY != undefined) {
				$target.scrollTop(offsetY);
			}
		}
	};

	$(document).ready(function () {
		var $element = $(selectors.diffReprot),
				$scrollableElement = $element.find(selectors.scrollableElement);

		$scrollableElement.on('scroll', function (event) {
			var $elem = $(event.target),
					$parent = $elem.parents(selectors.diffReprot),
					$target = $parent.find(selectors.scrollableElement).not($elem);

			api.scroll($target, $elem.scrollLeft(), $elem.scrollTop());
		});
	});

	return api;
})($, document);

var risksTooltip= (function ($, document) {
	"use strict";
	
	var risksMap = {},
	
	ui = {},

	selectors = {
		tooltips: '[data-toggle="risksTooltip"]'
	},
	
	populateRisksMap = function () {
		risksMap["cookieLIST"] = "<ul>"+
				"<li>The user can check all site cookies (name, value, expiry date) and find a cookie invalid expiry date or value. However, this mode (list) of cookie features is not intended to discover website issues.</li>"+
				"<li>This 'list' should be empty when a page does not intend to use cookies and The EU Cookie Law is respected.</li></ul>";
		risksMap["cookieTEST"] = "<ul>"+
				"<li>The lack of a cookie that occurred before might be caused by some website error (e.g. a bug in system functionality).</li>"+
				"<li>The lack of a cookie may result in further system errors (e.g. the user will not be able to stay logged in across pages, some data about the user will be lost).</li>"+
				"<li>The lack of an important cookie (e.g. a cookie with user localisation data) may cause a page to be display improperly.</li></ul>";
		risksMap["cookieCOMPARE"] = "<ul>"+
				"<li>The lack of a cookie that detected before might be caused by some website error (e.g. a bug in system functionality).</li>"+
				"<li>The lack of a cookie may result in further system errors (e.g. the user will not be able to stay logged in across pages, some data about the user will be lost).</li>"+
				"<li>An additional cookie may be caused by some unwanted content on a page, e.g. some 3rd party software adds its cookies.</li>"+
				"<li>When a page does not intend to use cookies and The EU Cookie Law is respected, lists of additional and detected cookies should always be empty.</li></ul>";
		risksMap["js-errors"] = "<ul>"+
				"<li>JS Errors can cause improper behavior of a page. Because of js errors, dynamic components may not work properly in some (or all) browsers.</li>"+
				"<li>JS Error can also occur when good practices are not followed in javascript code.</li></ul>";
		risksMap["layout"] = "<ul>"+
				"<li>Differences found on page screenshots may indicate undesired changes in the page layout (css, html structure) e.g. when a new functionality was implemented in a system it may have an impact on another system component(s). This may show itself as a changed page layout.</li>"+
				"<li>Content changes can be divided into two groups: <strong>wanted</strong> (intended) and <strong>unwanted</strong> (result of a mistake or an error). An example of a change that is not a defect (wanted) is: the <em>carousel component</em> with the latest news items displayed or the <em>twitter component</em> displaying latest tweets. In order to avoid detecting these sorts of changes in these dynamic components, the user can use the 'Hide Modifier' feature in the suite definition. Another example of a 'wanted' dynamic content is a cookies policy popup that may be hidden using the Cookie Modifier.</li></ul>";
		risksMap["source"] = "<ul>"+
				"<li>Differences found by source comparison may indicate undesired changes in a page layout (html structure) and content, e.g. when a new functionality is implemented in a system it might have an impact on other system component(s). This may occur as a changed page source.</li>"+
				"<li>Content changes can be divided into two groups: <strong>wanted</strong> (intended) and <strong>unwanted</strong> (result of a mistake or an error). In order to filter out wanted changes and detect changes that are a result of a mistake or an error, the user can use one of following filters in the suite definition:<ul>"+
				"<li>Extract Element Data Modifier (e.g. to find changes only in main menu that has parameter <code>id='main-menu'</code> set)</li>"+
				"<li>Remove Lines Data Filter (to remove lines that changes every time - e.g. current timestamp)</li>"+
				"<li>Remove Nodes Data Filter (e.g. to remove content displayed by the dynamic news carousel component).</li><ul></ul>";
		risksMap["status-codes"] = "<ul>"+
				"<li>All status codes with a number higher than 400 are potential errors and indicate that the resource that is used by a page is unreachable (e.g. a page logo image, a page layout css file)</li>"+
				"<li>Status code errors affect SEO (e.g. google page ranking is lowered for pages with 404 status codes).</li></ul>";
		risksMap["w3c"] = "<ul>"+
				"<li>W3C validation is important from the SEO point of view. Pages that do not comply to W3C standards are ranked low in <em>Google Page Rank</em> and other rankings.</li>"+
				"<li>Detected W3C errors may indicate serious html structure bugs (e.g. tags that haven't been closed) or content issues (e.g. invalid tags parameters: &lt;a&gt; without href).</li>"+
				"<li>Maintenance of pages that follow W3C standards is much easier to carry out because pages that keep these standards are much less prone to be displayed differently in different browsers or devices.</li>"+
				"<li>The W3C validation can also reveal page encoding and special characters displaying issues.</li></ul>";
		risksMap["w3c-html5"] = "<ul>"+
				"<li>W3C validation is important from the SEO point of view. Pages that do not comply to W3C standards are ranked low in <em>Google Page Rank</em> and other rankings.</li>"+
				"<li>Detected W3C errors may indicate serious html structure bugs (e.g. tags that haven't been closed) or content issues (e.g. invalid tags parameters: &lt;a&gt; without href).</li>"+
				"<li>Maintenance of pages that follow W3C standards is much easier to carry out because pages that keep these standards are much less prone to be displayed differently in different browsers or devices.</li>"+
				"<li>The W3C validation can also reveal page encoding and special characters displaying issues.</li></ul>";
		risksMap["client-side-performance"] = "<ul>"+
				"<li>Poor client side performance has negative impact on user experience.</li>"+
				"<li>Pages with grade F are slow and probably user will not want to use them. They are especially difficult to load on mobile devices.</li>"+
				"<li>Client side performance is taken into account by most of search engines. Good performance means better ranking.</li></ul>";
		risksMap["accessibility"] = "<ul><li>When page fails accessibility tests it could mean that will find it difficult to access information, e.g. there are images on page without description (alt attribute), anchors elements does not have link content, page styling and design is not clear enough for people with sight disabilities.</li></ul>";
	},
	
	registerUI = function () {
		ui.$tooltips = $(selectors.tooltips);
	},
	
	registerEvents = function () {
		ui.$tooltips.hover(function (event) {
			var $elem = $(this);
			var risksKey = $elem.attr('data-risksKey');
			var tooltipContent = risksMap[risksKey];
			if( tooltipContent != null){	
				$elem.attr('data-content','<p>'+tooltipContent+'</p>');
				$elem.attr('data-title','<strong>Risks</strong>');
				$elem.popover({trigger: 'hover'});
				$elem.popover('show');
			}	
		});
	};

	$(document).ready(function () {
		populateRisksMap();
		registerUI();
		registerEvents();
	});
	

})($, document);

var reportEventHandler = (function ($, document, reportParameters, baselineService) {
	"use strict";

	var api = {},

			ui = {},

			selectors = {
				reportRow: ".report-row",
				toggleAllBtn: ".toggle-all-btn",
				toggleAllErrorsBtn: ".toggle-all-errors-btn",
				baseLineButton: '.baseline-action',
				baseReportRowCollapsible: '#container > div > section > div.test-results > div.report-row > .collapse',
				baseReportRowCollapsibleError: '.panel-danger > .collapse',
				navbar: '.navbar',
				testSection: '.test-section',
				toogleBigScreenshots: '.toogle-big-screenshots'
			},

			$selectedReportRow = null,

			isExpandedAll = false,

			isExpandedErrors = false,

			isBigScreenshots = false,

			registerUi = function () {
				ui.$reportRow = $(selectors.reportRow);
				ui.$toogleAllBtn = $(selectors.toggleAllBtn);
				ui.$toogleErrorsBtn = $(selectors.toggleAllErrorsBtn);
				ui.$baseLineButton = $(selectors.baseLineButton);
				ui.$baseReportRowCollapsible = $(selectors.baseReportRowCollapsible);
				ui.$baseReportRowCollapsibleError = $(selectors.baseReportRowCollapsibleError);
				ui.$navbar = $(selectors.navbar);
				ui.$toogleBigScreenshotsBtn = $(selectors.toogleBigScreenshots);
			},

			registerEvents = function () {
				ui.$reportRow.click(function (event) {
					var $elem = $(this);
					if (event.shiftKey) {
						api.recursiveReportRowToggle($elem);
					} else if (event.which === 2 || (event.which === 1 && event.ctrlKey)) {
						api.openReportRowSourcePage($elem);
					}
					$selectedReportRow = $(event.currentTarget);
				});

				ui.$toogleAllBtn.click(function (event) {
					api.toggleAllTests();
				});

				ui.$toogleErrorsBtn.click(function (event) {
					api.toggleErrorTests();
				});

				ui.$baseLineButton.click(function (event) {
					var $elem = $(this);
					var confirm = true;
					var parameters = ($elem.get(0)).getAttribute("data-parameters");
					if (parameters !== null) {
						if (parameters.indexOf("collectorModuleName") > -1) {
							confirm = false;
						}
					}
					if (confirm !== true) {
						api.setBaseLine($elem);
					}
					else {
						$("#rebase-confirm").html("Patterns will be rebased. Proceed?");
						$("#rebase-confirm").dialog({
							modal: true,
							title: "Rebase confirmation",
							buttons: {
								"Yes": function () {
									$(this).dialog('close');
									api.setBaseLine($elem);
								},
								"No": function () {
									$(this).dialog('close');
								}
							}
						});
					}
					event.preventDefault();
				});

				ui.$toogleBigScreenshotsBtn.click(function (event) {
					api.toogleBigScreenshots();
				});


				$(document).on('historyChange', function (event) {
					var $elem = $("#" + event.message).parents(selectors.reportRow + ", " + selectors.testSection).first().find('.baseline-action');
					if ($elem !== undefined) {
						if (event.success) {
							api.markBaseLineAsDone($elem, event.time);
						} else if (event.finished) {
							api.markBaseLineAsFailed($elem, event.time);
						} else {
							api.markBaseLineAsInProgress($elem, event.time);
						}
					}
				});
			},

			registerKeyEvents = function () {
				$(document.body).keyup(function (event) {
					if(!$(event.target).is('textarea')){
						if (event.which === 81) { // q key
							api.toggleAllTests();
						} else if (event.which === 69) {// e key
							api.toggleErrorTests();
						} else if (event.which === 221) {// ] key
							api.scrollToNextTest();
						} else if (event.which === 219) {// [ key
							api.scrollToPrevTest();
						}
					}
				});
			};

	api.markBaseLine = function($elem, time, elementClass, iconClass) {
		$elem.attr('title', "Baselined on: " + time);
		$elem.removeClass('btn-primary').removeClass('btn-success').removeClass('btn-danger')
			.removeClass('btn-warning').addClass(elementClass);
		$elem.find('i').removeClass('glyphicon-cloud-upload').removeClass('glyphicon-ok')
			.removeClass('glyphicon-remove').removeClass('glyphicon-repeat').addClass(iconClass);
	};

	api.markBaseLineAsDone = function ($elem, time) {
		api.markBaseLine($elem, time, 'btn-success', 'glyphicon-ok');
	};

	api.markBaseLineAsFailed = function ($elem, time) {
		api.markBaseLine($elem, time, 'btn-danger', 'glyphicon-remove');
	};

	api.markBaseLineAsInProgress = function ($elem, time) {
		api.markBaseLine($elem, time, 'btn-warning', 'glyphicon-repeat');
	};

	api.scrollToNextTest = function () {
		var $nextItem;
		if ($selectedReportRow === null) {
			$selectedReportRow = $(selectors.reportRow).first();
		}
		$nextItem = $selectedReportRow.next();
		$(window).scrollTop($nextItem.offset().top - ui.$navbar.height() - 10);
		$selectedReportRow = $nextItem;
	};

	api.scrollToPrevTest = function () {
		var $prevItem;
		if ($selectedReportRow === null) {
			$selectedReportRow = $(selectors.reportRow).first();
		}
		$prevItem = $selectedReportRow.prev();
		$(window).scrollTop($prevItem.offset().top - ui.$navbar.height() - 10);
		$selectedReportRow = $prevItem;
	};

	api.toggleAllTests = function () {
		var $elems = ui.$baseReportRowCollapsible;
		if (isExpandedAll) {
			api.collapseTests($elems);
		} else {
			api.expandTests($elems);
		}
		isExpandedAll = !isExpandedAll;
		isExpandedErrors = isExpandedAll;
	};

	api.toggleErrorTests = function () {
		var $elems = ui.$baseReportRowCollapsibleError;
		if (isExpandedErrors) {
			api.collapseTests($elems);
		} else {
			api.expandTests($elems);
		}
		isExpandedErrors = !isExpandedErrors;
	};

	api.expandTests = function ($elem) {
		$elem.collapse('show');
	};

	api.collapseTests = function ($elem) {
		$elem.collapse('hide');
	};

	api.setBaseLine = function ($elem) {
		baselineService.setBaseLine($elem.data('parameters'), $elem.attr('id'))
	};

	api.recursiveReportRowToggle = function ($elem) {
		var $collapsible = $elem.children('.collapse'),
				$collapsibleChildren = $collapsible.find('.collapse');
		if ($collapsible.hasClass('in')) {
			api.collapseTests($collapsibleChildren)
		} else {
			api.expandTests($collapsibleChildren)
		}
	};

	api.openReportRowSourcePage = function ($elem) {
		window.open($elem.find('a:first').data('source'));
		event.preventDefault();
		event.stopPropagation();
	}

	api.toogleBigScreenshots = function () {
		if (isBigScreenshots) {
			$(".class-change-button[data-class='col-md-3 col-md-push-3']").click();
		} else {
			$(".class-change-button[data-class='col-md-6']").click();
		}
		isBigScreenshots = !isBigScreenshots;
	}

	$(document).ready(function () {
		registerUi();
		registerEvents();
		registerKeyEvents();
	});

	return api;
})($, document, reportParameters, baselineService);

$(document).ready(function () {
	reportSorter.sort();
	$('[data-toggle="tooltip"]').tooltip();
	$('.report-row .report-row').on('show.bs.collapse', function () {
		imageLazyLoadService.loadInScope($(this));
	});
});

<#include "comments-js.ftl">
</script>
