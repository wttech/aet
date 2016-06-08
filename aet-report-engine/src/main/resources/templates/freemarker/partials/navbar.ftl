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
<div role="navigation" class="navbar navbar-default navbar-fixed-top">
	<div class="container-fluid">
		<div class="navbar-header">
			<button data-target=".navbar-collapse" data-toggle="collapse" class="navbar-toggle" type="button">
				<span class="sr-only">Toggle navigation</span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
			</button>
			<span class="navbar-brand hidden-xs">
				${reportsProperties.rebasePatternsUrl.testSuiteName} - AET Tests Report
					${stats.dateTime?string("yyyy-MM-dd HH:mm:ss")}</span>					
		</div>
		<div class="collapse navbar-collapse navbar-right">
			<ul class="nav navbar-nav">
				<li class="nav-item-danger" data-toggle="tooltip" data-placement="bottom"
				    title="Failed: ${stats.failedCount} / ${stats.totalCount}">
					<a href="#">
					${utils.toPercent(stats.failedCount, stats.totalCount)}%
						<i class="glyphicon glyphicon-remove"></i>
					</a>
				</li>
				<li class="nav-item-warning" data-toggle="tooltip" data-placement="bottom"
				    title="Warnings: ${stats.warningCount} / ${stats.totalCount}">
					<a href="#">
					${utils.toPercent(stats.warningCount, stats.totalCount)}%
						<i class="glyphicon glyphicon-warning-sign"></i>
					</a>
				</li>
				<li class="nav-item-success" data-toggle="tooltip" data-placement="bottom"
				    title="Passed: ${stats.passedCount} / ${stats.totalCount}">
					<a href="#">
					${utils.toPercent(stats.passedCount, stats.totalCount)}%
						<i class="glyphicon glyphicon-ok"></i>
					</a>
				</li>
				<li class="dropdown">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown">
						<i class="glyphicon glyphicon glyphicon-cog"></i>
						<span class="caret"></span>
					</a>
					<ul class="dropdown-menu" role="menu">
						<li>
							<a href="#" class="toggle-all-btn">
								<i class="glyphicon glyphicon-chevron-down"></i>
								Toggle All
							</a>
						</li>
						<li>
							<a href="#" class="toggle-all-errors-btn">
								<i class="glyphicon glyphicon-chevron-down"></i>
								Toggle Errors
							</a>
						</li>
						<li>
							<a href="#" class="toogle-big-screenshots">
								<i class="glyphicon glyphicon-chevron-down"></i>
								Toogle Big screenshots
							</a>
						</li>
						<li>
							<a href="#" class="baseline-action" title="Set baseline for whole Test Suit">
								<i class="glyphicon glyphicon glyphicon-cloud-upload"></i>
								Set baseline TS
							</a>
						</li>
						<li>
							<a href="#help" data-target="#help" data-toggle="modal" class="nav-item-info">
								<i class="glyphicon glyphicon glyphicon-info-sign"></i>
								Help
							</a>
						</li>
					</ul>
				</li>
			</ul>
		</div>
	</div>
</div>