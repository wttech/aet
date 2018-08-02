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
define(['angularAMD', 'localStorageService'], function (angularAMD) {
  'use strict';
  angularAMD.factory('userSettingsService', UserSettingsService);

  /**
   * Service responsible for keeping user settings.
   * @param localStorageService - local session storage service
   */
  function UserSettingsService(localStorageService) {
    var service = {
          isScreenshotMaskVisible: isScreenshotMaskVisible,
          toggleScreenshotMask: toggleScreenshotMask,
          isFullSourceVisible: isFullSourceVisible,
          toggleFullSource: toggleFullSource,
          setLastTab: setLastTab,
          getLastTab: getLastTab,
        },
        SCREENSHOT_MASK_STORAGE_KEY = 'aet:settings.visibility.screenshotMask',
        FULL_SOURCE_MASK_STORAGE_KEY = 'aet:settings.visibility.fullSource',
        USER_TAB_MASK_STORAGE_KEY = 'aet:settings.raport.lastTab',
        settings = {
          screenshotMaskVisible: true,
          fullSourceMaskVisible: false,
          lastUserTab: null,
        };

    setupService();
    return service;

    function setupService() {
      settings.screenshotMaskVisible = getSetting(SCREENSHOT_MASK_STORAGE_KEY,
          true);
      settings.fullSourceMaskVisible = getSetting(FULL_SOURCE_MASK_STORAGE_KEY,
          false);
      settings.lastUserTab = getSetting(USER_TAB_MASK_STORAGE_KEY,
          null);  
    }

    function isScreenshotMaskVisible() {
      return settings.screenshotMaskVisible;
    }

    function toggleScreenshotMask() {
      settings.screenshotMaskVisible = toggleSetting(
          SCREENSHOT_MASK_STORAGE_KEY);
      return settings.screenshotMaskVisible;
    }

    function isFullSourceVisible() {
      return settings.fullSourceMaskVisible;
    }

    function toggleFullSource() {
      settings.fullSourceMaskVisible = toggleSetting(
          FULL_SOURCE_MASK_STORAGE_KEY);
      return settings.fullSourceMaskVisible;
    }

    function setLastTab(val) {
      settings.userTab = val;
    }

    function getLastTab() {
      return settings.userTab;
    }

    /***************************************
     ***********  Private methods  *********
     ***************************************/

    function getSetting(settingKey, defaultValue) {
      var settingValue = localStorageService.get(settingKey);
      if (settingValue === null) {
        settingValue = defaultValue;
        localStorageService.put(settingKey, defaultValue);
      }

      return settingValue;
    }

    function toggleSetting(settingKey) {
      var toggledValue = localStorageService.get(settingKey) !== true;
      localStorageService.put(settingKey, toggledValue);

      return toggledValue;
    }

  }
});
