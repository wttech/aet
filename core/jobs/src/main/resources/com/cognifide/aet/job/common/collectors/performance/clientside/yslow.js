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
/*global YSLOW:true*/
/*jslint white: true, onevar: true, undef: true, nomen: true, eqeqeq: true, plusplus: true, bitwise: true, regexp: true, newcap: true, immed: true */

/**
 * @module YSLOW
 * @class YSLOW
 * @static
 */
if (typeof YSLOW === 'undefined') {
    YSLOW = {};
}

/**
 * Enable/disable debbuging messages
 */
YSLOW.DEBUG = true;

/**
 *
 * Adds a new rule to the pool of rules.
 *
 * Rule objects must implement the rule interface or an error will be thrown. The interface
 * of a rule object is as follows:
 * <ul>
 *   <li><code>id</code>, e.g. "numreq"</li>
 *   <li><code>name</code>, e.g. "Minimize HTTP requests"</li>
 *   <li><code>url</code>, more info about the rule</li>
 *   <li><code>config</code>, configuration object with defaults</li>
 *   <li><code>lint()</code> a method that accepts a document, array of components and a config object and returns a reuslt object</li>
 * </ul>
 *
 * @param {YSLOW.Rule} rule A new rule object to add
 */
YSLOW.registerRule = function (rule) {
    YSLOW.controller.addRule(rule);
};

/**
 *
 * Adds a new ruleset (new grading algorithm).
 *
 * Ruleset objects must implement the ruleset interface or an error will be thrown. The interface
 * of a ruleset object is as follows:
 * <ul>
 *   <li><code>id</code>, e.g. "ydefault"</li>
 *   <li><code>name</code>, e.g. "Yahoo! Default"</li>
 *   <li><code>rules</code> a hash of ruleID => ruleconfig </li>
 *   <li><code>weights</code> a hash of ruleID => ruleweight </li>
 * </ul>
 *
 * @param {YSLOW.Ruleset} ruleset The new ruleset object to be registered
 */
YSLOW.registerRuleset = function (ruleset) {
    YSLOW.controller.addRuleset(ruleset);
};

/**
 * Register a renderer.
 *
 * Renderer objects must implement the renderer interface.
 * The interface is as follows:
 * <ul>
 * <li><code>id</code></li>
 * <li><code>supports</code> a hash of view_name => 1 or 0 to indicate what views are supported</li>
 * <li>and the methods</li>
 * </ul>
 *
 * For instance if you define a JSON renderer that only render grade. Your renderer object will look like this:
 * { id: 'json',
 *    supports: { reportcard: 1, components: 0, stats: 0, cookies: 0},
 *    reportcardView: function(resultset) { ... }
 * }
 *
 * Refer to YSLOW.HTMLRenderer for the function prototype.
 *
 *
 * @param {YSLOW.renderer} renderer The new renderer object to be registered.
 */
YSLOW.registerRenderer = function (renderer) {
    YSLOW.controller.addRenderer(renderer);
};

/**
 * Adds a new tool.
 *
 * Tool objects must implement the tool interface or an error will be thrown.
 * The interface of a tool object is as follows:
 * <ul>
 *   <li><code>id</code>, e.g. 'mytool'</li>
 *   <li><code>name</code>, eg. 'Custom tool #3'</li>
 *   <li><code>print_output</code>, whether this tool will produce output.</li>
 *   <li><code>run</code>, function that takes doc and componentset object, return content to be output</li>
 * </ul>
 *
 * @param {YSLOW.Tool} tool The new tool object to be registered
 */
YSLOW.registerTool = function (tool) {
    YSLOW.Tools.addCustomTool(tool);
};


/**
 * Register an event listener
 *
 * @param {String} event_name Name of the event
 * @param {Function} callback A function to be called when the event fires
 * @param {Object} that Object to be assigned to the "this" value of the callback function
 */
YSLOW.addEventListener = function (event_name, callback, that) {
    YSLOW.util.event.addListener(event_name, callback, that);
};

/**
 * Unregister an event listener.
 *
 * @param {String} event_name Name of the event
 * @param {Function} callback The callback function that was added as a listener
 * @return {Boolean} TRUE is the listener was removed successfully, FALSE otherwise (for example in cases when the listener doesn't exist)
 */
YSLOW.removeEventListener = function (event_name, callback) {
    return YSLOW.util.event.removeListener(event_name, callback);
};

/**
 * @namespace YSLOW
 * @constructor
 * @param {String} name Error type
 * @param {String} message Error description
 */
YSLOW.Error = function (name, message) {
    /**
     * Type of error, e.g. "Interface error"
     * @type String
     */
    this.name = name;
    /**
     * Error description
     * @type String
     */
    this.message = message;
};

YSLOW.Error.prototype = {
    toString: function () {
        return this.name + "\n" + this.message;
    }
};
/**
 * Copyright (c) 2012, Yahoo! Inc.  All rights reserved.
 * Copyrights licensed under the New BSD License. See the accompanying LICENSE file for terms.
 */

YSLOW.version = '3.1.8';
/**
 * Copyright (c) 2012, Yahoo! Inc.  All rights reserved.
 * Copyright (c) 2013, Marcel Duran and other contributors. All rights reserved.
 * Copyrights licensed under the New BSD License. See the accompanying LICENSE file for terms.
 */

/*global YSLOW,MutationEvent*/
/*jslint browser: true, continue: true, sloppy: true, maxerr: 50, indent: 4 */

/**
 * ComponentSet holds an array of all the components and get the response info from net module for each component.
 *
 * @constructor
 * @param {DOMElement} node DOM Element
 * @param {Number} onloadTimestamp onload timestamp
 */
YSLOW.ComponentSet = function (node, onloadTimestamp) {

    //
    // properties
    //
    this.root_node = node;
    this.components = [];
    this.outstanding_net_request = 0;
    this.component_info = [];
    this.onloadTimestamp = onloadTimestamp;
    this.nextID = 1;
    this.notified_fetch_done = false;

};

YSLOW.ComponentSet.prototype = {

    /**
     * Call this function when you don't use the component set any more.
     * A chance to do proper clean up, e.g. remove event listener.
     */
    clear: function () {
        this.components = [];
        this.component_info = [];
        this.cleared = true;
        if (this.outstanding_net_request > 0) {
            YSLOW.util.dump("YSLOW.ComponentSet.Clearing component set before all net requests finish.");
        }
    },

    /**
     * Add a new component to the set.
     * @param {String} url URL of component
     * @param {String} type type of component
     * @param {String} base_href base href of document that the component belongs.
     * @param {Object} obj DOMElement (for image type only)
     * @return Component object that was added to ComponentSet
     * @type ComponentObject
     */
    addComponent: function (url, type, base_href, o) {
        var comp, found, isDoc;

        if (!url) {
            if (!this.empty_url) {
                this.empty_url = [];
            }
            this.empty_url[type] = (this.empty_url[type] || 0) + 1;
        }
        if (url && type) {
            // check if url is valid.
            if (!YSLOW.ComponentSet.isValidProtocol(url) ||
                    !YSLOW.ComponentSet.isValidURL(url)) {
                return comp;
            }

            // Make sure url is absolute url.
            url = YSLOW.util.makeAbsoluteUrl(url, base_href);
            // For security purpose
            url = YSLOW.util.escapeHtml(url);

            found = typeof this.component_info[url] !== 'undefined';
            isDoc = type === 'doc';

            // make sure this component is not already in this component set,
            // but also check if a doc is coming after a redirect using same url
            if (!found || isDoc) {
                this.component_info[url] = {
                    'state': 'NONE',
                    'count': found ? this.component_info[url].count : 0
                };

                comp = new YSLOW.Component(url, type, this, o);
                if (comp) {
                    comp.id = this.nextID += 1;
                    this.components[this.components.length] = comp;

                    // shortcup for document component
                    if (!this.doc_comp && isDoc) {
                        this.doc_comp = comp;
                    }

                    if (this.component_info[url].state === 'NONE') {
                        // net.js has probably made an async request.
                        this.component_info[url].state = 'REQUESTED';
                        this.outstanding_net_request += 1;
                    }
                } else {
                    this.component_info[url].state = 'ERROR';
                    YSLOW.util.event.fire("componentFetchError");
                }
            }
            this.component_info[url].count += 1;
        }

        return comp;
    },

    /**
     * Add a new component to the set, ignore duplicate.
     * @param {String} url url of component
     * @param {String} type type of component
     * @param {String} base_href base href of document that the component belongs.
     */
    addComponentNoDuplicate: function (url, type, base_href) {

        if (url && type) {
            // For security purpose
            url = YSLOW.util.escapeHtml(url);
            url = YSLOW.util.makeAbsoluteUrl(url, base_href);
            if (this.component_info[url] === undefined) {
                return this.addComponent(url, type, base_href);
            }
        }

    },

    /**
     * Get components by type.
     *
     * @param {String|Array} type The type of component to get, e.g. "js" or
     *        ['js', 'css']
     * @param {Boolean} include_after_onload If component loaded after onload
     *        should be included in the returned results, default is FALSE,
     *        don't include
     * @param {Boolean} include_beacons If image beacons (1x1 images) should
     *        be included in the returned results, default is FALSE, don't
     *        include
     * @return An array of matching components
     * @type Array
     */
    getComponentsByType: function (type, includeAfterOnload, includeBeacons) {
        var i, j, len, lenJ, t, comp, info,
            components = this.components,
            compInfo = this.component_info,
            comps = [],
            types = {};

        if (typeof includeAfterOnload === 'undefined') {
            includeAfterOnload = !(YSLOW.util.Preference.getPref(
                'excludeAfterOnload',
                true
            ));
        }
        if (typeof includeBeacons === 'undefined') {
            includeBeacons = !(YSLOW.util.Preference.getPref(
                'excludeBeaconsFromLint',
                true
            ));
        }

        if (typeof type === 'string') {
            types[type] = 1;
        } else {
            for (i = 0, len = type.length; i < len; i += 1) {
                t = type[i];
                if (t) {
                    types[t] = 1;
                }
            }
        }

        for (i = 0, len = components.length; i < len; i += 1) {
            comp = components[i];
            if (!comp || (comp && !types[comp.type]) ||
                    (comp.is_beacon && !includeBeacons) ||
                    (comp.after_onload && !includeAfterOnload)) {
                continue;
            }
            comps[comps.length] = comp;
            info = compInfo[i];
            if (!info || (info && info.count <= 1)) {
                continue;
            }
            for (j = 1, lenJ = info.count; j < lenJ; j += 1) {
                comps[comps.length] = comp;
            }
        }

        return comps;
    },

    /**
     * @private
     * Get fetching progress.
     * @return { 'total' => total number of component, 'received' => number of components fetched  }
     */
    getProgress: function () {
        var i,
            total = 0,
            received = 0;

        for (i in this.component_info) {
            if (this.component_info.hasOwnProperty(i) &&
                    this.component_info[i]) {
                if (this.component_info[i].state === 'RECEIVED') {
                    received += 1;
                }
                total += 1;
            }
        }

        return {
            'total': total,
            'received': received
        };
    },

    /**
     * Event callback when component's GetInfoState changes.
     * @param {Object} event object
     */
    onComponentGetInfoStateChange: function (event_object) {
        var comp, state, progress;

        if (event_object) {
            if (typeof event_object.comp !== 'undefined') {
                comp = event_object.comp;
            }
            if (typeof event_object.state !== 'undefined') {
                state = event_object.state;
            }
        }
        if (typeof this.component_info[comp.url] === 'undefined') {
            // this should not happen.
            YSLOW.util.dump("YSLOW.ComponentSet.onComponentGetInfoStateChange(): Unexpected component: " + comp.url);
            return;
        }

        if (this.component_info[comp.url].state === "NONE" && state === 'DONE') {
            this.component_info[comp.url].state = "RECEIVED";
        } else if (this.component_info[comp.url].state === "REQUESTED" && state === 'DONE') {
            this.component_info[comp.url].state = "RECEIVED";
            this.outstanding_net_request -= 1;
            // Got all component detail info.
            if (this.outstanding_net_request === 0) {
                this.notified_fetch_done = true;
                YSLOW.util.event.fire("componentFetchDone", {
                    'component_set': this
                });
            }
        } else {
            // how does this happen?
            YSLOW.util.dump("Unexpected component info state: [" + comp.type + "]" + comp.url + "state: " + state + " comp_info_state: " + this.component_info[comp.url].state);
        }

        // fire event.
        progress = this.getProgress();
        YSLOW.util.event.fire("componentFetchProgress", {
            'total': progress.total,
            'current': progress.received,
            'last_component_url': comp.url
        });
    },

    /**
     * This is called when peeler is done.
     * If ComponentSet has all the component info, fire componentFetchDone event.
     */
    notifyPeelDone: function () {
        if (this.outstanding_net_request === 0 && !this.notified_fetch_done) {
            this.notified_fetch_done = true;
            YSLOW.util.event.fire("componentFetchDone", {
                'component_set': this
            });
        }
    },

    /**
     * After onload guess (simple version)
     * Checkes for elements with src or href attributes within
     * the original document html source
     */
    setSimpleAfterOnload: function (callback, obj) {
        var i, j, comp, doc_el, doc_comps, src,
            indoc, url, el, type, len, lenJ,
            docBody, doc, components, that;

        if (obj) {
            docBody = obj.docBody;
            doc = obj.doc;
            components = obj.components;
            that = obj.components;
        } else {
            docBody = this.doc_comp && this.doc_comp.body;
            doc = this.root_node;
            components = this.components;
            that = this;
        }

        // skip testing when doc not found
        if (!docBody) {
            YSLOW.util.dump('doc body is empty');
            return callback(that);
        }

        doc_el = doc.createElement('div');
        doc_el.innerHTML = docBody;
        doc_comps = doc_el.getElementsByTagName('*');

        for (i = 0, len = components.length; i < len; i += 1) {
            comp = components[i];
            type = comp.type;
            if (type === 'cssimage' || type === 'doc') {
                // docs are ignored
                // css images are likely to be loaded before onload
                continue;
            }
            indoc = false;
            url = comp.url;
            for (j = 0, lenJ = doc_comps.length; !indoc && j < lenJ; j += 1) {
                el = doc_comps[j];
                src = el.src || el.href || el.getAttribute('src') ||
                    el.getAttribute('href') ||
                    (el.nodeName === 'PARAM' && el.value);
                indoc = (src === url);
            }
            // if component wasn't found on original html doc
            // assume it was loaded after onload
            comp.after_onload = !indoc;
        }

        callback(that);
    },

    /**
     * After onload guess
     * Checkes for inserted elements with src or href attributes after the
     * page onload event triggers using an iframe with original doc html
     */
    setAfterOnload: function (callback, obj) {
        var ifrm, idoc, iwin, timer, done, noOnloadTimer,
            that, docBody, doc, components, ret, enough, triggered,
            util = YSLOW.util,
            addEventListener = util.addEventListener,
            removeEventListener = util.removeEventListener,
            setTimer = setTimeout,
            clearTimer = clearTimeout,
            comps = [],
            compsHT = {},

            // get changed component and push to comps array
            // reset timer for 1s after the last dom change
            getTarget = function (e) {
                var type, attr, target, src, oldSrc;

                clearTimer(timer);

                type = e.type;
                attr = e.attrName;
                target = e.target;
                src = target.src || target.href || (target.getAttribute && (
                    target.getAttribute('src') || target.getAttribute('href')
                ));
                oldSrc = target.dataOldSrc;

                if (src &&
                        (type === 'DOMNodeInserted' ||
                        (type === 'DOMSubtreeModified' && src !== oldSrc) ||
                        (type === 'DOMAttrModified' &&
                            (attr === 'src' || attr === 'href'))) &&
                        !compsHT[src]) {
                    compsHT[src] = 1;
                    comps.push(target);
                }

                timer = setTimer(done, 1000);
            },

            // temp iframe onload listener
            // - cancel noOnload timer since onload was fired
            // - wait 3s before calling done if no dom mutation happens
            // - set enough timer, limit is 10 seconds for mutations, this is
            //   for edge cases when page inserts/removes nodes within a loop
            iframeOnload =  function () {
                var i, len, all, el, src;

                clearTimer(noOnloadTimer);
                all = idoc.getElementsByTagName('*');
                for (i = 0, len = all.length; i < len; i += 1) {
                    el = all[i];
                    src = el.src || el.href;
                    if (src) {
                        el.dataOldSrc = src;
                    }
                }
                addEventListener(iwin, 'DOMSubtreeModified', getTarget);
                addEventListener(iwin, 'DOMNodeInserted', getTarget);
                addEventListener(iwin, 'DOMAttrModified', getTarget);
                timer = setTimer(done, 3000);
                enough = setTimer(done, 10000);
            };

        if (obj) {
            that = YSLOW.ComponentSet.prototype;
            docBody = obj.docBody;
            doc = obj.doc;
            components = obj.components;
            ret = components;
        } else {
            that = this;
            docBody = that.doc_comp && that.doc_comp.body;
            doc = that.root_node;
            components = that.components;
            ret = that;
        }

        // check for mutation event support or anti-iframe option
        if (typeof MutationEvent === 'undefined' || YSLOW.antiIframe) {
            return that.setSimpleAfterOnload(callback, obj);
        }

        // skip testing when doc not found
        if (!docBody) {
            util.dump('doc body is empty');

            return callback(ret);
        }

        // set afteronload properties for all components loaded after window onlod
        done = function () {
            var i, j, len, lenJ, comp, src, cmp;

            // to avoid executing this function twice
            // due to ifrm iwin double listeners
            if (triggered) {
                return;
            }

            // cancel timers
            clearTimer(enough);
            clearTimer(timer);

            // remove listeners
            removeEventListener(iwin, 'DOMSubtreeModified', getTarget);
            removeEventListener(iwin, 'DOMNodeInserted', getTarget);
            removeEventListener(iwin, 'DOMAttrModified', getTarget);
            removeEventListener(ifrm, 'load', iframeOnload);
            removeEventListener(iwin, 'load', iframeOnload);

            // changed components loop
            for (i = 0, len =  comps.length; i < len; i += 1) {
                comp = comps[i];
                src = comp.src || comp.href || (comp.getAttribute &&
                    (comp.getAttribute('src') || comp.getAttribute('href')));
                if (!src) {
                    continue;
                }
                for (j = 0, lenJ = components.length; j < lenJ; j += 1) {
                    cmp = components[j];
                    if (cmp.url === src) {
                        cmp.after_onload = true;
                    }
                }
            }

            // remove temp iframe and invoke callback passing cset
            ifrm.parentNode.removeChild(ifrm);
            triggered = 1;
            callback(ret);
        };

        // create temp iframe with doc html
        ifrm = doc.createElement('iframe');
        ifrm.style.cssText = 'position:absolute;top:-999em;';
        doc.body.appendChild(ifrm);
        iwin = ifrm.contentWindow;

        // set a fallback when onload is not triggered
        noOnloadTimer = setTimer(done, 3000);

        // set onload and ifram content
        if (iwin) {
            idoc = iwin.document;
        } else {
            iwin = idoc = ifrm.contentDocument;
        }
        addEventListener(iwin, 'load', iframeOnload);
        addEventListener(ifrm, 'load', iframeOnload);
        idoc.open().write(docBody);
        idoc.close();
        addEventListener(iwin, 'load', iframeOnload);
    }
};

/*
 * List of valid protocols in component sets.
 */
YSLOW.ComponentSet.validProtocols = ['http', 'https', 'ftp'];

/**
 * @private
 * Check if url has an allowed protocol (no chrome://, about:)
 * @param url
 * @return false if url does not contain hostname.
 */
YSLOW.ComponentSet.isValidProtocol = function (s) {
    var i, index, protocol,
        validProtocols = this.validProtocols,
        len = validProtocols.length;

    s = s.toLowerCase();
    index = s.indexOf(':');
    if (index > 0) {
        protocol = s.substr(0, index);
        for (i = 0; i < len; i += 1) {
            if (protocol === validProtocols[i]) {
                return true;
            }
        }
    }

    return false;
};


/**
 * @private
 * Check if passed url has hostname specified.
 * @param url
 * @return false if url does not contain hostname.
 */
YSLOW.ComponentSet.isValidURL = function (url) {
    var arr, host;

    url = url.toLowerCase();

    // all url is in the format of <protocol>:<the rest of the url>
    arr = url.split(":");

    // for http protocol, we want to make sure there is a host in the url.
    if (arr[0] === "http" || arr[0] === "https") {
        if (arr[1].substr(0, 2) !== "//") {
            return false;
        }
        host = arr[1].substr(2);
        if (host.length === 0 || host.indexOf("/") === 0) {
            // no host specified.
            return false;
        }
    }

    return true;
};
/**
 * Copyright (c) 2012, Yahoo! Inc.  All rights reserved.
 * Copyright (c) 2013, Marcel Duran and other contributors. All rights reserved.
 * Copyrights licensed under the New BSD License. See the accompanying LICENSE file for terms.
 */

/*global YSLOW*/
/*jslint white: true, onevar: true, undef: true, newcap: true, nomen: true, plusplus: true, bitwise: true, browser: true, maxerr: 50, indent: 4 */

/**
 * @namespace YSLOW
 * @class Component
 * @constructor
 */
YSLOW.Component = function (url, type, parent_set, o) {
    var obj = o && o.obj,
        comp = (o && o.comp) || {};

    /**
     * URL of the component
     * @type String
     */
    this.url = url;

    /**
     * Component type, one of the following:
     * <ul>
     *  <li>doc</li>
     *  <li>js</li>
     *  <li>css</li>
     *  <li>...</li>
     * </ul>
     * @type String
     */
    this.type = type;

    /**
     * Parent component set.
     */
    this.parent = parent_set;

    this.headers = {};
    this.raw_headers = '';
    this.req_headers = null;
    this.body = '';
    this.compressed = false;
    this.expires = undefined; // to be replaced by a Date object
    this.size = 0;
    this.status = 0;
    this.is_beacon = false;
    this.method = 'unknown';
    this.cookie = '';
    this.respTime = null;
    this.after_onload = false;

    // component object properties
    // e.g. for image, image element width, image element height, actual width, actual height
    this.object_prop = undefined;

    // construction part
    if (type === undefined) {
        this.type = 'unknown';
    }

    this.get_info_state = 'NONE';

    if (obj && type === 'image' && obj.width && obj.height) {
        this.object_prop = {
            'width': obj.width,
            'height': obj.height
        };
    }

    if (comp.containerNode) {
        this.containerNode = comp.containerNode;
    }

    this.setComponentDetails(o);
};

/**
 * Return the state of getting detail info from the net.
 */
YSLOW.Component.prototype.getInfoState = function () {
    return this.get_info_state;
};

YSLOW.Component.prototype.populateProperties = function (resolveRedirect, ignoreImgReq) {
    var comp, encoding, expires, content_length, img_src, obj, dataUri,
        that = this,
        NULL = null,
        UNDEF = 'undefined';

    // check location
    // bookmarklet and har already handle redirects
    if (that.headers.location && resolveRedirect && that.headers.location !== that.url) {
        // Add a new component.
        comp = that.parent.addComponentNoDuplicate(that.headers.location,
            (that.type !== 'redirect' ? that.type : 'unknown'), that.url);
        if (comp && that.after_onload) {
            comp.after_onload = true;
        }
        that.type = 'redirect';
    }

    content_length = that.headers['content-length'];

    // gzip, deflate
    encoding = YSLOW.util.trim(that.headers['content-encoding']);
    if (encoding === 'gzip' || encoding === 'deflate') {
        that.compressed = encoding;
        that.size = (that.body.length) ? that.body.length : NULL;
        if (content_length) {
            that.size_compressed = parseInt(content_length, 10) ||
                content_length;
        } else if (typeof that.nsize !== UNDEF) {
            that.size_compressed = that.nsize;
        } else {
            // a hack
            that.size_compressed = Math.round(that.size / 3);
        }
    } else {
        that.compressed = false;
        that.size_compressed = NULL;
        if (content_length) {
            that.size = parseInt(content_length, 10);
        } else if (typeof that.nsize !== UNDEF) {
            that.size = parseInt(that.nsize, 10);
        } else {
            that.size = that.body.length;
        }
    }

    // size check/correction, @todo be more precise here
    if (!that.size) {
        if (typeof that.nsize !== UNDEF) {
            that.size = that.nsize;
        } else {
            that.size = that.body.length;
        }
    }
    that.uncompressed_size = that.body.length;

    // expiration based on either Expires or Cache-Control headers
    // always use max-age if exists following 1.1 spec
    // http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.3                                                                                      
    if (that.getMaxAge() !== undefined) {
        that.expires = that.getMaxAge();
    }
    else if (that.headers.expires && that.headers.expires.length > 0) {
        that.expires = new Date(that.headers.expires);
    }

    // compare image original dimensions with actual dimensions, data uri is
    // first attempted to get the orginal dimension, if it fails (btoa) then
    // another request to the orginal image is made
    if (that.type === 'image' && !ignoreImgReq) {
        if (typeof Image !== UNDEF) {
            obj = new Image();
        } else {
            obj = document.createElement('img');
        }
        if (that.body.length) {
            img_src = 'data:' + that.headers['content-type'] + ';base64,' +
                YSLOW.util.base64Encode(that.body);
            dataUri = 1;
        } else {
            img_src = that.url;
        }
        obj.onerror = function () {
            obj.onerror = NULL;
            if (dataUri) {
                obj.src = that.url;
            }
        };
        obj.onload = function () {
            obj.onload = NULL;
            if (obj && obj.width && obj.height) {
                if (that.object_prop) {
                    that.object_prop.actual_width = obj.width;
                    that.object_prop.actual_height = obj.height;
                } else {
                    that.object_prop = {
                        'width': obj.width,
                        'height': obj.height,
                        'actual_width': obj.width,
                        'actual_height': obj.height
                    };
                }
                if (obj.width < 2 && obj.height < 2) {
                    that.is_beacon = true;
                }
            }
        };
        obj.src = img_src;
    }
};

/**
 *  Return true if this object has a last-modified date significantly in the past.
 */
YSLOW.Component.prototype.hasOldModifiedDate = function () {
    var now = Number(new Date()),
        modified_date = this.headers['last-modified'];

    if (typeof modified_date !== 'undefined') {
        // at least 1 day in the past
        return ((now - Number(new Date(modified_date))) > (24 * 60 * 60 * 1000));
    }

    return false;
};

/**
 * Return true if this object has a far future Expires.
 * @todo: make the "far" interval configurable
 * @param expires Date object
 * @return true if this object has a far future Expires.
 */
YSLOW.Component.prototype.hasFarFutureExpiresOrMaxAge = function () {
    var expires_in_seconds,
        now = Number(new Date()),
        minSeconds = YSLOW.util.Preference.getPref('minFutureExpiresSeconds', 2 * 24 * 60 * 60),
        minMilliSeconds = minSeconds * 1000;

    if (typeof this.expires === 'object') {
        expires_in_seconds = Number(this.expires);
        if ((expires_in_seconds - now) > minMilliSeconds) {
            return true;
        }
    }

    return false;
};

YSLOW.Component.prototype.getEtag = function () {
    return this.headers.etag || '';
};

YSLOW.Component.prototype.getMaxAge = function () {
    var index, maxage, expires,
        cache_control = this.headers['cache-control'];

    if (cache_control) {
        index = cache_control.indexOf('max-age');
        if (index > -1) {
            maxage = parseInt(cache_control.substring(index + 8), 10);
            if (maxage > 0) {
                expires = YSLOW.util.maxAgeToDate(maxage);
            }
        }
    }

    return expires;
};

/**
 * Return total size of Set-Cookie headers of this component.
 * @return total size of Set-Cookie headers of this component.
 * @type Number
 */
YSLOW.Component.prototype.getSetCookieSize = function () {
    // only return total size of cookie received.
    var aCookies, k,
        size = 0;

    if (this.headers && this.headers['set-cookie']) {
        aCookies = this.headers['set-cookie'].split('\n');
        if (aCookies.length > 0) {
            for (k = 0; k < aCookies.length; k += 1) {
                size += aCookies[k].length;
            }
        }
    }

    return size;
};

/**
 * Return total size of Cookie HTTP Request headers of this component.
 * @return total size of Cookie headers Request of this component.
 * @type Number
 */
YSLOW.Component.prototype.getReceivedCookieSize = function () {
    // only return total size of cookie sent.
    var aCookies, k,
        size = 0;

    if (this.cookie && this.cookie.length > 0) {
        aCookies = this.cookie.split('\n');
        if (aCookies.length > 0) {
            for (k = 0; k < aCookies.length; k += 1) {
                size += aCookies[k].length;
            }
        }
    }

    return size;
};

/**
 * Platform implementation of
 * YSLOW.Component.prototype.setComponentDetails = function (o) {}
 * goes here
/*
/**
 * Copyright (c) 2012, Yahoo! Inc.  All rights reserved.
 * Copyright (c) 2013, Marcel Duran and other contributors. All rights reserved.
 * Copyrights licensed under the New BSD License. See the accompanying LICENSE file for terms.
 */

/**
 * Parse details (HTTP headers, content, etc) from a
 * given source and set component properties.
 * @param o The object containing component details.
 */
YSLOW.Component.prototype.setComponentDetails = function (o) {
    var comp = this,
        
        parseEntry = function (entry) {
            var i, header, cookie, len,
                response = entry.response,
                request = entry.request;

            // copy from the response object
            comp.status = response.status;
            comp.headers = {};
            comp.raw_headers = '';
            for (i = 0, len = response.headers.length; i < len; i += 1) {
                header = response.headers[i];
                comp.headers[header.name.toLowerCase()] = header.value;
                comp.raw_headers += header.name + ': ' + header.value + '\n';
            }
            comp.req_headers = {};
            for (i = 0, len = request.headers.length; i < len; i += 1) {
                header = request.headers[i];
                comp.req_headers[header.name.toLowerCase()] = header.value;
            }
            comp.method = request.method;
            if (response.content && response.content.text) {
                comp.body = response.content.text;
            } else {
                // no body provided, getting size at least and mocking empty string content
                comp.body = {
                    toString: function () {return '';},
                    length: response.content.size || 0
                };  
            }   
            // for security checking
            comp.response_type = comp.type;
            comp.cookie = (comp.headers['set-cookie'] || '') + (comp.req_headers['cookie'] || '');
            comp.nsize = parseInt(comp.headers['content-length'], 10) ||
                response.bodySize || response.content.size;
            comp.respTime = entry.time;
            comp.after_onload = (new Date(entry.startedDateTime)
                .getTime()) > comp.parent.onloadTimestamp;

            // populate properties ignoring redirect
            // resolution and image request
            comp.populateProperties(false, true);

            comp.get_info_state = 'DONE';

            // notify parent ComponentSet that this component has gotten net response.
            comp.parent.onComponentGetInfoStateChange({
                'comp': comp,
                'state': 'DONE'
            });
        };

    parseEntry(o.entry);
};
/**
 * Copyright (c) 2012, Yahoo! Inc.  All rights reserved.
 * Copyright (c) 2013, Marcel Duran and other contributors. All rights reserved.
 * Copyrights licensed under the New BSD License. See the accompanying LICENSE file for terms.
 */

/*global YSLOW*/
/*jslint white: true, onevar: true, undef: true, nomen: true, eqeqeq: true, plusplus: true, bitwise: true, regexp: true, newcap: true, immed: true */

/**
 * YSlow context object that holds components set, result set and statistics of current page.
 *
 * @constructor
 * @param {Document} doc Document object of current page.
 */
YSLOW.context = function (doc) {
    this.document = doc;
    this.component_set = null;
    this.result_set = null;
    this.onloadTimestamp = null;

    // reset renderer variables
    if (YSLOW.renderer) {
        YSLOW.renderer.reset();
    }

    this.PAGE = {
        totalSize: 0,
        totalRequests: 0,
        totalObjCount: {},
        totalObjSize: {},

        totalSizePrimed: 0,
        totalRequestsPrimed: 0,
        totalObjCountPrimed: {},
        totalObjSizePrimed: {},

        canvas_data: {},

        statusbar: false,
        overallScore: 0,

        t_done: undefined,
        loaded: false
    };

};

YSLOW.context.prototype = {

    defaultview: "ysPerfButton",

    /**
     * @private
     * Compute statistics of current page.
     * @param {Boolean} bCacheFull set to true if based on primed cache, false for empty cache.
     * @return stats object
     * @type Object
     */
    computeStats: function (bCacheFull) {
        var comps, comp, compType, i, len, size, totalSize, aTypes,
            canvas_data, sType,
            hCount = {},
            hSize = {}, // hashes where the key is the object type
            nHttpRequests = 0;

        if (!this.component_set) {
            /* need to run peeler first */
            return;
        }

        comps = this.component_set.components;
        if (!comps) {
            return;
        }

        // SUMMARY - Find the number and total size for the categories.
        // Iterate over all the components and add things up.
        for (i = 0, len = comps.length; i < len; i += 1) {
            comp = comps[i];

            if (!bCacheFull || !comp.hasFarFutureExpiresOrMaxAge()) {
                // If the object has a far future Expires date it won't add any HTTP requests nor size to the page.
                // It adds to the HTTP requests (at least a condition GET request).
                nHttpRequests += 1;
                compType = comp.type;
                hCount[compType] = (typeof hCount[compType] === 'undefined' ? 1 : hCount[compType] + 1);
                size = 0;
                if (!bCacheFull || !comp.hasOldModifiedDate()) {
                    // If we're doing EMPTY cache stats OR this component is newly modified (so is probably changing).
                    if (comp.compressed === 'gzip' || comp.compressed === 'deflate') {
                        if (comp.size_compressed) {
                            size = parseInt(comp.size_compressed, 10);
                        }
                    } else {
                        size = comp.size;
                    }
                }
                hSize[compType] = (typeof hSize[compType] === 'undefined' ? size : hSize[compType] + size);
            }
        }

        totalSize = 0;
        aTypes = YSLOW.peeler.types;
        canvas_data = {};
        for (i = 0; i < aTypes.length; i += 1) {
            sType = aTypes[i];
            if (typeof hCount[sType] !== "undefined") {
                // canvas
                if (hSize[sType] > 0) {
                    canvas_data[sType] = hSize[sType];
                }
                totalSize += hSize[sType];
            }
        }

        return {
            'total_size': totalSize,
            'num_requests': nHttpRequests,
            'count_obj': hCount,
            'size_obj': hSize,
            'canvas_data': canvas_data
        };
    },

    /**
     * Collect Statistics of the current page.
     */
    collectStats: function () {
        var stats = this.computeStats();
        if (stats !== undefined) {
            this.PAGE.totalSize = stats.total_size;
            this.PAGE.totalRequests = stats.num_requests;
            this.PAGE.totalObjCount = stats.count_obj;
            this.PAGE.totalObjSize = stats.size_obj;
            this.PAGE.canvas_data.empty = stats.canvas_data;
        }

        stats = this.computeStats(true);
        if (stats) {
            this.PAGE.totalSizePrimed = stats.total_size;
            this.PAGE.totalRequestsPrimed = stats.num_requests;
            this.PAGE.totalObjCountPrimed = stats.count_obj;
            this.PAGE.totalObjSizePrimed = stats.size_obj;
            this.PAGE.canvas_data.primed = stats.canvas_data;
        }
    },

    /**
     * Call registered renderer to generate Grade view with the passed output format.
     *
     * @param {String} output_format output format, e.g. 'html', 'xml'
     * @return Grade in the passed output format.
     */
    genPerformance: function (output_format, doc) {
        if (this.result_set === null) {
            if (!doc) {
                doc = this.document;
            }
            YSLOW.controller.lint(doc, this);
        }
        return YSLOW.controller.render(output_format, 'reportcard', {
            'result_set': this.result_set
        });
    },

    /**
     * Call registered renderer to generate Stats view with the passed output format.
     *
     * @param {String} output_format output format, e.g. 'html', 'xml'
     * @return Stats in the passed output format.
     */
    genStats: function (output_format) {
        var stats = {};
        if (!this.PAGE.totalSize) {
            // collect stats
            this.collectStats();
        }
        stats.PAGE = this.PAGE;
        return YSLOW.controller.render(output_format, 'stats', {
            'stats': stats
        });
    },

    /**
     * Call registered renderer to generate Components view with the passed output format.
     *
     * @param {String} output_format output format, e.g. 'html', 'xml'
     * @return Components in the passed output format.
     */
    genComponents: function (output_format) {
        if (!this.PAGE.totalSize) {
            // collect stats
            this.collectStats();
        }
        return YSLOW.controller.render(output_format, 'components', {
            'comps': this.component_set.components,
            'total_size': this.PAGE.totalSize
        });
    },

    /**
     * Call registered renderer to generate Tools view with the passed output format.
     *
     * @param {String} output_format output format, e.g. 'html'
     * @return Tools in the passed output format.
     */
    genToolsView: function (output_format) {
        var tools = YSLOW.Tools.getAllTools();
        return YSLOW.controller.render(output_format, 'tools', {
            'tools': tools
        });
    },

    /**
     * Call registered renderer to generate Ruleset Settings view with the passed output format.
     *
     * @param {String} output_format output format, e.g. 'html'
     * @return Ruleset Settings in the passed output format.
     */
    genRulesetEditView: function (output_format) {
        return YSLOW.controller.render(output_format, 'rulesetEdit', {
            'rulesets': YSLOW.controller.getRegisteredRuleset()
        });
    }

};
/**
 * Copyright (c) 2012, Yahoo! Inc.  All rights reserved.
 * Copyright (c) 2013, Marcel Duran and other contributors. All rights reserved.
 * Copyrights licensed under the New BSD License. See the accompanying LICENSE file for terms.
 */

/*global YSLOW*/
/*jslint white: true, browser: true, onevar: true, undef: true, nomen: true, eqeqeq: true, plusplus: true, bitwise: true, regexp: true, newcap: true, immed: true */

/**
 * @namespace YSLOW
 * @class controller
 * @static
 */

YSLOW.controller = {

    rules: {},

    rulesets: {},

    onloadTimestamp: null,

    renderers: {},

    default_ruleset_id: 'ydefault',

    run_pending: 0,

    /**
     * Init code.  Add event listeners.
     */
    init: function () {
        var arr_rulesets, i, obj, value;

        // listen to onload event.
        YSLOW.util.event.addListener("onload", function (e) {
            this.onloadTimestamp = e.time;
            YSLOW.util.setTimer(function () {
                YSLOW.controller.run_pending_event();
            });
        }, this);

        // listen to onunload event.
        YSLOW.util.event.addListener("onUnload", function (e) {
            this.run_pending = 0;
            this.onloadTimestamp = null;
        }, this);

        // load custom ruleset
        arr_rulesets = YSLOW.util.Preference.getPrefList("customRuleset.", undefined);
        if (arr_rulesets && arr_rulesets.length > 0) {
            for (i = 0; i < arr_rulesets.length; i += 1) {
                value = arr_rulesets[i].value;
                if (typeof value === "string" && value.length > 0) {
                    obj = JSON.parse(value, null);
                    obj.custom = true;
                    this.addRuleset(obj);
                }
            }
        }

        this.default_ruleset_id = YSLOW.util.Preference.getPref("defaultRuleset", 'ydefault');

        // load rule config preference
        this.loadRulePreference();
    },

    /**
     * Run controller to start peeler. Don't start if the page is not done loading.
     * Delay the running until onload event.
     *
     * @param {Window} win window object
     * @param {YSLOW.context} yscontext YSlow context to use.
     * @param {Boolean} autorun value to indicate if triggered by autorun
     */
    run: function (win, yscontext, autorun) {
        var cset, line,
            doc = win.document;

        if (!doc || !doc.location || doc.location.href.indexOf("about:") === 0 || "undefined" === typeof doc.location.hostname) {
            if (!autorun) {
                line = 'Please enter a valid website address before running YSlow.';
                YSLOW.ysview.openDialog(YSLOW.ysview.panel_doc, 389, 150, line, '', 'Ok');
            }
            return;
        }

        // Since firebug 1.4, onload event is not passed to YSlow if firebug
        // panel is not opened. Recommendation from firebug dev team is to
        // refresh the page before running yslow, which is unnecessary from
        // yslow point of view.  For now, just don't enforce running YSlow
        // on a page has finished loading.
        if (!yscontext.PAGE.loaded) {
            this.run_pending = {
                'win': win,
                'yscontext': yscontext
            };
            // @todo: put up spining logo to indicate waiting for page finish loading.
            return;
        }

        YSLOW.util.event.fire("peelStart", undefined);
        cset = YSLOW.peeler.peel(doc, this.onloadTimestamp);
        // need to set yscontext_component_set before firing peelComplete,
        // otherwise, may run into infinite loop.
        yscontext.component_set = cset;
        YSLOW.util.event.fire("peelComplete", {
            'component_set': cset
        });

        // notify ComponentSet peeling is done.
        cset.notifyPeelDone();
    },

    /**
     * Start pending run function.
     */
    run_pending_event: function () {
        if (this.run_pending) {
            this.run(this.run_pending.win, this.run_pending.yscontext, false);
            this.run_pending = 0;
        }
    },

    /**
     * Run lint function of the ruleset matches the passed rulset_id.
     * If ruleset_id is undefined, use Controller's default ruleset.
     * @param {Document} doc Document object of the page to run lint.
     * @param {YSLOW.context} yscontext YSlow context to use.
     * @param {String} ruleset_id ID of the ruleset to run.
     * @return Lint result
     * @type YSLOW.ResultSet
     */
    lint: function (doc, yscontext, ruleset_id) {
        var rule, rules, i, conf, result, weight, score,
            ruleset = [],
            results = [],
            total_score = 0,
            total_weight = 0,
            that = this,
            rs = that.rulesets,
            defaultRuleSetId = that.default_ruleset_id;

        if (ruleset_id) {
            ruleset = rs[ruleset_id];
        } else if (defaultRuleSetId && rs[defaultRuleSetId]) {
            ruleset = rs[defaultRuleSetId];
        } else {
            // if no ruleset, take the first one available
            for (i in rs) {
                if (rs.hasOwnProperty(i) && rs[i]) {
                    ruleset = rs[i];
                    break;
                }
            }
        }

        rules = ruleset.rules;
        for (i in rules) {
            if (rules.hasOwnProperty(i) && rules[i] &&
                    this.rules.hasOwnProperty(i)) {
                try {
                    rule = this.rules[i];
                    conf = YSLOW.util.merge(rule.config, rules[i]);

                    result = rule.lint(doc, yscontext.component_set, conf);

                    // apply rule weight to result.
                    weight = (ruleset.weights ? ruleset.weights[i] : undefined);
                    if (weight !== undefined) {
                        weight = parseInt(weight, 10);
                    }
                    if (weight === undefined || weight < 0 || weight > 100) {
                        if (rs.ydefault.weights[i]) {
                            weight = rs.ydefault.weights[i];
                        } else {
                            weight = 5;
                        }
                    }
                    result.weight = weight;

                    if (result.score !== undefined) {
                        if (typeof result.score !== "number") {
                            score = parseInt(result.score, 10);
                            if (!isNaN(score)) {
                                result.score = score;
                            }
                        }

                        if (typeof result.score === 'number') {
                            total_weight += result.weight;

                            if (!YSLOW.util.Preference.getPref('allowNegativeScore', false)) {
                                if (result.score < 0) {
                                    result.score = 0;
                                }
                                if (typeof result.score !== 'number') {
                                    // for backward compatibilty of n/a
                                    result.score = -1;
                                }
                            }

                            if (result.score !== 0) {
                                total_score += result.score * (typeof result.weight !== 'undefined' ? result.weight : 1);
                            }
                        }
                    }

                    result.name = rule.name;
                    result.category = rule.category;
                    result.rule_id = i;

                    results[results.length] = result;
                } catch (err) {
                    YSLOW.util.dump("YSLOW.controller.lint: " + i, err);
                    YSLOW.util.event.fire("lintError", {
                        'rule': i,
                        'message': err
                    });
                }
            }
        }

        yscontext.PAGE.overallScore = total_score / (total_weight > 0 ? total_weight : 1);
        yscontext.result_set = new YSLOW.ResultSet(results, yscontext.PAGE.overallScore, ruleset);
        yscontext.result_set.url = yscontext.component_set.doc_comp.url;
        YSLOW.util.event.fire("lintResultReady", {
            'yslowContext': yscontext
        });

        return yscontext.result_set;
    },

    /**
     * Run tool that matches the passed tool_id
     * @param {String} tool_id ID of the tool to be run.
     * @param {YSLOW.context} yscontext YSlow context
     * @param {Object} param parameters to be passed to run method of tool.
     */
    runTool: function (tool_id, yscontext, param) {
        var result, html, doc, h, css, uri, req2, l, s, message, body,
            tool = YSLOW.Tools.getTool(tool_id);

        try {
            if (typeof tool === "object") {
                result = tool.run(yscontext.document, yscontext.component_set, param);
                if (tool.print_output) {
                    html = '';
                    if (typeof result === "object") {
                        html = result.html;
                    } else if (typeof result === "string") {
                        html = result;
                    }
                    doc = YSLOW.util.getNewDoc();
                    body = doc.body || doc.documentElement;
                    body.innerHTML = html;
                    h = doc.getElementsByTagName('head')[0];
                    if (typeof result.css === "undefined") {
                        // use default.
                        uri = 'chrome://yslow/content/yslow/tool.css';
                        req2 = new XMLHttpRequest();
                        req2.open('GET', uri, false);
                        req2.send(null);
                        css = req2.responseText;
                    } else {
                        css = result.css;
                    }
                    if (typeof css === "string") {
                        l = doc.createElement("style");
                        l.setAttribute("type", "text/css");
                        l.appendChild(doc.createTextNode(css));
                        h.appendChild(l);
                    }

                    if (typeof result.js !== "undefined") {
                        s = doc.createElement("script");
                        s.setAttribute("type", "text/javascript");
                        s.appendChild(doc.createTextNode(result.js));
                        h.appendChild(s);
                    }
                    if (typeof result.plot_component !== "undefined" && result.plot_component === true) {
                        // plot components
                        YSLOW.renderer.plotComponents(doc, yscontext);
                    }
                }
            } else {
                message = tool_id + " is not a tool.";
                YSLOW.util.dump(message);
                YSLOW.util.event.fire("toolError", {
                    'tool_id': tool_id,
                    'message': message
                });
            }
        } catch (err) {
            YSLOW.util.dump("YSLOW.controller.runTool: " + tool_id, err);
            YSLOW.util.event.fire("toolError", {
                'tool_id': tool_id,
                'message': err
            });
        }
    },

    /**
     * Find a registered renderer with the passed id to render the passed view.
     * @param {String} id ID of renderer to be used. eg. 'html'
     * @param {String} view id of view, e.g. 'reportcard', 'stats' and 'components'
     * @param {Object} params parameter object to pass to XXXview method of renderer.
     * @return content the renderer generated.
     */
    render: function (id, view, params) {
        var renderer = this.renderers[id],
            content = '';

        if (renderer.supports[view] !== undefined && renderer.supports[view] === 1) {
            switch (view) {
            case 'components':
                content = renderer.componentsView(params.comps, params.total_size);
                break;
            case 'reportcard':
                content = renderer.reportcardView(params.result_set);
                break;
            case 'stats':
                content = renderer.statsView(params.stats);
                break;
            case 'tools':
                content = renderer.toolsView(params.tools);
                break;
            case 'rulesetEdit':
                content = renderer.rulesetEditView(params.rulesets);
                break;
            }
        }
        return content;

    },

    /**
     * Get registered renderer with the passed id.
     * @param {String} id ID of the renderer
     */
    getRenderer: function (id) {
        return this.renderers[id];
    },

    /**
     * @see YSLOW.registerRule
     */
    addRule: function (rule) {
        var i, doc_obj,
            required = ['id', 'name', 'config', 'info', 'lint'];

        // check YSLOW.doc class for text
        if (YSLOW.doc.rules && YSLOW.doc.rules[rule.id]) {
            doc_obj = YSLOW.doc.rules[rule.id];
            if (doc_obj.name) {
                rule.name = doc_obj.name;
            }
            if (doc_obj.info) {
                rule.info = doc_obj.info;
            }
        }

        for (i = 0; i < required.length; i += 1) {
            if (typeof rule[required[i]] === 'undefined') {
                throw new YSLOW.Error('Interface error', 'Improperly implemented rule interface');
            }
        }
        if (this.rules[rule.id] !== undefined) {
            throw new YSLOW.Error('Rule register error', rule.id + " is already defined.");
        }
        this.rules[rule.id] = rule;
    },

    /**
     * @see YSLOW.registerRuleset
     */
    addRuleset: function (ruleset, update) {
        var i, required = ['id', 'name', 'rules'];

        for (i = 0; i < required.length; i += 1) {
            if (typeof ruleset[required[i]] === 'undefined') {
                throw new YSLOW.Error('Interface error', 'Improperly implemented ruleset interface');
            }
            if (this.checkRulesetName(ruleset.id) && update !== true) {
                throw new YSLOW.Error('Ruleset register error', ruleset.id + " is already defined.");
            }
        }
        this.rulesets[ruleset.id] = ruleset;
    },

    /**
     * Remove ruleset from controller.
     * @param {String} ruleset_id ID of the ruleset to be deleted.
     */
    removeRuleset: function (ruleset_id) {
        var ruleset = this.rulesets[ruleset_id];

        if (ruleset && ruleset.custom === true) {
            delete this.rulesets[ruleset_id];

            // if we are deleting the default ruleset, change default to 'ydefault'.
            if (this.default_ruleset_id === ruleset_id) {
                this.default_ruleset_id = 'ydefault';
                YSLOW.util.Preference.setPref("defaultRuleset", this.default_ruleset_id);
            }
            return ruleset;
        }

        return null;
    },

    /**
     * Save ruleset to preference.
     * @param {YSLOW.Ruleset} ruleset ruleset to be saved.
     */
    saveRulesetToPref: function (ruleset) {
        if (ruleset.custom === true) {
            YSLOW.util.Preference.setPref("customRuleset." + ruleset.id, JSON.stringify(ruleset, null, 2));
        }
    },

    /**
     * Remove ruleset from preference.
     * @param {YSLOW.Ruleset} ruleset ruleset to be deleted.
     */
    deleteRulesetFromPref: function (ruleset) {
        if (ruleset.custom === true) {
            YSLOW.util.Preference.deletePref("customRuleset." + ruleset.id);
        }
    },

    /**
     * Get ruleset with the passed id.
     * @param {String} ruleset_id ID of ruleset to be retrieved.
     */
    getRuleset: function (ruleset_id) {
        return this.rulesets[ruleset_id];
    },

    /**
     * @see YSLOW.registerRenderer
     */
    addRenderer: function (renderer) {
        this.renderers[renderer.id] = renderer;
    },

    /**
     * Return a hash of registered ruleset objects.
     * @return a hash of rulesets with ruleset_id => ruleset
     */
    getRegisteredRuleset: function () {
        return this.rulesets;
    },

    /**
     * Return a hash of registered rule objects.
     * @return all the registered rule objects in a hash. rule_id => rule object
     */
    getRegisteredRules: function () {
        return this.rules;
    },

    /**
     * Return the rule object identified by rule_id
     * @param {String} rule_id ID of rule object to be retrieved.
     * @return rule object.
     */
    getRule: function (rule_id) {
        return this.rules[rule_id];
    },

    /**
     * Check if name parameter is conflict with any existing ruleset name.
     * @param {String} name Name to check.
     * @return true if name conflicts, false otherwise.
     * @type Boolean
     */
    checkRulesetName: function (name) {
        var id, ruleset,
            rulesets = this.rulesets;

        name = name.toLowerCase();
        for (id in rulesets) {
            if (rulesets.hasOwnProperty(id)) {
                ruleset = rulesets[id];
                if (ruleset.id.toLowerCase() === name ||
                        ruleset.name.toLowerCase() === name) {
                    return true;
                }
            }
        }

        return false;
    },

    /**
     * Set default ruleset.
     * @param {String} id ID of the ruleset to be used as default.
     */
    setDefaultRuleset: function (id) {
        if (this.rulesets[id] !== undefined) {
            this.default_ruleset_id = id;
            // save to pref
            YSLOW.util.Preference.setPref("defaultRuleset", id);
        }
    },

    /**
     * Get default ruleset.
     * @return default ruleset
     * @type YSLOW.Ruleset
     */
    getDefaultRuleset: function () {
        if (this.rulesets[this.default_ruleset_id] === undefined) {
            this.setDefaultRuleset('ydefault');
        }
        return this.rulesets[this.default_ruleset_id];
    },

    /**
     * Get default ruleset id
     * @return ID of the default ruleset
     * @type String
     */
    getDefaultRulesetId: function () {
        return this.default_ruleset_id;
    },

    /**
     * Load user preference for some rules. This is needed before enabling user writing ruleset yslow plugin.
     */
    loadRulePreference: function () {
        var rule = this.getRule('yexpires'),
            minSeconds = YSLOW.util.Preference.getPref("minFutureExpiresSeconds", 2 * 24 * 60 * 60);

        if (minSeconds > 0 && rule) {
            rule.config.howfar = minSeconds;
        }
    }
};
/**
 * Copyright (c) 2012, Yahoo! Inc.  All rights reserved.
 * Copyrights licensed under the New BSD License. See the accompanying LICENSE file for terms.
 */

/*global YSLOW, Firebug, Components, ActiveXObject, gBrowser, window, getBrowser*/
/*jslint sloppy: true, bitwise: true, browser: true, regexp: true*/

/**
 * @namespace YSLOW
 * @class util
 * @static
 */
YSLOW.util = {

    /**
     * merges two objects together, the properties of the second
     * overwrite the properties of the first
     *
     * @param {Object} a Object a
     * @param {Object} b Object b
     * @return {Object} A new object, result of the merge
     */
    merge: function (a, b) {
        var i, o = {};

        for (i in a) {
            if (a.hasOwnProperty(i)) {
                o[i] = a[i];
            }
        }
        for (i in b) {
            if (b.hasOwnProperty(i)) {
                o[i] = b[i];
            }
        }
        return o;

    },


    /**
     * Dumps debug information in FB console, Error console or alert
     *
     * @param {Object} what Object to dump
     */
    dump: function () {
        var args;

        // skip when debbuging is disabled
        if (!YSLOW.DEBUG) {
            return;
        }

        // get arguments and normalize single parameter
        args = Array.prototype.slice.apply(arguments);
        args = args && args.length === 1 ? args[0] : args;

        try {
            if (typeof Firebug !== 'undefined' && Firebug.Console
                    && Firebug.Console.log) { // Firebug
                Firebug.Console.log(args);
            } else if (typeof Components !== 'undefined' && Components.classes
                    && Components.interfaces) { // Firefox
                Components.classes['@mozilla.org/consoleservice;1']
                    .getService(Components.interfaces.nsIConsoleService)
                    .logStringMessage(JSON.stringify(args, null, 2));
            }
        } catch (e1) {
            try {
                console.log(args);
            } catch (e2) {
                // alert shouldn't be used due to its annoying modal behavior
            }
        }
    },

    /**
     * Filters an object/hash using a callback
     *
     * The callback function will be passed two params - a key and a value of each element
     * It should return TRUE is the element is to be kept, FALSE otherwise
     *
     * @param {Object} hash Object to be filtered
     * @param {Function} callback A callback function
     * @param {Boolean} rekey TRUE to return a new array, FALSE to return an object and keep the keys/properties
     */
    filter: function (hash, callback, rekey) {
        var i,
            result = rekey ? [] : {};

        for (i in hash) {
            if (hash.hasOwnProperty(i) && callback(i, hash[i])) {
                result[rekey ? result.length : i] = hash[i];
            }
        }

        return result;
    },

    expires_month: {
        Jan: 1,
        Feb: 2,
        Mar: 3,
        Apr: 4,
        May: 5,
        Jun: 6,
        Jul: 7,
        Aug: 8,
        Sep: 9,
        Oct: 10,
        Nov: 11,
        Dec: 12
    },


    /**
     * Make a pretty string out of an Expires object.
     *
     * @todo Remove or replace by a general-purpose date formatting method
     *
     * @param {String} s_expires Datetime string
     * @return {String} Prity date
     */
    prettyExpiresDate: function (expires) {
        var month;

        if (Object.prototype.toString.call(expires) === '[object Date]' && expires.toString() !== 'Invalid Date' && !isNaN(expires)) {
            month = expires.getMonth() + 1;
            return expires.getFullYear() + "/" + month + "/" + expires.getDate();
        } else if (!expires) {
            return 'no expires';
        }
        return 'invalid date object';
    },

    /**
     * Converts cache-control: max-age=? into a JavaScript date
     *
     * @param {Integer} seconds Number of seconds in the cache-control header
     * @return {Date} A date object coresponding to the expiry date
     */
    maxAgeToDate: function (seconds) {
        var d = new Date();

        d = d.getTime() + parseInt(seconds, 10) * 1000;
        return new Date(d);
    },

    /**
     * Produces nicer sentences accounting for single/plural occurences.
     *
     * For example: "There are 3 scripts" vs "There is 1 script".
     * Currently supported tags to be replaced are:
     * %are%, %s% and %num%
     *
     *
     * @param {String} template A template with tags, like "There %are% %num% script%s%"
     * @param {Integer} num An integer value that replaces %num% and also deternmines how the other tags will be replaced
     * @return {String} The text after substitution
     */
    plural: function (template, number) {
        var i,
            res = template,
            repl = {
                are: ['are', 'is'],
                s: ['s', ''],
                'do': ['do', 'does'],
                num: [number, number]
            };


        for (i in repl) {
            if (repl.hasOwnProperty(i)) {
                res = res.replace(new RegExp('%' + i + '%', 'gm'), (number === 1) ? repl[i][1] : repl[i][0]);
            }
        }

        return res;
    },

    /**
     * Counts the number of expression in a given piece of stylesheet.
     *
     * Expressions are identified by the presence of the literal string "expression(".
     * There could be false positives in commented out styles.
     *
     * @param {String} content Text to inspect for the presence of expressions
     * @return {Integer} The number of expressions in the text
     */
    countExpressions: function (content) {
        var num_expr = 0,
            index;

        index = content.indexOf("expression(");
        while (index !== -1) {
            num_expr += 1;
            index = content.indexOf("expression(", index + 1);
        }

        return num_expr;
    },

    /**
     * Counts the number of AlphaImageLoader filter in a given piece of stylesheet.
     *
     * AlphaImageLoader filters are identified by the presence of the literal string "filter:" and
     * "AlphaImageLoader" .
     * There could be false positives in commented out styles.
     *
     * @param {String} content Text to inspect for the presence of filters
     * @return {Hash} 'filter type' => count. For Example, {'_filter' : count }
     */
    countAlphaImageLoaderFilter: function (content) {
        var index, colon, filter_hack, value,
            num_filter = 0,
            num_hack_filter = 0,
            result = {};

        index = content.indexOf("filter:");
        while (index !== -1) {
            filter_hack = false;
            if (index > 0 && content.charAt(index - 1) === '_') {
                // check underscore.
                filter_hack = true;
            }
            // check literal string "AlphaImageLoader"
            colon = content.indexOf(";", index + 7);
            if (colon !== -1) {
                value = content.substring(index + 7, colon);
                if (value.indexOf("AlphaImageLoader") !== -1) {
                    if (filter_hack) {
                        num_hack_filter += 1;
                    } else {
                        num_filter += 1;
                    }
                }
            }
            index = content.indexOf("filter:", index + 1);
        }

        if (num_hack_filter > 0) {
            result.hackFilter = num_hack_filter;
        }
        if (num_filter > 0) {
            result.filter = num_filter;
        }

        return result;
    },

    /**
     * Returns the hostname (domain) for a given URL
     * 
     * @param {String} url The absolute URL to get hostname from
     * @return {String} The hostname
     */
    getHostname: function (url) {
        var hostname = url.split('/')[2];

        return (hostname && hostname.split(':')[0]) || '';
    },

    /**
     * Returns an array of unique domain names, based on a given array of components
     *
     * @param {Array} comps An array of components (not a @see ComponentSet)
     * @param {Boolean} exclude_ips Whether to exclude IP addresses from the list of domains (for DNS check purposes)
     * @return {Array} An array of unique domian names
     */
    getUniqueDomains: function (comps, exclude_ips) {
        var i, len, parts,
            domains = {},
            retval = [];

        for (i = 0, len = comps.length; i < len; i += 1) {
            parts = comps[i].url.split('/');
            if (parts[2]) {
                // add to hash, but remove port number first
                domains[parts[2].split(':')[0]] = 1;
            }
        }

        for (i in domains) {
            if (domains.hasOwnProperty(i)) {
                if (!exclude_ips) {
                    retval.push(i);
                } else {
                    // exclude ips, identify them by the pattern "what.e.v.e.r.[number]"
                    parts = i.split('.');
                    if (isNaN(parseInt(parts[parts.length - 1], 10))) {
                        // the last part is "com" or something that is NaN
                        retval.push(i);
                    }
                }
            }
        }

        return retval;
    },

    summaryByDomain: function (comps, sumFields, excludeIPs) {
        var i, j, len, parts, hostname, domain, comp, sumLen, field, sum,
            domains = {},
            retval = [];

        // normalize sumField to array (makes things easier)
        sumFields = [].concat(sumFields);
        sumLen = sumFields.length;

        // loop components, count and summarize fields
        for (i = 0, len = comps.length; i < len; i += 1) {
            comp = comps[i];
            parts = comp.url.split('/');
            if (parts[2]) {
                // add to hash, but remove port number first
                hostname = parts[2].split(':')[0];
                domain = domains[hostname];
                if (!domain) {
                    domain = {
                        domain: hostname,
                        count: 0
                    };
                    domains[hostname] = domain;
                }
                domain.count += 1;
                // fields summary
                for (j = 0; j < sumLen; j += 1) {
                    field = sumFields[j];
                    sum = domain['sum_' + field] || 0;
                    sum += parseInt(comp[field], 10) || 0;
                    domain['sum_' + field] = sum;
                }
            }
        }

        // loop hash of unique domains
        for (domain in domains) {
            if (domains.hasOwnProperty(domain)) {
                if (!excludeIPs) {
                    retval.push(domains[domain]);
                } else {
                    // exclude ips, identify them by the pattern "what.e.v.e.r.[number]"
                    parts = domain.split('.');
                    if (isNaN(parseInt(parts[parts.length - 1], 10))) {
                        // the last part is "com" or something that is NaN
                        retval.push(domains[domain]);
                    }
                }
            }
        }

        return retval;
    },

    /**
     * Checks if a given piece of text (sctipt, stylesheet) is minified.
     *
     * The logic is: we strip consecutive spaces, tabs and new lines and
     * if this improves the size by more that 20%, this means there's room for improvement.
     *
     * @param {String} contents The text to be checked for minification
     * @return {Boolean} TRUE if minified, FALSE otherwise
     */
    isMinified: function (contents) {
        var len = contents.length,
            striplen;

        if (len === 0) { // blank is as minified as can be
            return true;
        }

        // TODO: enhance minifier logic by adding comment checking: \/\/[\w\d \t]*|\/\*[\s\S]*?\*\/
        // even better: add jsmin/cssmin
        striplen = contents.replace(/\n| {2}|\t|\r/g, '').length; // poor man's minifier
        if (((len - striplen) / len) > 0.2) { // we saved 20%, so this component can get some mifinication done
            return false;
        }

        return true;
    },


    /**
     * Inspects the ETag.
     *
     * Returns FALSE (bad ETag) only if the server is Apache or IIS and the ETag format
     * matches the default ETag format for the server. Anything else, including blank etag
     * returns TRUE (good ETag).
     * Default IIS: Filetimestamp:ChangeNumber
     * Default Apache: inode-size-timestamp
     *
     * @param {String} etag ETag response header
     * @return {Boolean} TRUE if ETag is good, FALSE otherwise
     */
    isETagGood: function (etag) {
        var reIIS = /^[0-9a-f]+:([1-9a-f]|[0-9a-f]{2,})$/,
            reApache = /^[0-9a-f]+\-[0-9a-f]+\-[0-9a-f]+$/;

        if (!etag) {
            return true; // no etag is ok etag
        }

        etag = etag.replace(/^["']|["'][\s\S]*$/g, ''); // strip " and '
        return !(reApache.test(etag) || reIIS.test(etag));
    },

    /**
     * Get internal component type from passed mime type.
     * @param {String} content_type mime type of the content.
     * @return yslow internal component type
     * @type String
     */
    getComponentType: function (content_type) {
        var c_type = 'unknown';

        if (content_type && typeof content_type === "string") {
            if (content_type === "text/html" || content_type === "text/plain") {
                c_type = 'doc';
            } else if (content_type === "text/css") {
                c_type = 'css';
            } else if (/javascript/.test(content_type)) {
                c_type = 'js';
            } else if (/flash/.test(content_type)) {
                c_type = 'flash';
            } else if (/image/.test(content_type)) {
                c_type = 'image';
            } else if (/font/.test(content_type)) {
                c_type = 'font';
            }
        }

        return c_type;
    },

    /**
     * base64 encode the data. This works with data that fails win.atob.
     * @param {bytes} data data to be encoded.
     * @return bytes array of data base64 encoded.
     */
    base64Encode: function (data) {
        var i, a, b, c, new_data = '',
            padding = 0,
            arr = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'];

        for (i = 0; i < data.length; i += 3) {
            a = data.charCodeAt(i);
            if ((i + 1) < data.length) {
                b = data.charCodeAt(i + 1);
            } else {
                b = 0;
                padding += 1;
            }
            if ((i + 2) < data.length) {
                c = data.charCodeAt(i + 2);
            } else {
                c = 0;
                padding += 1;
            }

            new_data += arr[(a & 0xfc) >> 2];
            new_data += arr[((a & 0x03) << 4) | ((b & 0xf0) >> 4)];
            if (padding > 0) {
                new_data += "=";
            } else {
                new_data += arr[((b & 0x0f) << 2) | ((c & 0xc0) >> 6)];
            }
            if (padding > 1) {
                new_data += "=";
            } else {
                new_data += arr[(c & 0x3f)];
            }
        }

        return new_data;
    },

    /**
     * Creates x-browser XHR objects
     *
     * @return {XMLHTTPRequest} A new XHR object
     */
    getXHR: function () {
        var i = 0,
            xhr = null,
            ids = ['MSXML2.XMLHTTP.3.0', 'MSXML2.XMLHTTP', 'Microsoft.XMLHTTP'];


        if (typeof XMLHttpRequest === 'function') {
            return new XMLHttpRequest();
        }

        for (i = 0; i < ids.length; i += 1) {
            try {
                xhr = new ActiveXObject(ids[i]);
                break;
            } catch (e) {}

        }

        return xhr;
    },

    /**
     * Returns the computed style
     *
     * @param {HTMLElement} el A node
     * @param {String} st Style identifier, e.g. "backgroundImage"
     * @param {Boolean} get_url Whether to return a url
     * @return {String|Boolean} The value of the computed style, FALSE if get_url is TRUE and the style is not a URL
     */
    getComputedStyle: function (el, st, get_url) {
        var style, urlMatch,
            res = '';

        if (el.currentStyle) {
            res = el.currentStyle[st];
        }

        if (el.ownerDocument && el.ownerDocument.defaultView && document.defaultView.getComputedStyle) {
            style = el.ownerDocument.defaultView.getComputedStyle(el, '');
            if (style) {
                res = style[st];
            }
        }

        if (!get_url) {
            return res;
        }

        if (typeof res !== 'string') {
            return false;
        }

        urlMatch = res.match(/\burl\((\'|\"|)([^\'\"]+?)\1\)/);
        if (urlMatch) {
            return urlMatch[2];
        } else {
            return false;
        }
    },

    /**
     * escape '<' and '>' in the passed html code.
     * @param {String} html code to be escaped.
     * @return escaped html code
     * @type String
     */
    escapeHtml: function (html) {
        return (html || '').toString()
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;");
    },

    /**
     * escape quotes in the passed html code.
     * @param {String} str string to be escaped.
     * @param {String} which type of quote to be escaped. 'single' or 'double'
     * @return escaped string code
     * @type String
     */
    escapeQuotes: function (str, which) {
        if (which === 'single') {
            return str.replace(/\'/g, '\\\''); // '
        }
        if (which === 'double') {
            return str.replace(/\"/g, '\\\"'); // "
        }
        return str.replace(/\'/g, '\\\'').replace(/\"/g, '\\\"'); // ' and "
    },

    /**
     * Convert a HTTP header name to its canonical form,
     * e.g. "content-length" => "Content-Length".
     * @param headerName the header name (case insensitive)
     * @return {String} the formatted header name
     */
    formatHeaderName: (function () {
        var specialCases = {
            'content-md5': 'Content-MD5',
            dnt: 'DNT',
            etag: 'ETag',
            p3p: 'P3P',
            te: 'TE',
            'www-authenticate': 'WWW-Authenticate',
            'x-att-deviceid': 'X-ATT-DeviceId',
            'x-cdn': 'X-CDN',
            'x-ua-compatible': 'X-UA-Compatible',
            'x-xss-protection': 'X-XSS-Protection'
        };
        return function (headerName) {
            var lowerCasedHeaderName = headerName.toLowerCase();
            if (specialCases.hasOwnProperty(lowerCasedHeaderName)) {
                return specialCases[lowerCasedHeaderName];
            } else {
                // Make sure that the first char and all chars following a dash are upper-case:
                return lowerCasedHeaderName.replace(/(^|-)([a-z])/g, function ($0, optionalLeadingDash, ch) {
                    return optionalLeadingDash + ch.toUpperCase();
                });
            }
        };
    }()),

    /**
     * Math mod method.
     * @param {Number} divisee
     * @param {Number} base
     * @return mod result
     * @type Number
     */
    mod: function (divisee, base) {
        return Math.round(divisee - (Math.floor(divisee / base) * base));
    },

    /**
     * Abbreviate the passed url to not exceed maxchars.
     * (Just display the hostname and first few chars after the last slash.
     * @param {String} url originial url
     * @param {Number} maxchars max. number of characters in the result string.
     * @return abbreviated url
     * @type String
     */
    briefUrl: function (url, maxchars) {
        var iDoubleSlash, iQMark, iFirstSlash, iLastSlash;

        maxchars = maxchars || 100; // default 100 characters
        if (url === undefined) {
            return '';
        }

        // We assume it's a full URL.
        iDoubleSlash = url.indexOf("//");
        if (-1 !== iDoubleSlash) {

            // remove query string
            iQMark = url.indexOf("?");
            if (-1 !== iQMark) {
                url = url.substring(0, iQMark) + "?...";
            }

            if (url.length > maxchars) {
                iFirstSlash = url.indexOf("/", iDoubleSlash + 2);
                iLastSlash = url.lastIndexOf("/");
                if (-1 !== iFirstSlash && -1 !== iLastSlash && iFirstSlash !== iLastSlash) {
                    url = url.substring(0, iFirstSlash + 1) + "..." + url.substring(iLastSlash);
                } else {
                    url = url.substring(0, maxchars + 1) + "...";
                }
            }
        }

        return url;
    },

    /**
     * Return a string with an anchor around a long piece of text.
     * (It's confusing, but often the "long piece of text" is the URL itself.)
     * Snip the long text if necessary.
     * Optionally, break the long text across multiple lines.
     * @param {String} text
     * @param {String} url
     * @param {String} sClass class name for the new anchor
     * @param {Boolean} bBriefUrl whether the url should be abbreviated.
     * @param {Number} maxChars max. number of chars allowed for each line.
     * @param {Number} numLines max. number of lines allowed
     * @param {String} rel rel attribute of anchor.
     * @return html code for the anchor.
     * @type String
     */
    prettyAnchor: function (text, url, sClass, bBriefUrl, maxChars, numLines, rel) {
        var escaped_dq_url,
            sTitle = '',
            sResults = '',
            iLines = 0;

        if (typeof url === 'undefined') {
            url = text;
        }
        if (typeof sClass === 'undefined') {
            sClass = '';
        } else {
            sClass = ' class="' + sClass + '"';
        }
        if (typeof maxChars === 'undefined') {
            maxChars = 100;
        }
        if (typeof numLines === 'undefined') {
            numLines = 1;
        }
        rel = (rel) ? ' rel="' + rel + '"' : '';

        url = YSLOW.util.escapeHtml(url);
        text = YSLOW.util.escapeHtml(text);

        escaped_dq_url = YSLOW.util.escapeQuotes(url, 'double');

        if (bBriefUrl) {
            text = YSLOW.util.briefUrl(text, maxChars);
            sTitle = ' title="' + escaped_dq_url + '"';
        }

        while (0 < text.length) {
            sResults += '<a' + rel + sClass + sTitle + ' href="' +
                escaped_dq_url +
                '" onclick="javascript:document.ysview.openLink(\'' +
                YSLOW.util.escapeQuotes(url) +
                '\'); return false;">' + text.substring(0, maxChars);
            text = text.substring(maxChars);
            iLines += 1;
            if (iLines >= numLines) {
                // We've reached the maximum number of lines.
                if (0 < text.length) {
                    // If there's still text leftover, snip it.
                    sResults += "[snip]";
                }
                sResults += "</a>";
                break;
            } else {
                // My (weak) attempt to break long URLs.
                sResults += "</a><font style='font-size: 0px;'> </font>";
            }
        }

        return sResults;
    },

    /**
     * Convert a number of bytes into a readable KB size string.
     * @param {Number} size
     * @return readable KB size string
     * @type String
     */
    kbSize: function (size) {
        var remainder = size % (size > 100 ? 100 : 10);
        size -= remainder;
        return parseFloat(size / 1000) + (0 === (size % 1000) ? ".0" : "") + "K";
    },

    /**
     * @final
     */
    prettyTypes: {
        "image": "Image",
        "doc": "HTML/Text",
        "cssimage": "CSS Image",
        "css": "Stylesheet File",
        "js": "JavaScript File",
        "flash": "Flash Object",
        "iframe": "IFrame",
        "xhr": "XMLHttpRequest",
        "redirect": "Redirect",
        "favicon": "Favicon",
        "unknown": "Unknown"
    },

/*
     *  Convert a type (eg, "cssimage") to a prettier name (eg, "CSS Images").
     * @param {String} sType component type
     * @return display name of component type
     * @type String
     */
    prettyType: function (sType) {
        return YSLOW.util.prettyTypes[sType];
    },

    /**
     *  Return a letter grade for a score.
     * @param {String or Number} iScore
     * @return letter grade for a score
     * @type String
     */
    prettyScore: function (score) {
        var letter = 'F';

        if (!parseInt(score, 10) && score !== 0) {
            return score;
        }
        if (score === -1) {
            return 'N/A';
        }

        if (score >= 90) {
            letter = 'A';
        } else if (score >= 80) {
            letter = 'B';
        } else if (score >= 70) {
            letter = 'C';
        } else if (score >= 60) {
            letter = 'D';
        } else if (score >= 50) {
            letter = 'E';
        }

        return letter;
    },

    /**
     * Returns YSlow results as an Object.
     * @param {YSLOW.context} yscontext yslow context.
     * @param {String|Array} info Information to be shown
     *        (basic|grade|stats|comps|all) [basic].
     * @return {Object} the YSlow results object.
     */
    getResults: function (yscontext, info) {
        var i, l, results, url, type, comps, comp, encoded_url, obj, cr,
            cs, etag, name, len, include_grade, include_comps, include_stats,
            result, len2, spaceid, header, sourceHeaders, targetHeaders,
            reButton = / <button [\s\S]+<\/button>/,
            util = YSLOW.util,
            isArray = util.isArray,
            stats = {},
            stats_c = {},
            comp_objs = [],
            params = {},
            g = {};

        // default
        info = (info || 'basic').split(',');

        for (i = 0, len = info.length; i < len; i += 1) {
            if (info[i] === 'all') {
                include_grade = include_stats = include_comps = true;
                break;
            } else {
                switch (info[i]) {
                case 'grade':
                    include_grade = true;
                    break;
                case 'stats':
                    include_stats = true;
                    break;
                case 'comps':
                    include_comps = true;
                    break;
                }
            }
        }

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

        if (include_grade) {
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
                            url = encodeURIComponent(url.replace(reButton, ''));
                            obj.components.push(url);
                        }
                    }
                }
                g[result.rule_id] = obj;
            }
            params.g = g;
        }

        if (include_stats) {
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
        }

        if (include_comps) {
            comps = yscontext.component_set.components;
            for (i = 0, len = comps.length; i < len; i += 1) {
                comp = comps[i];
                encoded_url = encodeURIComponent(comp.url);
                obj = {
                    'type': comp.type,
                    'url': encoded_url,
                    'size': comp.size,
                    'resp': comp.respTime
                };
                if (comp.size_compressed) {
                    obj.gzip = comp.size_compressed;
                }
                if (comp.expires && comp.expires instanceof Date) {
                    obj.expires = util.prettyExpiresDate(comp.expires);
                }
                cr = comp.getReceivedCookieSize();
                if (cr > 0) {
                    obj.cr = cr;
                }
                cs = comp.getSetCookieSize();
                if (cs > 0) {
                    obj.cs = cs;
                }
                etag = comp.getEtag();
                if (typeof etag === 'string' && etag.length > 0) {
                    obj.etag = etag;
                }
                // format req/res headers
                obj.headers = {};
                if (comp.req_headers) {
                    sourceHeaders = comp.req_headers;
                    obj.headers.request = {};
                    targetHeaders = obj.headers.request;
                    for (header in sourceHeaders) {
                        if (sourceHeaders.hasOwnProperty(header)) {
                            targetHeaders[util.formatHeaderName(header)] =
                                sourceHeaders[header];
                        }
                    }
                }
                if (comp.headers) {
                    sourceHeaders = comp.headers;
                    obj.headers.response = {};
                    targetHeaders = obj.headers.response;
                    for (header in sourceHeaders) {
                        if (sourceHeaders.hasOwnProperty(header)) {
                            targetHeaders[util.formatHeaderName(header)] =
                                sourceHeaders[header];
                        }
                    }
                }
                comp_objs.push(obj);
            }
            params.comps = comp_objs;
        }

        return params;
    },

    /**
     * Send YSlow beacon.
     * @param {Object} results Results object
     *        generated by {@link YSLOW.util.getResults}.
     * @param {String|Array} info Information to be beaconed
     *        (basic|grade|stats|comps|all).
     * @param {String} url The URL to fire beacon to.
     * @return {String} The beacon content sent.
     */
    sendBeacon: function (results, info, url) {
        var i, len, req, name, img,
            beacon = '',
            util = YSLOW.util,
            pref = util.Preference,
            method = 'get';

        // default
        info = (info || 'basic').split(',');

        for (i = 0, len = info.length; i < len; i += 1) {
            if (info[i] === 'all') {
                method = 'post';
                break;
            } else {
                switch (info[i]) {
                case 'grade':
                    method = 'post';
                    break;
                case 'stats':
                    method = 'post';
                    break;
                case 'comps':
                    method = 'post';
                    break;
                }
            }
        }

        if (method === 'post') {
            beacon = JSON.stringify(results, null);
            req = util.getXHR();
            req.open('POST', url, true);
            req.setRequestHeader('Content-Length', beacon.length);
            req.setRequestHeader('Content-Type', 'application/json');
            req.send(beacon);
        } else {
            for (name in results) {
                if (results.hasOwnProperty(name)) {
                    beacon += name + '=' + results[name] + '&';
                }
            }
            img = new Image();
            img.src = url + '?' + beacon;
        }

        return beacon;
    },

    /**
     * Get the dictionary of params used in results.
     * @param {String|Array} info Results information
     *        (basic|grade|stats|comps|all).
     * @param {String} ruleset The Results ruleset used
     *        (ydefault|yslow1|yblog).
     * @return {Object} The dictionary object {key: value}.
     */
    getDict: function (info, ruleset) {
        var i, len, include_grade, include_stats, include_comps,
            weights, rs,
            yslow = YSLOW,
            controller = yslow.controller,
            rules = yslow.doc.rules,
            dict = {
                v: 'version',
                w: 'size',
                o: 'overall score',
                u: 'url',
                r: 'total number of requests',
                s: 'space id of the page',
                i: 'id of the ruleset used',
                lt: 'page load time',
                grades: '100 >= A >= 90 > B >= 80 > C >= 70 > ' +
                    'D >= 60 > E >= 50 > F >= 0 > N/A = -1'
            };

        // defaults
        info = (info || 'basic').split(',');
        ruleset = ruleset || 'ydefault';
        weights = controller.rulesets[ruleset].weights;

        // check which info will be included
        for (i = 0, len = info.length; i < len; i += 1) {
            if (info[i] === 'all') {
                include_grade = include_stats = include_comps = true;
                break;
            } else {
                switch (info[i]) {
                case 'grade':
                    include_grade = true;
                    break;
                case 'stats':
                    include_stats = true;
                    break;
                case 'comps':
                    include_comps = true;
                    break;
                }
            }
        }

        // include dictionary
        if (include_grade) {
            dict.g = 'scores of all rules in the ruleset';
            dict.rules = {};
            for (rs in weights) {
                if (weights.hasOwnProperty(rs)) {
                    dict.rules[rs] = rules[rs];
                    dict.rules[rs].weight = weights[rs];
                }
            }
        }
        if (include_stats) {
            dict.w_c = 'page weight with primed cache';
            dict.r_c = 'number of requests with primed cache';
            dict.stats = 'number of requests and weight grouped by ' +
                'component type';
            dict.stats_c = 'number of request and weight of ' +
                'components group by component type with primed cache';
        }
        if (include_comps) {
            dict.comps = 'array of all the components found on the page';
        }

        return dict;
    },

    /**
     * Check if input is an Object
     * @param {Object} the input to check wheter it's an object or not
     * @return {Booleam} true for Object
     */
    isObject: function (o) {
        return Object.prototype.toString.call(o).indexOf('Object') > -1;
    },

    /**
     * Check if input is an Array
     * @param {Array} the input to check wheter it's an array or not
     * @return {Booleam} true for Array
     */
    isArray: function (o) {
        if (Array.isArray) {
            return Array.isArray(o);
        } else {
            return Object.prototype.toString.call(o).indexOf('Array') > -1;
        }
    },


    /**
     * Wrapper for decodeURIComponent, try to decode
     * otherwise return the input value.
     * @param {String} value The value to be decoded.
     * @return {String} The decoded value.
     */
    decodeURIComponent: function (value) {
        try {
            return decodeURIComponent(value);
        } catch (err) {
            return value;
        }
    },

    /**
     * Decode html entities. e.g.: &lt; becomes <
     * @param {String} str the html string to decode entities from.
     * @return {String} the input html with entities decoded.
     */
    decodeEntities: function (str) {
        return String(str)
            .replace(/&amp;/g, '&')
            .replace(/&lt;/g, '<')
            .replace(/&gt;/g, '>')
            .replace(/&quot;/g, '"');
    },

    safeXML: (function () {
        var decodeComp = this.decodeURIComponent,
            reInvalid = /[<&>]/;

        return function (value, decode) {
            if (decode) {
                value = decodeComp(value);
            }
            if (reInvalid.test(value)) {
                return '<![CDATA[' + value + ']]>';
            }
            return value;
        };
    }()),

    /**
     * convert Object to XML
     * @param {Object} obj the Object to be converted to XML
     * @param {String} root the XML root (default = results)
     * @return {String} the XML
     */
    objToXML: function (obj, root) {
        var toXML,
            util = YSLOW.util,
            safeXML = util.safeXML,
            xml = '<?xml version="1.0" encoding="UTF-8"?>';

        toXML = function (o) {
            var item, value, i, len, val, type;

            for (item in o) {
                if (o.hasOwnProperty(item)) {
                    value = o[item];
                    xml += '<' + item + '>';
                    if (util.isArray(value)) {
                        for (i = 0, len = value.length; i < len; i += 1) {
                            val = value[i];
                            type = typeof val;
                            xml += '<item>';
                            if (type === 'string' || type === 'number') {
                                xml += safeXML(val, item === 'components');
                            } else {
                                toXML(val);
                            }
                            xml += '</item>';
                        }
                    } else if (util.isObject(value)) {
                        toXML(value);
                    } else {
                        xml += safeXML(value, item === 'u' || item === 'url');
                    }
                    xml += '</' + item + '>';
                }
            }
        };

        root = root || 'results';
        xml += '<' + root + '>';
        toXML(obj);
        xml += '</' + root + '>';

        return xml;
    },

    /**
     * Pretty print results
     * @param {Object} obj the Object with YSlow results
     * @return {String} the results in plain text (pretty printed)
     */
    prettyPrintResults: function (obj) {
        var pp,
            util = YSLOW.util,
            str = '',
            mem = {},

            dict = {
                v: 'version',
                w: 'size',
                o: 'overall score',
                u: 'url',
                r: '# of requests',
                s: 'space id',
                i: 'ruleset',
                lt: 'page load time',
                g: 'scores',
                w_c: 'page size (primed cache)',
                r_c: '# of requests (primed cache)',
                stats: 'statistics by component',
                stats_c: 'statistics by component (primed cache)',
                comps: 'components found on the page',
                components: 'offenders',
                cr: 'received cookie size',
                cs: 'set cookie size',
                resp: 'response time'
            },

            indent = function (n) {
                var arr,
                    res = mem[n];

                if (typeof res === 'undefined') {
                    arr = [];
                    arr.length = (4 * n) + 1;
                    mem[n] = res = arr.join(' ');
                }

                return res;
            };

        pp = function (o, level) {
            var item, value, i, len, val, type;

            for (item in o) {
                if (o.hasOwnProperty(item)) {
                    value = o[item];
                    str += indent(level) + (dict[item] || item) + ':';
                    if (util.isArray(value)) {
                        str += '\n';
                        for (i = 0, len = value.length; i < len; i += 1) {
                            val = value[i];
                            type = typeof val;
                            if (type === 'string' || type === 'number') {
                                str += indent(level + 1) +
                                    util.decodeURIComponent(val) + '\n';
                            } else {
                                pp(val, level + 1);
                                if (i < len - 1) {
                                    str += '\n';
                                }
                            }
                        }
                    } else if (util.isObject(value)) {
                        str += '\n';
                        pp(value, level + 1);
                    } else {
                        if (item === 'score' || item === 'o') {
                            value = util.prettyScore(value) + ' (' + value + ')';
                        }
                        if (item === 'w' || item === 'w_c' ||
                                item === 'size' || item === 'gzip' ||
                                item === 'cr' || item === 'cs') {
                            value = util.kbSize(value) + ' (' + value + ' bytes)';
                        }
                        str += ' ' + util.decodeURIComponent(value) + '\n';
                    }
                }
            }
        };

        pp(obj, 0);

        return str;
    },

    /**
     * Test result against a certain threshold for CI
     * @param {Object} obj the Object with YSlow results
     * @param {String|Number|Object} threshold The definition of OK (inclusive)
     *        Anything >= threshold == OK. It can be a number [0-100],
     *        a letter [A-F] as follows:
     *        100 >= A >= 90 > B >= 80 > C >= 70 > D >= 60 > E >= 50 > F >= 0 > N/A = -1
     *        It can also be a specific per rule. e.g:
     *        {overall: 80, ycdn: 65, ynumreq: 'B'}
     *        where overall is the common threshold to be
     *        used by all rules except those listed
     * @return {Array} the test result array containing each test result details:
     */
    testResults: function (obj, threshold) {
        var overall, g, grade, grades, score, commonScore, i, len,
            tests = [],
            scores = {
                a: 90,
                b: 80,
                c: 70,
                d: 60,
                e: 50,
                f: 0,
                'n/a': -1
            },
            yslow = YSLOW,
            util = yslow.util,
            isObj = util.isObject(threshold),
            rules = yslow.doc.rules,

            getScore = function (value) {
                var score = parseInt(value, 10);

                if (isNaN(score) && typeof value === 'string') {
                    score = scores[value.toLowerCase()];
                }

                // edge case for F or 0
                if (score === 0) {
                    return 0;
                }

                return score || overall || scores.b;
            },

            getThreshold = function (name) {
                if (commonScore) {
                    return commonScore;
                }

                if (!isObj) {
                    commonScore = getScore(threshold);
                    return commonScore;
                } else if (threshold.hasOwnProperty(name)) {
                    return getScore(threshold[name]);
                } else {
                    return overall || scores.b;
                }
            },

            test = function (score, ts, name, message, offenders) {
                var desc = rules.hasOwnProperty(name) && rules[name].name;

                tests.push({
                    ok: score >= ts,
                    score: score,
                    grade: util.prettyScore(score),
                    name: name,
                    description: desc || '',
                    message: message,
                    offenders: offenders
                });
            };

        // overall threshold (default b [80])
        overall = getThreshold('overall');

        // overall score
        test(obj.o, overall, 'overall score');

        // grades
        grades = obj.g;
        if (grades) {
            for (grade in grades) {
                if (grades.hasOwnProperty(grade)) {
                    g = grades[grade];
                    score = g.score;
                    if (typeof score === 'undefined') {
                        score = -1;
                    }
                    test(score, getThreshold(grade), grade,
                        g.message, g.components);
                }
            }
        }

        return tests;
    },

    /**
     * Format test results as TAP for CI
     * @see: http://testanything.org/wiki/index.php/TAP_specification
     * @param {Array} tests the arrays containing the test results from testResults.
     * @return {Object}:
     *    failures: {Number} total test failed,
     *    content: {String} the results as TAP plain text
     */
    formatAsTAP: function (results) {
        var i, res, line, offenders, j, lenJ,
            failures = 0,
            len = results.length,
            tap = [],
            util = YSLOW.util,
            decodeURI = util.decodeURIComponent;

        // tap version
        tap.push('TAP version 13');

        // test plan
        tap.push('1..' + len);

        for (i = 0; i < len; i += 1) {
            res = results[i];
            line = res.ok || res.score < 0 ? 'ok' : 'not ok';
            failures += (res.ok || res.score < 0) ? 0 : 1;
            line += ' ' + (i + 1) + ' ' + res.grade +
                ' (' + res.score + ') ' + res.name;
            if (res.description) {
                line += ': ' + res.description;
            }
            if (res.score < 0) {
                line += ' # SKIP score N/A';
            }
            tap.push(line);

            // message
            if (res.message) {
                tap.push('  ---');
                tap.push('  message: ' + res.message);
            }

            // offenders
            offenders = res.offenders;
            if (offenders) {
                lenJ = offenders.length;
                if (lenJ > 0) {
                    if (!res.message) {
                        tap.push('  ---');
                    }
                    tap.push('  offenders:');
                    for (j = 0; j < lenJ; j += 1) {
                        tap.push('    - "' +
                            decodeURI(offenders[j]) + '"');
                    }
                }
            }

            if (res.message || lenJ > 0) {
                tap.push('  ...');
            }
        }

        return {
          failures: failures,
          content: tap.join('\n')
        };
    },

    /**
     * Format test results as JUnit XML for CI
     * @see: http://www.junit.org/
     * @param {Array} tests the arrays containing the test results from testResults.
     * @return {Object}:
     *    failures: {Number} total test failed,
     *    content: {String} the results as JUnit XML text
     */
    formatAsJUnit: function (results) {
        var i, res, line, offenders, j, lenJ,
            len = results.length,
            skipped = 0,
            failures = 0,
            junit = [],
            cases = [],
            util = YSLOW.util,
            decodeURI = util.decodeURIComponent,
            safeXML = util.safeXML,

            safeAttr = function (str) {
                return str
                    .replace(/&/g, '&amp;')
                    .replace(/</g, '&lt;')
                    .replace(/>/g, '&gt;')
                    .replace(/"/g, '&quot;');
            };

        for (i = 0; i < len; i += 1) {
            res = results[i];
            line = '    <testcase name="' + safeAttr(res.name +
                (res.description ? ': ' + res.description : '')) + '"';
            line += ' status="' + res.grade +
                ' (' + res.score + ')';
            if (res.ok) {
                cases.push(line + '"/>');
            } else {
                cases.push(line + '">');

                // skipped
                if (res.score < 0) {
                    skipped += 1;
                    cases.push('      <skipped>score N/A</skipped>');
                } else {
                  failures += 1;
                }

                line = '      <failure';
                if (res.message) {
                    line += ' message="' + safeAttr(res.message) + '"';
                }

                // offenders
                offenders = res.offenders;
                if (offenders) {
                    cases.push(line + '>');
                    lenJ = offenders.length;
                    for (j = 0; j < lenJ; j += 1) {
                        cases.push('        ' + safeXML(decodeURI(offenders[j])));
                    }
                    cases.push('      </failure>');
                } else {
                    cases.push(line + '/>');
                }

                cases.push('    </testcase>');
            }
        }

        // xml
        junit.push('<?xml version="1.0" encoding="UTF-8" ?>');

        // open test suites wrapper
        junit.push('<testsuites>');

        // open test suite w/ summary
        line = '  <testsuite name="YSlow" tests="' + len + '"';
        if (failures) {
            line += ' failures="' + failures + '"';
        }
        if (skipped) {
            line += ' skipped="' + skipped + '"';
        }
        line += '>';
        junit.push(line);

        // concat test cases
        junit = junit.concat(cases);

        // close test suite
        junit.push('  </testsuite>');

        // close test suites wrapper
        junit.push('</testsuites>');

        return {
            failures: failures,
            content: junit.join('\n')
        };
    },

    /**
     *  Try to find a spaceid in the HTML document source.
     * @param {YSLOW.ComponentSet} cset Component set.
     * @return spaceID string
     * @type string
     */
    getPageSpaceid: function (cset) {
        var sHtml, aDelims, aTerminators, i, sDelim, i1, i2, spaceid,
            reDigits = /^\d+$/,
            aComponents = cset.getComponentsByType('doc');

        if (aComponents[0] && typeof aComponents[0].body === 'string' && aComponents[0].body.length > 0) {
            sHtml = aComponents[0].body; // assume the first "doc" is the original HTML doc
            aDelims = ["%2fE%3d", "/S=", "SpaceID=", "?f=", " sid="]; // the beginning delimiter
            aTerminators = ["%2fR%3d", ":", " ", "&", " "]; // the terminating delimiter
            // Client-side counting (yzq) puts the spaceid in it as "/E=95810469/R=" but it's escaped!
            for (i = 0; i < aDelims.length; i += 1) {
                sDelim = aDelims[i];
                if (-1 !== sHtml.indexOf(sDelim)) { // if the delimiter is present
                    i1 = sHtml.indexOf(sDelim) + sDelim.length; // skip over the delimiter
                    i2 = sHtml.indexOf(aTerminators[i], i1); // find the terminator
                    if (-1 !== i2 && (i2 - i1) < 15) { // if the spaceid is < 15 digits
                        spaceid = sHtml.substring(i1, i2);
                        if (reDigits.test(spaceid)) { // make sure it's all digits
                            return spaceid;
                        }
                    }
                }
            }
        }

        return "";
    },

    /**
     *  Dynamically add a stylesheet to the document.
     * @param {String} url URL of the css file
     * @param {Document} doc Documnet object
     * @return CSS element
     * @type HTMLElement
     */
    loadCSS: function (url, doc) {
        var newCss;

        if (!doc) {
            YSLOW.util.dump('YSLOW.util.loadCSS: doc is not specified');
            return '';
        }

        newCss = doc.createElement("link");
        newCss.rel = "stylesheet";
        newCss.type = "text\/css";
        newCss.href = url;
        doc.body.appendChild(newCss);

        return newCss;
    },

    /**
     * Open a link.
     * @param {String} url URL of page to be opened.
     */
    openLink: function (url) {
        if (YSLOW.util.Preference.getPref("browser.link.open_external") === 3) {
            gBrowser.selectedTab = gBrowser.addTab(url);
        } else {
            window.open(url, " blank");
        }
    },

    /**
     * Sends a URL to smush.it for optimization
     * Example usage:
     * <code>YSLOW.util.smushIt('http://smush.it/css/skin/screenshot.png', function(resp){alert(resp.dest)});</code>
     * This code alerts the path to the optimized result image.
     *
     * @param {String} imgurl URL of the image to optimize
     * @param {Function} callback Callback function that accepts an object returned from smush.it
     */
    smushIt: function (imgurl, callback) {
        var xhr,
            smushurl = this.getSmushUrl(),
            url = smushurl + '/ws.php?img=' + encodeURIComponent(imgurl),
            req = YSLOW.util.getXHR();

        req.open('GET', url, true);
        req.onreadystatechange = function (e) {
            xhr = (e ? e.target : req);
            if (xhr.readyState === 4) {
                callback(JSON.parse(xhr.responseText));
            }
        };
        req.send(null);
    },

    /**
     * Get SmushIt server URL.
     * @return URL of SmushIt server.
     * @type String
     */
    getSmushUrl: function () {
        var g_default_smushit_url = 'http://www.smushit.com/ysmush.it';

        return YSLOW.util.Preference.getPref('smushItURL', g_default_smushit_url) + '/';
    },

    /**
     * Create new tab and return its document object
     * @return document object of the new tab content.
     * @type Document
     */
    getNewDoc: function () {
        var generatedPage = null,
            request = new XMLHttpRequest();

        getBrowser().selectedTab = getBrowser().addTab('about:blank');
        generatedPage = window;
        request.open("get", "about:blank", false);
        request.overrideMimeType('text/html');
        request.send(null);

        return generatedPage.content.document;
    },

    /**
     * Make absolute url.
     * @param url
     * @param base href
     * @return absolute url built with base href.
     */
    makeAbsoluteUrl: function (url, baseHref) {
        var hostIndex, path, lpath, protocol;

        if (typeof url === 'string' && baseHref) {
            hostIndex = baseHref.indexOf('://');
            protocol = baseHref.slice(0, 4);
            if (url.indexOf('://') < 0 && (protocol === 'http' ||
                    protocol === 'file')) {
                // This is a relative url
                if (url.slice(0, 1) === '/') {
                    // absolute path
                    path = baseHref.indexOf('/', hostIndex + 3);
                    if (path > -1) {
                        url = baseHref.slice(0, path) + url;
                    } else {
                        url = baseHref + url;
                    }
                } else {
                    // relative path
                    lpath = baseHref.lastIndexOf('/');
                    if (lpath > hostIndex + 3) {
                        url = baseHref.slice(0, lpath + 1) + url;
                    } else {
                        url = baseHref + '/' + url;
                    }
                }
            }
        }

        return url;
    },

    /**
     * Prevent event default action
     * @param {Object} event the event to prevent default action from
     */
    preventDefault: function (event) {
        if (typeof event.preventDefault === 'function') {
            event.preventDefault();
        } else {
            event.returnValue = false;
        }
    },

    /**
     * String Trim
     * @param string s the string to remove trail and header spaces
     */
    trim: function (s) {
        try {
            return (s && s.trim) ? s.trim() : s.replace(/^\s+|\s+$/g, '');
        } catch (e) {
            return s;
        }
    },

    /**
     * Add Event Listener
     * @param HTMLElement el the element to add an event listener
     * @param string ev the event name to be added
     * @param function fn the function to be invoked by event listener
     */
    addEventListener: function (el, ev, fn) {
        var util = YSLOW.util;

        if (el.addEventListener) {
            util.addEventListener = function (el, ev, fn) {
                el.addEventListener(ev, fn, false);
            };
        } else if (el.attachEvent) {
            util.addEventListener = function (el, ev, fn) {
                el.attachEvent('on' + ev, fn);
            };
        } else {
            util.addEventListener = function (el, ev, fn) {
                el['on' + ev] = fn;
            };
        }
        util.addEventListener(el, ev, fn);
    },

    /**
     * Remove Event Listener
     * @param HTMLElement el the element to remove event listener from
     * @param string ev the event name to be removed
     * @param function fn the function invoked by the removed listener
     */
    removeEventListener: function (el, ev, fn) {
        var util = YSLOW.util;

        if (el.removeEventListener) {
            util.removeEventListener = function (el, ev, fn) {
                el.removeEventListener(ev, fn, false);
            };
        } else if (el.detachEvent) {
            util.removeEventListener = function (el, ev, fn) {
                el.detachEvent('on' + ev, fn);
            };
        } else {
            util.removeEventListener = function (el, ev, fn) {
                delete el['on' + ev];
            };
        }
        util.removeEventListener(el, ev, fn);
    },

    /**
     * Normalize currentTarget
     * @param evt the event received
     * @return HTMLElement the normilized currentTarget
     */
    getCurrentTarget: function (evt) {
        return evt.currentTarget || evt.srcElement;
    },

    /**
     * Normalize target
     * @param evt the event received
     * @return HTMLElement the normilized target
     */
    getTarget: function (evt) {
        return evt.target || evt.srcElement;
    },

    /**
     * Get all inline elements (style and script) from a document
     * @param doc (optional) the document to get all inline elements
     * @param head (optional) the head node to get inline elements, ignores doc
     * @param body (optional) the body node to get inline elements, ignores doc
     * @return object with scripts and styles arrays with the following info:
     * containerNode: either head or body
     * body: the innerHTML content
     */
    getInlineTags: function (doc, head, body) {
        var styles, scripts,

            loop = function (node, tag, contentNode) {
                var i, len, els, el,
                    objs = [];

                if (!node) {
                    return objs;
                }

                els = node.getElementsByTagName(tag);
                for (i = 0, len = els.length; i < len; i += 1) {
                    el = els[i];
                    if (!el.src) {
                        objs.push({
                            contentNode: contentNode,
                            body: el.innerHTML
                        });
                    }
                }

                return objs;
            };

        head = head || (doc && doc.getElementsByTagName('head')[0]);
        body = body || (doc && doc.getElementsByTagName('body')[0]);

        styles = loop(head, 'style', 'head');
        styles = styles.concat(loop(body, 'style', 'body'));
        scripts = loop(head, 'script', 'head');
        scripts = scripts.concat(loop(body, 'script', 'body'));

        return {
            styles: styles,
            scripts: scripts
        };
    },

    /**
     * Count all DOM elements from a node
     * @param node the root node to count all DOM elements from
     * @return number of DOM elements found on given node
     */
    countDOMElements: function (node) {
        return (node && node.getElementsByTagName('*').length) || 0;
    },

    /**
     * Get cookies from a document
     * @param doc the document to get the cookies from
     * @return the cookies string
     */
    getDocCookies: function (doc) {
        return (doc && doc.cookie) || '';
    },

    /**
     * identifies injected elements (js, css, iframe, flash, image)
     * @param doc the document to create/manipulate dom elements 
     * @param comps the component set components
     * @param body the root (raw) document body (html)
     * @return the same components with injected info
     */
    setInjected: function (doc, comps, body) {
        var i, len, els, el, src, comp, found, div,
            nodes = {};

        if (!body) {
            return comps;
        }

        // har uses a temp div already, reuse it
        if (typeof doc.createElement === 'function') {
            div = doc.createElement('div');
            div.innerHTML = body;
        } else {
            div = doc;
        }

        // js
        els = div.getElementsByTagName('script');
        for (i = 0, len = els.length; i < len; i += 1) {
            el = els[i];
            src = el.src || el.getAttribute('src');
            if (src) {
                nodes[src] = {
                    defer: el.defer || el.getAttribute('defer'),
                    async: el.async || el.getAttribute('async')
                };
            }
        }

        // css
        els = div.getElementsByTagName('link');
        for (i = 0, len = els.length; i < len; i += 1) {
            el = els[i];
            src = el.href || el.getAttribute('href');
            if (src && (el.rel === 'stylesheet' || el.type === 'text/css')) {
                nodes[src] = 1;
            }
        }

        // iframe
        els = div.getElementsByTagName('iframe');
        for (i = 0, len = els.length; i < len; i += 1) {
            el = els[i];
            src = el.src || el.getAttribute('src');
            if (src) {
                nodes[src] = 1;
            }
        }

        // flash
        els = div.getElementsByTagName('embed');
        for (i = 0, len = els.length; i < len; i += 1) {
            el = els[i];
            src = el.src || el.getAttribute('src');
            if (src) {
                nodes[src] = 1;
            }
        }
        els = div.getElementsByTagName('param');
        for (i = 0, len = els.length; i < len; i += 1) {
            el = els[i];
            src = el.value || el.getAttribute('value');
            if (src) {
                nodes[src] = 1;
            }
        }

        // image
        els = div.getElementsByTagName('img');
        for (i = 0, len = els.length; i < len; i += 1) {
            el = els[i];
            src = el.src || el.getAttribute('src');
            if (src) {
                nodes[src] = 1;
            }
        }

        // loop components and look it up on nodes
        // if not found then component was injected
        // for js, set defer and async attributes
        for (i = 0, len = comps.length; i < len; i += 1) {
            comp = comps[i];
            if (comp.type === 'js' || comp.type === 'css' ||
                    comp.type === 'flash' || comp.type === 'flash' ||
                    comp.type === 'image') {
                found = nodes[comp.url];
                comp.injected = !found;
                if (comp.type === 'js' && found) {
                    comp.defer = found.defer;
                    comp.async = found.async;
                }
            }
        }

        return comps;
    },

    // default setTimeout, FF overrides this with proprietary Mozilla timer
    setTimer: function (callback, delay) {
        setTimeout(callback, delay);
    }
};

/**
 * Class that implements the observer pattern.
 *
 * Oversimplified usage:
 * <pre>
 * // subscribe
 * YSLOW.util.event.addListener('martiansAttack', alert);
 * // fire the event
 * YSLOW.util.event.fire('martiansAttack', 'panic!');
 * </pre>
 *
 * More real life usage
 * <pre>
 * var myobj = {
 *   default_action: alert,
 *   panic: function(event) {
 *     this.default_action.call(null, event.message);
 *   }
 * };
 *
 * // subscribe
 * YSLOW.util.event.addListener('martiansAttack', myobj.panic, myobj);
 * // somewhere someone fires the event
 * YSLOW.util.event.fire('martiansAttack', {date: new Date(), message: 'panic!'});
 *
 *
 * @namespace YSLOW.util
 * @class event
 * @static
 */
YSLOW.util.event = {
    /**
     * Hash of subscribers where the key is the event name and the value is an array of callbacks-type objects
     * The callback objects have keys "callback" which is the function to be called and "that" which is the value
     * to be assigned to the "this" object when the function is called
     */
    subscribers: {},

    /**
     * Adds a new listener
     *
     * @param {String} event_name Name of the event
     * @param {Function} callback A function to be called when the event fires
     * @param {Object} that Object to be assigned to the "this" value of the callback function
     */
    addListener: function (eventName, callback, that) {
        var subs = this.subscribers,
            subscribers = subs[eventName];

        if (!subscribers) {
            subscribers = subs[eventName] = [];
        }
        subscribers.push({
            callback: callback,
            that: that
        });
    },

    /**
     * Removes a listener
     *
     * @param {String} event_name Name of the event
     * @param {Function} callback The callback function that was added as a listener
     * @return {Boolean} TRUE is the listener was removed successfully, FALSE otherwise (for example in cases when the listener doesn't exist)
     */
    removeListener: function (eventName, callback) {
        var i,
            subscribers = this.subscribers[eventName],
            len = (subscribers && subscribers.length) || 0;

        for (i = 0; i < len; i += 1) {
            if (subscribers[i].callback === callback) {
                subscribers.splice(i, 1);
                return true;
            }
        }

        return false;
    },

    /**
     * Fires the event
     *
     * @param {String} event_nama Name of the event
     * @param {Object} event_object Any object that will be passed to the subscribers, can be anything
     */
    fire: function (event_name, event_object) {
        var i, listener;

        if (typeof this.subscribers[event_name] === 'undefined') {
            return false;
        }

        for (i = 0; i < this.subscribers[event_name].length; i += 1) {
            listener = this.subscribers[event_name][i];
            try {
                listener.callback.call(listener.that, event_object);
            } catch (e) {}
        }

        return true;
    }

};

/**
 * Class that implements setting and unsetting preferences
 *
 * @namespace YSLOW.util
 * @class Preference
 * @static
 *
 */
YSLOW.util.Preference = {

    /**
     * @private
     */
    nativePref: null,

    /**
     * Register native preference mechanism.
     */
    registerNative: function (o) {
        this.nativePref = o;
    },

    /**
     * Get Preference with default value.  If the preference does not exist,
     * return the passed default_value.
     * @param {String} name name of preference
     * @return preference value or default value.
     */
    getPref: function (name, default_value) {
        if (this.nativePref) {
            return this.nativePref.getPref(name, default_value);
        }
        return default_value;
    },

    /**
     * Get child preference list in branch.
     * @param {String} branch_name
     * @return array of preference values.
     * @type Array
     */
    getPrefList: function (branch_name, default_value) {
        if (this.nativePref) {
            return this.nativePref.getPrefList(branch_name, default_value);
        }
        return default_value;
    },

    /**
     * Set Preference with passed value.
     * @param {String} name name of preference
     * @param {value type} value value to be used to set the preference
     */
    setPref: function (name, value) {
        if (this.nativePref) {
            this.nativePref.setPref(name, value);
        }
    },

    /**
     * Delete Preference with passed name.
     * @param {String} name name of preference to be deleted
     */
    deletePref: function (name) {
        if (this.nativePref) {
            this.nativePref.deletePref(name);
        }
    }
};
/**
 * Copyright (c) 2012, Yahoo! Inc.  All rights reserved.
 * Copyright (c) 2013, Marcel Duran and other contributors. All rights reserved.
 * Copyrights licensed under the New BSD License. See the accompanying LICENSE file for terms.
 */

/*global YSLOW*/
/*jslint white: true, onevar: true, undef: true, nomen: true, eqeqeq: true, plusplus: true, bitwise: true, regexp: true, newcap: true, immed: true */

/**
 * A class that collects all in-product text.
 * @namespace YSLOW
 * @class doc
 * @static
 */
YSLOW.doc = {

    tools_desc: undefined,

    view_names: {},

    splash: {},

    rules: {},

    tools: {},

    components_legend: {},

    addRuleInfo: function (id, name, info) {
        if (typeof id === "string" && typeof name === "string" && typeof info === "string") {
            this.rules[id] = {
                'name': name,
                'info': info
            };
        }
    },

    addToolInfo: function (id, name, info) {
        if (typeof id === "string" && typeof name === "string" && typeof info === "string") {
            this.tools[id] = {
                'name': name,
                'info': info
            };
        }
    }

};

//
// Rules text
//
YSLOW.doc.addRuleInfo('ynumreq', 'Make fewer HTTP requests', 'Decreasing the number of components on a page reduces the number of HTTP requests required to render the page, resulting in faster page loads.  Some ways to reduce the number of components include:  combine files, combine multiple scripts into one script, combine multiple CSS files into one style sheet, and use CSS Sprites and image maps.');

YSLOW.doc.addRuleInfo('ycdn', 'Use a Content Delivery Network (CDN)', 'User proximity to web servers impacts response times.  Deploying content across multiple geographically dispersed servers helps users perceive that pages are loading faster.');

YSLOW.doc.addRuleInfo('yexpires', 'Add Expires headers', 'Web pages are becoming increasingly complex with more scripts, style sheets, images, and Flash on them.  A first-time visit to a page may require several HTTP requests to load all the components.  By using Expires headers these components become cacheable, which avoids unnecessary HTTP requests on subsequent page views.  Expires headers are most often associated with images, but they can and should be used on all page components including scripts, style sheets, and Flash.');

YSLOW.doc.addRuleInfo('ycompress', 'Compress components with gzip', 'Compression reduces response times by reducing the size of the HTTP response.  Gzip is the most popular and effective compression method currently available and generally reduces the response size by about 70%.  Approximately 90% of today\'s Internet traffic travels through browsers that claim to support gzip.');

YSLOW.doc.addRuleInfo('ycsstop', 'Put CSS at top', 'Moving style sheets to the document HEAD element helps pages appear to load quicker since this allows pages to render progressively.');

YSLOW.doc.addRuleInfo('yjsbottom', 'Put JavaScript at bottom', 'JavaScript scripts block parallel downloads; that is, when a script is downloading, the browser will not start any other downloads.  To help the page load faster, move scripts to the bottom of the page if they are deferrable.');

YSLOW.doc.addRuleInfo('yexpressions', 'Avoid CSS expressions', 'CSS expressions (supported in IE beginning with Version 5) are a powerful, and dangerous, way to dynamically set CSS properties.  These expressions are evaluated frequently:  when the page is rendered and resized, when the page is scrolled, and even when the user moves the mouse over the page.  These frequent evaluations degrade the user experience.');

YSLOW.doc.addRuleInfo('yexternal', 'Make JavaScript and CSS external', 'Using external JavaScript and CSS files generally produces faster pages because the files are cached by the browser.  JavaScript and CSS that are inlined in HTML documents get downloaded each time the HTML document is requested.  This reduces the number of HTTP requests but increases the HTML document size.  On the other hand, if the JavaScript and CSS are in external files cached by the browser, the HTML document size is reduced without increasing the number of HTTP requests.');

YSLOW.doc.addRuleInfo('ydns', 'Reduce DNS lookups', 'The Domain Name System (DNS) maps hostnames to IP addresses, just like phonebooks map people\'s names to their phone numbers.  When you type URL www.yahoo.com into the browser, the browser contacts a DNS resolver that returns the server\'s IP address.  DNS has a cost; typically it takes 20 to 120 milliseconds for it to look up the IP address for a hostname.  The browser cannot download anything from the host until the lookup completes.');

YSLOW.doc.addRuleInfo('yminify', 'Minify JavaScript and CSS', 'Minification removes unnecessary characters from a file to reduce its size, thereby improving load times.  When a file is minified, comments and unneeded white space characters (space, newline, and tab) are removed.  This improves response time since the size of the download files is reduced.');

YSLOW.doc.addRuleInfo('yredirects', 'Avoid URL redirects', 'URL redirects are made using HTTP status codes 301 and 302.  They tell the browser to go to another location.  Inserting a redirect between the user and the final HTML document delays everything on the page since nothing on the page can be rendered and no components can be downloaded until the HTML document arrives.');

YSLOW.doc.addRuleInfo('ydupes', 'Remove duplicate JavaScript and CSS', 'Duplicate JavaScript and CSS files hurt performance by creating unnecessary HTTP requests (IE only) and wasted JavaScript execution (IE and Firefox).  In IE, if an external script is included twice and is not cacheable, it generates two HTTP requests during page loading.  Even if the script is cacheable, extra HTTP requests occur when the user reloads the page.  In both IE and Firefox, duplicate JavaScript scripts cause wasted time evaluating the same scripts more than once.  This redundant script execution happens regardless of whether the script is cacheable.');

YSLOW.doc.addRuleInfo('yetags', 'Configure entity tags (ETags)', 'Entity tags (ETags) are a mechanism web servers and the browser use to determine whether a component in the browser\'s cache matches one on the origin server.  Since ETags are typically constructed using attributes that make them unique to a specific server hosting a site, the tags will not match when a browser gets the original component from one server and later tries to validate that component on a different server.');

YSLOW.doc.addRuleInfo('yxhr', 'Make AJAX cacheable', 'One of AJAX\'s benefits is it provides instantaneous feedback to the user because it requests information asynchronously from the backend web server.  However, using AJAX does not guarantee the user will not wait for the asynchronous JavaScript and XML responses to return.  Optimizing AJAX responses is important to improve performance, and making the responses cacheable is the best way to optimize them.');

YSLOW.doc.addRuleInfo('yxhrmethod', 'Use GET for AJAX requests', 'When using the XMLHttpRequest object, the browser implements POST in two steps:  (1) send the headers, and (2) send the data.  It is better to use GET instead of POST since GET sends the headers and the data together (unless there are many cookies).  IE\'s maximum URL length is 2 KB, so if you are sending more than this amount of data you may not be able to use GET.');

YSLOW.doc.addRuleInfo('ymindom', 'Reduce the number of DOM elements', 'A complex page means more bytes to download, and it also means slower DOM access in JavaScript.  Reduce the number of DOM elements on the page to improve performance.');

YSLOW.doc.addRuleInfo('yno404', 'Avoid HTTP 404 (Not Found) error', 'Making an HTTP request and receiving a 404 (Not Found) error is expensive and degrades the user experience.  Some sites have helpful 404 messages (for example, "Did you mean ...?"), which may assist the user, but server resources are still wasted.');

YSLOW.doc.addRuleInfo('ymincookie', 'Reduce cookie size', 'HTTP cookies are used for authentication, personalization, and other purposes.  Cookie information is exchanged in the HTTP headers between web servers and the browser, so keeping the cookie size small minimizes the impact on response time.');

YSLOW.doc.addRuleInfo('ycookiefree', 'Use cookie-free domains', 'When the browser requests a static image and sends cookies with the request, the server ignores the cookies.  These cookies are unnecessary network traffic.  To workaround this problem, make sure that static components are requested with cookie-free requests by creating a subdomain and hosting them there.');

YSLOW.doc.addRuleInfo('ynofilter', 'Avoid AlphaImageLoader filter', 'The IE-proprietary AlphaImageLoader filter attempts to fix a problem with semi-transparent true color PNG files in IE versions less than Version 7.  However, this filter blocks rendering and freezes the browser while the image is being downloaded.  Additionally, it increases memory consumption.  The problem is further multiplied because it is applied per element, not per image.');

YSLOW.doc.addRuleInfo('yimgnoscale', 'Do not scale images in HTML', 'Web page designers sometimes set image dimensions by using the width and height attributes of the HTML image element.  Avoid doing this since it can result in images being larger than needed.  For example, if your page requires image myimg.jpg which has dimensions 240x720 but displays it with dimensions 120x360 using the width and height attributes, then the browser will download an image that is larger than necessary.');

YSLOW.doc.addRuleInfo('yfavicon', 'Make favicon small and cacheable', 'A favicon is an icon associated with a web page; this icon resides in the favicon.ico file in the server\'s root.  Since the browser requests this file, it needs to be present; if it is missing, the browser returns a 404 error (see "Avoid HTTP 404 (Not Found) error" above).  Since favicon.ico resides in the server\'s root, each time the browser requests this file, the cookies for the server\'s root are sent.  Making the favicon small and reducing the cookie size for the server\'s root cookies improves performance for retrieving the favicon.  Making favicon.ico cacheable avoids frequent requests for it.');

YSLOW.doc.addRuleInfo('yemptysrc', 'Avoid empty src or href', 'You may expect a browser to do nothing when it encounters an empty image src.  However, it is not the case in most browsers. IE makes a request to the directory in which the page is located; Safari, Chrome, Firefox 3 and earlier make a request to the actual page itself. This behavior could possibly corrupt user data, waste server computing cycles generating a page that will never be viewed, and in the worst case, cripple your servers by sending a large amount of unexpected traffic.');

//
// Tools text
//
YSLOW.doc.tools_desc = 'Click on the tool name to launch the tool.';

YSLOW.doc.addToolInfo('jslint', 'JSLint', 'Run JSLint on all JavaScript code in this document');

YSLOW.doc.addToolInfo('alljs', 'All JS', 'Show all JavaScript code in this document');

YSLOW.doc.addToolInfo('jsbeautified', 'All JS Beautified', 'Show all JavaScript code in this document in an easy to read format');

YSLOW.doc.addToolInfo('jsminified', 'All JS Minified', 'Show all JavaScript code in this document in a minified (no comments or white space) format');

YSLOW.doc.addToolInfo('allcss', 'All CSS', 'Show all CSS code in this document');

YSLOW.doc.addToolInfo('cssmin', 'YUI CSS Compressor', 'Show all CSS code in the document in a minified format');

YSLOW.doc.addToolInfo('smushItAll', 'All Smush.it&trade;', 'Run Smush.it&trade; on all image components in this document');

YSLOW.doc.addToolInfo('printableview', 'Printable View', 'Show a printable view of grades, component lists, and statistics');

//
// Splash text
//
YSLOW.doc.splash.title = 'Grade your web pages with YSlow';

YSLOW.doc.splash.content = {
    'header': 'YSlow gives you:',
    'text': '<ul><li>Grade based on the performance of the page (you can define your own ruleset)</li><li>Summary of the page components</li><li>Chart with statistics</li><li>Tools for analyzing performance, including Smush.it&trade; and JSLint</li></ul>'
};

YSLOW.doc.splash.more_info = 'Learn more about YSlow and the Yahoo! Developer Network';

//
// Rule Settings
//
YSLOW.doc.rulesettings_desc = 'Choose which ruleset (YSlow V2, Classic V1, or Small Site/Blog) best fits your specific needs.  Or create a new set and click Save as... to save it.';

//
// Components table legend
//
YSLOW.doc.components_legend.beacon = 'type column indicates the component is loaded after window onload event';
YSLOW.doc.components_legend.after_onload = 'denotes 1x1 pixels image that may be image beacon';

//
// View names
//
YSLOW.doc.view_names = {
    grade: 'Grade',
    components: 'Components',
    stats: 'Statistics',
    tools: 'Tools',
    rulesetedit: 'Rule Settings'
};

// copyright text
YSLOW.doc.copyright = 'Copyright &copy; ' + (new Date()).getFullYear() + ' Yahoo! Inc. All rights reserved.';
/**
 * Copyright (c) 2012, Yahoo! Inc.  All rights reserved.
 * Copyrights licensed under the New BSD License. See the accompanying LICENSE file for terms.
 */

/*global YSLOW*/
/*jslint white: true, onevar: true, undef: true, nomen: true, regexp: true, continue: true, plusplus: true, bitwise: true, newcap: true, type: true, unparam: true, maxerr: 50, indent: 4*/

/**
 *
 * Example of a rule object:
 *
 * <pre>
 * YSLOW.registerRule({
 *
 *     id: 'myrule',
 *     name: 'Never say never',
 *     url: 'http://never.never/never.html',
 *     info: 'Short description of the rule',
 *
 *     config: {
 *          when: 'ever'
 *     },
 *
 *     lint: function(doc, components, config) {
 *         return {
 *             score: 100,
 *             message: "Did you just say never?",
 *             components: []
 *         };
 *     }
 * });
  </pre>
 */

//
// 3/2/2009
// Centralize all name and info of builtin tool to YSLOW.doc class.
//
YSLOW.registerRule({
    id: 'ynumreq',
    //name: 'Make fewer HTTP requests',
    url: 'http://developer.yahoo.com/performance/rules.html#num_http',
    category: ['content'],

    config: {
        max_js: 3,
        // the number of scripts allowed before we start penalizing
        points_js: 4,
        // penalty points for each script over the maximum
        max_css: 2,
        // number of external stylesheets allowed before we start penalizing
        points_css: 4,
        // penalty points for each external stylesheet over the maximum
        max_cssimages: 6,
        // // number of background images allowed before we start penalizing
        points_cssimages: 3 // penalty points for each bg image over the maximum
    },

    lint: function (doc, cset, config) {
        var js = cset.getComponentsByType('js').length - config.max_js,
            css = cset.getComponentsByType('css').length - config.max_css,
            cssimg = cset.getComponentsByType('cssimage').length - config.max_cssimages,
            score = 100,
            messages = [];

        if (js > 0) {
            score -= js * config.points_js;
            messages[messages.length] = 'This page has ' + YSLOW.util.plural('%num% external Javascript script%s%', (js + config.max_js)) + '.  Try combining them into one.';
        }
        if (css > 0) {
            score -= css * config.points_css;
            messages[messages.length] = 'This page has ' + YSLOW.util.plural('%num% external stylesheet%s%', (css + config.max_css)) + '.  Try combining them into one.';
        }
        if (cssimg > 0) {
            score -= cssimg * config.points_cssimages;
            messages[messages.length] = 'This page has ' + YSLOW.util.plural('%num% external background image%s%', (cssimg + config.max_cssimages)) + '.  Try combining them with CSS sprites.';
        }

        return {
            score: score,
            message: messages.join('\n'),
            components: []
        };
    }
});

YSLOW.registerRule({
    id: 'ycdn',
    //name: 'Use a CDN',
    url: 'http://developer.yahoo.com/performance/rules.html#cdn',
    category: ['server'],

    config: {
        // how many points to take out for each component not on CDN
        points: 10,
        // array of regexps that match CDN-ed components
        patterns: [
            '^([^\\.]*)\\.([^\\.]*)\\.yimg\\.com/[^/]*\\.yimg\\.com/.*$',
            '^([^\\.]*)\\.([^\\.]*)\\.yimg\\.com/[^/]*\\.yahoo\\.com/.*$',
            '^sec.yimg.com/',
            '^a248.e.akamai.net',
            '^[dehlps].yimg.com',
            '^(ads|cn|mail|maps|s1).yimg.com',
            '^[\\d\\w\\.]+.yimg.com',
            '^a.l.yimg.com',
            '^us.(js|a)2.yimg.com',
            '^yui.yahooapis.com',
            '^adz.kr.yahoo.com',
            '^img.yahoo.co.kr',
            '^img.(shopping|news|srch).yahoo.co.kr',
            '^pimg.kr.yahoo.com',
            '^kr.img.n2o.yahoo.com',
            '^s3.amazonaws.com',
            '^(www.)?google-analytics.com',
            '.cloudfront.net', //Amazon CloudFront
            '.ak.fbcdn.net', //Facebook images ebeded
            'platform.twitter.com', //Twitter widget - Always via a CDN
            'cdn.api.twitter.com', //Twitter API calls, served via Akamai
            'apis.google.com', //Google's API Hosting
            '.akamaihd.net', //Akamai - Facebook uses this for SSL assets
            '.rackcdn.com' //Generic RackSpace CloudFiles CDN
        ],
        // array of regexps that will be treated as exception.
        exceptions: [
            '^chart.yahoo.com',
            '^(a1|f3|f5|f3c|f5c).yahoofs.com', // Images for 360 and YMDB
            '^us.(a1c|f3).yahoofs.com' // Personals photos
        ],
        // array of regexps that match CDN Server HTTP headers
        servers: [
            'cloudflare-nginx' // not using ^ and $ due to invisible
        ],
        // which component types should be on CDN
        types: ['js', 'css', 'image', 'cssimage', 'flash', 'favicon']
    },

    lint: function (doc, cset, config) {
        var i, j, url, re, match, hostname,
            offender, len, lenJ, comp, patterns, headers,
            score = 100,
            offenders = [],
            exceptions = [],
            message = '',
            util = YSLOW.util,
            plural = util.plural,
            kbSize = util.kbSize,
            getHostname = util.getHostname,
            docDomain = getHostname(cset.doc_comp.url),
            comps = cset.getComponentsByType(config.types),
            userCdns = util.Preference.getPref('cdnHostnames', ''),
            hasPref = util.Preference.nativePref;

        // array of custom cdns
        if (userCdns) {
            userCdns = userCdns.split(',');
        }

        for (i = 0, len = comps.length; i < len; i += 1) {
            comp = comps[i];
            url = comp.url;
            hostname = getHostname(url);
            headers = comp.headers;

            // ignore /favicon.ico
            if (comp.type === 'favicon' && hostname === docDomain) {
                continue;
            }

            // experimental custom header, use lowercase
            match = headers['x-cdn'] || headers['x-amz-cf-id'] || headers['x-edge-location'] || headers['powered-by-chinacache'];
            if (match) {
                continue;
            }

            // by hostname
            patterns = config.patterns;
            for (j = 0, lenJ = patterns.length; j < lenJ; j += 1) {
                re = new RegExp(patterns[j]);
                if (re.test(hostname)) {
                    match = 1;
                    break;
                }
            }
            // by custom hostnames
            if (userCdns) {
                for (j = 0, lenJ = userCdns.length; j < lenJ; j += 1) {
                    re = new RegExp(util.trim(userCdns[j]));
                    if (re.test(hostname)) {
                        match = 1;
                        break;
                    }
                }
            }

            if (!match) {
                // by Server HTTP header
                patterns = config.servers;
                for (j = 0, lenJ = patterns.length; j < lenJ; j += 1) {
                    re = new RegExp(patterns[j]);
                    if (re.test(headers.server)) {
                        match = 1;
                        break;
                    }
                }
                if (!match) {
                    // by exception
                    patterns = config.exceptions;
                    for (j = 0, lenJ = patterns.length; j < lenJ; j += 1) {
                        re = new RegExp(patterns[j]);
                        if (re.test(hostname)) {
                            exceptions.push(comp);
                            match = 1;
                            break;
                        }
                    }
                    if (!match) {
                        offenders.push(comp);
                    }
                }
            }
        }

        score -= offenders.length * config.points;

        offenders.concat(exceptions);

        if (offenders.length > 0) {
            message = plural('There %are% %num% static component%s% ' +
                'that %are% not on CDN. ', offenders.length);
        }
        if (exceptions.length > 0) {
            message += plural('There %are% %num% component%s% that %are% not ' +
                'on CDN, but %are% exceptions:', exceptions.length) + '<ul>';
            for (i = 0, len = offenders.length; i < len; i += 1) {
                message += '<li>' + util.prettyAnchor(exceptions[i].url,
                    exceptions[i].url, null, true, 120, null,
                    exceptions[i].type) + '</li>';
            }
            message += '</ul>';
        }

        if (userCdns) {
            message += '<p>Using these CDN hostnames from your preferences: ' +
                userCdns + '</p>';
        } else {
            message += '<p>You can specify CDN hostnames in your ' +
                'preferences. See <a href="javascript:document.ysview.' +
                'openLink(\'http://yslow.org/faq/#faq_cdn\')">YSlow FAQ</a> ' +
                'for details.</p>';
        }

        // list unique domains only to avoid long list of offenders
        if (offenders.length) {
            offenders = util.summaryByDomain(offenders,
                ['size', 'size_compressed'], true);
            for (i = 0, len = offenders.length; i < len; i += 1) {
                offender = offenders[i];
                offenders[i] = offender.domain + ': ' +
                    plural('%num% component%s%, ', offender.count) +
                    kbSize(offender.sum_size) + (
                        offender.sum_size_compressed > 0 ? ' (' +
                        kbSize(offender.sum_size_compressed) + ' GZip)' : ''
                    ) + (hasPref ? (
                    ' <button onclick="javascript:document.ysview.addCDN(\'' +
                    offender.domain + '\')">Add as CDN</button>') : '');
            }
        }

        return {
            score: score,
            message: message,
            components: offenders
        };
    }
});

YSLOW.registerRule({
    id: 'yexpires',
    //name: 'Add an Expires header',
    url: 'http://developer.yahoo.com/performance/rules.html#expires',
    category: ['server'],

    config: {
        // how many points to take for each component without Expires header
        points: 11,
        // 2 days = 2 * 24 * 60 * 60 seconds, how far is far enough
        howfar: 172800,
        // component types to be inspected for expires headers
        types: ['css', 'js', 'image', 'cssimage', 'flash', 'favicon']
    },

    lint: function (doc, cset, config) {
        var ts, i, expiration, score, len,
            // far-ness in milliseconds
            far = parseInt(config.howfar, 10) * 1000,
            offenders = [],
            comps = cset.getComponentsByType(config.types);

        for (i = 0, len = comps.length; i < len; i += 1) {
            expiration = comps[i].expires;
            if (typeof expiration === 'object' &&
                    typeof expiration.getTime === 'function') {
                // looks like a Date object
                ts = new Date().getTime();
                if (expiration.getTime() > ts + far) {
                    continue;
                }
            }
            offenders.push(comps[i]);
        }

        score = 100 - offenders.length * parseInt(config.points, 10);

        return {
            score: score,
            message: (offenders.length > 0) ? YSLOW.util.plural(
                'There %are% %num% static component%s%',
                offenders.length
            ) + ' without a far-future expiration date.' : '',
            components: offenders
        };
    }
});

YSLOW.registerRule({
    id: 'ycompress',
    //name: 'Compress components',
    url: 'http://developer.yahoo.com/performance/rules.html#gzip',
    category: ['server'],

    config: {
        // files below this size are exceptions of the gzip rule
        min_filesize: 500,
        // file types to inspect
        types: ['doc', 'iframe', 'xhr', 'js', 'css'],
        // points to take out for each non-compressed component
        points: 11
    },

    lint: function (doc, cset, config) {
        var i, len, score, comp,
            offenders = [],
            comps = cset.getComponentsByType(config.types);

        for (i = 0, len = comps.length; i < len; i += 1) {
            comp = comps[i];
            if (comp.compressed || comp.size < 500) {
                continue;
            }
            offenders.push(comp);
        }

        score = 100 - offenders.length * parseInt(config.points, 10);

        return {
            score: score,
            message: (offenders.length > 0) ? YSLOW.util.plural(
                'There %are% %num% plain text component%s%',
                offenders.length
            ) + ' that should be sent compressed' : '',
            components: offenders
        };
    }
});

YSLOW.registerRule({
    id: 'ycsstop',
    //name: 'Put CSS at the top',
    url: 'http://developer.yahoo.com/performance/rules.html#css_top',
    category: ['css'],

    config: {
        points: 10
    },

    lint: function (doc, cset, config) {
        var i, len, score, comp,
            comps = cset.getComponentsByType('css'),
            offenders = [];

        // expose all offenders
        for (i = 0, len = comps.length; i < len; i += 1) {
            comp = comps[i];
            if (comp.containerNode === 'body') {
                offenders.push(comp);
            }
        }

        score = 100;
        if (offenders.length > 0) {
            // start at 99 so each ding drops us a grade
            score -= 1 + offenders.length * parseInt(config.points, 10);
        }

        return {
            score: score,
            message: (offenders.length > 0) ? YSLOW.util.plural(
                'There %are% %num% stylesheet%s%',
                offenders.length
            ) + ' found in the body of the document' : '',
            components: offenders
        };
    }
});

YSLOW.registerRule({
    id: 'yjsbottom',
    //name: 'Put Javascript at the bottom',
    url: 'http://developer.yahoo.com/performance/rules.html#js_bottom',
    category: ['javascript'],
    config: {
        points: 5 // how many points for each script in the <head>
    },

    lint: function (doc, cset, config) {
        var i, len, comp, score,
            offenders = [],
            comps = cset.getComponentsByType('js');

        // offenders are components not injected (tag found on document payload)
        // except if they have either defer or async attributes
        for (i = 0, len = comps.length; i < len; i += 1) {
            comp = comps[i];
            if (comp.containerNode === 'head' &&
                    !comp.injected && (!comp.defer || !comp.async)) {
                offenders.push(comp);
            }
        }

        score = 100 - offenders.length * parseInt(config.points, 10);

        return {
            score: score,
            message: (offenders.length > 0) ?
                YSLOW.util.plural(
                    'There %are% %num% JavaScript script%s%',
                    offenders.length
                ) + ' found in the head of the document' : '',
            components: offenders
        };
    }
});

YSLOW.registerRule({
    id: 'yexpressions',
    //name: 'Avoid CSS expressions',
    url: 'http://developer.yahoo.com/performance/rules.html#css_expressions',
    category: ['css'],

    config: {
        points: 2 // how many points for each expression
    },

    lint: function (doc, cset, config) {
        var i, len, expr_count, comp,
            instyles = (cset.inline && cset.inline.styles) || [],
            comps = cset.getComponentsByType('css'),
            offenders = [],
            score = 100,
            total = 0;

        for (i = 0, len = comps.length; i < len; i += 1) {
            comp = comps[i];
            if (typeof comp.expr_count === 'undefined') {
                expr_count = YSLOW.util.countExpressions(comp.body);
                comp.expr_count = expr_count;
            } else {
                expr_count = comp.expr_count;
            }

            // offence
            if (expr_count > 0) {
                comp.yexpressions = YSLOW.util.plural(
                    '%num% expression%s%',
                    expr_count
                );
                total += expr_count;
                offenders.push(comp);
            }
        }

        for (i = 0, len = instyles.length; i < len; i += 1) {
            expr_count = YSLOW.util.countExpressions(instyles[i].body);
            if (expr_count > 0) {
                offenders.push('inline &lt;style&gt; tag #' + (i + 1) + ' (' +
                    YSLOW.util.plural(
                        '%num% expression%s%',
                        expr_count
                    ) + ')'
                    );
                total += expr_count;
            }
        }

        if (total > 0) {
            score = 90 - total * config.points;
        }

        return {
            score: score,
            message: total > 0 ? 'There is a total of ' +
                YSLOW.util.plural('%num% expression%s%', total) : '',
            components: offenders
        };
    }
});

YSLOW.registerRule({
    id: 'yexternal',
    //name: 'Make JS and CSS external',
    url: 'http://developer.yahoo.com/performance/rules.html#external',
    category: ['javascript', 'css'],
    config: {},

    lint: function (doc, cset, config) {
        var message,
            inline = cset.inline,
            styles = (inline && inline.styles) || [],
            scripts = (inline && inline.scripts) || [],
            offenders = [];

        if (styles.length) {
            message = YSLOW.util.plural(
                'There is a total of %num% inline css',
                styles.length
            );
            offenders.push(message);
        }

        if (scripts.length) {
            message = YSLOW.util.plural(
                'There is a total of %num% inline script%s%',
                scripts.length
            );
            offenders.push(message);
        }

        return {
            score: 'n/a',
            message: 'Only consider this if your property is a common user home page.',
            components: offenders
        };
    }
});

YSLOW.registerRule({
    id: 'ydns',
    //name: 'Reduce DNS lookups',
    url: 'http://developer.yahoo.com/performance/rules.html#dns_lookups',
    category: ['content'],

    config: {
        // maximum allowed domains, excluding ports and IP addresses
        max_domains: 4,
        // the cost of each additional domain over the maximum
        points: 5
    },

    lint: function (doc, cset, config) {
        var i, len, domain,
            util = YSLOW.util,
            kbSize = util.kbSize,
            plural = util.plural,
            score = 100,
            domains = util.summaryByDomain(cset.components,
                ['size', 'size_compressed'], true);

        if (domains.length > config.max_domains) {
            score -= (domains.length - config.max_domains) * config.points;
        }

        // list unique domains only to avoid long list of offenders
        if (domains.length) {
            for (i = 0, len = domains.length; i < len; i += 1) {
                domain = domains[i];
                domains[i] = domain.domain + ': ' +
                    plural('%num% component%s%, ', domain.count) +
                    kbSize(domain.sum_size) + (
                        domain.sum_size_compressed > 0 ? ' (' +
                        kbSize(domain.sum_size_compressed) + ' GZip)' : ''
                    );
            }
        }

        return {
            score: score,
            message: (domains.length > config.max_domains) ? plural(
                'The components are split over more than %num% domain%s%',
                config.max_domains
            ) : '',
            components: domains
        };
    }
});

YSLOW.registerRule({
    id: 'yminify',
    //name: 'Minify JS and CSS',
    url: 'http://developer.yahoo.com/performance/rules.html#minify',
    category: ['javascript', 'css'],

    config: {
        // penalty for each unminified component
        points: 10,
        // types of components to inspect for minification
        types: ['js', 'css']
    },

    lint: function (doc, cset, config) {
        var i, len, score, minified, comp,
            inline = cset.inline,
            styles = (inline && inline.styles) || [],
            scripts = (inline && inline.scripts) || [],
            comps = cset.getComponentsByType(config.types),
            offenders = [];

        // check all peeled components
        for (i = 0, len = comps.length; i < len; i += 1) {
            comp = comps[i];
            // set/get minified flag
            if (typeof comp.minified === 'undefined') {
                minified = YSLOW.util.isMinified(comp.body);
                comp.minified = minified;
            } else {
                minified = comp.minified;
            }

            if (!minified) {
                offenders.push(comp);
            }
        }

        // check inline scripts/styles/whatever
        for (i = 0, len = styles.length; i < len; i += 1) {
            if (!YSLOW.util.isMinified(styles[i].body)) {
                offenders.push('inline &lt;style&gt; tag #' + (i + 1));
            }
        }
        for (i = 0, len = scripts.length; i < len; i += 1) {
            if (!YSLOW.util.isMinified(scripts[i].body)) {
                offenders.push('inline &lt;script&gt; tag #' + (i + 1));
            }
        }

        score = 100 - offenders.length * config.points;

        return {
            score: score,
            message: (offenders.length > 0) ? YSLOW.util.plural('There %are% %num% component%s% that can be minified', offenders.length) : '',
            components: offenders
        };
    }
});

YSLOW.registerRule({
    id: 'yredirects',
    //name: 'Avoid redirects',
    url: 'http://developer.yahoo.com/performance/rules.html#redirects',
    category: ['content'],

    config: {
        points: 10 // the penalty for each redirect
    },

    lint: function (doc, cset, config) {
        var i, len, comp, score,
            offenders = [],
            briefUrl = YSLOW.util.briefUrl,
            comps = cset.getComponentsByType('redirect');

        for (i = 0, len = comps.length; i < len; i += 1) {
            comp = comps[i];
            offenders.push(briefUrl(comp.url, 80) + ' redirects to ' +
                briefUrl(comp.headers.location, 60));
        }
        score = 100 - comps.length * parseInt(config.points, 10);

        return {
            score: score,
            message: (comps.length > 0) ? YSLOW.util.plural(
                'There %are% %num% redirect%s%',
                comps.length
            ) : '',
            components: offenders
        };
    }
});

YSLOW.registerRule({
    id: 'ydupes',
    //name: 'Remove duplicate JS and CSS',
    url: 'http://developer.yahoo.com/performance/rules.html#js_dupes',
    category: ['javascript', 'css'],

    config: {
        // penalty for each duplicate
        points: 5,
        // component types to check for duplicates
        types: ['js', 'css']
    },

    lint: function (doc, cset, config) {
        var i, url, score, len,
            hash = {},
            offenders = [],
            comps = cset.getComponentsByType(config.types);

        for (i = 0, len = comps.length; i < len; i += 1) {
            url = comps[i].url;
            if (typeof hash[url] === 'undefined') {
                hash[url] = {
                    count: 1,
                    compindex: i
                };
            } else {
                hash[url].count += 1;
            }
        }

        for (i in hash) {
            if (hash.hasOwnProperty(i) && hash[i].count > 1) {
                offenders.push(comps[hash[i].compindex]);
            }
        }

        score = 100 - offenders.length * parseInt(config.points, 10);

        return {
            score: score,
            message: (offenders.length > 0) ? YSLOW.util.plural(
                'There %are% %num% duplicate component%s%',
                offenders.length
            ) : '',
            components: offenders
        };
    }
});

YSLOW.registerRule({
    id: 'yetags',
    //name: 'Configure ETags',
    url: 'http://developer.yahoo.com/performance/rules.html#etags',
    category: ['server'],

    config: {
        // points to take out for each misconfigured etag
        points: 11,
        // types to inspect for etags
        types: ['flash', 'js', 'css', 'cssimage', 'image', 'favicon']
    },

    lint: function (doc, cset, config) {

        var i, len, score, comp, etag,
            offenders = [],
            comps = cset.getComponentsByType(config.types);

        for (i = 0, len = comps.length; i < len; i += 1) {
            comp = comps[i];
            etag = comp.headers && comp.headers.etag;
            if (etag && !YSLOW.util.isETagGood(etag)) {
                offenders.push(comp);
            }
        }

        score = 100 - offenders.length * parseInt(config.points, 10);

        return {
            score: score,
            message: (offenders.length > 0) ? YSLOW.util.plural(
                'There %are% %num% component%s% with misconfigured ETags',
                offenders.length
            ) : '',
            components: offenders
        };
    }
});

YSLOW.registerRule({
    id: 'yxhr',
    //name: 'Make Ajax cacheable',
    url: 'http://developer.yahoo.com/performance/rules.html#cacheajax',
    category: ['content'],

    config: {
        // points to take out for each non-cached XHR
        points: 5,
        // at least an hour in cache.
        min_cache_time: 3600
    },

    lint: function (doc, cset, config) {
        var i, expiration, ts, score, cache_control,
            // far-ness in milliseconds
            min = parseInt(config.min_cache_time, 10) * 1000,
            offenders = [],
            comps = cset.getComponentsByType('xhr');

        for (i = 0; i < comps.length; i += 1) {
            // check for cache-control: no-cache and cache-control: no-store
            cache_control = comps[i].headers['cache-control'];
            if (cache_control) {
                if (cache_control.indexOf('no-cache') !== -1 ||
                        cache_control.indexOf('no-store') !== -1) {
                    continue;
                }
            }

            expiration = comps[i].expires;
            if (typeof expiration === 'object' &&
                    typeof expiration.getTime === 'function') {
                // looks like a Date object
                ts = new Date().getTime();
                if (expiration.getTime() > ts + min) {
                    continue;
                }
                // expires less than min_cache_time => BAD.
            }
            offenders.push(comps[i]);
        }

        score = 100 - offenders.length * parseInt(config.points, 10);

        return {
            score: score,
            message: (offenders.length > 0) ? YSLOW.util.plural(
                'There %are% %num% XHR component%s% that %are% not cacheable',
                offenders.length
            ) : '',
            components: offenders
        };
    }
});

YSLOW.registerRule({
    id: 'yxhrmethod',
    //name: 'Use GET for AJAX Requests',
    url: 'http://developer.yahoo.com/performance/rules.html#ajax_get',
    category: ['server'],

    config: {
        // points to take out for each ajax request
        // that uses http method other than GET.
        points: 5
    },

    lint: function (doc, cset, config) {
        var i, score,
            offenders = [],
            comps = cset.getComponentsByType('xhr');

        for (i = 0; i < comps.length; i += 1) {
            if (typeof comps[i].method === 'string') {
                if (comps[i].method !== 'GET' && comps[i].method !== 'unknown') {
                    offenders.push(comps[i]);
                }
            }
        }
        score = 100 - offenders.length * parseInt(config.points, 10);

        return {
            score: score,
            message: (offenders.length > 0) ? YSLOW.util.plural(
                'There %are% %num% XHR component%s% that %do% not use GET HTTP method',
                offenders.length
            ) : '',
            components: offenders
        };
    }
});

YSLOW.registerRule({
    id: 'ymindom',
    //name: 'Reduce the Number of DOM Elements',
    url: 'http://developer.yahoo.com/performance/rules.html#min_dom',
    category: ['content'],

    config: {
        // the range
        range: 250,
        // points to take out for each range of DOM that's more than max.
        points: 10,
        // number of DOM elements are considered too many if exceeds maxdom.
        maxdom: 900
    },

    lint: function (doc, cset, config) {
        var numdom = cset.domElementsCount,
            score = 100;

        if (numdom > config.maxdom) {
            score = 99 - Math.ceil((numdom - parseInt(config.maxdom, 10)) /
                parseInt(config.range, 10)) * parseInt(config.points, 10);
        }

        return {
            score: score,
            message: (numdom > config.maxdom) ? YSLOW.util.plural(
                'There %are% %num% DOM element%s% on the page',
                numdom
            ) : '',
            components: []
        };
    }
});

YSLOW.registerRule({
    id: 'yno404',
    //name: 'No 404s',
    url: 'http://developer.yahoo.com/performance/rules.html#no404',
    category: ['content'],

    config: {
        // points to take out for each 404 response.
        points: 5,
        // component types to be inspected for expires headers
        types: ['css', 'js', 'image', 'cssimage', 'flash', 'xhr', 'favicon']
    },

    lint: function (doc, cset, config) {
        var i, len, comp, score,
            offenders = [],
            comps = cset.getComponentsByType(config.types);

        for (i = 0, len = comps.length; i < len; i += 1) {
            comp = comps[i];
            if (parseInt(comp.status, 10) === 404) {
                offenders.push(comp);
            }
        }
        score = 100 - offenders.length * parseInt(config.points, 10);
        return {
            score: score,
            message: (offenders.length > 0) ? YSLOW.util.plural(
                'There %are% %num% request%s% that %are% 404 Not Found',
                offenders.length
            ) : '',
            components: offenders
        };
    }
});

YSLOW.registerRule({
    id: 'ymincookie',
    //name: 'Reduce Cookie Size',
    url: 'http://developer.yahoo.com/performance/rules.html#cookie_size',
    category: ['cookie'],

    config: {
        // points to take out if cookie size is more than config.max_cookie_size
        points: 10,
        // 1000 bytes.
        max_cookie_size: 1000
    },

    lint: function (doc, cset, config) {
        var n,
            cookies = cset.cookies,
            cookieSize = (cookies && cookies.length) || 0,
            message = '',
            score = 100;

        if (cookieSize > config.max_cookie_size) {
            n = Math.floor(cookieSize / config.max_cookie_size);
            score -= 1 + n * parseInt(config.points, 10);
            message = YSLOW.util.plural(
                'There %are% %num% byte%s% of cookies on this page',
                cookieSize
            );
        }

        return {
            score: score,
            message: message,
            components: []
        };
    }
});

YSLOW.registerRule({
    id: 'ycookiefree',
    //name: 'Use Cookie-free Domains',
    url: 'http://developer.yahoo.com/performance/rules.html#cookie_free',
    category: ['cookie'],

    config: {
        // points to take out for each component that send cookie.
        points: 5,
        // which component types should be cookie-free
        types: ['js', 'css', 'image', 'cssimage', 'flash', 'favicon']
    },

    lint: function (doc, cset, config) {
        var i, len, score, comp, cookie,
            offenders = [],
            getHostname = YSLOW.util.getHostname,
            docDomain = getHostname(cset.doc_comp.url),
            comps = cset.getComponentsByType(config.types);

        for (i = 0, len = comps.length; i < len; i += 1) {
            comp = comps[i];

            // ignore /favicon.ico
            if (comp.type === 'favicon' &&
                    getHostname(comp.url) === docDomain) {
                continue;
            }

            cookie = comp.cookie;
            if (typeof cookie === 'string' && cookie.length) {
                offenders.push(comps[i]);
            }
        }
        score = 100 - offenders.length * parseInt(config.points, 10);

        return {
            score: score,
            message: (offenders.length > 0) ? YSLOW.util.plural(
                'There %are% %num% component%s% that %are% not cookie-free',
                offenders.length
            ) : '',
            components: offenders
        };
    }
});

YSLOW.registerRule({
    id: 'ynofilter',
    //name: 'Avoid Filters',
    url: 'http://developer.yahoo.com/performance/rules.html#no_filters',
    category: ['css'],

    config: {
        // points to take out for each AlphaImageLoader filter not using _filter hack.
        points: 5,
        // points to take out for each AlphaImageLoader filter using _filter hack.
        halfpoints: 2
    },

    lint: function (doc, cset, config) {
        var i, len, score, comp, type, count, filter_count,
            instyles = (cset.inline && cset.inline.styles) || [],
            comps = cset.getComponentsByType('css'),
            offenders = [],
            filter_total = 0,
            hack_filter_total = 0;

        for (i = 0, len = comps.length; i < len; i += 1) {
            comp = comps[i];
            if (typeof comp.filter_count === 'undefined') {
                filter_count = YSLOW.util.countAlphaImageLoaderFilter(comp.body);
                comp.filter_count = filter_count;
            } else {
                filter_count = comp.filter_count;
            }

            // offence
            count = 0;
            for (type in filter_count) {
                if (filter_count.hasOwnProperty(type)) {
                    if (type === 'hackFilter') {
                        hack_filter_total += filter_count[type];
                        count += filter_count[type];
                    } else {
                        filter_total += filter_count[type];
                        count += filter_count[type];
                    }
                }
            }
            if (count > 0) {
                comps[i].yfilters = YSLOW.util.plural('%num% filter%s%', count);
                offenders.push(comps[i]);
            }
        }

        for (i = 0, len = instyles.length; i < len; i += 1) {
            filter_count = YSLOW.util.countAlphaImageLoaderFilter(instyles[i].body);
            count = 0;
            for (type in filter_count) {
                if (filter_count.hasOwnProperty(type)) {
                    if (type === 'hackFilter') {
                        hack_filter_total += filter_count[type];
                        count += filter_count[type];
                    } else {
                        filter_total += filter_count[type];
                        count += filter_count[type];
                    }
                }
            }
            if (count > 0) {
                offenders.push('inline &lt;style&gt; tag #' + (i + 1) + ' (' +
                    YSLOW.util.plural('%num% filter%s%', count) + ')');
            }
        }

        score = 100 - (filter_total * config.points + hack_filter_total *
            config.halfpoints);

        return {
            score: score,
            message: (filter_total + hack_filter_total) > 0 ?
                'There is a total of ' + YSLOW.util.plural(
                    '%num% filter%s%',
                    filter_total + hack_filter_total
                ) : '',
            components: offenders
        };
    }
});

YSLOW.registerRule({
    id: 'yimgnoscale',
    //name: 'Don\'t Scale Images in HTML',
    url: 'http://developer.yahoo.com/performance/rules.html#no_scale',
    category: ['images'],

    config: {
        points: 5 // points to take out for each image that scaled.
    },

    lint: function (doc, cset, config) {
        var i, prop, score,
            offenders = [],
            comps = cset.getComponentsByType('image');

        for (i = 0; i < comps.length; i += 1) {
            prop = comps[i].object_prop;
            if (prop && typeof prop.width !== 'undefined' &&
                    typeof prop.height !== 'undefined' &&
                    typeof prop.actual_width !== 'undefined' &&
                    typeof prop.actual_height !== 'undefined') {
                if (prop.width < prop.actual_width ||
                        prop.height < prop.actual_height) {
                    // allow scale up
                    offenders.push(comps[i]);
                }
            }
        }
        score = 100 - offenders.length * parseInt(config.points, 10);

        return {
            score: score,
            message: (offenders.length > 0) ? YSLOW.util.plural(
                'There %are% %num% image%s% that %are% scaled down',
                offenders.length
            ) : '',
            components: offenders
        };
    }
});

YSLOW.registerRule({
    id: 'yfavicon',
    //name: 'Make favicon Small and Cacheable',
    url: 'http://developer.yahoo.com/performance/rules.html#favicon',
    category: ['images'],

    config: {
        // points to take out for each offend.
        points: 5,
        // deduct point if size of favicon is more than this number.
        size: 2000,
        // at least this amount of seconds in cache to consider cacheable.
        min_cache_time: 3600
    },

    lint: function (doc, cset, config) {
        var ts, expiration, comp, score, cacheable,
            messages = [],
            min = parseInt(config.min_cache_time, 10) * 1000,
            comps = cset.getComponentsByType('favicon');

        if (comps.length) {
            comp = comps[0];

            // check if favicon was found
            if (parseInt(comp.status, 10) === 404) {
                messages.push('Favicon was not found');
            }

            // check size
            if (comp.size > config.size) {
                messages.push(YSLOW.util.plural(
                    'Favicon is more than %num% bytes',
                    config.size
                ));
            }
            // check cacheability
            expiration = comp.expires;

            if (typeof expiration === 'object' &&
                    typeof expiration.getTime === 'function') {
                // looks like a Date object
                ts = new Date().getTime();
                cacheable = expiration.getTime() >= ts + min;
            }
            if (!cacheable) {
                messages.push('Favicon is not cacheable');
            }
        }
        score = 100 - messages.length * parseInt(config.points, 10);

        return {
            score: score,
            message: (messages.length > 0) ? messages.join('\n') : '',
            components: []
        };
    }

});

YSLOW.registerRule({
    id: 'yemptysrc',
    // name: 'Avoid empty src or href',
    url: 'http://developer.yahoo.com/performance/rules.html#emptysrc',
    category: ['server'],
    config: {
        points: 100
    },
    lint: function (doc, cset, config) {
        var type, score, count,
            emptyUrl = cset.empty_url,
            offenders = [],
            messages = [],
            msg = '',
            points = parseInt(config.points, 10);

        score = 100;
        if (emptyUrl) {
            for (type in emptyUrl) {
                if (emptyUrl.hasOwnProperty(type)) {
                    count = emptyUrl[type];
                    score -= count * points;
                    messages.push(count + ' ' + type);
                }
            }
            msg = messages.join(', ') + YSLOW.util.plural(
                ' component%s% with empty link were found.',
                messages.length
            );
        }

        return {
            score: score,
            message: msg,
            components: offenders
        };
    }
});

/**
 * YSLOW.registerRuleset({
 *
 *     id: 'myalgo',
 *     name: 'The best algo',
 *     rules: {
 *         myrule: {
 *             ever: 2,
 *         }
 *     }
 *
 * });
 */

YSLOW.registerRuleset({ // yahoo default with default configuration
    id: 'ydefault',
    name: 'YSlow(V2)',
    rules: {
        ynumreq: {},
        //  1
        ycdn: {},
        //  2
        yemptysrc: {},
        yexpires: {},
        //  3
        ycompress: {},
        //  4
        ycsstop: {},
        //  5
        yjsbottom: {},
        //  6
        yexpressions: {},
        //  7
        yexternal: {},
        //  8
        ydns: {},
        //  9
        yminify: {},
        // 10
        yredirects: {},
        // 11
        ydupes: {},
        // 12
        yetags: {},
        // 13
        yxhr: {},
        // 14
        yxhrmethod: {},
        // 16
        ymindom: {},
        // 19
        yno404: {},
        // 22
        ymincookie: {},
        // 23
        ycookiefree: {},
        // 24
        ynofilter: {},
        // 28
        yimgnoscale: {},
        // 31
        yfavicon: {} // 32
    },
    weights: {
        ynumreq: 8,
        ycdn: 6,
        yemptysrc: 30,
        yexpires: 10,
        ycompress: 8,
        ycsstop: 4,
        yjsbottom: 4,
        yexpressions: 3,
        yexternal: 4,
        ydns: 3,
        yminify: 4,
        yredirects: 4,
        ydupes: 4,
        yetags: 2,
        yxhr: 4,
        yxhrmethod: 3,
        ymindom: 3,
        yno404: 4,
        ymincookie: 3,
        ycookiefree: 3,
        ynofilter: 4,
        yimgnoscale: 3,
        yfavicon: 2
    }

});

YSLOW.registerRuleset({

    id: 'yslow1',
    name: 'Classic(V1)',
    rules: {
        ynumreq: {},
        //  1
        ycdn: {},
        //  2
        yexpires: {},
        //  3
        ycompress: {},
        //  4
        ycsstop: {},
        //  5
        yjsbottom: {},
        //  6
        yexpressions: {},
        //  7
        yexternal: {},
        //  8
        ydns: {},
        //  9
        yminify: { // 10
            types: ['js'],
            check_inline: false
        },
        yredirects: {},
        // 11
        ydupes: { // 12
            types: ['js']
        },
        yetags: {} // 13
    },
    weights: {
        ynumreq: 8,
        ycdn: 6,
        yexpires: 10,
        ycompress: 8,
        ycsstop: 4,
        yjsbottom: 4,
        yexpressions: 3,
        yexternal: 4,
        ydns: 3,
        yminify: 4,
        yredirects: 4,
        ydupes: 4,
        yetags: 2
    }

});


YSLOW.registerRuleset({
    id: 'yblog',
    name: 'Small Site or Blog',
    rules: {
        ynumreq: {},
        //  1
        yemptysrc: {},
        ycompress: {},
        //  4
        ycsstop: {},
        //  5
        yjsbottom: {},
        //  6
        yexpressions: {},
        //  7
        ydns: {},
        //  9
        yminify: {},
        // 10
        yredirects: {},
        // 11
        ydupes: {},
        // 12
        ymindom: {},
        // 19
        yno404: {},
        // 22
        ynofilter: {},
        // 28
        yimgnoscale: {},
        // 31
        yfavicon: {} // 32
    },
    weights: {
        ynumreq: 8,
        yemptysrc: 30,
        ycompress: 8,
        ycsstop: 4,
        yjsbottom: 4,
        yexpressions: 3,
        ydns: 3,
        yminify: 4,
        yredirects: 4,
        ydupes: 4,
        ymindom: 3,
        yno404: 4,
        ynofilter: 4,
        yimgnoscale: 3,
        yfavicon: 2
    }
});
/**
 * Custom ruleset must be placed in this directory as rulseset_name.js
 *
 * Here is a very simplified snippet for registering a new rules and ruleset:
 *
 * YSLOW.registerRule({
 *     id: 'foo-rule1',
 *     name: 'Sample Test #1',
 *     info: 'How simple is that?',
 * 
 *     lint: function (doc, cset, config) {
 *         return {
 *             score: 90,
 *              message: 'close but no cigar',
 *            components: ['element1']
 *         };
 *     }
 * });
 * 
 * YSLOW.registerRuleset({
 *     id: 'foo',
 *     name: 'Foobar Ruleset',
 *     rules: {
 *         'foo-rule1': {}
 *     },
 *     weights: {
 *         'foo-rule1': 3
 *     }
 * });
 *
 */
/**
 * Copyright (c) 2012, Yahoo! Inc.  All rights reserved.
 * Copyright (c) 2013, Marcel Duran and other contributors. All rights reserved.
 * Copyrights licensed under the New BSD License. See the accompanying LICENSE file for terms.
 */

/*global YSLOW*/
/*jslint white: true, onevar: true, undef: true, nomen: true, eqeqeq: true, plusplus: true, bitwise: true, regexp: true, newcap: true, immed: true */

/**
 * ResultSet class
 * @constructor
 * @param {Array} results array of lint result
 * @param {Number} overall_score overall score
 * @param {YSLOW.Ruleset} ruleset_applied Ruleset used to generate the result.
 */
YSLOW.ResultSet = function (results, overall_score, ruleset_applied) {
    this.ruleset_applied = ruleset_applied;
    this.overall_score = overall_score;
    this.results = results;
};

YSLOW.ResultSet.prototype = {

    /**
     * Get results array from ResultSet.
     * @return results array
     * @type Array
     */
    getResults: function () {
        return this.results;
    },

    /**
     * Get ruleset applied from ResultSet
     * @return ruleset applied
     * @type YSLOW.Ruleset
     */
    getRulesetApplied: function () {
        return this.ruleset_applied;
    },

    /**
     * Get overall score from ResultSet
     * @return overall score
     * @type Number
     */
    getOverallScore: function () {
        return this.overall_score;
    }

};
/**
 * Copyright (c) 2012, Yahoo! Inc.  All rights reserved.
 * Copyright (c) 2013, Marcel Duran and other contributors. All rights reserved.
 * Copyrights licensed under the New BSD License. See the accompanying LICENSE file for terms.
 */

/*global YSLOW*/
/*jslint white: true, onevar: true, undef: true, newcap: true, nomen: true, plusplus: true, bitwise: true, continue: true, maxerr: 50, indent: 4 */

/**
 * @todo:
 * - need better way to discover @import stylesheets, the current one doesn't find them
 * - add request type - post|get - when possible, maybe in the net part of the peeling process
 *
 */

/**
 * Peeler singleton
 * @class
 * @static
 */
YSLOW.peeler = {

    /**
     * @final
     */
    types: ['doc', 'js', 'css', 'iframe', 'flash', 'cssimage', 'image',
        'favicon', 'xhr', 'redirect', 'font'],

    NODETYPE: {
        ELEMENT: 1,
        DOCUMENT: 9
    },

/*
     * http://www.w3.org/TR/DOM-Level-2-Style/css.html#CSS-CSSRule
     */
    CSSRULE: {
        IMPORT_RULE: 3,
        FONT_FACE_RULE: 5
    },

    /**
     * Start peeling the document in passed window object.
     * The component may be requested asynchronously.
     *
     * @param {DOMElement} node object
     * @param {Number} onloadTimestamp onload timestamp
     * @return ComponentSet
     * @type YSLOW.ComponentSet
     */
    peel: function (node, onloadTimestamp) {
        // platform implementation goes here
    },

    /**
     * @private
     * Finds all frames/iframes recursively
     * @param {DOMElement} node object
     * @return an array of documents in the passed DOM node.
     * @type Array
     */
    findDocuments: function (node) {
        var frames, doc, docUrl, type, i, len, el, frameDocs, parentDoc,
            allDocs = {};

        YSLOW.util.event.fire('peelProgress', {
            'total_step': 7,
            'current_step': 1,
            'message': 'Finding documents'
        });

        if (!node) {
            return;
        }

        // check if frame digging was disabled, if so, return the top doc and return.
        if (!YSLOW.util.Preference.getPref('extensions.yslow.getFramesComponents', true)) {
            allDocs[node.URL] = {
                'document': node,
                'type': 'doc'
            };
            return allDocs;
        }

        type = 'doc';
        if (node.nodeType === this.NODETYPE.DOCUMENT) {
            // Document node
            doc = node;
            docUrl = node.URL;
        } else if (node.nodeType === this.NODETYPE.ELEMENT &&
                node.nodeName.toLowerCase() === 'frame') {
            // Frame node
            doc = node.contentDocument;
            docUrl = node.src;
        } else if (node.nodeType === this.NODETYPE.ELEMENT &&
                node.nodeName.toLowerCase() === 'iframe') {
            doc = node.contentDocument;
            docUrl = node.src;
            type = 'iframe';
            try {
                parentDoc = node.contentWindow;
                parentDoc = parentDoc && parentDoc.parent;
                parentDoc = parentDoc && parentDoc.document;
                parentDoc = parentDoc || node.ownerDocument;
                if (parentDoc && parentDoc.URL === docUrl) {
                    // check attribute
                    docUrl = !node.getAttribute('src') ? '' : 'about:blank';
                }
            } catch (err) {
                YSLOW.util.dump(err);
            }
        } else {
            return allDocs;
        }
        allDocs[docUrl] = {
            'document': doc,
            'type': type
        };

        try {
            frames = doc.getElementsByTagName('iframe');
            for (i = 0, len = frames.length; i < len; i += 1) {
                el = frames[i];
                if (el.src) {
                    frameDocs = this.findDocuments(el);
                    if (frameDocs) {
                        allDocs = YSLOW.util.merge(allDocs, frameDocs);
                    }
                }
            }

            frames = doc.getElementsByTagName('frame');
            for (i = 0, len = frames.length; i < len; i += 1) {
                el = frames[i];
                frameDocs = this.findDocuments(el);
                if (frameDocs) {
                    allDocs = YSLOW.util.merge(allDocs, frameDocs);
                }
            }
        } catch (e) {
            YSLOW.util.dump(e);
        }

        return allDocs;
    },

    /**
     * @private
     * Find all components in the passed node.
     * @param {DOMElement} node DOM object
     * @param {String} doc_location document.location
     * @param {String} baseHref href
     * @return array of object (array[] = {'type': object.type, 'href': object.href } )
     * @type Array
     */
    findComponentsInNode: function (node, baseHref, type) {
        var comps = [];
        
        try {
            comps = this.findStyleSheets(node, baseHref);
        } catch (e1) {
            YSLOW.util.dump(e1);
        }
        try {
            comps = comps.concat(this.findScripts(node));
        } catch (e2) {
            YSLOW.util.dump(e2);
        }
        try {
            comps = comps.concat(this.findFlash(node));
        } catch (e3) {
            YSLOW.util.dump(e3);
        }
        try {
            comps = comps.concat(this.findCssImages(node));
        } catch (e4) {
            YSLOW.util.dump(e4);
        }
        try {
            comps = comps.concat(this.findImages(node));
        } catch (e5) {
            YSLOW.util.dump(e5);
        }
        try {
            if (type === 'doc') {
                comps = comps.concat(this.findFavicon(node, baseHref));
            }
        } catch (e6) {
            YSLOW.util.dump(e6);
        }
        
        return comps;
    },

    /**
     * @private
     * Add components in Net component that are not component list found by
     * peeler. These can be xhr requests or images that are preloaded by
     * javascript.
     *
     * @param {YSLOW.ComponentSet} component_set ComponentSet to be checked
     * against.
     * @param {String} base_herf base href
     */
    addComponentsNotInNode: function (component_set, base_href) {
        var i, j, imgs, type, objs,
            types = ['flash', 'js', 'css', 'doc', 'redirect'],
            xhrs = YSLOW.net.getResponseURLsByType('xhr');

        // Now, check net module for xhr component.
        if (xhrs.length > 0) {
            for (j = 0; j < xhrs.length; j += 1) {
                component_set.addComponent(xhrs[j], 'xhr', base_href);
            }
        }

        // check image beacons
        imgs = YSLOW.net.getResponseURLsByType('image');
        if (imgs.length > 0) {
            for (j = 0; j < imgs.length; j += 1) {
                type = 'image';
                if (imgs[j].indexOf("favicon.ico") !== -1) {
                    type = 'favicon';
                }
                component_set.addComponentNoDuplicate(imgs[j], type, base_href);
            }
        }

        // should we check other types?
        for (i = 0; i < types.length; i += 1) {
            objs = YSLOW.net.getResponseURLsByType(types[i]);
            for (j = 0; j < objs.length; j += 1) {
                component_set.addComponentNoDuplicate(objs[j], types[i], base_href);
            }
        }
    },

    /**
     * @private
     * Find all stylesheets in the passed DOM node.
     * @param {DOMElement} node DOM object
     * @param {String} doc_location document.location
     * @param {String} base_href base href
     * @return array of object (array[] = {'type' : 'css', 'href': object.href})
     * @type Array
     */
    findStyleSheets: function (node, baseHref) {
        var styles, style, i, len,
            head = node.getElementsByTagName('head')[0],
            body = node.getElementsByTagName('body')[0],
            comps = [],
            that = this,

            loop = function (els, container) {
                var i, len, el, href, cssUrl;

                for (i = 0, len = els.length; i < len; i += 1) {
                    el = els[i];
                    href = el.href || el.getAttribute('href');
                    if (href && (el.rel === 'stylesheet' ||
                            el.type === 'text/css')) {
                        comps.push({
                            type: 'css',
                            href: href === node.URL ? '' : href,
                            containerNode: container
                        });
                        cssUrl = YSLOW.util.makeAbsoluteUrl(href, baseHref);
                        comps = comps.concat(that.findImportedStyleSheets(el.sheet, cssUrl));
                    }
                }
            };

        YSLOW.util.event.fire('peelProgress', {
            'total_step': 7,
            'current_step': 2,
            'message': 'Finding StyleSheets'
        });

        if (head || body) {
            if (head) {
                loop(head.getElementsByTagName('link'), 'head');
            }
            if (body) {
                loop(body.getElementsByTagName('link'), 'body');
            }
        } else {
            loop(node.getElementsByTagName('link'));
        }

        styles = node.getElementsByTagName('style');
        for (i = 0, len = styles.length; i < len; i += 1) {
            style = styles[i];
            comps = comps.concat(that.findImportedStyleSheets(style.sheet, baseHref));
        }

        return comps;
    },

    /**
     * @private
     * Given a css rule, if it's an "@import" rule then add the style sheet
     * component. Also, do a recursive check to see if this imported stylesheet
     * itself contains an imported stylesheet. (FF only)
     * @param {DOMElement} stylesheet DOM stylesheet object
     * @return array of object
     * @type Array
     */
    findImportedStyleSheets: function (styleSheet, parentUrl) {
        var i, rules, rule, cssUrl, ff, len,
            reFile = /url\s*\(["']*([^"'\)]+)["']*\)/i,
            comps = [];

        try {
            if (!(rules = styleSheet.cssRules)) {
                return comps;
            }
            for (i = 0, len = rules.length; i < len; i += 1) {
                rule = rules[i];
                if (rule.type === YSLOW.peeler.CSSRULE.IMPORT_RULE && rule.styleSheet && rule.href) {
                    // It is an imported stylesheet!
                    comps.push({
                        type: 'css',
                        href: rule.href,
                        base: parentUrl
                    });
                    // Recursively check if this stylesheet itself imports any other stylesheets.
                    cssUrl = YSLOW.util.makeAbsoluteUrl(rule.href, parentUrl);
                    comps = comps.concat(this.findImportedStyleSheets(rule.styleSheet, cssUrl));
                } else if (rule.type === YSLOW.peeler.CSSRULE.FONT_FACE_RULE) {
                    if (rule.style && typeof rule.style.getPropertyValue === 'function') {
                        ff = rule.style.getPropertyValue('src');
                        ff = reFile.exec(ff);
                        if (ff) {
                            ff = ff[1];
                            comps.push({
                                type: 'font',
                                href: ff,
                                base: parentUrl
                            });
                        }
                    }
                } else {
                    break;
                }
            }
        } catch (e) {
            YSLOW.util.dump(e);
        }

        return comps;
    },

    /**
     * @private
     * Find all scripts in the passed DOM node.
     * @param {DOMElement} node DOM object
     * @return array of object (array[] = {'type': 'js', 'href': object.href})
     * @type Array
     */
    findScripts: function (node) {
        var comps = [],
            head = node.getElementsByTagName('head')[0],
            body = node.getElementsByTagName('body')[0],

            loop = function (scripts, container) {
                var i, len, script, type, src;

                for (i = 0, len = scripts.length; i < len; i += 1) {
                    script = scripts[i];
                    type = script.type;
                    if (type &&
                            type.toLowerCase().indexOf('javascript') < 0) {
                        continue;
                    }
                    src = script.src || script.getAttribute('src');
                    if (src) {
                        comps.push({
                            type: 'js',
                            href: src === node.URL ? '' : src,
                            containerNode: container
                        });
                    }
                }
            };

        YSLOW.util.event.fire('peelProgress', {
            'total_step': 7,
            'current_step': 3,
            'message': 'Finding JavaScripts'
        });

        if (head || body) {
            if (head) {
                loop(head.getElementsByTagName('script'), 'head');
            }
            if (body) {
                loop(body.getElementsByTagName('script'), 'body');
            }
        } else {
            loop(node.getElementsByTagName('script'));
        }

        return comps;
    },

    /**
     * @private
     * Find all flash in the passed DOM node.
     * @param {DOMElement} node DOM object
     * @return array of object (array[] =  {'type' : 'flash', 'href': object.href } )
     * @type Array
     */
    findFlash: function (node) {
        var i, el, els, len,
            comps = [];

        YSLOW.util.event.fire('peelProgress', {
            'total_step': 7,
            'current_step': 4,
            'message': 'Finding Flash'
        });

        els = node.getElementsByTagName('embed');
        for (i = 0, len = els.length; i < len; i += 1) {
            el = els[i];
            if (el.src) {
                comps.push({
                    type: 'flash',
                    href: el.src
                });
            }
        }

        els = node.getElementsByTagName('object');
        for (i = 0, len = els.length; i < len; i += 1) {
            el = els[i];
            if (el.data && el.type === 'application/x-shockwave-flash') {
                comps.push({
                    type: 'flash',
                    href: el.data
                });
            }
        }

        return comps;
    },

    /**
     * @private
     * Find all css images in the passed DOM node.
     * @param {DOMElement} node DOM object
     * @return array of object (array[] = {'type' : 'cssimage', 'href': object.href } )
     * @type Array
     */
    findCssImages: function (node) {
        var i, j, el, els, prop, url, len,
            comps = [],
            hash = {},
            props = ['backgroundImage', 'listStyleImage', 'content', 'cursor'],
            lenJ = props.length;

        YSLOW.util.event.fire('peelProgress', {
            'total_step': 7,
            'current_step': 5,
            'message': 'Finding CSS Images'
        });

        els = node.getElementsByTagName('*');
        for (i = 0, len = els.length; i < len; i += 1) {
            el = els[i];
            for (j = 0; j < lenJ; j += 1) {
                prop = props[j];
                url = YSLOW.util.getComputedStyle(el, prop, true);
                if (url && !hash[url]) {
                    comps.push({
                        type: 'cssimage',
                        href: url
                    });
                    hash[url] = 1;
                }
            }
        }

        return comps;
    },

    /**
     * @private
     * Find all images in the passed DOM node.
     * @param {DOMElement} node DOM object
     * @return array of object (array[] = {'type': 'image', 'href': object.href} )
     * @type Array
     */
    findImages: function (node) {
        var i, img, imgs, src, len,
            comps = [],
            hash = {};

        YSLOW.util.event.fire('peelProgress', {
            'total_step': 7,
            'current_step': 6,
            'message': 'Finding Images'
        });

        imgs = node.getElementsByTagName('img');
        for (i = 0, len = imgs.length; i < len; i += 1) {
            img = imgs[i];
            src = img.src;
            if (src && !hash[src]) {
                comps.push({
                    type: 'image',
                    href: src,
                    obj: {
                        width: img.width,
                        height: img.height
                    }
                });
                hash[src] = 1;
            }
        }

        return comps;
    },

    /**
     * @private
     * Find favicon link.
     * @param {DOMElement} node DOM object
     * @return array of object (array[] = {'type': 'favicon', 'href': object.href} )
     * @type Array
     */
    findFavicon: function (node, baseHref) {
        var i, len, link, links, rel,
            comps = [];

        YSLOW.util.event.fire('peelProgress', {
            'total_step': 7,
            'current_step': 7,
            'message': 'Finding favicon'
        });

        links = node.getElementsByTagName('link');
        for (i = 0, len = links.length; i < len; i += 1) {
            link = links[i];
            rel = (link.rel || '').toLowerCase(); 
            if (link.href && (rel === 'icon' ||
                rel === 'shortcut icon')) {
                comps.push({
                    type: 'favicon',
                    href: link.href
                });
            }
        }

        // add default /favicon.ico if none informed
        if (!comps.length) {
            comps.push({
                type: 'favicon',
                href: YSLOW.util.makeAbsoluteUrl('/favicon.ico', baseHref)
            });
        }

        return comps;
    },

    /**
     * @private
     * Get base href of document.  If <base> element is not found, use doc.location.
     * @param {Document} doc Document object
     * @return base href
     * @type String
     */
    getBaseHref: function (doc) {
        var base;
        
        try {
            base = doc.getElementsByTagName('base')[0];
            base = (base && base.href) || doc.URL; 
        } catch (e) {
            YSLOW.util.dump(e);
        }

        return base;
    }
};
/**
 * Copyright (c) 2012, Yahoo! Inc.  All rights reserved.
 * Copyright (c) 2013, Marcel Duran and other contributors. All rights reserved.
 * Copyrights licensed under the New BSD License. See the accompanying LICENSE file for terms.
 */

/*global YSLOW*/

(function () {
    var types = {
            'text/javascript': 'js',
            'text/jscript': 'js',
            'application/javascript': 'js',
            'application/x-javascript': 'js',
            'text/js': 'js',

            'text/plain': 'doc',
            'text/html': 'doc',
            'application/xhtml+xml': 'doc',

            'text/css': 'css',

            'image/png': 'image',
            'image/jpeg': 'image',
            'image/gif': 'image',
            'image/x-icon': 'image',

            'application/x-shockwave-flash': 'flash',
                
            'text/x-json': 'json',
            'text/x-js': 'json',
            'application/json': 'json',
            'application/x-js': 'json',

            'application/xml': 'xml',
            'application/vnd.mozilla.xul+xml': 'xml',
            'text/xml': 'xml',
            'text/xul': 'xml',
            'application/rdf+xml': 'xml'
        },
        
        // distinguish images from css images
        setCssImages = function (cset) {
            var i, len, comp,
                urls = {},
                comps = cset.getComponentsByType('css');

            // loop all css and build url hash
            for (i = 0, len = comps.length; i < len; i += 1) {
                urls[comps[i].url] = 1;
            }

            // loop all images and set as cssimage those with referer
            comps = cset.getComponentsByType('image');
            for (i = 0, len = comps.length; i < len; i += 1) {
                comp = comps[i];
                if (urls[comp.req_headers.referer]) {
                    comp.type = 'cssimage';
                }
            }
        },

        // split html (string) into head and body elements
        splitHtml = function (doc, base, html) {
            var reHEAD = /<\s*head[^>]*>([\s\S]*)<\s*\/\s*head\s*>/i,
                reBODY = /<\s*body[^>]*>([\s\S]*)<\s*\/\s*body\s*>/i,
                match = reHEAD.exec(html),
                ret = {
                    head: doc.createElement('div'),
                    body: doc.createElement('div')
                };

            base = '<base href="' + base + '" />';

            if (match) {
                ret.head.innerHTML = base + match[1];
                html = html.slice(match.index + match[0].length);
            }
            match = reBODY.exec(html);
            if (match) {
                ret.body.innerHTML = base + match[1];
            }

            return ret;
        },

        /**
         * Import har file
         * @param doct host document to support dom level 0 methods
         * @param har the har object
         */
        run = function (doct, har, ruleset) {
            var i, j, page, pageId, entry, comp, doc, len, lenJ, split, status,
                content, baseHref, base, onloadTimestamp, cset, html, tDone,
                comps, favicons, favicon, type, response, head, body, score,
                yscontext = new YSLOW.context(doct),
                log = har.log || {},
                pages = log.pages || [],
                entries = log.entries || [];
                
            // pages loop (first page only for now)
            // TODO: think of a way to analyze all pages
            for (i = 0, len = (pages.length ? 1 : 0); i < len; i += 1) {
                page = pages[i];
                pageId = page.id;
                tDone = page.pageTimings.onLoad || 0;
                onloadTimestamp = new Date((new Date(page.startedDateTime))
                    .getTime() + tDone);
                comps = [];

                // page entries loop
                for (j = 0, lenJ = entries.length; j < lenJ; j += 1) {
                    entry = entries[j];
                    response = entry.response;
                    status = parseInt(response.status, 10);
                    content = response.content;
                    type = (content.mimeType || '').toLowerCase();
                    idx = type.indexOf(';');
                    if (idx > -1) {
                        type = type.slice(0, idx);
                    }
                    type = types[type];

                    if (status === 301 || status === 302) {
                        type = 'redirect';
                    }

                    if (!type || status === 204 || entry.pageref !== pageId) {
                        continue;
                    }

                    // get page info
                    // TODO: find a better way to get multiple docs
                    if (!baseHref && type === 'doc') {
                        baseHref = entry.request.url;
                        html = content.text;
                        base = YSLOW.util.makeAbsoluteUrl('', baseHref);
                        doc = doct.createElement('div');
                        doc.innerHTML = '<base href="' + base + '" />' + html;

                        cset = new YSLOW.ComponentSet(doc, onloadTimestamp);
                    }

                    comps.push({
                        type: type,
                        href: entry.request.url,
                        entry: entry
                    });
                }

                // use empty doc if none found (redirect chain with no content)
                if (!cset) {
                    doc = doct.createElement('div');
                    cset = new YSLOW.ComponentSet(doc, onloadTimestamp);
                    entry = entries.length && entries[0];
                    baseHref = entry && entry.request.url;
                    cset.doc_comp = {
                        url: baseHref
                    };
                }

                // build YSlow component set
                for (j = 0, lenJ = comps.length; j < lenJ; j += 1) {
                    comp = comps[j]; 
                    cset.addComponent(comp.href, comp.type, baseHref, {
                            entry: comp.entry,
                            comp: comp
                        }
                    );
                }
            }

            // if no doc was provided return error
            if (!doc) {
                throw new Error('No document found');
                return;
            }

            // set favicon
            favicons = YSLOW.peeler.findFavicon(doc, baseHref);
            comps = cset.components;
            for (i = 0, len = favicons.length; i < len; i += 1) {
                favicon = favicons[i];
                for (j = 0, lenJ = comps.length; j < lenJ; j += 1) {
                    comp = comps[j];
                    if (comp.url === favicon.href) {
                        comp.type = 'favicon';
                    }  
                }
            }

            // refinement
            if (base && html) {
                split = splitHtml(doct, base, html);
                head = split.head;
                body = split.body;
                setCssImages(cset);
                cset.inline = YSLOW.util.getInlineTags(null, head, body);
                cset.domElementsCount = YSLOW.util.countDOMElements(doc);
                cset.cookies = cset.doc_comp.cookie;
                cset.components = YSLOW.util.setInjected(doc,
                    cset.components, cset.doc_comp.body);
            }

            // run analysis
            yscontext.component_set = cset;
            YSLOW.controller.lint(doc, yscontext, ruleset || 'ydefault');
            yscontext.result_set.url = baseHref;
            score = yscontext.PAGE.overallScore;
            yscontext.PAGE.t_done = tDone;
            yscontext.collectStats();

            return {
                score: Math.round(score),
                resultSet: yscontext.result_set,
                componentSet: cset,
                grade: YSLOW.util.prettyScore(score),
                url: baseHref,
                context: yscontext
            };
        };

    // expose to YSLOW
    YSLOW.harImporter = {
        run: run
    };
}());
/**
 * Copyright (c) 2012, Yahoo! Inc.  All rights reserved.
 * Copyright (c) 2013, Marcel Duran and other contributors. All rights reserved.
 * Copyrights licensed under the New BSD License. See the accompanying LICENSE file for terms.
 */

if (typeof exports !== 'undefined') {
    exports.YSLOW = YSLOW;
}
