/*
 * Automated Exploratory Tests
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
require.config({
	baseUrl: 'app/',
	paths: {
		// **** LIBRARIES ****
		'jquery': '../assets/libs/jquery/jquery',
		'bootstrap': '../assets/libs/bootstrap-sass-twbs/bootstrap',
		'angular': '../assets/libs/angular/angular',
		'angular-ui-router': '../assets/libs/angular-ui-router/angular-ui-router',
		'angularAMD': '../assets/libs/angularAMD/angularAMD',
		'lodash': '../assets/libs/lodash/dist/lodash',
		'angular-bootstrap': '../assets/libs/angular-bootstrap/ui-bootstrap-tpls',
		// ************** OLD *************
		'UrlController': '_old_js/controllers/url',
		'toggleLinkDirective': '_old_js/directives/toggleLinkDirective',
		'commentDirective': '_old_js/directives/commentDirective',
		'saveChangesDirective': '_old_js/directives/saveChangesDirective',
		'scrollTablesDirective': '_old_js/directives/scrollTablesDirective',
		'tabNavigationDirective' : '_old_js/directives/tabNavigationDirective',
		'truncateUrlsDirective': '_old_js/directives/truncateUrlsDirective',
		'hidePopoversDirective':'_old_js/directives/hidePopoversDirective',
		'apiServices': '_old_js/services/apiService',
		'sessionStorageService': '_old_js/services/sessionStorageService',
		'configService':'_old_js/services/configService',
		'cachingService': '_old_js/services/cachingService',
		'tabStateService':'_old_js/services/tabStateService',
		'suiteParamsService': '_old_js/services/suiteParamsService',
		'scroller':'../assets/libs/malihu-custom-scrollbar-plugin/jquery.mCustomScrollbar',
		// **************** new structure ********************
		//components
		'testUrlsSearch': 'components/testUrlsSearch.filter',
		'testUrlsStatusFilter': 'components/testUrlsStatus.filter',
		'urlStatusFilter': 'components/urlStatus.filter',
		'keyboardShortcutsDirective': 'components/keyboardShortcuts.directive',
		//services
		'metadataEndpointService': 'services/metadataEndpoint.service',
		'metadataLoaderService': 'services/metadataLoader.service',
		'localStorageService': 'services/localStorage.service',
		'requestParametersService' : 'services/requestParameters.service',
		'metadataCacheService': 'services/metadataCache.service',
		'metadataService': 'services/metadata.service',
		'metadataAccessService': 'services/metadataAccess.service',
		'notesService': 'services/notes.service',
		'suiteInfoService': 'services/suiteInfo.service',
		'patternsService': 'services/patterns.service',
		'userSettingsService': 'services/userSettings.service',
		'viewModeService': 'services/viewMode.service',
		//toolbarTop
		'toolbarTopController': 'layout/toolbar/toolbarTop.controller',
		//toolbarBottom
		'toolbarBottomController': 'layout/toolbar/toolbarBottom.controller',
		//sidepanel
		'sidepanelToggleDirective': 'layout/sidepanel/sidepanelToggle.directive',
		'sidepanelStatusFilterDirective': 'layout/sidepanel/sidepanelStatusFilter.directive',
		'sidepanelSearchDirective': 'layout/sidepanel/sidepanelSearch.directive',
		'sidepanelController': 'layout/sidepanel/sidepanel.controller',
		//main
		'suiteController': 'layout/main/suite/mainView.suite.controller',
		'testController': 'layout/main/test/mainView.test.controller',
		//modals
		'unsavedChangesModalController': 'layout/modal/unsavedChanges/unsavedChangesModal.controller',
		'noteModalController': 'layout/modal/note/noteModal.controller'
	},
	shim: {
		jquery: {
			exports: '$'
		},
		angular: {
			exports: 'angular'
		},
		bootstrap: {
			deps: ['jquery']
		},
		lodash: {
			exports: '_'
		},
		scroller: {
			deps: ['jquery'],
			exports: 'mCustomScrollbar'
		},
		'angular-bootstrap': ['angular'],
		'angular-ui-router': ['angular'],
		'angularAMD': ['angular']
	},
	deps: ['app.module']
});
