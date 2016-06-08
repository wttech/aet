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
<link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css" rel="stylesheet">
<link href="https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.3/themes/smoothness/jquery-ui.css" rel="stylesheet" />
<style>
	body {
		padding-top: 60px;
	}

	.panel-title {
		position: relative;
	}
	
	.panel-title > a {
		display: block;
		padding-right: 40px;
	}
	
	.panel-title > div {
		position: absolute; right: 0; top: 50%; margin-top: -11px;
		padding-left: 10px; z-index: 999;
	}	

	.navbar .navbar-nav .nav-item-danger > a {
		background-color: #D9534F;
		color: white;
	}

	.navbar .navbar-nav .nav-item-warning > a {
		background-color: #F0AD4E;
		color: white;
	}

	.navbar .navbar-nav .nav-item-success > a {
		background-color: #5CB85C;
		color: white;
	}

	.navbar .navbar-nav .nav-item-info > a {
		background-color: #2A6496;
		color: white;
	}

	#flash-message-container {
		position: fixed;
		top: 0px;
		width: 100%;
		z-index: 9999;
	}

	img.loading {
		height: 200px;
		background-color: #acacac;
	}

	.diff-report table{
		table-layout: fixed;
	}

	.diff-report table > thead{
		position: relative;
		display: block;
	}

	.diff-report table > tbody {
		white-space: nowrap;
		font-size: 11px;
		font-family: 'Courier New', Courier, 'Lucida Console', monospace;
		display: block;
		height: 500px;
		overflow: auto;
	}

	.diff-report table th.num{
		min-width: 50px;
	}
	.diff-report table th.code{
		width: 100%;
	}
	.diff-report table tr td.code{
		width: 100%;
		white-space: pre;
	}

	.diff-report table td{
		min-width: 100px;
	}
	.diff-report table td.num{
		min-width: 50px;
	}
	
	.popover {
		max-width: 600px;
	}
	
	.popover-input-group {
		padding-bottom:5px;
	}
	
	.glyphicon-comment{
		font-size:16px;
	}
	
	textarea.popover-content {
		max-width: 100%;
	}
	
	h2  div {
		position: absolute; right: 0;margin-right: 11px;
		padding-left: 10px;
		display:inline;
	}
	
	.comment-div{
		margin-right: 50px
	}
	
	h4  div.comment-div {
		margin-right: 39px;
	}
	
	i[data-toggle="commentTooltip"] {
		display:none;
	}

	.tabs-left>.nav-tabs {
		border-bottom: 0;
	}
	
	.tab-content>.tab-pane {
		display: none;
	}
	
	.tab-content>.active {
		display: block;
	}
	
	.tabs-left>.nav-tabs>li {
		float: none;
	}
	
	.tabs-left>.nav-tabs>li>a {
		min-width: 74px;
		margin-right: 0;
		margin-bottom: 3px;
	}
	
	.tabs-left>.nav-tabs {
		float: left;
		margin-right: 19px;
		border-right: 1px solid #ddd;
	}
	
	.tabs-left>.nav-tabs>li>a {
		margin-right: -1px;
		-webkit-border-radius: 4px 0 0 4px;
		-moz-border-radius: 4px 0 0 4px;
		border-radius: 4px 0 0 4px;
	}
	
	.tabs-left>.nav-tabs>li>a:hover, .tabs-left>.nav-tabs>li>a:focus {
		border-color: #eeeeee #dddddd #eeeeee #eeeeee;
	}
	
	.tabs-left>.nav-tabs .active>a, .tabs-left>.nav-tabs .active>a:hover,
		.tabs-left>.nav-tabs .active>a:focus {
		border-color: #ddd transparent #ddd #ddd;
		*border-right-color: #ffffff;
	}
	.image-wrapper {
    	position: relative;
    }

    .image-wrapper:hover .mask {
        display: none;
    }

    .mask{
        background-color: rgba(0, 0, 0, 0);
        position: absolute;
            	top:0;
            	left:0;
            	width: 100%;
    }

</style>
