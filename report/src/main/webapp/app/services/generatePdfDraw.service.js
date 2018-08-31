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
define(['angularAMD', 'endpointConfiguration', 'generatePdfDataService'], function (angularAMD) {
  'use strict';
  angularAMD.factory('generatePdfDrawService', generatePdfDrawService);

  /**
   * Service responsible for drawing pdf report
   */
  function generatePdfDrawService(generatePdfDataService) {
    var service = {
      generateDocHeader: generateDocHeader,
      generateChart: generateChart,
      generateChartsForTests: generateChartsForTests,
    };

    return service;

    function generateDocHeader(ctx) {
      var projectName = $('.toolbar-blocks>.toolbar-block:nth-child(3)>.name').text();
      var suiteName = $('.toolbar-blocks>.toolbar-block:nth-child(4)>.name').text();
      var suiteVersion = $('.toolbar-blocks>.toolbar-block:nth-child(4)>.preformatted').text();
      var runDate = $('.toolbar-blocks>.toolbar-block:nth-child(5)').text().trim();

      var urlString = window.location.href;
      var url = new URL(urlString);
      var company = url.searchParams.get('company');
      var project = url.searchParams.get('project');
      var correlationId = $('.toolbar-blocks>.toolbar-block:first-child>span').text().trim();
      var domain = urlString.split('report.html')[0];
      var testUrl = domain + 'report.html?company=' + company + '&project=' + project + '&correlationId=' + correlationId;

      ctx.font = '32px Helvetica';
      ctx.fillStyle = 'black';
      ctx.fillText('AET Report', 50, 40);
      ctx.font = '16px Helvetica';
      ctx.fillStyle = 'black';
      ctx.fillText('Suite: ' + projectName + ' ' + suiteName + ' ' + suiteVersion, 50, 70);
      ctx.fillText('Date: ' + runDate, 380, 42);
      ctx.font = '10px Helvetica';
      wrapText(ctx, 'Test URL: ' + testUrl, 50, 110, 400, 20);
    }

    function generateChart(ctx, chart) {
      ctx.strokeStyle = 'black';
      var arrowHeight = 5;
      var arrowLenght = 7;
      drawLine(ctx, chart.parameters.startingX, chart.parameters.offsetY + chart.parameters.startingY + chart.parameters.height, chart.parameters.startingX + chart.parameters.width, chart.parameters.offsetY + chart.parameters.startingY + chart.parameters.height);
      drawLine(ctx, chart.parameters.startingX, chart.parameters.offsetY + chart.parameters.startingY + chart.parameters.height, chart.parameters.startingX, chart.parameters.offsetY + chart.parameters.startingY);
      drawLine(ctx, chart.parameters.startingX + arrowHeight, chart.parameters.offsetY + chart.parameters.startingY + arrowLenght, chart.parameters.startingX, chart.parameters.offsetY + chart.parameters.startingY);
      drawLine(ctx, chart.parameters.startingX - arrowHeight, chart.parameters.offsetY + chart.parameters.startingY + arrowLenght, chart.parameters.startingX, chart.parameters.offsetY + chart.parameters.startingY);
      drawLine(ctx, chart.parameters.startingX + chart.parameters.width - arrowLenght, chart.parameters.offsetY + chart.parameters.startingY + chart.parameters.height - arrowHeight, chart.parameters.startingX + chart.parameters.width, chart.parameters.offsetY + chart.parameters.startingY + chart.parameters.height);
      drawLine(ctx, chart.parameters.startingX + chart.parameters.width - arrowLenght, chart.parameters.offsetY + chart.parameters.startingY + chart.parameters.height + arrowHeight, chart.parameters.startingX + chart.parameters.width, chart.parameters.offsetY + chart.parameters.startingY + chart.parameters.height);
      generateChartLegend(ctx, chart);
    }

    function generateChartsForTests(ctx, testsByCategories, testColors, testLabels) {
      for (var i = 0; i < testsByCategories.length; i++) {
        var test = generatePdfDataService.getCategorizedTestObject(testsByCategories[i], testColors, testLabels);
        if (i % 2 === 1) {
          test.parameters.offsetY = 300;
        } else {
          test.parameters.offsetY = 0;
          if (i !== 0) {
            ctx.doc.addPage();
          }
        }
        test = generatePdfDataService.setObjectInitialParams(test, testsByCategories[i].category + ' - Total: ');
        generateChart(ctx, test);
      }
    }

    /***************************************
     ***********  Private methods  *********
     ***************************************/

    function generateChartLegend(ctx, chart) {
      ctx.strokeStyle = 'black';
      ctx.fillStyle = 'black';
      ctx.font = '12px Helvetica';
      ctx.textAlign = 'right';
      chart.parameters.rowHeight = 40;

      chart.parameters.numOfYAxisPoints = 4;
      chart.parameters.maxValue = 100;
      chart.parameters.step = chart.parameters.maxValue / chart.parameters.numOfYAxisPoints;
      chart.parameters.step = Math.ceil(chart.parameters.step);
      chart.parameters.axisValues = [];
      chart.parameters.rowHeight = (chart.parameters.height - chart.parameters.height * 0.2) / chart.parameters.numOfYAxisPoints;

      var textOffsetX = 10;
      var xAxisSpacerLenght = 5;

      for (var i = 1; i <= chart.parameters.numOfYAxisPoints; i++) {
        chart.parameters.axisValues.push((chart.parameters.maxValue - (chart.parameters.step * (i - 1))));
        drawLine(ctx, chart.parameters.startingX - xAxisSpacerLenght, chart.parameters.offsetY + chart.parameters.startingY + chart.parameters.height - i * chart.parameters.rowHeight, chart.parameters.startingX + xAxisSpacerLenght, chart.parameters.offsetY + chart.parameters.startingY + chart.parameters.height - i * chart.parameters.rowHeight);
        ctx.fillText((i * 25 + '%').toString(), chart.parameters.startingX - textOffsetX, chart.parameters.offsetY + chart.parameters.startingY + chart.parameters.height - i * chart.parameters.rowHeight);
      }
      ctx.font = '16px Helvetica';
      ctx.fillStyle = 'black';
      ctx.textAlign = 'center';
      ctx.fillText(chart.parameters.chartName, chart.parameters.startingX + chart.parameters.width / 2, chart.parameters.offsetY + chart.parameters.startingY + chart.parameters.height + 40);
      generateChartValues(ctx, chart);
    }

    function generateChartValues(ctx, chart) {
      chart.parameters.columnMargin = chart.parameters.width / 20;
      chart.parameters.columnWidth = chart.parameters.width / chart.parameters.axisValues.length - 2 * chart.parameters.columnMargin;

      if (chart.parameters.columnWidth > 50) {
        chart.parameters.columnWidth = 50;
      }
      chart.parameters.columnStartingPoint = chart.parameters.columnWidth + 2 * chart.parameters.columnMargin;
      for (var i = 0; i < chart.parameters.axisValues.length; i++) {
        chart.parameters.columnHeight = generateColumn(ctx, chart, i);
        generateXAxisLegend(ctx, chart, i);
        generateColumnValue(ctx, chart, i);
      }
    }

    function generateColumn(ctx, chart, columnNumber) {
      var sumOfValues = 0;
      var colors = Object.values(chart.colors);
      var values = Object.values(chart.stats);
      for (var i = 0; i < chart.parameters.axisValues.length; i++) {
        sumOfValues += +values[i];
      }
      chart.parameters.columnHeight = values[columnNumber] / sumOfValues * chart.parameters.rowHeight * 4;
      ctx.fillStyle = colors[columnNumber];
      ctx.fillRect(columnNumber * chart.parameters.columnStartingPoint + (chart.parameters.startingX + chart.parameters.columnMargin), chart.parameters.offsetY + chart.parameters.startingY + chart.parameters.height - chart.parameters.columnHeight - 1, chart.parameters.columnWidth, chart.parameters.columnHeight);
      return chart.parameters.columnHeight;
    }

    function generateXAxisLegend(ctx, chart, columnNumber) {
      var labels = Object.values(chart.labels);
      var colors = Object.values(chart.colors);
      ctx.font = '10px Helvetica';
      ctx.fillStyle = colors[columnNumber];
      ctx.textAlign = 'center';
      ctx.fillText(labels[columnNumber], columnNumber * chart.parameters.columnStartingPoint + (chart.parameters.startingX + chart.parameters.columnMargin + (chart.parameters.columnWidth / 2)), chart.parameters.offsetY + chart.parameters.startingY + chart.parameters.height + 15);
    }

    function generateColumnValue(ctx, chart, columnNumber) {
      var values = Object.values(chart.stats);
      ctx.font = '10px Helvetica';
      ctx.fillStyle = 'black';
      ctx.textAlign = 'center';
      if (chart.parameters.columnHeight > 0) {
        ctx.fillText(values[columnNumber].toString(), columnNumber * chart.parameters.columnStartingPoint + (chart.parameters.startingX + chart.parameters.columnMargin + (chart.parameters.columnWidth / 2)), chart.parameters.offsetY + chart.parameters.startingY + chart.parameters.height - chart.parameters.columnHeight - 10);
      }
    }

    function drawLine(ctx, fromX, fromY, toX, toY, lineWidth) {
      if (typeof lineWidth == 'undefined') {
        lineWidth = 2;
      }
      ctx.lineWidth = lineWidth;
      ctx.moveTo(fromX, fromY);
      ctx.lineTo(toX, toY);
      ctx.stroke();
    }

    function wrapText(context, text, x, y, maxWidth, lineHeight) {
      var words = text.split(' ');
      var line = '';

      for (var n = 0; n < words.length; n++) {
        var testLine = line + words[n] + ' ';
        var metrics = context.measureText(testLine);
        var testWidth = metrics.width;
        if (testWidth > maxWidth && n > 0) {
          context.fillText(line, x, y);
          line = words[n] + ' ';
          y += lineHeight;
        } else {
          line = testLine;
        }
      }
      context.fillText(line, x, y);
    }
  }
});