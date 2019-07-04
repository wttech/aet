'use strict';

const envData = require('../data/env.json');
const waitHelper = require('../helpers/wait-helper.js');
const testPages = require('../data/pages_list.json');

function AbstractPage() {
    const params = envData.env[browser.params.env];

    this.domain = "AET";

    this.open = function (pageName, verifyTitle = true, params = '') {
        browser.driver.manage().window().maximize();
        if (pageName) {
            const url = this.getFullUrl(pageName) + params;
            browser.get(url);
            if (verifyTitle) {
                expect(browser.getTitle()).toBe(this.getPageTitle(pageName));
            }
        } else {
            const url = this.getFullUrl();
            browser.get(url);
        }
        return this;
    };

    this.getDomain = function () {
        return params[this.domain];
    };

    this.setDomain = function (domain) {
        this.domain = domain;
    };

    this.getPagePath = function () {
        return this.pagePath;
    };

    this.getFullUrl = function () {
        if (arguments.length == 0) {
            return this.getDomain().concat(this.pagePath);
        } else {
            return this.getDomain().concat(testPages[arguments[0]].url);
        }
    };

    this.getPageTitle = function (key) {
        return testPages[key].title;
    };

    this.getTitle = function () {
        return browser.getTitle();
    };

    this.isLoaded = function (pageTitle) {
        return waitHelper.forPageToLoad(pageTitle, 'long');
    };
}

module.exports = new AbstractPage();
