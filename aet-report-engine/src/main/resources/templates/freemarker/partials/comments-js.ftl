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
var comments = (function ($, document, reportParameters) {
	"use strict";

	var ui = {},

	selectors = {
		commentPopovers: '[data-toggle="commentPopover"]'
	},

	registerUI = function () {
		ui.$commentPopovers = $(selectors.commentPopovers)
	},

	commentPathBuilder = function(params, builder){
		if(!isNotEmpty(builder)){
			builder = new UrlBuilder()
		}
		if (params.testName !== undefined) {
			builder.add(params.testName)
		}
		if (isNotEmpty(params.urlName)) {
			builder.add(encodeURIComponent(params.urlName))
		}
		if (params.collectorModule !== undefined && params.collectorModuleName !== undefined) {
			builder.add(params.collectorModule)
				.add(params.collectorModuleName);
		}
		if (params.comparatorModule !== undefined && params.comparatorModuleName !== undefined) {
			builder.add(params.comparatorModule)
				.add(params.comparatorModuleName);
		}
		return builder;
	},

	sendRequest = function($elem, method, data){
		var builder, params;
		params = $elem.attr("data-parameters");
		params = $.parseJSON(params);
		params = $.extend({}, reportParameters, params);
		builder = new UrlBuilder()
			.add(params.servletUrl)
			.add('comments')
			.add(params.company)
			.add(params.project)
			.add(params.testSuiteName)
			.add(params.environment);
		commentPathBuilder(params,builder);
		builder.add(params.rebaseCorrelationId);
		$.ajax({
			url: builder.build(),
			type: method,
			timeout: 5000,
			success: onSuccess,
			error: onError,
			data:data
		});
	},

	onSuccess = function (data) {
		flashMessageService.removeAll();
		var $elem = flashMessageService.add(data.message, (data.success) ? 'success' : 'danger');

		setTimeout(function () {
			flashMessageService.remove($elem)
		}, 3000);
	},

	onError = function (XMLHttpRequest, textStatus, errorThrown) {
		flashMessageService.removeAll();
		var $elem = flashMessageService.add("Could not connect to the server, cause: " + textStatus, 'danger');
		setTimeout(function () {
			flashMessageService.remove($elem)
		}, 3000);
	},

	isNotEmpty = function(string){
		return string !== undefined && string !== null;
	},

	showCommentTooltip = function(elemTooltip,title){
		$(elemTooltip).show();
		$(elemTooltip).attr('title',title);
		$(elemTooltip).tooltip({
			trigger: 'hover'
		});
	},

	fillComments = function(data){
		var commentMap = {}, comment, commentUrl;
		for (var i = 0; i < data.length; i++) {
			comment = data[i];
			commentUrl= commentPathBuilder(comment.metadata).build();
			commentMap[commentUrl] = comment.content;
		}

		ui.$commentPopovers.each(function () {
			var $elem = $(this);
			var template = $elem.find(".popover")[0],
				params = $elem.attr("data-parameters"),
			 	elemTooltip = $elem.parents().eq(2).find('[data-toggle="commentTooltip"]')[0];
			params = $.parseJSON(params);
			params = $.extend({}, reportParameters, params);

			//resiter UI events
			var contentInput = $(template).find('textarea'),
			buttons = $(template).find("button");
			var $saveButton= $(buttons[0]),
				$deleteButton = $(buttons[1]),
				$cancelButton = $(buttons[2]);

			$cancelButton.on('click',function(){
				$elem.popover('hide');
			});
			$saveButton.on('click',function(){
				var content = 'content='+ $(contentInput).val();
				sendRequest($elem, 'POST', content);
				if(isNotEmpty($(contentInput).val())){
					showCommentTooltip(elemTooltip,$(contentInput).val());
				}
			});
			$deleteButton.on('click',function(){
				sendRequest($elem, 'DELETE');
				$(contentInput).val('');
				$(elemTooltip).hide();
			});


			//build popover for managing comments
			var commentUrl= commentPathBuilder(params).build();
			$elem.popover({
				trigger: 'click',
				html: false,
				template: template,
				content: commentMap[commentUrl]
			});

			//build comment icon with tooltip if any
			if(isNotEmpty(commentMap[commentUrl])){
				showCommentTooltip(elemTooltip,commentMap[commentUrl]);
			}
		});

	},

	registerEvents = function() {
		ui.$commentPopovers.on('click', function (e) {
			ui.$commentPopovers.not(this).popover('hide');
		});
	},

	loadComments = function (){
		var builder = new UrlBuilder()
			.add(reportParameters.servletUrl)
			.add('comments')
			.add(reportParameters.company)
			.add(reportParameters.project)
			.add(reportParameters.testSuiteName)
			.add(reportParameters.environment)
			.add(reportParameters.rebaseCorrelationId);
		$.ajax({
			url: builder.build(),
			type: "GET",
			timeout: 5000,
			success: function(data){fillComments(data)},
			error: function(){onError('Something went wrong while loading comments for report!')}
		});

	};

	$(document).ready(function () {
		registerUI();
		loadComments();
		registerEvents();
	});

})($, document, reportParameters);

