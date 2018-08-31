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
define(['angularAMD', 'blob-stream', 'canvas2pdf', 'pdfkit'], function (angularAMD, blobStream, canvas2pdf, pdfkit) {
  'use strict';
  angularAMD.directive('aetGeneratePdfReport', ['generatePdfDataService', 'generatePdfDrawService', generatePDFDirective]);

  function generatePDFDirective(generatePdfDataService, generatePdfDrawService) {

    return {
      restrict: 'A',
      link: linkFunc
    };

    function linkFunc(scope) {
      scope.generatePDF = function () {
        window.PDFDocument = pdfkit; // PDFDocument has to be declared globally because canvas2pdf needs
        // var testsStatistics = metadataService.getTestStatistics();
        var currentTestData = {
          failed: $('.pull-right>.failed').text(),
          warning: $('.pull-right>.warning').text(),
          passed: $('.pull-right>.passed').text().replace(/\s/g, "").replace(/\(\d.*/g, ""),
          rebased: $('.pull-right>.rebased').text(),
        }

        var testsByCategories = generatePdfDataService.getTestData();
        var testStats = generatePdfDataService.getTestStats(testsByCategories);
        var testColors = generatePdfDataService.getTestColors();
        var testLabels = generatePdfDataService.getTestLabels();
        var testsData = generatePdfDataService.getTestsObject(currentTestData, testColors, testLabels);
        var casesData = generatePdfDataService.getCasesObject(testStats, testColors, testLabels);

        testsData = generatePdfDataService.setObjectInitialParams(testsData, 'Tests Statistics - Total: ');
        casesData = generatePdfDataService.setObjectInitialParams(casesData, 'Cases Statistics - Total: ');

        var ctx = new canvas2pdf.PdfContext(blobStream());
        generatePdfDrawService.generateDocHeader(ctx);
        generatePdfDrawService.generateChart(ctx, testsData);
        generatePdfDrawService.generateChart(ctx, casesData);
        ctx.doc.addPage();
        generatePdfDrawService.generateChartsForTests(ctx, testsByCategories, testColors, testLabels);

        ctx.stream.on('finish', function () {
          var suiteID = $('.toolbar-blocks>.toolbar-block:first-child>span').text().trim();
          var blob = ctx.stream.toBlob('application/pdf');
          saveAs(blob, 'AET-Report-' + suiteID + '.pdf', true);
        });
        ctx.end();
      };
    }
  }
});