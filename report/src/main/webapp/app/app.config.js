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
require.config({
  baseUrl: 'app/',
  paths: {
    // **** LIBRARIES ****
    'jquery': '../assets/libs/jquery/dist/jquery',
    'bootstrap': '../assets/libs/bootstrap-sass-twbs/assets/javascripts/bootstrap',
    'angular': '../assets/libs/angular/angular',
    'angular-ui-router': '../assets/libs/angular-ui-router/release/angular-ui-router',
    'angularAMD': '../assets/libs/angularAMD/angularAMD',
    'lodash': '../assets/libs/lodash/dist/lodash',
    'angular-bootstrap': '../assets/libs/angular-bootstrap/ui-bootstrap-tpls',
    'snowfall': '../assets/js/snowfall/snowfall.min',
    // **************** AET custom ********************
    //components
    'testSearchFilter': 'components/testSearch.filter',
    'testStatusFilter': 'components/testStatus.filter',
    'urlSearchFilter': 'components/urlSearch.filter',
    'urlStatusFilter': 'components/urlStatus.filter',
    'keyboardShortcutsDirective': 'components/keyboardShortcuts.directive',
    'hidePopoversDirective': 'components/hidePopovers.directive',
    'compareScreensDirective': 'components/compareScreens.directive',
    'winterEdition': 'themes/winterEdition.directive',
    //services
    'endpointConfiguration': 'services/endpointConfiguration.service',
    'artifactsService': 'services/artifacts.service',
    'metadataEndpointService': 'services/metadataEndpoint.service',
    'metadataLoaderService': 'services/metadataLoader.service',
    'localStorageService': 'services/localStorage.service',
    'requestParametersService': 'services/requestParameters.service',
    'metadataCacheService': 'services/metadataCache.service',
    'metadataService': 'services/metadata.service',
    'metadataAccessService': 'services/metadataAccess.service',
    'notesService': 'services/notes.service',
    'historyService': 'services/history.service',
    'suiteInfoService': 'services/suiteInfo.service',
    'patternsService': 'services/patterns.service',
    'userSettingsService': 'services/userSettings.service',
    'viewModeService': 'services/viewMode.service',
    //toolbarTop
    'toolbarTopController': 'layout/toolbar/toolbarTop.controller',
    //toolbarBottom
    'toolbarBottomController': 'layout/toolbar/toolbarBottom.controller',
    //sidepanel
    'sidepanelDirective': 'layout/sidepanel/sidepanel.directive',
    'sidepanelStatusFilterDirective': 'layout/sidepanel/sidepanelStatusFilter.directive',
    'sidepanelSearchDirective': 'layout/sidepanel/sidepanelSearch.directive',
    'sidepanelToggleLinkDirective': 'layout/sidepanel/toggleLink.directive',
    'sidepanelSaveChangesDirective': 'layout/sidepanel/saveChanges.directive',
    'sidepanelTruncateUrlsDirective': 'layout/sidepanel/truncateUrls.directive',
    'sidepanelController': 'layout/sidepanel/sidepanel.controller',
    //main
    'suiteController': 'layout/main/suite/mainView.suite.controller',
    'testController': 'layout/main/test/mainView.test.controller',
    'urlController': 'layout/main/url/mainView.url.controller',
    'caseFactory': 'layout/main/url/caseFactory.service',
    'expandablePanelDirective': 'layout/main/url/expandablePanel.directive',
    'filterInformationDirective': 'layout/main/url/filterInformation/filterInformation.directive',
    'includedCommentPopoverDirective': 'layout/main/url/includedCommentPopover.directive',
    //modals
    'unsavedChangesModalController': 'layout/modal/unsavedChanges/unsavedChangesModal.controller',
    'noteModalController': 'layout/modal/note/noteModal.controller',
    'historyModalController': 'layout/modal/history/historyModal.controller'
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
    'angular-bootstrap': ['angular'],
    'angular-ui-router': ['angular'],
    'angularAMD': ['angular']
  },
  deps: ['app.module'],
  waitSeconds: 0
});
