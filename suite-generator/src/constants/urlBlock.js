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
const urlBlock = {
  deps: null,
  dropTo: "URLs",
  group: "URLs",
  parameters: {
    name: {
      current: null,
      default: null,
      isMandatory: false,
      description: "URL's name for the report app",
      name: "Name",
      tag: "name",
      values: null,
    },
    href: {
      current: null,
      default: null,
      isMandatory: true,
      description: "URL to be tested",
      name: "URL",
      tag: "href",
      values: null,
    }
  },
  proxy: false,
  tag: "url",
  type: "URL",
  wiki: "https://github.com/Cognifide/aet/wiki/Urls",
};

export default urlBlock;