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
define(['angularAMD'], function (angularAMD) {
  'use strict';
  angularAMD.factory('extensionsFactory', ExtensionsFactoryService);

  /**
   * Service responsible for producing cases extensions that can handle specific types of data.
   */
  function ExtensionsFactoryService($rootScope) {
    var service = {
          getExtension: getExtension
        },
        EXTENSIONS = {
          'accessibility': new NoPatternExtension(),
          'client-side-performance': new NoPatternExtension(),
          'cookie': new NoPatternExtension(),
          'js-errors': new JsErrorsExtension(),
          'screen_layout': new LayoutExtension(),
          'source': new SourceComparisonExtension(),
          'source_w3c-html5': new W3CExtension(),
          'status-codes': new NoPatternExtension()
        };

    return service;

    function getExtension(step, comparator) {
      var type = step.type,
          comparatorAlgorithm = comparator.parameters ? comparator.parameters.comparator : null,
          key = comparatorAlgorithm ? type + '_' + comparatorAlgorithm : type;

      console.log('exteinsion', key, EXTENSIONS[key]);

      return EXTENSIONS[key];
    }

    /***************************************
     ***********  Private methods  *********
     ***************************************/

  }

  function NoPatternExtension() {
    return {
      setup: setup,
      handleDataArtifact: handleDataArtifact,
      handleResultArtifact: handleResultArtifact
    };
    
    function setup(caseModel, step, comparator, index) {
      caseModel.getResultArtifact();
    }

    function handleDataArtifact(data) {
      return data;
    }

    function handleResultArtifact(data) {
      return data;
    }

  }
  
  function JsErrorsExtension() {
    
  }

  function LayoutExtension() {

  }

  function SourceComparisonExtension() {

  }

  function W3CExtension() {

  }

});
