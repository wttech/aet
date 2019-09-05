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
const listOfCollectors =
[
  {
    type: "Open",
    tag: "open",
    parameters: null,
    deps: null,
    depType: null,
    dropTo: "Collectors",
    group: "Open",
    proxy: "false",
  },
  {
    type: 'Accessibility',
    tag: "accessibility",
    parameters: {
      standard: {
        name: "Standard",
        tag: "standard",
        values: ['WCAG2A', 'WCAG2AA', 'WCAG2AAA'],
        default: 'WCAG2AA',
        isMandatory: false,
        description: "The parameter specifies the standard which the page is validated against",
        current: null,
      }
    },
    deps: "accessibility-comparators",
    depType: "Warning",
    dropTo: "Collectors",
    group: "Collectors",
    proxy: "false",
    wiki: "https://github.com/Cognifide/aet/wiki/AccessibilityCollector",
  },
  {
    type: 'Client Side Performance',
    tag: "client-side-performance",
    parameters: null,
    deps: "clientsideperformance-comparators",
    depType: "Warning",
    dropTo: "Collectors",
    group: "Collectors",
    proxy: "true",
    wiki: "https://github.com/Cognifide/aet/wiki/ClientSidePerformanceComparator",
  },
  {
    type: 'Cookie',
    tag: "cookie",
    parameters: null,
    deps: "cookie-comparators",
    depType: "Warning",
    dropTo: "Collectors",
    group: "Collectors",
    proxy: "false",
    wiki: "https://github.com/Cognifide/aet/wiki/CookieCollector",
  },
  {
    type: 'JS Errors',
    tag: "js-errors",
    parameters: null,
    deps: "jserrors-comparators",
    depType: "Warning",
    dropTo: "Collectors",
    group: "Collectors",
    proxy: "false",
    wiki: "https://github.com/Cognifide/aet/wiki/JSErrorsCollector",
  },
  {
    type: 'Screen',
    tag: "screen",
    parameters: {
      name: {
        name: "Name",
        tag: "name",
        values: null,
        default: null,
        isMandatory: false,
        description: "Name of that test.",
        current: null,
      },
      xpath: {
        name: "XPath",
        tag: "xpath",
        values: null,
        default: null,
        isMandatory: false,
        description: "XPath to element(s)",
        current: null,
      },
      css: {
        name: "CSS",
        tag: "css",
        values: null,
        default: null,
        isMandatory: false,
        description: "CSS selector to element(s)",
        current: null,
      },
      timeout: {
        name: "Timeout",
        tag: "timeout",
        values: null,
        default: "1000ms",
        isMandatory: false,
        description: "The timeout for the element to appear, in milliseconds. The max value of this parameter is 15000ms",
        current: null,
      },
      excludeelements: {
        name: "Exclude Elements",
        tag: "exclude-elements",
        values: null,
        default: null,
        isMandatory: false,
        description: "Elements found with that selector will be ignored by layout comparator (they won't affect its results) but will be rendered on the report as captured.",
        current: null,
      },
    },
    deps: "screen-comparators",
    depType: "Warning",
    dropTo: "Collectors",
    group: "Collectors",
    proxy: "false",
    wiki: "https://github.com/Cognifide/aet/wiki/ScreenCollector",
  },
  {
    type: 'Source',
    tag: "source",
    parameters: null,
    deps: "source-comparators",
    depType: "Warning",
    dropTo: "Collectors",
    group: "Collectors",
    proxy: "false",
    wiki: "https://github.com/Cognifide/aet/wiki/SourceCollector",
  },
  {
    type: 'Status Codes',
    tag: "status-codes",
    parameters: null,
    deps: "statuscodes-comparators",
    depType: "Warning",
    dropTo: "Collectors",
    group: "Collectors",
    proxy: "true",
    wiki: "https://github.com/Cognifide/aet/wiki/StatusCodesCollector",
  },
];

export default listOfCollectors;
