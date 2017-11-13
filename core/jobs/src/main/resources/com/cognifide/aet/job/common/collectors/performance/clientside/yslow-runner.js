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
load(yslow_path);
load(env_path);

var content, res, doc;
function getResults(yscontext) {
    var i, l, results, url, type, comps, comp, encoded_url, obj, cr,
        cs, etag, name, len,
        result, len2, spaceid, header, sourceHeaders, targetHeaders,
        reButton = / <button [\s\S]+<\/button>/,
        util = YSLOW.util,
        isArray = util.isArray,
        stats = {},
        stats_c = {},
        params = {},
        g = {};

    params.v = YSLOW.version;
    params.w = parseInt(yscontext.PAGE.totalSize, 10);
    params.o = parseInt(yscontext.PAGE.overallScore, 10);
    params.u = encodeURIComponent(yscontext.result_set.url);
    params.r = parseInt(yscontext.PAGE.totalRequests, 10);
    spaceid = util.getPageSpaceid(yscontext.component_set);
    if (spaceid) {
        params.s = encodeURI(spaceid);
    }
    params.i = yscontext.result_set.getRulesetApplied().id;
    if (yscontext.PAGE.t_done) {
        params.lt = parseInt(yscontext.PAGE.t_done, 10);
    }
    params.prettyOverallScore = util.prettyScore(yscontext.PAGE.overallScore);

    //include grades
    results = yscontext.result_set.getResults();
    for (i = 0, len = results.length; i < len; i += 1) {
        obj = {};
        result = results[i];
        if (result.hasOwnProperty('score')) {
            if (result.score >= 0) {
                obj.score = parseInt(result.score, 10);
            } else if (result.score === -1) {
                obj.score = 'n/a';
            }
            obj.prettyScore = util.prettyScore(result.score);
        }
        if (result.hasOwnProperty('name')) {
            obj.name = result.name;
        }

        // removing hardcoded open link,
        // TODO: remove those links from original messages
        obj.message = result.message.replace(
            /javascript:document\.ysview\.openLink\('(.+)'\)/,
            '$1'
        );
        comps = result.components;
        if (isArray(comps)) {
            obj.components = [];
            for (l = 0, len2 = comps.length; l < len2; l += 1) {
                comp = comps[l];
                if (typeof comp === 'string') {
                    url = comp;
                } else if (typeof comp.url === 'string') {
                    url = comp.url;
                }
                if (url) {
                    url = url.replace(reButton, '');
                    obj.components.push(url);
                }
            }
        }
        g[result.rule_id] = obj;
    }
    params.g = g;

    // include stats
    params.w_c = parseInt(yscontext.PAGE.totalSizePrimed, 10);
    params.r_c = parseInt(yscontext.PAGE.totalRequestsPrimed, 10);

    for (type in yscontext.PAGE.totalObjCount) {
        if (yscontext.PAGE.totalObjCount.hasOwnProperty(type)) {
            stats[type] = {
                'r': yscontext.PAGE.totalObjCount[type],
                'w': yscontext.PAGE.totalObjSize[type]
            };
        }
    }
    params.stats = stats;

    for (type in yscontext.PAGE.totalObjCountPrimed) {
        if (yscontext.PAGE.totalObjCountPrimed.hasOwnProperty(type)) {
            stats_c[type] = {
                'r': yscontext.PAGE.totalObjCountPrimed[type],
                'w': yscontext.PAGE.totalObjSizePrimed[type]
            };
        }
    }
    params.stats_c = stats_c;

    return params;
};

window.onload = function () {
    doc = document;
    res = YSLOW.harImporter.run(doc, har_content, 'ydefault');
    content = getResults(res.context);
    print(JSON.stringify(content));
};
window.location = blank_path;

