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
/*
* based on https://github.com/yargalot/AccessSniff version 3.2.0
* changed scope of variable to window
* added HTMLCS.getMessagesJSON()
* wrapped everything in IIFE to fix minifier syntax errors
*/
(function(arguments){
(function (root, factory) {
    if (typeof define === 'function' && define.amd) {
        define(['htmlcs'], factory);
    } else if (typeof exports === 'object') {
        module.exports = factory();
    } else {
        var exports = factory();
        for (var prop in exports) {
            root[prop] = exports[prop];
        }
    }
}(this, function () {
    var _global = {}

_global.HTMLCS_Section508 = {
    name: 'Section508',
    description: 'U.S. Section 508 Standard',
    sniffs: [
        'A',
        'B',
        'C',
        'D',
        'G',
        'H',
        'I',
        'J',
        'K',
        'L',
        'M',
        'N',
        'O',
        'P'
    ],
    getMsgInfo: function(code) {
        var msgCodeParts  = code.split('.', 3);
        var paragraph     = msgCodeParts[1].toLowerCase();

        var retval = [
            ['Section', '1194.22 (' + paragraph + ')']
        ];

        return retval;
    }
};

_global.HTMLCS_WCAG2AA = {
    name: 'WCAG2AA',
    description: 'Web Content Accessibility Guidelines (WCAG) 2.0 AA',
    sniffs: [
        {
            standard: 'WCAG2AAA',
            include: [
               'Principle1.Guideline1_1.1_1_1',
               'Principle1.Guideline1_2.1_2_1',
               'Principle1.Guideline1_2.1_2_2',
               'Principle1.Guideline1_2.1_2_4',
               'Principle1.Guideline1_2.1_2_5',
               'Principle1.Guideline1_3.1_3_1',
               'Principle1.Guideline1_3.1_3_1_A',
               'Principle1.Guideline1_3.1_3_2',
               'Principle1.Guideline1_3.1_3_3',
               'Principle1.Guideline1_4.1_4_1',
               'Principle1.Guideline1_4.1_4_2',
               'Principle1.Guideline1_4.1_4_3',
               'Principle1.Guideline1_4.1_4_3_F24',
               'Principle1.Guideline1_4.1_4_3_Contrast',
               'Principle1.Guideline1_4.1_4_4',
               'Principle1.Guideline1_4.1_4_5',
               'Principle2.Guideline2_1.2_1_1',
               'Principle2.Guideline2_1.2_1_2',
               'Principle2.Guideline2_2.2_2_1',
               'Principle2.Guideline2_2.2_2_2',
               'Principle2.Guideline2_3.2_3_1',
               'Principle2.Guideline2_4.2_4_1',
               'Principle2.Guideline2_4.2_4_2',
               'Principle2.Guideline2_4.2_4_3',
               'Principle2.Guideline2_4.2_4_4',
               'Principle2.Guideline2_4.2_4_5',
               'Principle2.Guideline2_4.2_4_6',
               'Principle2.Guideline2_4.2_4_7',
               'Principle3.Guideline3_1.3_1_1',
               'Principle3.Guideline3_1.3_1_2',
               'Principle3.Guideline3_2.3_2_1',
               'Principle3.Guideline3_2.3_2_2',
               'Principle3.Guideline3_2.3_2_3',
               'Principle3.Guideline3_2.3_2_4',
               'Principle3.Guideline3_3.3_3_1',
               'Principle3.Guideline3_3.3_3_2',
               'Principle3.Guideline3_3.3_3_3',
               'Principle3.Guideline3_3.3_3_4',
               'Principle4.Guideline4_1.4_1_1',
               'Principle4.Guideline4_1.4_1_2'
            ]
        }
    ],
    getMsgInfo: function(code) {
        return HTMLCS_WCAG2AAA.getMsgInfo(code);
    }
};

_global.HTMLCS_WCAG2A = {
    name: 'WCAG2A',
    description: 'Web Content Accessibility Guidelines (WCAG) 2.0 A',
    sniffs: [
        {
            standard: 'WCAG2AAA',
            include: [
               'Principle1.Guideline1_1.1_1_1',
               'Principle1.Guideline1_2.1_2_1',
               'Principle1.Guideline1_2.1_2_2',
               'Principle1.Guideline1_2.1_2_3',
               'Principle1.Guideline1_3.1_3_1',
               'Principle1.Guideline1_3.1_3_1_A',
               'Principle1.Guideline1_3.1_3_2',
               'Principle1.Guideline1_3.1_3_3',
               'Principle1.Guideline1_4.1_4_1',
               'Principle1.Guideline1_4.1_4_2',
               'Principle2.Guideline2_1.2_1_1',
               'Principle2.Guideline2_1.2_1_2',
               'Principle2.Guideline2_2.2_2_1',
               'Principle2.Guideline2_2.2_2_2',
               'Principle2.Guideline2_3.2_3_1',
               'Principle2.Guideline2_4.2_4_1',
               'Principle2.Guideline2_4.2_4_2',
               'Principle2.Guideline2_4.2_4_3',
               'Principle2.Guideline2_4.2_4_4',
               'Principle3.Guideline3_1.3_1_1',
               'Principle3.Guideline3_2.3_2_1',
               'Principle3.Guideline3_2.3_2_2',
               'Principle3.Guideline3_3.3_3_1',
               'Principle3.Guideline3_3.3_3_2',
               'Principle4.Guideline4_1.4_1_1',
               'Principle4.Guideline4_1.4_1_2'
            ]
        }
    ],
    getMsgInfo: function(code) {
        return HTMLCS_WCAG2AAA.getMsgInfo(code);
    }
};

_global.HTMLCS_WCAG2AAA = {
    name: 'WCAG2AAA',
    description: 'Web Content Accessibility Guidelines (WCAG) 2.0 AAA',
    sniffs: [
        'Principle1.Guideline1_1.1_1_1',
        'Principle1.Guideline1_2.1_2_1',
        'Principle1.Guideline1_2.1_2_2',
        'Principle1.Guideline1_2.1_2_4',
        'Principle1.Guideline1_2.1_2_5',
        'Principle1.Guideline1_2.1_2_6',
        'Principle1.Guideline1_2.1_2_7',
        'Principle1.Guideline1_2.1_2_8',
        'Principle1.Guideline1_2.1_2_9',
        'Principle1.Guideline1_3.1_3_1',
        'Principle1.Guideline1_3.1_3_1_AAA',
        'Principle1.Guideline1_3.1_3_2',
        'Principle1.Guideline1_3.1_3_3',
        'Principle1.Guideline1_4.1_4_1',
        'Principle1.Guideline1_4.1_4_2',
        'Principle1.Guideline1_4.1_4_3_F24',
        'Principle1.Guideline1_4.1_4_3_Contrast',
        'Principle1.Guideline1_4.1_4_6',
        'Principle1.Guideline1_4.1_4_7',
        'Principle1.Guideline1_4.1_4_8',
        'Principle1.Guideline1_4.1_4_9',
        'Principle2.Guideline2_1.2_1_1',
        'Principle2.Guideline2_1.2_1_2',
        'Principle2.Guideline2_2.2_2_2',
        'Principle2.Guideline2_2.2_2_3',
        'Principle2.Guideline2_2.2_2_4',
        'Principle2.Guideline2_2.2_2_5',
        'Principle2.Guideline2_3.2_3_2',
        'Principle2.Guideline2_4.2_4_1',
        'Principle2.Guideline2_4.2_4_2',
        'Principle2.Guideline2_4.2_4_3',
        'Principle2.Guideline2_4.2_4_5',
        'Principle2.Guideline2_4.2_4_6',
        'Principle2.Guideline2_4.2_4_7',
        'Principle2.Guideline2_4.2_4_8',
        'Principle2.Guideline2_4.2_4_9',
        'Principle3.Guideline3_1.3_1_1',
        'Principle3.Guideline3_1.3_1_2',
        'Principle3.Guideline3_1.3_1_3',
        'Principle3.Guideline3_1.3_1_4',
        'Principle3.Guideline3_1.3_1_5',
        'Principle3.Guideline3_1.3_1_6',
        'Principle3.Guideline3_2.3_2_1',
        'Principle3.Guideline3_2.3_2_2',
        'Principle3.Guideline3_2.3_2_3',
        'Principle3.Guideline3_2.3_2_4',
        'Principle3.Guideline3_2.3_2_5',
        'Principle3.Guideline3_3.3_3_1',
        'Principle3.Guideline3_3.3_3_2',
        'Principle3.Guideline3_3.3_3_3',
        'Principle3.Guideline3_3.3_3_5',
        'Principle3.Guideline3_3.3_3_6',
        'Principle4.Guideline4_1.4_1_1',
        'Principle4.Guideline4_1.4_1_2'
    ],
    getMsgInfo: function(code) {
        var principles = {
            'Principle1': {
                name: 'Perceivable',
                link: 'http://www.w3.org/TR/WCAG20/#perceivable'
               },
            'Principle2': {
                name: 'Operable',
                link: 'http://www.w3.org/TR/WCAG20/#operable'
               },
            'Principle3': {
                name: 'Understandable',
                link: 'http://www.w3.org/TR/WCAG20/#understandable'
               },
            'Principle4': {
                name: 'Robust',
                link: 'http://www.w3.org/TR/WCAG20/#robust'
               }
        }

        /**
         * List of success criteria, their links in the WCAG20 doc, and their
         * "priority" (to use a WCAG1 term)... priority 1 = single-A, 3 = triple-A.
         *
         * Priority 0 indicates a conformance requirement. CR1 isn't shown because
         * all it says is "to conform to each level, it must pass all at that level".
         */
        var successCritList = {
            'CR2': {
                name: 'Full pages',
                landmark: 'cc2',
                priority: 0,
            },
            'CR3': {
                name: 'Complete processes',
                landmark: 'cc3',
                priority: 0,
            },
            'CR4': {
                name: 'Only Accessibility-Supported Ways of Using Technologies',
                landmark: 'cc4',
                priority: 0,
            },
            'CR5': {
                name: 'Non-Interference',
                landmark: 'cc5',
                priority: 0,
            },
            '1.1.1': {
                name: 'Non-Text Content',
                landmark: 'text-equiv-all',
                priority: 1,
            },
            '1.2.1': {
                name: 'Audio-only and Video-only (Prerecorded)',
                landmark: 'media-equiv-av-only-alt',
                priority: 1,
            },
            '1.2.2': {
                name: 'Captions (Prerecorded)',
                landmark: 'media-equiv-captions',
                priority: 1,
            },
            '1.2.3': {
                name: 'Audio Description or Media Alternative (Prerecorded)',
                landmark: 'media-equiv-audio-desc',
                priority: 1,
            },
            '1.2.4': {
                name: 'Captions (Live)',
                landmark: 'media-equiv-captions',
                priority: 2,
            },
            '1.2.5': {
                name: 'Audio Description (Prerecorded)',
                landmark: 'media-equiv-audio-desc',
                priority: 2,
            },
            '1.2.6': {
                name: 'Sign Language (Prerecorded)',
                landmark: 'media-equiv-sign',
                priority: 3,
            },
            '1.2.7': {
                name: 'Extended Audio Description (Prerecorded)',
                landmark: 'media-equiv-extended-ad',
                priority: 3,
            },
            '1.2.8': {
                name: 'Media Alternative (Prerecorded)',
                landmark: 'media-equiv-text-doc',
                priority: 3,
            },
            '1.2.9': {
                name: 'Audio-only (Live)',
                landmark: 'media-equiv-live-audio-only',
                priority: 3,
            },
            '1.3.1': {
                name: 'Info and Relationships',
                landmark: 'content-structure-separation-programmatic',
                priority: 1,
            },
            '1.3.2': {
                name: 'Meaningful Sequence',
                landmark: 'content-structure-separation-sequence',
                priority: 1,
            },
            '1.3.3': {
                name: 'Sensory Characteristics',
                landmark: 'content-structure-separation-understanding',
                priority: 1,
            },
            '1.4.1': {
                name: 'Use of Colour',
                landmark: 'visual-audio-contrast-without-color',
                priority: 1,
            },
            '1.4.2': {
                name: 'Audio Control',
                landmark: 'visual-audio-contrast-dis-audio',
                priority: 1,
            },
            '1.4.3': {
                name: 'Contrast (Minimum)',
                landmark: 'visual-audio-contrast-contrast',
                priority: 1,
            },
            '1.4.4': {
                name: 'Resize Text',
                landmark: 'visual-audio-contrast-scale',
                priority: 1,
            },
            '1.4.5': {
                name: 'Images of Text',
                landmark: 'visual-audio-contrast-text-presentation',
                priority: 1,
            },
            '1.4.6': {
                name: 'Contrast (Enhanced)',
                landmark: 'visual-audio-contrast7',
                priority: 3,
            },
            '1.4.7': {
                name: 'Low or No Background Audio',
                landmark: 'visual-audio-contrast-noaudio',
                priority: 3,
            },
            '1.4.8': {
                name: 'Visual Presentation',
                landmark: 'visual-audio-contrast-visual-presentation',
                priority: 3,
            },
            '1.4.9': {
                name: 'Images of Text (No Exception)',
                landmark: 'visual-audio-contrast-text-images',
                priority: 3,
            },
            '2.1.1': {
                name: 'Keyboard',
                landmark: 'keyboard-operation-keyboard-operable',
                priority: 1,
            },
            '2.1.2': {
                name: 'No Keyboard Trap',
                landmark: 'keyboard-operation-trapping',
                priority: 1,
            },
            '2.1.3': {
                name: 'Keyboard (No Exception)',
                landmark: 'keyboard-operation-all-funcs',
                priority: 3,
            },
            '2.2.1': {
                name: 'Timing Adjustable',
                landmark: 'time-limits-required-behaviors',
                priority: 1,
            },
            '2.2.2': {
                name: 'Pause, Stop, Hide',
                landmark: 'time-limits-pause',
                priority: 1,
            },
            '2.2.3': {
                name: 'No Timing',
                landmark: 'time-limits-no-exceptions',
                priority: 3,
            },
            '2.2.4': {
                name: 'Interruptions',
                landmark: 'time-limits-postponed',
                priority: 3,
            },
            '2.2.5': {
                name: 'Re-authenticating',
                landmark: 'time-limits-server-timeout',
                priority: 3,
            },
            '2.3.1': {
                name: 'Three Flashes or Below Threshold',
                landmark: 'seizure-does-not-violate',
                priority: 1,
            },
            '2.3.2': {
                name: 'Three Flashes',
                landmark: 'seizure-three-times',
                priority: 3,
            },
            '2.4.1': {
                name: 'Bypass Blocks',
                landmark: 'navigation-mechanisms-skip',
                priority: 1,
            },
            '2.4.2': {
                name: 'Page Titled',
                landmark: 'navigation-mechanisms-title',
                priority: 1,
            },
            '2.4.3': {
                name: 'Focus Order',
                landmark: 'navigation-mechanisms-focus-order',
                priority: 1,
            },
            '2.4.4': {
                name: 'Link Purpose (In Context)',
                landmark: 'navigation-mechanisms-refs',
                priority: 1,
            },
            '2.4.5': {
                name: 'Multiple Ways',
                landmark: 'navigation-mechanisms-mult-loc',
                priority: 2,
            },
            '2.4.6': {
                name: 'Headings and Labels',
                landmark: 'navigation-mechanisms-descriptive',
                priority: 2,
            },
            '2.4.7': {
                name: 'Focus Visible',
                landmark: 'navigation-mechanisms-focus-visible',
                priority: 2,
            },
            '2.4.8': {
                name: 'Location',
                landmark: 'navigation-mechanisms-location',
                priority: 3,
            },
            '2.4.9': {
                name: 'Link Purpose (Link Only)',
                landmark: 'navigation-mechanisms-link',
                priority: 3,
            },
            '2.4.10': {
                name: 'Section Headings',
                landmark: 'navigation-mechanisms-headings',
                priority: 3,
            },
            '3.1.1': {
                name: 'Language of Page',
                landmark: 'meaning-doc-lang-id',
                priority: 1,
            },
            '3.1.2': {
                name: 'Language of Parts',
                landmark: 'meaning-other-lang-id',
                priority: 2,
            },
            '3.1.3': {
                name: 'Unusual Words',
                landmark: 'meaning-idioms',
                priority: 3,
            },
            '3.1.4': {
                name: 'Abbreviations',
                landmark: 'meaning-located',
                priority: 3,
            },
            '3.1.5': {
                name: 'Reading Level',
                landmark: 'meaning-supplements',
                priority: 3,
            },
            '3.1.6': {
                name: 'Pronunciation',
                landmark: 'meaning-pronunciation',
                priority: 3,
            },
            '3.2.1': {
                name: 'On Focus',
                landmark: 'consistent-behavior-receive-focus',
                priority: 1,
            },
            '3.2.2': {
                name: 'On Input',
                landmark: 'consistent-behavior-unpredictable-change',
                priority: 1,
            },
            '3.2.3': {
                name: 'Consistent Navigation',
                landmark: 'consistent-behavior-consistent-locations',
                priority: 2,
            },
            '3.2.4': {
                name: 'Consistent Navigation',
                landmark: 'consistent-behavior-consistent-functionality',
                priority: 2,
            },
            '3.2.5': {
                name: 'Change on Request',
                landmark: 'consistent-behavior-no-extreme-changes-context',
                priority: 3,
            },
            '3.3.1': {
                name: 'Error Identification',
                landmark: 'minimize-error-identified',
                priority: 1,
            },
            '3.3.2': {
                name: 'Labels or Instructions',
                landmark: 'minimize-error-cues',
                priority: 1,
            },
            '3.3.3': {
                name: 'Error Suggestion',
                landmark: 'minimize-error-suggestions',
                priority: 2,
            },
            '3.3.4': {
                name: 'Error Prevention (Legal, Financial, Data)',
                landmark: 'minimize-error-reversible',
                priority: 2,
            },
            '3.3.5': {
                name: 'Help',
                landmark: 'minimize-error-context-help',
                priority: 3,
            },
            '3.3.6': {
                name: 'Error Prevention (All)',
                landmark: 'minimize-error-reversible-all',
                priority: 3,
            },
            '4.1.1': {
                name: 'Parsing',
                landmark: 'ensure-compat-parses',
                priority: 1,
            },
            '4.1.2': {
                name: 'Name, Role, Value',
                landmark: 'ensure-compat-rsv',
                priority: 1,
            },
        };

        var msgCodeParts  = code.split('.', 5);
        var principle     = msgCodeParts[1];
        var successCrit   = msgCodeParts[3].split('_').slice(0, 3).join('.');
        var techniques    = msgCodeParts[4].split(',');
        var techniquesStr = [];

        for (var i = 0; i < techniques.length; i++) {
            techniques[i]  = techniques[i].split('.');
            techniquesStr.push('<a href="http://www.w3.org/TR/WCAG20-TECHS/' + techniques[i][0] + '" target="_blank">' + techniques[i][0] + '</a>');
        }

        var successCritStr = ['<a href="http://www.w3.org/TR/WCAG20/#' + successCritList[successCrit].landmark, '" target="_blank">', successCrit, ': ', successCritList[successCrit].name, '</a>'].join('');
        var principleStr   = ['<a href="', principles[principle].link, '" target="_blank">', principles[principle].name, '</a>'].join('');
        var retval = [
            ['Success Criterion', successCritStr],
            ['Suggested Techniques', techniquesStr.join(' ')]
        ];

        return retval;
    }
};

_global.HTMLCS_Section508_Sniffs_A = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return [
            '_top',
            'img',
            'object',
            'bgsound',
            'audio'
        ];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        if (element === top) {
            this.addNullAltTextResults(top);
            this.addMediaAlternativesResults(top);
        } else {
            var nodeName = element.nodeName.toLowerCase();
            if ((nodeName === 'object') || (nodeName === 'bgsound') || (nodeName === 'audio')) {
                // Audio transcript notice. Yes, this is in A rather than B, since
                // audio is not considered "multimedia" (roughly equivalent to a
                // "synchronised media" presentation in WCAG 2.0). It is non-text,
                // though, so a transcript is required.
                HTMLCS.addMessage(HTMLCS.NOTICE, element, 'For multimedia containing audio only, ensure an alternative is available, such as a full text transcript.', 'Audio');
            }
        }
    },

    /**
     * Test for missing or null alt text in certain elements.
     *
     * Tested elements are:
     * - IMG elements
     * - INPUT elements with type="image" (ie. image submit buttons).
     * - AREA elements (ie. in client-side image maps).
     *
     * @param {DOMNode} element The element to test.
     *
     * @returns {Object} A structured list of errors.
     */
    testNullAltText: function(top)
    {
        var errors = {
            img: {
                generalAlt: [],
                missingAlt: [],
                ignored: [],
                nullAltWithTitle: [],
                emptyAltInLink: []
            },
            inputImage: {
                generalAlt: [],
                missingAlt: []
            },
            area: {
                generalAlt: [],
                missingAlt: []
            }
        };

        elements = HTMLCS.util.getAllElements(top, 'img, area, input[type="image"]');

        for (var el = 0; el < elements.length; el++) {
            var element = elements[el];

            var nodeName      = element.nodeName.toLowerCase();
            var linkOnlyChild = false;
            var missingAlt    = false;
            var nullAlt       = false;

            if (element.parentNode.nodeName.toLowerCase() === 'a') {
                var prevNode = HTMLCS.util.getPreviousSiblingElement(element, null);
                var nextNode = HTMLCS.util.getNextSiblingElement(element, null);

                if ((prevNode === null) && (nextNode === null)) {
                    var textContent = element.parentNode.textContent;

                    if (element.parentNode.textContent !== undefined) {
                        var textContent = element.parentNode.textContent;
                    } else {
                        // Keep IE8 happy.
                        var textContent = element.parentNode.innerText;
                    }

                    if (HTMLCS.util.isStringEmpty(textContent) === true) {
                        linkOnlyChild = true;
                    }
                }
            }//end if

            if (element.hasAttribute('alt') === false) {
                missingAlt = true;
            } else if (!element.getAttribute('alt') || HTMLCS.util.isStringEmpty(element.getAttribute('alt')) === true) {
                nullAlt = true;
            }

            // Now determine which test(s) should fire.
            switch (nodeName) {
                case 'img':
                    if ((linkOnlyChild === true) && ((missingAlt === true) || (nullAlt === true))) {
                        // Img tags cannot have an empty alt text if it is the
                        // only content in a link (as the link would not have a text
                        // alternative).
                        errors.img.emptyAltInLink.push(element.parentNode);
                    } else if (missingAlt === true) {
                        errors.img.missingAlt.push(element);
                    } else if (nullAlt === true) {
                        if ((element.hasAttribute('title') === true) && (HTMLCS.util.isStringEmpty(element.getAttribute('title')) === false)) {
                            // Title attribute present and not empty. This is wrong when
                            // an image is marked as ignored.
                            errors.img.nullAltWithTitle.push(element);
                        } else {
                            errors.img.ignored.push(element);
                        }
                    } else {
                        errors.img.generalAlt.push(element);
                    }
                break;

                case 'input':
                    // Image submit buttons.
                    if ((missingAlt === true) || (nullAlt === true)) {
                        errors.inputImage.missingAlt.push(element);
                    } else {
                        errors.inputImage.generalAlt.push(element);
                    }
                break;

                case 'area':
                    // Area tags in a client-side image map.
                    if ((missingAlt === true) || (nullAlt === true)) {
                        errors.area.missingAlt.push(element);
                    } else {
                        errors.inputImage.generalAlt.push(element);
                    }
                break;

                default:
                    // No other tags defined.
                break;
            }//end switch
        }//end for

        return errors;
    },

    /**
     * Driver function for the null alt text tests.
     *
     * This takes the generic result given by the alt text testing functions
     * (located in WCAG 2.0 SC 1.1.1), and converts them into Section 508-specific
     * messages.
     *
     * @param {DOMNode} element The element to test.
     */
    addNullAltTextResults: function(top)
    {
        var errors = this.testNullAltText(top);

        for (var i = 0; i < errors.img.emptyAltInLink.length; i++) {
            HTMLCS.addMessage(HTMLCS.ERROR, errors.img.emptyAltInLink[i], 'Img element is the only content of the link, but is missing alt text. The alt text should describe the purpose of the link.', 'Img.EmptyAltInLink');
        }

        for (var i = 0; i < errors.img.nullAltWithTitle.length; i++) {
            HTMLCS.addMessage(HTMLCS.ERROR, errors.img.nullAltWithTitle[i], 'Img element with empty alt text must have absent or empty title attribute.', 'Img.NullAltWithTitle');
        }

        for (var i = 0; i < errors.img.ignored.length; i++) {
            HTMLCS.addMessage(HTMLCS.WARNING, errors.img.ignored[i], 'Img element is marked so that it is ignored by Assistive Technology.', 'Img.Ignored');
        }

        for (var i = 0; i < errors.img.missingAlt.length; i++) {
            HTMLCS.addMessage(HTMLCS.ERROR, errors.img.missingAlt[i], 'Img element missing an alt attribute. Use the alt attribute to specify a short text alternative.', 'Img.MissingAlt');
        }

        for (var i = 0; i < errors.img.generalAlt.length; i++) {
            HTMLCS.addMessage(HTMLCS.NOTICE, errors.img.generalAlt[i], 'Ensure that the img element\'s alt text serves the same purpose and presents the same information as the image.', 'Img.GeneralAlt');
        }

        for (var i = 0; i < errors.inputImage.missingAlt.length; i++) {
            HTMLCS.addMessage(HTMLCS.ERROR, errors.inputImage.missingAlt[i], 'Image submit button missing an alt attribute. Specify a text alternative that describes the button\'s function, using the alt attribute.', 'InputImage.MissingAlt');
        }

        for (var i = 0; i < errors.inputImage.generalAlt.length; i++) {
            HTMLCS.addMessage(HTMLCS.NOTICE, errors.inputImage.generalAlt[i], 'Ensure that the image submit button\'s alt text identifies the purpose of the button.', 'InputImage.GeneralAlt');
        }

        for (var i = 0; i < errors.area.missingAlt.length; i++) {
            HTMLCS.addMessage(HTMLCS.ERROR, errors.area.missingAlt[i], 'Area element in an image map missing an alt attribute. Each area element must have a text alternative that describes the function of the image map area.', 'Area.MissingAlt');
        }

        for (var i = 0; i < errors.area.generalAlt.length; i++) {
            HTMLCS.addMessage(HTMLCS.NOTICE, errors.area.generalAlt[i], 'Ensure that the area element\'s text alternative serves the same purpose as the part of image map image it references.', 'Area.GeneralAlt');
        }
    },

    testMediaTextAlternatives: function(top)
    {
        var errors = {
            object: {
                missingBody: [],
                generalAlt: []
            },
            applet: {
                missingBody: [],
                missingAlt: [],
                generalAlt: []
            }
        };

        var elements = HTMLCS.util.getAllElements(top, 'object');

        for (var el = 0; el < elements.length; el++) {
            var element  = elements[el];
            var nodeName = element.nodeName.toLowerCase();

            var childObject = element.querySelector('object');

            // If we have an object as our alternative, skip it. Pass the blame onto
            // the child.
            if (childObject === null) {
                var textAlt = HTMLCS.util.getElementTextContent(element, true);
                if (textAlt === '') {
                    errors.object.missingBody.push(element);
                } else {
                    errors.object.generalAlt.push(element);
                }
            }//end if
        }//end if

        var elements = HTMLCS.util.getAllElements(top, 'applet');

        for (var el = 0; el < elements.length; el++) {
            // Test firstly for whether we have an object alternative.
            var childObject = element.querySelector('object');
            var hasError    = false;

            // If we have an object as our alternative, skip it. Pass the blame onto
            // the child. (This is a special case: those that don't understand APPLET
            // may understand OBJECT, but APPLET shouldn't be nested.)
            if (childObject === null) {
                var textAlt = HTMLCS.util.getElementTextContent(element, true);
                if (HTMLCS.util.isStringEmpty(textAlt) === true) {
                    errors.applet.missingBody.push(element);
                    hasError = true;
                }
            }//end if

            var altAttr = element.getAttribute('alt') || '';
            if (HTMLCS.util.isStringEmpty(altAttr) === true) {
                errors.applet.missingAlt.push(element);
                hasError = true;
            }

            if (hasError === false) {
                // No error? Remind of obligations about equivalence of alternatives.
                errors.applet.generalAlt.push(element);
            }
        }//end if

        return errors;
    },

    /**
     * Driver function for the media alternative (object/applet) tests.
     *
     * This takes the generic result given by the media alternative testing function
     * (located in WCAG 2.0 SC 1.1.1), and converts them into Section
     * 508-specific messages.
     *
     * @param {DOMNode} element The element to test.
     */
    addMediaAlternativesResults: function(top)
    {
        var errors = HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_1_1_1_1.testMediaTextAlternatives(top);

        for (var i = 0; i < errors.object.missingBody.length; i++) {
            HTMLCS.addMessage(HTMLCS.ERROR, errors.object.missingBody[i], 'Object elements must contain a text alternative after all other alternatives are exhausted.', 'Object.MissingBody');
        }

        for (var i = 0; i < errors.object.generalAlt.length; i++) {
            HTMLCS.addMessage(HTMLCS.NOTICE, errors.object.generalAlt[i], 'Check that short (and if appropriate, long) text alternatives are available for non-text content that serve the same purpose and present the same information.', 'Object.GeneralAlt');
        }

        for (var i = 0; i < errors.applet.missingBody.length; i++) {
            HTMLCS.addMessage(HTMLCS.ERROR, errors.applet.missingBody[i], 'Applet elements must contain a text alternative in the element\'s body, for browsers without support for the applet element.', 'Applet.MissingBody');
        }

        for (var i = 0; i < errors.applet.missingAlt.length; i++) {
            HTMLCS.addMessage(HTMLCS.ERROR, errors.applet.missingAlt[i], 'Applet elements must contain an alt attribute, to provide a text alternative to browsers supporting the element but are unable to load the applet.', 'Applet.MissingAlt');
        }

        for (var i = 0; i < errors.applet.generalAlt.length; i++) {
            HTMLCS.addMessage(HTMLCS.NOTICE, errors.applet.generalAlt[i], 'Check that short (and if appropriate, long) text alternatives are available for non-text content that serve the same purpose and present the same information.', 'Applet.GeneralAlt');
        }
    }
};

_global.HTMLCS_Section508_Sniffs_B = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return [
            'object',
            'applet',
            'embed',
            'video'
        ];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        var nodeName = element.nodeName.toLowerCase();
        HTMLCS.addMessage(HTMLCS.NOTICE, element, 'For multimedia containing video, ensure a synchronised audio description or text alternative for the video portion is provided.', 'Video');
        HTMLCS.addMessage(HTMLCS.NOTICE, element, 'For multimedia containing synchronised audio and video, ensure synchronised captions are provided for the audio portion.', 'Captions');

    }
};

_global.HTMLCS_Section508_Sniffs_C = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['_top'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, top, 'Ensure that any information conveyed using colour alone is also available without colour, such as through context or markup.', 'Colour');

    }
};

_global.HTMLCS_Section508_Sniffs_D = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['_top'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        if (element === top) {
            HTMLCS.addMessage(HTMLCS.NOTICE, top, 'Ensure that content is ordered in a meaningful sequence when linearised, such as when style sheets are disabled.', 'Linearised');
            this.testPresentationMarkup(top);
            this.testHeadingOrder(top);

            // Look for any script elements, and fire off another notice regarding
            // potentially hidden text (eg. "click to expand" sections). For instance,
            // such text should be stored semantically in the page, not loaded into
            // a container through AJAX (and thus not accessible with scripting off).
            var hasScript = HTMLCS.util.getAllElements(top, 'script, link[rel="stylesheet"]');
            if (hasScript.length > 0) {
                HTMLCS.addMessage(HTMLCS.NOTICE, top, 'If content is hidden and made visible using scripting (such as "click to expand" sections), ensure this content is readable when scripts and style sheets are disabled.', 'HiddenText');
            }
        }
    },

    /**
     * Test for the use of presentational elements.
     *
     * @param [DOMNode] top The top element of the tested code.
     */
    testPresentationMarkup: function(top)
    {
        _global.HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_3_1_3_1.testPresentationMarkup(top);
    },

    testHeadingOrder: function(top) {
        var lastHeading = 0;
        var headings    = HTMLCS.util.getAllElements(top, 'h1, h2, h3, h4, h5, h6');

        for (var i = 0; i < headings.length; i++) {
            var headingNum = parseInt(headings[i].nodeName.substr(1, 1));
            if (headingNum - lastHeading > 1) {
                var exampleMsg = 'should be an h' + (lastHeading + 1) + ' to be properly nested';
                if (lastHeading === 0) {
                    // If last heading is empty, we are at document top and we are
                    // expecting a H1, generally speaking.
                    exampleMsg = 'appears to be the primary document heading, so should be an h1 element';
                }

                HTMLCS.addMessage(HTMLCS.ERROR, headings[i], 'The heading structure is not logically nested. This h' + headingNum + ' element ' + exampleMsg + '.', 'HeadingOrder');
            }

            lastHeading = headingNum;
        }
    }

};

_global.HTMLCS_Section508_Sniffs_G = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['table'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        // If no table headers, emit notice about the table.
        if (HTMLCS.util.isLayoutTable(element) === true) {
            HTMLCS.addMessage(HTMLCS.NOTICE, element, 'This table has no headers. If this is a data table, ensure row and column headers are identified using th elements.', 'TableHeaders');
        }
    }

};

_global.HTMLCS_Section508_Sniffs_H = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['table'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(table, top)
    {
        var headersAttr = HTMLCS.util.testTableHeaders(table);

        // Incorrect usage of headers - error; emit always.
        for (var i = 0; i < headersAttr.wrongHeaders.length; i++) {
            HTMLCS.addMessage(HTMLCS.ERROR, headersAttr.wrongHeaders[i].element, 'Incorrect headers attribute on this td element. Expected "' + headersAttr.wrongHeaders[i].expected + '" but found "' + headersAttr.wrongHeaders[i].actual + '"', 'IncorrectHeadersAttr');
        }

        // Errors where headers are compulsory.
        if ((headersAttr.required === true) && (headersAttr.allowScope === false)) {
            if (headersAttr.used === false) {
                // Headers not used at all, and they are mandatory.
                HTMLCS.addMessage(HTMLCS.ERROR, table, 'The relationship between td elements and their associated th elements is not defined. As this table has multiple levels of th elements, you must use the headers attribute on td elements.', 'MissingHeadersAttrs');
            } else {
                // Missing TH IDs - error; emit at this stage only if headers are compulsory.
                if (headersAttr.missingThId.length > 0) {
                    HTMLCS.addMessage(HTMLCS.ERROR, table, 'Not all th elements in this table contain an id attribute. These cells should contain ids so that they may be referenced by td elements\' headers attributes.', 'MissingHeaderIds');
                }

                // Missing TD headers attributes - error; emit at this stage only if headers are compulsory.
                if (headersAttr.missingTd.length > 0) {
                    HTMLCS.addMessage(HTMLCS.ERROR, table, 'Not all td elements in this table contain a headers attribute. Each headers attribute should list the ids of all th elements associated with that cell.', 'IncompleteHeadersAttrs');
                }
            }//end if
        }//end if
    }

};

_global.HTMLCS_Section508_Sniffs_I = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return [
            'frame',
            'iframe',
            'object'
        ];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        var nodeName   = element.nodeName.toLowerCase();
        var hasTitle   = element.hasAttribute('title');
        var titleEmpty = true;

        if (hasTitle === true) {
            titleEmpty = HTMLCS.util.isStringEmpty(element.getAttribute('title'));
        }

        if (titleEmpty === true) {
            HTMLCS.addMessage(HTMLCS.ERROR, top, 'This ' + nodeName + ' element is missing title text. Frames should be titled with text that facilitates frame identification and navigation.', 'Frames');
        }
    }
};

_global.HTMLCS_Section508_Sniffs_J = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['_top'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        // The term in Sec. 508 is "flicker" rather than flash.
        HTMLCS.addMessage(HTMLCS.NOTICE, top, 'Check that no component of the content flickers at a rate of greater than 2 and less than 55 times per second.', 'Flicker');
    }
};

_global.HTMLCS_Section508_Sniffs_K = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['_top'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, top, 'If this page cannot be made compliant, a text-only page with equivalent information or functionality should be provided. The alternative page needs to be updated in line with this page\'s content.', 'AltVersion');
    }

};

_global.HTMLCS_Section508_Sniffs_L = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['_top'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        if (element === top) {
            this.addProcessLinksMessages(top);
            this.testKeyboard(top);
        }
    },

    addProcessLinksMessages: function(top)
    {
        var errors = this.processLinks(top);
        for (var i = 0; i < errors.emptyNoId.length; i++) {
            HTMLCS.addMessage(HTMLCS.ERROR, errors.emptyNoId[i], 'Anchor element found with no link content and no name and/or ID attribute.', 'EmptyAnchorNoId');
        }

        for (var i = 0; i < errors.placeholder.length; i++) {
            HTMLCS.addMessage(HTMLCS.WARNING, errors.placeholder[i], 'Anchor element found with link content, but no href, ID, or name attribute has been supplied.', 'PlaceholderAnchor');
        }

        for (var i = 0; i < errors.noContent.length; i++) {
            HTMLCS.addMessage(HTMLCS.ERROR, errors.noContent[i], 'Anchor element found with a valid href attribute, but no link content has been supplied.', 'NoContentAnchor');
        }
    },

    processLinks: function(top)
    {
        var errors   = {
            empty: [],
            emptyWithName: [],
            emptyNoId: [],
            noHref: [],
            placeholder: [],
            noContent: []
        };

        var elements = HTMLCS.util.getAllElements(top, 'a');

        for (var el = 0; el < elements.length; el++) {
            var element = elements[el];

            var nameFound = false;
            var hrefFound = false;
            var content   = HTMLCS.util.getElementTextContent(element);

            if ((element.hasAttribute('title') === true) && (/^\s*$/.test(element.getAttribute('title')) === false)) {
                nameFound = true;
            } else if (/^\s*$/.test(content) === false) {
                nameFound = true;
            }

            if ((element.hasAttribute('href') === true) && (/^\s*$/.test(element.getAttribute('href')) === false)) {
                hrefFound = true;
            }

            if (hrefFound === false) {
                // No href. We don't want these because, although they are commonly used
                // to create targets, they can be picked up by screen readers and
                // displayed to the user as empty links. A elements are defined by H91 as
                // having an (ARIA) role of "link", and using them as targets are
                // essentially misusing them. Place an ID on a parent element instead.
                if (/^\s*$/.test(content) === true) {
                    // Also no content. (eg. <a id=""></a> or <a name=""></a>)
                    if (element.hasAttribute('id') === true) {
                        errors.empty.push(element);
                    } else if (element.hasAttribute('name') === true) {
                        errors.emptyWithName.push(element);
                    } else {
                        errors.emptyNoId.push(element);
                    }
                } else {
                    // Giving a benefit of the doubt here - if a link has text and also
                    // an ID, but no href, it might be because it is being manipulated by
                    // a script.
                    if ((element.hasAttribute('id') === true) || (element.hasAttribute('name') === true)) {
                        errors.noHref.push(element);
                    } else {
                        // HTML5 allows A elements with text but no href, "for where a
                        // link might otherwise have been placed, if it had been relevant".
                        // Hence, thrown as a warning, not an error.
                        errors.placeholder.push(element);
                    }
                }//end if
            } else {
                if (/^\s*$/.test(content) === true) {
                    // Href provided, but no content.
                    // We only fire this message when there are no images in the content.
                    // A link around an image with no alt text is already covered in SC
                    // 1.1.1 (test H30).
                    if (element.querySelectorAll('img').length === 0) {
                        errors.noContent.push(element);
                    }
                }//end if
            }//end if
        }//end for

        return errors;
    },

    /**
     * Process mouse-specific functions.
     *
     * @param {DOMNode} top The top element of the tested code.
     */
    testKeyboard: function(top)
    {
        // Testing for elements that have explicit attributes for mouse-specific
        // events. Note: onclick is considered keyboard accessible, as it is actually
        // tied to the default action of a link or button - not merely a click.
        var dblClickEls = HTMLCS.util.getAllElements(top, '*[ondblclick]');
        for (var i = 0; i < dblClickEls.length; i++) {
            HTMLCS.addMessage(HTMLCS.WARNING, dblClickEls[i], 'Ensure the functionality provided by double-clicking on this element is available through the keyboard.', 'DblClick');
        }

        var mouseOverEls = HTMLCS.util.getAllElements(top, '*[onmouseover]');
        for (var i = 0; i < mouseOverEls.length; i++) {
            HTMLCS.addMessage(HTMLCS.WARNING, mouseOverEls[i], 'Ensure the functionality provided by mousing over this element is available through the keyboard; for instance, using the focus event.', 'MouseOver');
        }

        var mouseOutEls = HTMLCS.util.getAllElements(top, '*[onmouseout]');
        for (var i = 0; i < mouseOutEls.length; i++) {
            HTMLCS.addMessage(HTMLCS.WARNING, mouseOutEls[i], 'Ensure the functionality provided by mousing out of this element is available through the keyboard; for instance, using the blur event.', 'MouseOut');
        }

        var mouseMoveEls = HTMLCS.util.getAllElements(top, '*[onmousemove]');
        for (var i = 0; i < mouseMoveEls.length; i++) {
            HTMLCS.addMessage(HTMLCS.WARNING, mouseMoveEls[i], 'Ensure the functionality provided by moving the mouse on this element is available through the keyboard.', 'MouseMove');
        }

        var mouseDownEls = HTMLCS.util.getAllElements(top, '*[onmousedown]');
        for (var i = 0; i < mouseDownEls.length; i++) {
            HTMLCS.addMessage(HTMLCS.WARNING, mouseDownEls[i], 'Ensure the functionality provided by mousing down on this element is available through the keyboard; for instance, using the keydown event.', 'MouseDown');
        }

        var mouseUpEls = HTMLCS.util.getAllElements(top, '*[onmouseup]');
        for (var i = 0; i < mouseUpEls.length; i++) {
            HTMLCS.addMessage(HTMLCS.WARNING, mouseUpEls[i], 'Ensure the functionality provided by mousing up on this element is available through the keyboard; for instance, using the keyup event.', 'MouseUp');
        }
    }

};



_global.HTMLCS_Section508_Sniffs_M = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return [
            'object',
            'applet',
            'bgsound',
            'embed',
            'audio',
            'video'
        ];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, element, 'If external media requires a plugin or application to view, ensure a link is provided to a plugin or application that complies with Section 508 accessibility requirements for applications.', 'PluginLink');
    }

};



_global.HTMLCS_Section508_Sniffs_N = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['form'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        var nodeName = element.nodeName.toLowerCase();
        if (nodeName === 'form') {
            HTMLCS.addMessage(HTMLCS.NOTICE, element, 'If an input error is automatically detected in this form, check that the item(s) in error are identified and the error(s) are described to the user in text.', 'Errors');
            HTMLCS.addMessage(HTMLCS.NOTICE, element, 'Check that descriptive labels or instructions (including for required fields) are provided for user input in this form.', 'Labels');
            HTMLCS.addMessage(HTMLCS.NOTICE, element, 'Ensure that this form can be navigated using the keyboard and other accessibility tools.', 'KeyboardNav');
        }
    }

};



_global.HTMLCS_Section508_Sniffs_O = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return [
            '_top',
            'a',
            'area'
        ];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        if (element === top) {
            HTMLCS.addMessage(HTMLCS.NOTICE, top, 'Ensure that any common navigation elements can be bypassed; for instance, by use of skip links, header elements, or ARIA landmark roles.', 'SkipLinks');
        } else {
            if (element.hasAttribute('href') === true) {
                var href = element.getAttribute('href');
                href     = HTMLCS.util.trim(href);
                if ((href.length > 1) && (href.charAt(0) === '#')) {
                    var id = href.substr(1);

                    try {
                        var doc = top;
                        if (doc.ownerDocument) {
                            doc = doc.ownerDocument;
                        }

                        // First search for an element with the appropriate ID, then search for a
                        // named anchor using the name attribute.
                        var target = doc.getElementById(id);
                        if (target === null) {
                            target = doc.querySelector('a[name="' + id + '"]');
                        }

                        if ((target === null) || (HTMLCS.util.contains(top, target) === false)) {
                            if ((HTMLCS.isFullDoc(top) === true) || (top.nodeName.toLowerCase() === 'body')) {
                                HTMLCS.addMessage(HTMLCS.ERROR, element, 'This link points to a named anchor "' + id + '" within the document, but no anchor exists with that name.', 'NoSuchID');
                            } else {
                                HTMLCS.addMessage(HTMLCS.WARNING, element, 'This link points to a named anchor "' + id + '" within the document, but no anchor exists with that name in the fragment tested.', 'NoSuchIDFragment');
                            }
                        }
                    } catch (ex) {
                    }//end try
                }//end if
            }
        }
    }

};



_global.HTMLCS_Section508_Sniffs_P = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return [
            '_top',
            'meta'
        ];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        if (element === top) {
            HTMLCS.addMessage(HTMLCS.NOTICE, top, 'If a timed response is required on this page, alert the user and provide sufficient time to allow them to indicate that more time is required.', 'TimeLimit');
        } else {
            if (element.hasAttribute('http-equiv') === true) {
                if ((String(element.getAttribute('http-equiv'))).toLowerCase() === 'refresh') {
                    if (/^[1-9]\d*/.test(element.getAttribute('content').toLowerCase()) === true) {
                        if (/url=/.test(element.getAttribute('content').toLowerCase()) === true) {
                            // Redirect.
                            HTMLCS.addMessage(HTMLCS.ERROR, element, 'Meta refresh tag used to redirect to another page, with a time limit that is not zero. Users cannot control this time limit.', 'MetaRedirect');
                        } else {
                            // Just a refresh.
                            HTMLCS.addMessage(HTMLCS.ERROR, element, 'Meta refresh tag used to refresh the current page. Users cannot control the time limit for this refresh.', 'MetaRefresh');
                        }
                    }
                }//end if
            }//end if
        }//end if
    }

};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_1_1_1_1 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return [
            '_top',
            'img'
        ];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        if (element === top) {
            this.addNullAltTextResults(top);
            this.addMediaAlternativesResults(top);
        } else {
            var nodeName = element.nodeName.toLowerCase();

            switch (nodeName) {
                case 'img':
                    this.testLinkStutter(element);
                    this.testLongdesc(element);
                break;
            }//end if
        }//end if
    },

    /**
     * Driver function for the null alt text tests.
     *
     * This takes the generic result given by the alt text testing functions,
     * and converts them into WCAG 2.0-specific messages.
     *
     * @param {DOMNode} element The element to test.
     */
    addNullAltTextResults: function(top)
    {
        var errors = this.testNullAltText(top);

        for (var i = 0; i < errors.img.emptyAltInLink.length; i++) {
            HTMLCS.addMessage(HTMLCS.ERROR, errors.img.emptyAltInLink[i], 'Img element is the only content of the link, but is missing alt text. The alt text should describe the purpose of the link.', 'H30.2');
        }

        for (var i = 0; i < errors.img.nullAltWithTitle.length; i++) {
            HTMLCS.addMessage(HTMLCS.ERROR, errors.img.nullAltWithTitle[i], 'Img element with empty alt text must have absent or empty title attribute.', 'H67.1');
        }

        for (var i = 0; i < errors.img.ignored.length; i++) {
            HTMLCS.addMessage(HTMLCS.WARNING, errors.img.ignored[i], 'Img element is marked so that it is ignored by Assistive Technology.', 'H67.2');
        }

        for (var i = 0; i < errors.img.missingAlt.length; i++) {
            HTMLCS.addMessage(HTMLCS.ERROR, errors.img.missingAlt[i], 'Img element missing an alt attribute. Use the alt attribute to specify a short text alternative.', 'H37');
        }

        for (var i = 0; i < errors.img.generalAlt.length; i++) {
            HTMLCS.addMessage(HTMLCS.NOTICE, errors.img.generalAlt[i], 'Ensure that the img element\'s alt text serves the same purpose and presents the same information as the image.', 'G94.Image');
        }

        for (var i = 0; i < errors.inputImage.missingAlt.length; i++) {
            HTMLCS.addMessage(HTMLCS.ERROR, errors.inputImage.missingAlt[i], 'Image submit button missing an alt attribute. Specify a text alternative that describes the button\'s function, using the alt attribute.', 'H36');
        }

        for (var i = 0; i < errors.inputImage.generalAlt.length; i++) {
            HTMLCS.addMessage(HTMLCS.NOTICE, errors.inputImage.generalAlt[i], 'Ensure that the image submit button\'s alt text identifies the purpose of the button.', 'G94.Button');
        }

        for (var i = 0; i < errors.area.missingAlt.length; i++) {
            HTMLCS.addMessage(HTMLCS.ERROR, errors.area.missingAlt[i], 'Area element in an image map missing an alt attribute. Each area element must have a text alternative that describes the function of the image map area.', 'H24');
        }

        for (var i = 0; i < errors.area.generalAlt.length; i++) {
            HTMLCS.addMessage(HTMLCS.NOTICE, errors.area.generalAlt[i], 'Ensure that the area element\'s text alternative serves the same purpose as the part of image map image it references.', 'H24.2');
        }
    },

    /**
     * Test for missing or null alt text in certain elements.
     *
     * Tested elements are:
     * - IMG elements
     * - INPUT elements with type="image" (ie. image submit buttons).
     * - AREA elements (ie. in client-side image maps).
     *
     * @param {DOMNode} element The element to test.
     *
     * @returns {Object} A structured list of errors.
     */
    testNullAltText: function(top)
    {
        var errors = {
            img: {
                generalAlt: [],
                missingAlt: [],
                ignored: [],
                nullAltWithTitle: [],
                emptyAltInLink: []
            },
            inputImage: {
                generalAlt: [],
                missingAlt: []
            },
            area: {
                generalAlt: [],
                missingAlt: []
            }
        };

        elements = HTMLCS.util.getAllElements(top, 'img, area, input[type="image"]');

        for (var el = 0; el < elements.length; el++) {
            var element = elements[el];

            var nodeName      = element.nodeName.toLowerCase();
            var linkOnlyChild = false;
            var missingAlt    = false;
            var nullAlt       = false;

            if (element.parentNode.nodeName.toLowerCase() === 'a') {
                var prevNode = HTMLCS.util.getPreviousSiblingElement(element, null);
                var nextNode = HTMLCS.util.getNextSiblingElement(element, null);

                if ((prevNode === null) && (nextNode === null)) {
                    var textContent = element.parentNode.textContent;

                    if (element.parentNode.textContent !== undefined) {
                        var textContent = element.parentNode.textContent;
                    } else {
                        // Keep IE8 happy.
                        var textContent = element.parentNode.innerText;
                    }

                    if (HTMLCS.util.isStringEmpty(textContent) === true) {
                        linkOnlyChild = true;
                    }
                }
            }//end if

            if (element.hasAttribute('alt') === false) {
                missingAlt = true;
            } else if (!element.getAttribute('alt') || HTMLCS.util.isStringEmpty(element.getAttribute('alt')) === true) {
                nullAlt = true;
            }

            // Now determine which test(s) should fire.
            switch (nodeName) {
                case 'img':
                    if ((linkOnlyChild === true) && ((missingAlt === true) || (nullAlt === true))) {
                        // Img tags cannot have an empty alt text if it is the
                        // only content in a link (as the link would not have a text
                        // alternative).
                        errors.img.emptyAltInLink.push(element.parentNode);
                    } else if (missingAlt === true) {
                        errors.img.missingAlt.push(element);
                    } else if (nullAlt === true) {
                        if ((element.hasAttribute('title') === true) && (HTMLCS.util.isStringEmpty(element.getAttribute('title')) === false)) {
                            // Title attribute present and not empty. This is wrong when
                            // an image is marked as ignored.
                            errors.img.nullAltWithTitle.push(element);
                        } else {
                            errors.img.ignored.push(element);
                        }
                    } else {
                        errors.img.generalAlt.push(element);
                    }
                break;

                case 'input':
                    // Image submit buttons.
                    if ((missingAlt === true) || (nullAlt === true)) {
                        errors.inputImage.missingAlt.push(element);
                    } else {
                        errors.inputImage.generalAlt.push(element);
                    }
                break;

                case 'area':
                    // Area tags in a client-side image map.
                    if ((missingAlt === true) || (nullAlt === true)) {
                        errors.area.missingAlt.push(element);
                    } else {
                        errors.inputImage.generalAlt.push(element);
                    }
                break;

                default:
                    // No other tags defined.
                break;
            }//end switch
        }//end for

        return errors;
    },

    /**
     * Test for longdesc attributes on images (technique H45).
     *
     * We throw a notice to ensure that a longdesc is available in an accessible
     * way - ie. using body text or a link. Longdesc is specifically ignored as it
     * is not accessible to sighted users.
     *
     * @param {DOMNode} element The element to test.
     *
     * @returns void
     */
    testLongdesc: function(element)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, element, 'If this image cannot be fully described in a short text alternative, ensure a long text alternative is also available, such as in the body text or through a link.', 'G73,G74');

    },

    /**
     * Test for link stutter with adjacent images and text (technique H2).
     *
     * Only runs on IMG elements contained inside an anchor (A) element. We test that
     * its alt text does not duplicate the text content of a link directly beside it.
     * We also test that the technique hasn't been applied incorrectly (Failure
     * Examples 4 and 5 in technique H2).
     *
     * Error messages are given codes in the form "H2.EG5", meaning it is a case of
     * the applicable failure example (3, 4, or 5).
     *
     * @param {DOMNode} element The image element to test.
     */
    testLinkStutter: function(element)
    {
        if (element.parentNode.nodeName.toLowerCase() === 'a') {
            var anchor = element.parentNode;

            // If contained by an "a" link, check that the alt text does not duplicate
            // the link text, or if no link text, check an adjacent link does not
            // duplicate it.
            var nodes = {
                anchor: {
                    href: anchor.getAttribute('href'),
                    text: HTMLCS.util.getElementTextContent(anchor, false),
                    alt: this._getLinkAltText(anchor)
                }
            }

            if (nodes.anchor.alt === null) {
                nodes.anchor.alt = '';
            }

            if ((nodes.anchor.alt !== null) && (nodes.anchor.alt !== '')) {
                if (HTMLCS.util.trim(nodes.anchor.alt).toLowerCase() === HTMLCS.util.trim(nodes.anchor.text).toLowerCase()) {
                    // H2 "Failure Example 5": they're in one link, but the alt text
                    // duplicates the link text. Trimmed and lowercased because they
                    // would sound the same to a screen reader.
                    HTMLCS.addMessage(HTMLCS.ERROR, element, 'Img element inside a link must not use alt text that duplicates the text content of the link.', 'H2.EG5');
                }
            }

            // If there is no supplementary text, try to catch H2 "Failure Examples"
            // in cases where there are adjacent links with the same href:
            // 3 - img text that duplicates link text in an adjacent link. (Screen
            //     readers will stutter.)
            // 4 - img text is blank when another link adjacent contains link text.
            //     (This leaves one link with no text at all - the two should be
            //      combined into one link.)
            if (nodes.anchor.text === '') {
                var prevLink = HTMLCS.util.getPreviousSiblingElement(anchor, 'a', true);
                var nextLink = HTMLCS.util.getNextSiblingElement(anchor, 'a', true);

                if (prevLink !== null) {
                    nodes.previous = {
                        href: prevLink.getAttribute('href'),
                        text: HTMLCS.util.getElementTextContent(prevLink, false),
                        alt: this._getLinkAltText(prevLink)
                    }

                    if (nodes.previous.alt === null) {
                        nodes.previous.alt = '';
                    }
                }

                if (nextLink !== null) {
                    nodes.next = {
                        href: nextLink.getAttribute('href'),
                        text: HTMLCS.util.getElementTextContent(nextLink, false),
                        alt: this._getLinkAltText(nextLink)
                    }

                    if (nodes.next.alt === null) {
                        nodes.next.alt = '';
                    }
                }

                // Test against the following link, if any.
                if (nodes.next && (nodes.next.href !== '') && (nodes.next.href !== null) && (nodes.anchor.href === nodes.next.href)) {
                    if ((nodes.next.text !== '') && (nodes.anchor.alt === '')) {
                        HTMLCS.addMessage(HTMLCS.ERROR, element, 'Img element inside a link has empty or missing alt text when a link beside it contains link text. Consider combining the links.', 'H2.EG4');
                    } else if (nodes.next.text.toLowerCase() === nodes.anchor.alt.toLowerCase()) {
                        HTMLCS.addMessage(HTMLCS.ERROR, element, 'Img element inside a link must not use alt text that duplicates the content of a text link beside it.', 'H2.EG3');
                    }
                }

                // Test against the preceding link, if any.
                if (nodes.previous && (nodes.previous.href !== '') && (nodes.previous.href !== null) && (nodes.anchor.href === nodes.previous.href)) {
                    if ((nodes.previous.text !== '') && (nodes.anchor.alt === '')) {
                        HTMLCS.addMessage(HTMLCS.ERROR, element, 'Img element inside a link has empty or missing alt text when a link beside it contains link text. Consider combining the links.', 'H2.EG4');
                    } else if (nodes.previous.text.toLowerCase() === nodes.anchor.alt.toLowerCase()) {
                        HTMLCS.addMessage(HTMLCS.ERROR, element, 'Img element inside a link must not use alt text that duplicates the content of a text link beside it.', 'H2.EG3');
                    }
                }
            }//end if
        }//end if
    },

    /**
     * Driver function for the media alternative (object/applet) tests.
     *
     * This takes the generic result given by the media alternative testing function,
     * and converts them into WCAG 2.0-specific messages.
     *
     * @param {DOMNode} element The element to test.
     */
    addMediaAlternativesResults: function(top)
    {
        var errors = this.testMediaTextAlternatives(top);

        for (var i = 0; i < errors.object.missingBody.length; i++) {
            HTMLCS.addMessage(HTMLCS.ERROR, errors.object.missingBody[i], 'Object elements must contain a text alternative after all other alternatives are exhausted.', 'H53,ARIA6');
        }

        for (var i = 0; i < errors.object.generalAlt.length; i++) {
            HTMLCS.addMessage(HTMLCS.NOTICE, errors.object.generalAlt[i], 'Check that short (and if appropriate, long) text alternatives are available for non-text content that serve the same purpose and present the same information.', 'G94,G92.Object,ARIA6');
        }

        for (var i = 0; i < errors.applet.missingBody.length; i++) {
            HTMLCS.addMessage(HTMLCS.ERROR, errors.applet.missingBody[i], 'Applet elements must contain a text alternative in the element\'s body, for browsers without support for the applet element.', 'H35.3');
        }

        for (var i = 0; i < errors.applet.missingAlt.length; i++) {
            HTMLCS.addMessage(HTMLCS.ERROR, errors.applet.missingAlt[i], 'Applet elements must contain an alt attribute, to provide a text alternative to browsers supporting the element but are unable to load the applet.', 'H35.2');
        }

        for (var i = 0; i < errors.applet.generalAlt.length; i++) {
            HTMLCS.addMessage(HTMLCS.NOTICE, errors.applet.generalAlt[i], 'Check that short (and if appropriate, long) text alternatives are available for non-text content that serve the same purpose and present the same information.', 'G94,G92.Applet');
        }
    },

    testMediaTextAlternatives: function(top)
    {
        var errors = {
            object: {
                missingBody: [],
                generalAlt: []
            },
            applet: {
                missingBody: [],
                missingAlt: [],
                generalAlt: []
            }
        };

        var elements = HTMLCS.util.getAllElements(top, 'object');

        for (var el = 0; el < elements.length; el++) {
            var element  = elements[el];
            var nodeName = element.nodeName.toLowerCase();

            var childObject = element.querySelector('object');

            // If we have an object as our alternative, skip it. Pass the blame onto
            // the child.
            if (childObject === null) {
                if (HTMLCS.util.isStringEmpty(HTMLCS.util.getElementTextContent(element, true)) === true) {
                    if (HTMLCS.util.hasValidAriaLabel(element) === false) {
                        errors.object.missingBody.push(element);
                    }
                } else {
                    if (HTMLCS.util.hasValidAriaLabel(element) === false) {
                        errors.object.generalAlt.push(element);
                    }
                }
            }//end if
        }//end if

        var elements = HTMLCS.util.getAllElements(top, 'applet');

        for (var el = 0; el < elements.length; el++) {
            // Test firstly for whether we have an object alternative.
            var childObject = element.querySelector('object');
            var hasError    = false;

            // If we have an object as our alternative, skip it. Pass the blame onto
            // the child. (This is a special case: those that don't understand APPLET
            // may understand OBJECT, but APPLET shouldn't be nested.)
            if (childObject === null) {
                var textAlt = HTMLCS.util.getElementTextContent(element, true);
                if (HTMLCS.util.isStringEmpty(textAlt) === true) {
                    errors.applet.missingBody.push(element);
                    hasError = true;
                }
            }//end if

            var altAttr = element.getAttribute('alt') || '';
            if (HTMLCS.util.isStringEmpty(altAttr) === true) {
                errors.applet.missingAlt.push(element);
                hasError = true;
            }

            // Catch anything with a valid aria label.
            if (HTMLCS.util.hasValidAriaLabel(element) === true) {
                hasError = false;
            }

            if (hasError === false) {
                // No error? Remind of obligations about equivalence of alternatives.
                errors.applet.generalAlt.push(element);
            }
        }//end if

        return errors;
    },

    /**
     * Gets just the alt text from any images on a link.
     *
     * @param {DOMNode} anchor The link element being inspected.
     *
     * @returns {String} The alt text.
     */
    _getLinkAltText: function(anchor)
    {
        var anchor = anchor.cloneNode(true);
        var nodes  = [];
        for (var i = 0; i < anchor.childNodes.length; i++) {
            nodes.push(anchor.childNodes[i]);
        }

        var alt = null;
        while (nodes.length > 0) {
            var node = nodes.shift();

            // If it's an element, add any sub-nodes to the process list.
            if (node.nodeType === 1) {
                if (node.nodeName.toLowerCase() === 'img') {
                    if (node.hasAttribute('alt') === true) {
                        alt = node.getAttribute('alt');
                        if (!alt) {
                            alt = '';
                        } else {
                            // Trim the alt text.
                            alt = alt.replace(/^\s+|\s+$/g,'');
                        }

                        break;
                    }
                }
            }
        }

        return alt;
    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_2_1_2_1 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return [
            'object',
            'embed',
            'applet',
            'bgsound',
            'audio',
            'video'
        ];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        var nodeName = element.nodeName.toLowerCase();

        if (nodeName !== 'video') {
            HTMLCS.addMessage(HTMLCS.NOTICE, element, 'If this embedded object contains pre-recorded audio only, and is not provided as an alternative for text content, check that an alternative text version is available.', 'G158');
        }

        if ((nodeName !== 'bgsound') && (nodeName !== 'audio')) {
            HTMLCS.addMessage(HTMLCS.NOTICE, element, 'If this embedded object contains pre-recorded video only, and is not provided as an alternative for text content, check that an alternative text version is available, or an audio track is provided that presents equivalent information.', 'G159,G166');
        }

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_2_1_2_2 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return [
            'object',
            'embed',
            'applet',
            'video'
        ];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, element, 'If this embedded object contains pre-recorded synchronised media and is not provided as an alternative for text content, check that captions are provided for audio content.', 'G87,G93');

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_2_1_2_3 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return [
            'object',
            'embed',
            'applet',
            'video'
        ];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, element, 'If this embedded object contains pre-recorded synchronised media and is not provided as an alternative for text content, check that an audio description of its video, and/or an alternative text version of the content is provided.', 'G69,G78,G173,G8');

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_2_1_2_4 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return [
            'object',
            'embed',
            'applet',
            'video'
        ];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, element, 'If this embedded object contains synchronised media, check that captions are provided for live audio content.', 'G9,G87,G93');

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_2_1_2_5 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return [
            'object',
            'embed',
            'applet',
            'video'
        ];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, element, 'If this embedded object contains pre-recorded synchronised media, check that an audio description is provided for its video content.', 'G78,G173,G8');

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_2_1_2_6 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return [
            'object',
            'embed',
            'applet',
            'video'
        ];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, element, 'If this embedded object contains pre-recorded synchronised media, check that a sign language interpretation is provided for its audio.', 'G54,G81');

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_2_1_2_7 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return [
            'object',
            'embed',
            'applet',
            'video'
        ];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        // Check for elements that could potentially contain video.
        HTMLCS.addMessage(HTMLCS.NOTICE, element, 'If this embedded object contains synchronised media, and where pauses in foreground audio is not sufficient to allow audio descriptions to convey the sense of pre-recorded video, check that an extended audio description is provided, either through scripting or an alternate version.', 'G8');

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_2_1_2_8 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return [
            'object',
            'embed',
            'applet',
            'video'
        ];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, element, 'If this embedded object contains pre-recorded synchronised media or video-only content, check that an alternative text version of the content is provided.', 'G69,G159');

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_2_1_2_9 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return [
            'object',
            'embed',
            'applet',
            'bgsound',
            'audio'
        ];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, element, 'If this embedded object contains live audio-only content, check that an alternative text version of the content is provided.', 'G150,G151,G157');

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_3_1_3_1 = {
	_labelNames: null,

	register: function()
	{
		return [
			'_top',
			'p',
			'div',
			'input',
			'select',
			'textarea',
			'button',
			'table',
			'fieldset',
			'form',
			'h1',
			'h2',
			'h3',
			'h4',
			'h5',
			'h6'
		];

	},

	/**
	 * Process the registered element.
	 *
	 * @param {DOMNode} element The element registered.
	 * @param {DOMNode} top     The top element of the tested code.
	 */
	process: function(element, top)
	{
		var nodeName = element.nodeName.toLowerCase();

		if (element === top) {
			this.testPresentationMarkup(top);
			this.testEmptyDupeLabelForAttrs(top);
		} else {
			switch (nodeName) {
				case 'input':
				case 'textarea':
				case 'button':
					this.testLabelsOnInputs(element, top);
				break;

				case 'form':
					this.testRequiredFieldsets(element);
				break;

				case 'select':
					this.testLabelsOnInputs(element, top);
					this.testOptgroup(element);
				break;

				case 'p':
				case 'div':
					this.testNonSemanticHeading(element);
					this.testListsWithBreaks(element);
					this.testUnstructuredNavLinks(element);
				break;

				case 'table':
					this.testGeneralTable(element);
					this.testTableHeaders(element);
					this.testTableCaptionSummary(element);
				break;

				case 'fieldset':
					this.testFieldsetLegend(element);
				break;

				case 'h1':
				case 'h2':
				case 'h3':
				case 'h4':
				case 'h5':
				case 'h6':
					this.testEmptyHeading(element);
				break;
			}//end switch
		}//end if
	},

	/**
	 * Test elements for presentation roles that also contain semantic child elements.
	 *
	 * @param {DOMNode} element The element to test.
	 */
	testSemanticPresentationRole: function(element)
	{
		if (HTMLCS.util.isAriaHidden(element) === false
			&& element.hasAttribute('role')
			&& element.getAttribute('role') === 'presentation'
		) {
			var permitted = ['div', 'span', 'b', 'i'];
			var children  = element.querySelectorAll('*:not('+permitted.join('):not(')+')');
			children      = [].filter.call(children, function(child) {
				return child.hasAttribute('role') === false;
			});
			if (children.length) {
				HTMLCS.addMessage(
					HTMLCS.ERROR,
					element,
					'This element\'s role is "presentation" but contains child elements with semantic meaning.',
					'F92,ARIA4'
				);
			}
		}
	},

	/**
	 * Top-level test for labels that have no for attribute, or duplicate ones.
	 *
	 * @param {DOMNode} top The top element of the tested code.
	 */
	testEmptyDupeLabelForAttrs: function(top)
	{
		this._labelNames = {};
		var labels = top.getElementsByTagName('label');
		for (var i = 0; i < labels.length; i++) {
			if ((labels[i].getAttribute('for') !== null) && (labels[i].getAttribute('for') !== '')) {
				var labelFor = labels[i].getAttribute('for');
				if ((this._labelNames[labelFor]) && (this._labelNames[labelFor] !== null)) {
					this._labelNames[labelFor] = null;
				} else {
					this._labelNames[labelFor] = labels[i];

					if (top.ownerDocument) {
						var refNode = top.ownerDocument.getElementById(labelFor);
					} else {
						var refNode = top.getElementById(labelFor);
					}

					if (refNode === null) {
						var level = HTMLCS.ERROR;
						var msg   = 'This label\'s "for" attribute contains an ID that does not exist in the document.';
						var code  = 'H44.NonExistent';
						if ((HTMLCS.isFullDoc(top) === true) || (top.nodeName.toLowerCase() === 'body')) {
							level = HTMLCS.WARNING;
							msg   = 'This label\'s "for" attribute contains an ID that does not exist in the document fragment.';
							var code  = 'H44.NonExistentFragment';
						}
						HTMLCS.addMessage(level, labels[i], msg, code);
					} else {
						var nodeName = refNode.nodeName.toLowerCase();
						if ('input|select|textarea|button|keygen|meter|output|progress'.indexOf(nodeName) === -1) {
							HTMLCS.addMessage(HTMLCS.WARNING, labels[i], 'This label\'s "for" attribute contains an ID for an element that is not a form control. Ensure that you have entered the correct ID for the intended element.', 'H44.NotFormControl');
						}
					}
				}
			}
		}//end for
	},

	/**
	 * Test for appropriate labels on inputs.
	 *
	 * The appropriate WCAG2 techniques test is failure F68.
	 * This test uses the September 2014 version of the technique:
	 * http://www.w3.org/TR/2014/NOTE-WCAG20-TECHS-20140916/F68
	 *
	 * For all input elements of type "radio", "checkbox", "text", "file" or "password",
	 * and all textarea and select elements in the Web page:
	 *
	 * 1. Check that the visual design uses a text label that identifies the purpose of the control
	 * 2. Check that these input elements have a programmatically determined label associated in one
	 *    of the following ways:
	 *    (a) the text label is contained in a label element that is correctly associated to the
	 *        respective input element via the label's for attribute (the id given as value in the
	 *        for attribute matches the id of the input element).
	 *    (b) the control is contained within a label element that contains the label text.
	 *    (c) the text label is correctly programmatically associated with the input element via the
	 *        aria-labelledby attribute (the id given as value in the aria-labelledby attribute
	 *        matches the id of the input element).
	 *    (d) the [label] is programmatically determined through the value of either its
	 *        aria-label or title attributes.
	 *
	 * This changed in March 2014. Before then, only 2(a) was permitted or 2(d) (title attribute only).
	 * Notably, labels made through wrapping an element in a label attribute were not permitted.
	 *
	 * Associated techniques: H44 (LABEL element), H65 (title attribute),
	 * ARIA6/ARIA14 (aria-label), ARIA9/ARIA16 (aria-labelledby).
	 *
	 * @param {DOMNode} element The element registered.
	 * @param {DOMNode} top     The top element of the tested code.
	 */
	testLabelsOnInputs: function(element, top, muteErrors)
	{
		var nodeName  = element.nodeName.toLowerCase();
		var inputType = nodeName;
		if (inputType === 'input') {
			if (element.hasAttribute('type') === true) {
				inputType = element.getAttribute('type');
			} else {
				inputType = 'text';
			}
		}

		var hasLabel = false;
		var addToLabelList = function(found) {
			if (!hasLabel) hasLabel = {};
			hasLabel[found] = true;
		};

		// Firstly, work out whether it needs a label.
		var needsLabel = false;
		var labelPos   = 'left';
		var inputType  = inputType.toLowerCase();
		if ((inputType === 'select' || inputType === 'textarea')) {
			needsLabel = true;
		} else if (/^(radio|checkbox|text|file|password)$/.test(inputType) === true) {
			needsLabel = true;
		}

		if (element.getAttribute('hidden') !== null) {
			needsLabel = false;
		}

		// Find an explicit label.
		var explicitLabel = element.ownerDocument.querySelector('label[for="' + element.id + '"]');
		if (explicitLabel) {
			addToLabelList('explicit');
		}

		// Find an implicit label.
		var implicitLabel = element.parentNode;
		if (implicitLabel && (implicitLabel.nodeName.toLowerCase() === 'label')) {
			addToLabelList('implicit');
		}

		// Find a title attribute.
		var title = element.getAttribute('title');
		if (title !== null) {
			if ((/^\s*$/.test(title) === true) && (needsLabel === true)) {
				HTMLCS.addMessage(
					HTMLCS.WARNING,
					element,
					'This form control has a "title" attribute that is empty or contains only spaces. It will be ignored for labelling test purposes.',
					'H65'
				);
			} else {
				addToLabelList('title');
			}
		}

		// Find an aria-label attribute.
		if (element.hasAttribute('aria-label') === true) {
			if (HTMLCS.util.hasValidAriaLabel(element) === false) {
				HTMLCS.addMessage(
					HTMLCS.WARNING,
					element,
					'This form control has an "aria-label" attribute that is empty or contains only spaces. It will be ignored for labelling test purposes.',
					'ARIA6'
				);
			} else {
				addToLabelList('aria-label');
			}
		}


		// Find an aria-labelledby attribute.
		if (element.hasAttribute('aria-labelledby') === true) {
			if (HTMLCS.util.hasValidAriaLabel(element) === false) {
				HTMLCS.addMessage(
					HTMLCS.WARNING,
					element,
					'This form control contains an aria-labelledby attribute, however it includes an ID "' + element.getAttribute('aria-labelledby') + '" that does not exist on an element. The aria-labelledby attribute will be ignored for labelling test purposes.',
					'ARIA16,ARIA9'
				);
			} else {
				addToLabelList('aria-labelledby');
			}
		}

		if (!(muteErrors === true)) {
			if ((hasLabel !== false) && (needsLabel === false)) {
				// Note that it is okay for buttons to have aria-labelledby or
				// aria-label, or title. The former two override the button text,
				// while title is a lower priority than either: the button text,
				// and in submit/reset cases, the localised name for the words
				// "Submit" and "Reset".
				// http://www.w3.org/TR/html-aapi/#accessible-name-and-description-calculation
				if (inputType === 'hidden') {
					HTMLCS.addMessage(
						HTMLCS.WARNING,
						element,
						'This hidden form field is labelled in some way. There should be no need to label a hidden form field.',
						'F68.Hidden'
					);
				} else if (element.getAttribute('hidden') !== null) {
					HTMLCS.addMessage(
						HTMLCS.WARNING,
						element,
						'This form field is intended to be hidden (using the "hidden" attribute), but is also labelled in some way. There should be no need to label a hidden form field.',
						'F68.HiddenAttr'
					);
				}
			} else if ((hasLabel === false) && (needsLabel === true)) {
				// Needs label.
				HTMLCS.addMessage(
					HTMLCS.ERROR,
					element,
					'This form field should be labelled in some way.' + ' ' +
					'Use the label element (either with a "for" attribute or wrapped around the form field), or "title", "aria-label" or "aria-labelledby" attributes as appropriate.',
					'F68'
				);
			}//end if
		}

		return hasLabel;
	},

	/**
	 * Test for the use of presentational elements (technique H49).
	 *
	 * In HTML4, certain elements are considered presentational code. In HTML5, they
	 * are redefined (based on "they are being used, so they shouldn't be
	 * deprecated") so they can be considered "somewhat" semantic. They should still
	 * be considered a last resort.
	 *
	 * @param [DOMNode] top The top element of the tested code.
	 */
	testPresentationMarkup: function(top)
	{
		// In HTML4, the following were marked as presentational:
		// b, i, u, s, strike, tt, big, small, center, font
		// In HTML5, the following were repurposed as pseudo-semantic:
		// b, i, u, s, small
		var _doc    = HTMLCS.util.getElementWindow(top).document;
		var doctype = HTMLCS.util.getDocumentType(_doc);

		if (doctype && (doctype === 'html5' || doctype === 'xhtml5')) {
			var tags = HTMLCS.util.getAllElements(top, 'strike, tt, big, center, font');
			for (var i = 0; i < tags.length; i++) {
				var msgCode = 'H49.' + tags[i].nodeName.substr(0, 1).toUpperCase() + tags[i].nodeName.substr(1).toLowerCase();
				HTMLCS.addMessage(HTMLCS.ERROR, tags[i], 'Presentational markup used that has become obsolete in HTML5.', msgCode);
			}

			// Align attributes, too.
			var tags = HTMLCS.util.getAllElements(top, '*[align]');

			for (var i = 0; i < tags.length; i++) {
				var msgCode = 'H49.AlignAttr';
				HTMLCS.addMessage(HTMLCS.ERROR, tags[i], 'Align attributes .', msgCode);
			}
		} else {
			var tags = HTMLCS.util.getAllElements(top, 'b, i, u, s, strike, tt, big, small, center, font');
			for (var i = 0; i < tags.length; i++) {
				var msgCode = 'H49.' + tags[i].nodeName.substr(0, 1).toUpperCase() + tags[i].nodeName.substr(1).toLowerCase();
				HTMLCS.addMessage(HTMLCS.WARNING, tags[i], 'Semantic markup should be used to mark emphasised or special text so that it can be programmatically determined.', msgCode);
			}

			// Align attributes, too.
			var tags = HTMLCS.util.getAllElements(top, '*[align]');

			for (var i = 0; i < tags.length; i++) {
				var msgCode = 'H49.AlignAttr';
				HTMLCS.addMessage(HTMLCS.WARNING, tags[i], 'Semantic markup should be used to mark emphasised or special text so that it can be programmatically determined.', msgCode);
			}
		}
	},

	/**
	 * Test for the possible use of non-semantic headings (technique H42).
	 *
	 * Test for P|DIV > STRONG|EM|other inline styling, when said inline
	 * styling tag is the only element in the tag. It could possibly be a header
	 * that should be using h1..h6 tags instead.
	 *
	 * @param [DOMNode] element The paragraph or DIV element to test.
	 */
	testNonSemanticHeading: function(element)
	{
		// Test for P|DIV > STRONG|EM|other inline styling, when said inline
		// styling tag is the only element in the tag. It could possibly a header
		// that should be using h1..h6 tags instead.
		var tag = element.nodeName.toLowerCase();
		if (tag === 'p' || tag === 'div') {
			var children = element.childNodes;
			if ((children.length === 1) && (children[0].nodeType === 1)) {
				var childTag = children[0].nodeName.toLowerCase();

				if (/^(strong|em|b|i|u)$/.test(childTag) === true) {
					HTMLCS.addMessage(HTMLCS.WARNING, element, 'Heading markup should be used if this content is intended as a heading.', 'H42');
				}
			}
		}
	},

	/**
	 * Test for the correct association of table data cells with their headers.
	 *
	 * This is actually a two-part test, using either the scope attribute (H63) or
	 * the headers attribute (H43). Which one(s) are required or appropriate
	 * depend on the types of headings available:
	 * - If only one row or one column header, no association is required.
	 * - If one row AND one column headers, scope or headers is suitable.
	 * - If multi-level headers of any type, use of headers (only) is required.
	 *
	 * This test takes the results of two tests - one of headers and one of scope -
	 * and works out which error messages should apply given the above type of table.
	 *
	 * Invalid or incorrect usage of scope or headers are always reported when used,
	 * and cases where scope/headers are used on some of the table but not all is
	 * also thrown.
	 *
	 * @param {DOMNode} table The table element to evaluate.
	 *
	 * @return void
	 */
	testTableHeaders: function(table)
	{
		var headersAttr = HTMLCS.util.testTableHeaders(table);
		var scopeAttr   = this._testTableScopeAttrs(table);

		// Invalid scope attribute - emit always if scope tested.
		for (var i = 0; i < scopeAttr.invalid.length; i++) {
			HTMLCS.addMessage(HTMLCS.ERROR, scopeAttr.invalid[i], 'Table cell has an invalid scope attribute. Valid values are row, col, rowgroup, or colgroup.', 'H63.3');
		}

		// TDs with scope attributes are obsolete in HTML5 - emit warnings if
		// scope tested, but not as errors as they are valid HTML4.
		for (var i = 0; i < scopeAttr.obsoleteTd.length; i++) {
			HTMLCS.addMessage(HTMLCS.WARNING, scopeAttr.obsoleteTd[i], 'Scope attributes on td elements that act as headings for other elements are obsolete in HTML5. Use a th element instead.', 'H63.2');
		}

		if (headersAttr.allowScope === true) {
			if (scopeAttr.missing.length === 0) {
				// If all scope attributes are set, let them be used, even if the
				// attributes are in error. If the scope attrs are fixed, the table
				// will be legitimate.
				headersAttr.required === false;
			}
		} else {
			if (scopeAttr.used === true) {
				HTMLCS.addMessage(HTMLCS.WARNING, table, 'Scope attributes on th elements are ambiguous in a table with multiple levels of headings. Use the headers attribute on td elements instead.', 'H43.ScopeAmbiguous');
				scopeAttr = null;
			}
		}//end if

		// Incorrect usage of headers - error; emit always.
		for (var i = 0; i < headersAttr.wrongHeaders.length; i++) {
			HTMLCS.addMessage(HTMLCS.ERROR, headersAttr.wrongHeaders[i].element, 'Incorrect headers attribute on this td element. Expected "' + headersAttr.wrongHeaders[i].expected + '" but found "' + headersAttr.wrongHeaders[i].actual + '"', 'H43.IncorrectAttr');
		}

		// Errors where headers are compulsory.
		if ((headersAttr.required === true) && (headersAttr.allowScope === false)) {
			if (headersAttr.used === false) {
				// Headers not used at all, and they are mandatory.
				HTMLCS.addMessage(HTMLCS.ERROR, table, 'The relationship between td elements and their associated th elements is not defined. As this table has multiple levels of th elements, you must use the headers attribute on td elements.', 'H43.HeadersRequired');
			} else {
				// Missing TH IDs - error; emit at this stage only if headers are compulsory.
				if (headersAttr.missingThId.length > 0) {
					HTMLCS.addMessage(HTMLCS.ERROR, table, 'Not all th elements in this table contain an id attribute. These cells should contain ids so that they may be referenced by td elements\' headers attributes.', 'H43.MissingHeaderIds');
				}

				// Missing TD headers attributes - error; emit at this stage only if headers are compulsory.
				if (headersAttr.missingTd.length > 0) {
					HTMLCS.addMessage(HTMLCS.ERROR, table, 'Not all td elements in this table contain a headers attribute. Each headers attribute should list the ids of all th elements associated with that cell.', 'H43.MissingHeadersAttrs');
				}
			}//end if
		}//end if

		// Errors where either is permitted, but neither are done properly (missing
		// certain elements).
		// If they've only done it one way, presume that that is the way they want
		// to continue. Otherwise provide a generic message if none are done or
		// both have been done incorrectly.
		if ((headersAttr.required === true) && (headersAttr.allowScope === true) && (headersAttr.correct === false) && (scopeAttr.correct === false)) {
			if ((scopeAttr.used === false) && (headersAttr.used === false)) {
				// Nothing used at all.
				HTMLCS.addMessage(HTMLCS.ERROR, table, 'The relationship between td elements and their associated th elements is not defined. Use either the scope attribute on th elements, or the headers attribute on td elements.', 'H43,H63');
			} else if ((scopeAttr.used === false) && ((headersAttr.missingThId.length > 0) || (headersAttr.missingTd.length > 0))) {
				// Headers attribute is used, but not all th elements have ids.
				if (headersAttr.missingThId.length > 0) {
					HTMLCS.addMessage(HTMLCS.ERROR, table, 'Not all th elements in this table contain an id attribute. These cells should contain ids so that they may be referenced by td elements\' headers attributes.', 'H43.MissingHeaderIds');
				}

				// Headers attribute is used, but not all td elements have headers attrs.
				if (headersAttr.missingTd.length > 0) {
					HTMLCS.addMessage(HTMLCS.ERROR, table, 'Not all td elements in this table contain a headers attribute. Each headers attribute should list the ids of all th elements associated with that cell.', 'H43.MissingHeadersAttrs');
				}
			} else if ((scopeAttr.missing.length > 0) && (headersAttr.used === false)) {
				// Scope is used rather than headers, but not all th elements have them.
				HTMLCS.addMessage(HTMLCS.ERROR, table, 'Not all th elements in this table have a scope attribute. These cells should contain a scope attribute to identify their association with td elements.', 'H63.1');
			} else if ((scopeAttr.missing.length > 0) && ((headersAttr.missingThId.length > 0) || (headersAttr.missingTd.length > 0))) {
				// Both are used and both were done incorrectly. Provide generic message.
				HTMLCS.addMessage(HTMLCS.ERROR, table, 'The relationship between td elements and their associated th elements is not defined. Use either the scope attribute on th elements, or the headers attribute on td elements.', 'H43,H63');
			}
		}
	},

	/**
	 * Test for the correct scope attributes on table cell elements.
	 *
	 * Return value contains the following elements:
	 * - used (Boolean):       Whether scope has been used on at least one cell.
	 * - correct (Boolean):    Whether scope has been correctly used (obsolete
	 *                         elements do not invalidate this).
	 * - missing (Array):      Array of th elements that have no scope attribute.
	 * - invalid (Array):      Array of elements with incorrect scope attributes.
	 * - obsoleteTd (Array):   Array of elements where we should throw a warning
	 *                         about scope on td being obsolete in HTML5.
	 *
	 * @param {DOMNode} element Table element to test upon.
	 *
	 * @return {Object} The above return value structure.
	 */
	_testTableScopeAttrs: function(table)
	{
		var elements = {
			th: table.getElementsByTagName('th'),
			td: table.getElementsByTagName('td')
		};

		// Types of errors:
		// - missing:    Errors that a th does not contain a scope attribute.
		// - invalid:    Errors that the scope attribute is not a valid value.
		// - obsoleteTd: Warnings that scopes on tds are obsolete in HTML5.
		var retval = {
			used: false,
			correct: true,
			missing: [],
			invalid: [],
			obsoleteTd: []
		};

		for (var tagType in elements) {
			for (var i = 0; i < elements[tagType].length; i++) {
				var element = elements[tagType][i];

				var scope = '';
				if (element.hasAttribute('scope') === true) {
					retval.used = true;
					if (element.getAttribute('scope')) {
						scope = element.getAttribute('scope');
					}
				}

				if (element.nodeName.toLowerCase() === 'th') {
					if (/^\s*$/.test(scope) === true) {
						// Scope empty or just whitespace.
						retval.correct = false;
						retval.missing.push(element);
					} else if (/^(row|col|rowgroup|colgroup)$/.test(scope) === false) {
						// Invalid scope value.
						retval.correct = false;
						retval.invalid.push(element);
					}
				} else {
					if (scope !== '') {
						// Scope attribute found on TD element. This is obsolete in
						// HTML5. Does not make it incorrect.
						retval.obsoleteTd.push(element);

						// Test for an invalid scope value regardless.
						if (/^(row|col|rowgroup|colgroup)$/.test(scope) === false) {
							retval.correct = false;
							retval.invalid.push(element);
						}
					}//end if
				}//end if
			}//end for
		}//end for

		return retval;
	},

	/**
	 * Test table captions and summaries (techniques H39, H73).
	 *
	 * @param {DOMNode} table Table element to test upon.
	 */
	testTableCaptionSummary: function(table) {
		var summary   = table.getAttribute('summary') || '';
		var captionEl = table.getElementsByTagName('caption');
		var caption   = '';

		if (captionEl.length > 0) {
			caption = captionEl[0].innerHTML.replace(/^\s*(.*?)\s*$/g, '$1');
		}

		// In HTML5, Summary no longer exists, so only run this for older versions.
		var doctype = HTMLCS.util.getDocumentType(table.ownerDocument);
		if (doctype && doctype.indexOf('html5') === -1) {
			summary = summary.replace(/^\s*(.*?)\s*$/g, '$1');
			if (summary !== '') {
				if (HTMLCS.util.isLayoutTable(table) === true) {
					HTMLCS.addMessage(HTMLCS.ERROR, table, 'This table appears to be used for layout, but contains a summary attribute. Layout tables must not contain summary attributes, or if supplied, must be empty.', 'H73.3.LayoutTable');
				} else {
					if (caption === summary) {
						HTMLCS.addMessage(HTMLCS.ERROR, table, 'If this table is a data table, and both a summary attribute and a caption element are present, the summary should not duplicate the caption.', 'H39,H73.4');
					}

					HTMLCS.addMessage(HTMLCS.NOTICE, table, 'If this table is a data table, check that the summary attribute describes the table\'s organization or explains how to use the table.', 'H73.3.Check');
				}
			} else {
				if (HTMLCS.util.isLayoutTable(table) === false) {
					HTMLCS.addMessage(HTMLCS.WARNING, table, 'If this table is a data table, consider using the summary attribute of the table element to give an overview of this table.', 'H73.3.NoSummary');
				}
			}//end if
		}//end if

		if (caption !== '') {
			if (HTMLCS.util.isLayoutTable(table) === true) {
				HTMLCS.addMessage(HTMLCS.ERROR, table, 'This table appears to be used for layout, but contains a caption element. Layout tables must not contain captions.', 'H39.3.LayoutTable');
			} else {
				HTMLCS.addMessage(HTMLCS.NOTICE, table, 'If this table is a data table, check that the caption element accurately describes this table.', 'H39.3.Check');
			}
		} else {
			if (HTMLCS.util.isLayoutTable(table) === false) {
				HTMLCS.addMessage(HTMLCS.WARNING, table, 'If this table is a data table, consider using a caption element to the table element to identify this table.', 'H39.3.NoCaption');
			}
		}//end if
	},

	/**
	 * Test for fieldsets without legends (technique H71)
	 *
	 * @param {DOMNode} fieldset Fieldset element to test upon.
	 */
	testFieldsetLegend: function(fieldset) {
		var legend = fieldset.querySelector('legend');

		if ((legend === null) || (legend.parentNode !== fieldset)) {
			HTMLCS.addMessage(HTMLCS.ERROR, fieldset, 'Fieldset does not contain a legend element. All fieldsets should contain a legend element that describes a description of the field group.', 'H71.NoLegend');
		}
	},

	/**
	 * Test for select fields without optgroups (technique H85).
	 *
	 * It won't always be appropriate, so the error is emitted as a warning.
	 *
	 * @param {DOMNode} select Select element to test upon.
	 */
	testOptgroup: function(select) {
		var optgroup = select.querySelector('optgroup');

		if (optgroup === null) {
			// Optgroup isn't being used.
			HTMLCS.addMessage(HTMLCS.WARNING, select, 'If this selection list contains groups of related options, they should be grouped with optgroup.', 'H85.2');
		}
	},

	/**
	 * Test for radio buttons and checkboxes with same name in a fieldset.
	 *
	 * One error will be fired at a form level, rather than firing one for each
	 * violating group of inputs (as there could be many).
	 *
	 * @param {DOMNode} form The form to test.
	 *
	 * @returns void
	 */
	testRequiredFieldsets: function(form) {
		var optionInputs = form.querySelectorAll('input[type=radio], input[type=checkbox]');
		var usedNames     = {};

		for (var i = 0; i < optionInputs.length; i++) {
			var option = optionInputs[i];

			if (option.hasAttribute('name') === true) {
				var optionName = option.getAttribute('name');

				// Now find if we are in a fieldset. Stop at the top of the DOM, or
				// at the form element.
				var fieldset = option.parentNode;
				while ((fieldset.nodeName.toLowerCase() !== 'fieldset') && (fieldset !== null) && (fieldset !== form)) {
					fieldset = fieldset.parentNode;
				}

				if (fieldset.nodeName.toLowerCase() !== 'fieldset') {
					// Record that this name is used, but there is no fieldset.
					fieldset = null;
				}
			}//end if

			if (usedNames[optionName] === undefined) {
				usedNames[optionName] = fieldset;
			} else if ((fieldset === null) || (fieldset !== usedNames[optionName])) {
				// Multiple names detected = should be in a fieldset.
				// Either first instance or this one wasn't in a fieldset, or they
				// are in different fieldsets.
				HTMLCS.addMessage(HTMLCS.WARNING, form, 'If these radio buttons or check boxes require a further group-level description, they should be contained within a fieldset element.', 'H71.SameName');
				break;
			}//end if
		}//end for
	},

	/**
	 * Test for paragraphs that appear manually bulleted or numbered (technique H48).
	 *
	 * @param {DOMNode} element The element to test upon.
	 */
	testListsWithBreaks: function(element) {
		var firstBreak = element.querySelector('br');
		var items      = [];

		// If there is a br tag, go break up the element and see what each line
		// starts with.
		if (firstBreak !== null) {
			var nodes    = [];

			// Convert child nodes NodeList into an array.
			for (var i = 0; i < element.childNodes.length; i++) {
				nodes.push(element.childNodes[i]);
			}

			var thisItem = [];
			while (nodes.length > 0) {
				var subel = nodes.shift();

				if (subel.nodeType === 1) {
					// Element node.
					if (subel.nodeName.toLowerCase() === 'br') {
						// Line break. Join and trim what we have now.
						items.push(thisItem.join(' ').replace(/^\s*(.*?)\s*$/g, '$1'));
						thisItem = [];
					} else {
						// Shift the contents of the sub element in, but in reverse.
						for (var i = subel.childNodes.length - 1; i >= 0; --i) {
							nodes.unshift(subel.childNodes[i]);
						}
					}
				} else if (subel.nodeType === 3) {
					// Text node.
					thisItem.push(subel.nodeValue);
				}
			}//end while

			if (thisItem.length > 0) {
				items.push(thisItem.join(' ').replace(/^\s*(.*?)\s*$/g, '$1'));
			}

			for (var i = 0; i < items.length; i++) {
				if (/^[\-*]\s+/.test(items[0]) === true) {
					// Test for "- " or "* " cases.
					HTMLCS.addMessage(HTMLCS.WARNING, element, 'This content looks like it is simulating an unordered list using plain text. If so, marking up this content with a ul element would add proper structure information to the document.', 'H48.1');
					break;
				} if (/^\d+[:\/\-.]?\s+/.test(items[0]) === true) {
					// Test for "1 " cases (or "1. ", "1: ", "1- ").
					HTMLCS.addMessage(HTMLCS.WARNING, element, 'This content looks like it is simulating an ordered list using plain text. If so, marking up this content with an ol element would add proper structure information to the document.', 'H48.2');
					break;
				}
			}//end for
		}//end if
	},

	testHeadingOrder: function(top, level) {
		var lastHeading = 0;
		var headings    = HTMLCS.util.getAllElements(top, 'h1, h2, h3, h4, h5, h6');

		for (var i = 0; i < headings.length; i++) {
			var headingNum = parseInt(headings[i].nodeName.substr(1, 1));
			if (headingNum - lastHeading > 1) {
				var exampleMsg = 'should be an h' + (lastHeading + 1) + ' to be properly nested';
				if (lastHeading === 0) {
					// If last heading is empty, we are at document top and we are
					// expecting a H1, generally speaking.
					exampleMsg = 'appears to be the primary document heading, so should be an h1 element';
				}

				HTMLCS.addMessage(level, headings[i], 'The heading structure is not logically nested. This h' + headingNum + ' element ' + exampleMsg + '.', 'G141');
			}

			lastHeading = headingNum;
		}
	},

	/**
	 * Test for headings with no text, which should either be filled, or tags removed.
	 *
	 * @param {DOMNode} element The element to test.
	 *
	 * @returns void
	 */
	testEmptyHeading: function(element) {
		var text = HTMLCS.util.getElementTextContent(element, true);

		if (/^\s*$/.test(text) === true) {
			HTMLCS.addMessage(HTMLCS.ERROR, element, 'Heading tag found with no content. Text that is not intended as a heading should not be marked up with heading tags.', 'H42.2');
		}
	},

	/**
	 * Test for the presence of a list around common navigation links (H48).
	 *
	 * @param {DOMNode} element The element to test.
	 *
	 * @returns void
	 */
	testUnstructuredNavLinks: function(element)
	{
		var nodeName    = element.nodeName.toLowerCase();
		var linksLength = 0;

		var childNodes  = element.childNodes;
		for (var i = 0; i < childNodes.length; i++) {
			if ((childNodes[i].nodeType === 1) && (childNodes[i].nodeName.toLowerCase() === 'a')) {
				linksLength++;
				if (linksLength > 1) {
					break;
				}
			}
		}//end for

		if (linksLength > 1) {
			// Going to throw a warning here, mainly because we cannot easily tell
			// whether it is just a paragraph with multiple links, or a navigation
			// structure.
			var parent = element.parentNode;
			while ((parent !== null) && (parent.nodeName.toLowerCase() !== 'ul') && (parent.nodeName.toLowerCase() !== 'ol')) {
				parent = parent.parentNode;
			}

			if (parent === null) {
				HTMLCS.addMessage(HTMLCS.WARNING, element, 'If this element contains a navigation section, it is recommended that it be marked up as a list.', 'H48');
			}
		}//end if
	},

	/**
	 * Provide generic messages for tables depending on what type of table they
	 * are - layout or data.
	 *
	 * @param {DOMNode} table The table element to test.
	 *
	 * @returns void
	 */
	testGeneralTable: function(table) {
		if (HTMLCS.util.isLayoutTable(table) === true) {
			HTMLCS.addMessage(HTMLCS.NOTICE, table, 'This table appears to be a layout table. If it is meant to instead be a data table, ensure header cells are identified using th elements.', 'LayoutTable');
		} else {
			HTMLCS.addMessage(HTMLCS.NOTICE, table, 'This table appears to be a data table. If it is meant to instead be a layout table, ensure there are no th elements, and no summary or caption.', 'DataTable');
		}
	}
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_3_1_3_1_A = {
    _labelNames: null,

    register: function()
    {
        return [
            '_top'
        ];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        var sniff = HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_3_1_3_1;

        if (element === top) {
            sniff.testHeadingOrder(top, HTMLCS.WARNING);
        }

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_3_1_3_1_AAA = {
    _labelNames: null,

    register: function()
    {
        return [
            '_top'
        ];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        var sniff = HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_3_1_3_1;

        if (element === top) {
            sniff.testHeadingOrder(top, HTMLCS.ERROR);
        }

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_3_1_3_2 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['_top'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, top, 'Check that the content is ordered in a meaningful sequence when linearised, such as when style sheets are disabled.', 'G57');

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_3_1_3_3 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['_top'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, top, 'Where instructions are provided for understanding the content, do not rely on sensory characteristics alone (such as shape, size or location) to describe objects.', 'G96');

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_4_1_4_1 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['_top'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, top, 'Check that any information conveyed using colour alone is also available in text, or through other visual cues.', 'G14,G182');

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_4_1_4_2 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return [
            'object',
            'embed',
            'applet',
            'bgsound',
            'audio',
            'video'
        ];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, top, 'If this element contains audio that plays automatically for longer than 3 seconds, check that there is the ability to pause, stop or mute the audio.', 'F23');

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_4_1_4_3 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['_top'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        if (element === top) {
            var failures = HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_4_1_4_3_Contrast.testContrastRatio(top, 4.5, 3.0);

            for (var i = 0; i < failures.length; i++) {
                var element   = failures[i].element;

                var decimals  = 2;
                var value     = (Math.round(failures[i].value * Math.pow(10, decimals)) / Math.pow(10, decimals));
                var required  = failures[i].required;
                var recommend = failures[i].recommendation;
                var hasBgImg  = failures[i].hasBgImage || false;
                var bgColour   = failures[i].bgColour || false;
                var isAbsolute = failures[i].isAbsolute || false;

                // If the values would look identical, add decimals to the value.
                while (required === value) {
                    decimals++;
                    value = (Math.round(failures[i].value * Math.pow(10, decimals)) / Math.pow(10, decimals));
                }

                if (required === 4.5) {
                    var code = 'G18';
                } else if (required === 3.0) {
                    var code = 'G145';
                }

                var recommendText = [];
                if (recommend) {
                    if (recommend.fore.from !== recommend.fore.to) {
                        recommendText.push('text colour to ' + recommend.fore.to);
                    }
                    if (recommend.back.from !== recommend.back.to) {
                        recommendText.push('background to ' + recommend.back.to);
                    }
                }//end if

                if (recommendText.length > 0) {
                    recommendText = ' Recommendation: change ' + recommendText.join(', ') + '.';
                }

                if (isAbsolute === true) {
                    code += '.Abs';
                    HTMLCS.addMessage(HTMLCS.WARNING, element, 'This element is absolutely positioned and the background color can not be determined. Ensure the contrast ratio between the text and all covered parts of the background are at least ' + required + ':1.', code);
                } else if (hasBgImg === true) {
                    code += '.BgImage';
                    HTMLCS.addMessage(HTMLCS.WARNING, element, 'This element\'s text is placed on a background image. Ensure the contrast ratio between the text and all covered parts of the image are at least ' + required + ':1.', code);
                } else {
                    code += '.Fail';
                    HTMLCS.addMessage(HTMLCS.ERROR, element, 'This element has insufficient contrast at this conformance level. Expected a contrast ratio of at least ' + required + ':1, but text in this element has a contrast ratio of ' + value + ':1.' + recommendText, code);
                }//end if
            }//end for
        }//end if
    }
};



_global.HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_4_1_4_3_Contrast = {
    testContrastRatio: function (top, minContrast, minLargeContrast)
    {
        var startDate = new Date();
        var count     = 0;
        var xcount    = 0;
        var failures  = [];

        if (!top.ownerDocument) {
            var toProcess = [top.getElementsByTagName('body')[0]];
        } else {
            var toProcess = [top];
        }

        while (toProcess.length > 0) {
            var node = toProcess.shift();

            // This is an element.
            if (node && (node.nodeType === 1) && (HTMLCS.util.isVisuallyHidden(node) === false) && (HTMLCS.util.isDisabled(node) === false)) {
                var processNode = false;
                for (var i = 0; i < node.childNodes.length; i++) {
                    // Load up new nodes, but also only process this node when
                    // there are direct text elements.
                    if (node.childNodes[i].nodeType === 1) {
                        toProcess.push(node.childNodes[i]);
                    } else if (node.childNodes[i].nodeType === 3) {
                        if (HTMLCS.util.trim(node.childNodes[i].nodeValue) !== '') {
                            processNode = true;
                        }
                    }
                }

                if (processNode === true) {
                    var style = HTMLCS.util.style(node);

                    if (style) {
                        var bgColour   = style.backgroundColor;
                        var foreColour = style.color;
                        var bgElement  = node;
                        var hasBgImg   = false;
                        var isAbsolute = false;

			if (style.backgroundImage !== 'none') {
                            hasBgImg = true;
                        }

                        if (style.position == 'absolute') {
                            isAbsolute = true;
                        }

                        var parent = node.parentNode;

                        // Calculate font size. Note that CSS 2.1 fixes a reference pixel
                        // as 96 dpi (hence "pixel ratio" workarounds for Hi-DPI devices)
                        // so this calculation should be safe.
                        var fontSize     = parseFloat(style.fontSize, 10) * (72 / 96);
                        var minLargeSize = 18;

                        if ((style.fontWeight === 'bold') || (parseInt(style.fontWeight, 10) >= 600)) {
                            var minLargeSize = 14;
                        }

                        var reqRatio = minContrast;
                        if (fontSize >= minLargeSize) {
                            reqRatio = minLargeContrast;
                        }

                        // Check for a solid background colour.
                        while ((bgColour === 'transparent') || (bgColour === 'rgba(0, 0, 0, 0)')) {
                            if ((!parent) || (!parent.ownerDocument)) {
                                break;
                            }

                            var parentStyle = HTMLCS.util.style(parent);
                            var bgColour    = parentStyle.backgroundColor;
                            if (parentStyle.backgroundImage !== 'none') {
                                hasBgImg = true;
                            }
                            if (parentStyle.position == 'absolute') {
                                isAbsolute = true;
                            }

                            parent = parent.parentNode;
                        }//end while

                        if (hasBgImg === true) {
                            // If we have a background image, skip the contrast ratio checks,
                            // and push a warning instead.
                            failures.push({
                                element: node,
                                colour: style.color,
                                bgColour: undefined,
                                value: undefined,
                                required: reqRatio,
                                hasBgImage: true
                            });
                            continue;
                        } else if (isAbsolute === true) {
                            failures.push({
                                element: node,
                                colour: foreColour,
                                bgColour: undefined,
                                value: undefined,
                                required: reqRatio,
                                isAbsolute: true
                            });
                            continue;
                        } else if ((bgColour === 'transparent') || (bgColour === 'rgba(0, 0, 0, 0)')) {
                            // If the background colour is still transparent, this is probably
                            // a fragment with which we cannot reliably make a statement about
                            // contrast ratio. Skip the element.
                            continue;
                        }

                        var contrastRatio = HTMLCS.util.contrastRatio(bgColour, style.color);
                        if (contrastRatio < reqRatio) {
                            var recommendation = this.recommendColour(bgColour, style.color, reqRatio);

                            failures.push({
                                element: node,
                                colour: style.color,
                                bgColour: bgColour,
                                value: contrastRatio,
                                required: reqRatio,
                                recommendation: recommendation
                            });
                        }//end if
                    }//end if
                }//end if
            }//end if
        }//end while

        return failures;
    },

    recommendColour: function(back, fore, target) {
        // Canonicalise the colours.
        var fore = HTMLCS.util.RGBtoColourStr(HTMLCS.util.colourStrToRGB(fore));
        var back = HTMLCS.util.RGBtoColourStr(HTMLCS.util.colourStrToRGB(back));

        var cr = HTMLCS.util.contrastRatio(fore, back);
        var foreDiff = Math.abs(HTMLCS.util.relativeLum(fore) - 0.5);
        var backDiff = Math.abs(HTMLCS.util.relativeLum(back) - 0.5);

        var recommendation = null;

        if (cr < target) {
            // Work out which colour has more room to move.
            // If they are the same, prefer changing the foreground colour.
            var multiplier = (1 + 1 / 400);
            if (foreDiff <= backDiff) {
                var change = 'back';
                var newCol = back;
                if (HTMLCS.util.relativeLum(back) < 0.5) {
                    var multiplier = (1 / multiplier);
                }
            } else {
                var change = 'fore';
                var newCol = fore;
                if (HTMLCS.util.relativeLum(fore) < 0.5) {
                    var multiplier = (1 / multiplier);
                }
            }

            var hsv     = HTMLCS.util.sRGBtoHSV(newCol);
            var chroma  = hsv.saturation * hsv.value;
            var newFore = fore;
            var newBack = back;
            var changed = false;

            var i = 0;

            while (cr < target) {
                if ((newCol === '#fff') || (newCol === '#000')) {
                    // Couldn't go far enough. Reset and try the other colour.
                    if (changed === true) {
                        // We've already switched colours, so we have to start
                        // winding back the other colour.
                        if (change === 'fore') {
                            var oldBack = newBack;
                            var j = 1;
                            while (newBack === oldBack) {
                                var newBack = this.multiplyColour(newBack, Math.pow(1 / multiplier, j));
                                j++;
                            }
                        } else {
                            var oldFore = newFore;
                            var j = 1;
                            while (newFore === oldFore) {
                                var newFore = this.multiplyColour(newFore, Math.pow(1 / multiplier, j));
                                j++;
                            }
                        }
                    } else {
                        newFore = fore;
                        newBack = back;
                        multiplier = 1 / multiplier;
                        if (change === 'fore') {
                            change = 'back';
                            var hsv = back;
                        } else {
                            change = 'fore';
                            var hsv = fore;
                        }

                        hsv     = HTMLCS.util.sRGBtoHSV(hsv);
                        chroma  = hsv.saturation * hsv.value;
                        changed = true;
                    }
                }

                i++;
                var newCol = HTMLCS.util.HSVtosRGB(hsv);
                var newCol = this.multiplyColour(newCol, Math.pow(multiplier, i));

                if (change === 'fore') {
                    var newFore = newCol;
                } else {
                    var newBack = newCol;
                }

                var cr = HTMLCS.util.contrastRatio(newFore, newBack);
            }//end while

            recommendation = {
                fore: {
                    from: fore,
                    to: newFore
                },
                back: {
                    from: back,
                    to: newBack
                }
            }
        }//end if

        return recommendation;
    },

    multiplyColour: function(colour, multiplier) {
        var hsvColour = HTMLCS.util.sRGBtoHSV(colour);
        var chroma    = hsvColour.saturation * hsvColour.value;

        // If we are starting from black, start it from #010101 instead.
        if (hsvColour.value === 0) {
            hsvColour.value = (1 / 255);
        }

        hsvColour.value = hsvColour.value * multiplier;
        if (hsvColour.value === 0) {
            hsvColour.saturation = 0;
        } else {
            hsvColour.saturation = chroma / hsvColour.value;
        }

        hsvColour.value      = Math.min(1, hsvColour.value);
        hsvColour.saturation = Math.min(1, hsvColour.saturation);

        var newColour = HTMLCS.util.RGBtoColourStr(HTMLCS.util.HSVtosRGB(hsvColour));
        return newColour;
    }
}


_global.HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_4_1_4_3_F24 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['_top'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        // Test for background/foreground stuff.
        var elements = HTMLCS.util.getAllElements(top, '*');
        for (var i = 0; i < elements.length; i++) {
            this.testColourComboFail(elements[i]);
        }
    },

    /**
     * Tests for setting foreground without background, or vice versa (failure F24).
     *
     * It is a failure for a background colour to be set without a foreground colour,
     * or vice versa. A user agent style sheet could try and set both, and because
     * one is overridden, the result could be unreadable.
     *
     * This is being thrown as a warning, not an error. The failure allows the FG
     * and BG colours to be set further up the chain, as long as the content has both
     * foreground and background colours set by  the time.

     * Further, we can only test inline styles (either through attributes, CSS, or
     * JavaScript setting through eg. jQuery) because computed styles cause issues.
     * For instance, if no user style sheet is set, the default stylesheet (in
     * Firefox) at least is "transparent background/black text", and this would show
     * up in the computed style (and fail, since transparent is "not set"). The F24
     * description (by my reading) allows the colours to be set further up the chain,
     * as long as the content has -a- BG and -a- FG colour.
     *
     * @param Node element The element to test.
     */
    testColourComboFail: function(element)
    {
        var hasFg = element.hasAttribute('color');
        hasFg     = hasFg || element.hasAttribute('link');
        hasFg     = hasFg || element.hasAttribute('vlink');
        hasFg     = hasFg || element.hasAttribute('alink');
        var hasBg = element.hasAttribute('bgcolor');

        if (element.style) {
            var fgStyle = element.style.color;
            var bgStyle = element.style.background;

            if ((fgStyle !== '') && (fgStyle !== 'auto')) {
                hasFg = true;
            }

            if ((bgStyle !== '') && (bgStyle !== 'auto')) {
                hasBg = true;
            }
        }//end if

        if (hasBg !== hasFg) {
            if (hasBg === true) {
                HTMLCS.addMessage(HTMLCS.WARNING, element, 'Check that this element has an inherited foreground colour to complement the corresponding inline background colour or image.', 'F24.BGColour');
            } else {
                HTMLCS.addMessage(HTMLCS.WARNING, element, 'Check that this element has an inherited background colour or image to complement the corresponding inline foreground colour.', 'F24.FGColour');
            }
        }
    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_4_1_4_4 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['_top'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, top, 'Check that text can be resized without assistive technology up to 200 percent without loss of content or functionality.', 'G142');

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_4_1_4_5 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['_top'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        var imgObj = top.querySelector('img');

        if (imgObj !== null) {
            HTMLCS.addMessage(HTMLCS.NOTICE, top, 'If the technologies being used can achieve the visual presentation, check that text is used to convey information rather than images of text, except when the image of text is essential to the information being conveyed, or can be visually customised to the user\'s requirements.', 'G140,C22,C30.AALevel');
        }

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_4_1_4_6 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['_top'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        if (element === top) {
            var failures = HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_4_1_4_3_Contrast.testContrastRatio(top, 7.0, 4.5);

            for (var i = 0; i < failures.length; i++) {
                var element   = failures[i].element;

                var decimals  = 2;
                var value     = (Math.round(failures[i].value * Math.pow(10, decimals)) / Math.pow(10, decimals));
                var required  = failures[i].required;
                var recommend = failures[i].recommendation;
                var hasBgImg  = failures[i].hasBgImage || false;
                var isAbsolute = failures[i].isAbsolute || false;

                // If the values would look identical, add decimals to the value.
                while (required === value) {
                    decimals++;
                    value = (Math.round(failures[i].value * Math.pow(10, decimals)) / Math.pow(10, decimals));
                }

                if (required === 4.5) {
                    var code = 'G18';
                } else if (required === 7.0) {
                    var code = 'G17';
                }

                var recommendText = [];
                if (recommend) {
                    if (recommend.fore.from !== recommend.fore.to) {
                        recommendText.push('text colour to ' + recommend.fore.to);
                    }
                    if (recommend.back.from !== recommend.back.to) {
                        recommendText.push('background to ' + recommend.back.to);
                    }
                }//end if

                if (recommendText.length > 0) {
                    recommendText = ' Recommendation: change ' + recommendText.join(', ') + '.';
                }

                if (isAbsolute === true) {
                    code += '.Abs';
                    HTMLCS.addMessage(HTMLCS.WARNING, element, 'This element is absolutely positioned and the background color can not be determined. Ensure the contrast ratio between the text and all covered parts of the background are at least ' + required + ':1.', code);
                } else if (hasBgImg === true) {
                    code += '.BgImage';
                    HTMLCS.addMessage(HTMLCS.WARNING, element, 'This element\'s text is placed on a background image. Ensure the contrast ratio between the text and all covered parts of the image are at least ' + required + ':1.', code);
                } else {
                    code += '.Fail';
                    HTMLCS.addMessage(HTMLCS.ERROR, element, 'This element has insufficient contrast at this conformance level. Expected a contrast ratio of at least ' + required + ':1, but text in this element has a contrast ratio of ' + value + ':1.' + recommendText, code);
                }//end if
            }//end for
        }//end if
    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_4_1_4_7 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return [
            'object',
            'embed',
            'applet',
            'bgsound',
            'audio'
        ];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, element, 'For pre-recorded audio-only content in this element that is primarily speech (such as narration), any background sounds should be muteable, or be at least 20 dB (or about 4 times) quieter than the speech.', 'G56');

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_4_1_4_8 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['_top'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        // This Success Criterion has five prongs, and each should be thrown as a
        // separate notice as separate techniques apply to each.
        HTMLCS.addMessage(HTMLCS.NOTICE, top, 'Check that a mechanism is available for the user to select foreground and background colours for blocks of text, either through the Web page or the browser.', 'G148,G156,G175');
        HTMLCS.addMessage(HTMLCS.NOTICE, top, 'Check that a mechanism exists to reduce the width of a block of text to no more than 80 characters (or 40 in Chinese, Japanese or Korean script).', 'H87,C20');
        HTMLCS.addMessage(HTMLCS.NOTICE, top, 'Check that blocks of text are not fully justified - that is, to both left and right edges - or a mechanism exists to remove full justification.', 'C19,G172,G169');
        HTMLCS.addMessage(HTMLCS.NOTICE, top, 'Check that line spacing in blocks of text are at least 150% in paragraphs, and paragraph spacing is at least 1.5 times the line spacing, or that a mechanism is available to achieve this.', 'G188,C21');
        HTMLCS.addMessage(HTMLCS.NOTICE, top, 'Check that text can be resized without assistive technology up to 200 percent without requiring the user to scroll horizontally on a full-screen window.', 'H87,G146,C26');

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_4_1_4_9 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['_top'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        var imgObj = top.querySelector('img');

        if (imgObj !== null) {
            HTMLCS.addMessage(HTMLCS.NOTICE, top, 'Check that images of text are only used for pure decoration or where a particular presentation of text is essential to the information being conveyed.', 'G140,C22,C30.NoException');
        }
    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle2_Guideline2_1_2_1_1 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['_top'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        // Testing for elements that have explicit attributes for mouse-specific
        // events. Note: onclick is considered keyboard accessible, as it is actually
        // tied to the default action of a link or button - not merely a click.
        if (element === top) {

            // Cannot detect event listeners here so only onclick attributes are checked.
            var keyboardTriggers = HTMLCS.util.getAllElements(top, '*[onclick], *[onkeyup], *[onkeydown], *[onkeypress], *[onfocus], *[onblur]');
            keyboardTriggers.forEach(function(elem) {
                if (HTMLCS.util.isKeyboardNavigable(elem) === false) {
                    HTMLCS.addMessage(
                        HTMLCS.WARNING,
                        elem,
                        'Ensure the functionality provided by an event handler for this element is available through the keyboard',
                        'G90'
                    );
                }
            });

            var dblClickEls = HTMLCS.util.getAllElements(top, '*[ondblclick]');
            for (var i = 0; i < dblClickEls.length; i++) {
                HTMLCS.addMessage(HTMLCS.WARNING, dblClickEls[i], 'Ensure the functionality provided by double-clicking on this element is available through the keyboard.', 'SCR20.DblClick');
            }

            var mouseOverEls = HTMLCS.util.getAllElements(top, '*[onmouseover]');
            for (var i = 0; i < mouseOverEls.length; i++) {
                HTMLCS.addMessage(HTMLCS.WARNING, mouseOverEls[i], 'Ensure the functionality provided by mousing over this element is available through the keyboard; for instance, using the focus event.', 'SCR20.MouseOver');
            }

            var mouseOutEls = HTMLCS.util.getAllElements(top, '*[onmouseout]');
            for (var i = 0; i < mouseOutEls.length; i++) {
                HTMLCS.addMessage(HTMLCS.WARNING, mouseOutEls[i], 'Ensure the functionality provided by mousing out of this element is available through the keyboard; for instance, using the blur event.', 'SCR20.MouseOut');
            }

            var mouseMoveEls = HTMLCS.util.getAllElements(top, '*[onmousemove]');
            for (var i = 0; i < mouseMoveEls.length; i++) {
                HTMLCS.addMessage(HTMLCS.WARNING, mouseMoveEls[i], 'Ensure the functionality provided by moving the mouse on this element is available through the keyboard.', 'SCR20.MouseMove');
            }

            var mouseDownEls = HTMLCS.util.getAllElements(top, '*[onmousedown]');
            for (var i = 0; i < mouseDownEls.length; i++) {
                HTMLCS.addMessage(HTMLCS.WARNING, mouseDownEls[i], 'Ensure the functionality provided by mousing down on this element is available through the keyboard; for instance, using the keydown event.', 'SCR20.MouseDown');
            }

            var mouseUpEls = HTMLCS.util.getAllElements(top, '*[onmouseup]');
            for (var i = 0; i < mouseUpEls.length; i++) {
                HTMLCS.addMessage(HTMLCS.WARNING, mouseUpEls[i], 'Ensure the functionality provided by mousing up on this element is available through the keyboard; for instance, using the keyup event.', 'SCR20.MouseUp');
            }
        }

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle2_Guideline2_1_2_1_2 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return [
            'object',
            'applet',
            'embed'
        ];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.WARNING, element, 'Check that this applet or plugin provides the ability to move the focus away from itself when using the keyboard.', 'F10');

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle2_Guideline2_3_2_3_1 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['_top'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        // The "small" flashing area is deliberately vague - users should see
        // technique G176 for more details, as the threshold depends on both the
        // size and resolution of a screen.
        // The technique gives a baseline (based on a 15-17 inch monitor read at
        // 22-26 inches, at 1024 x 768 resolution). A 10-degree field of vision is
        // approximately 341 x 256 pixels in this environment, and a flashing area
        // needs to be no more than 25% of this (not necessarily rectangular).
        HTMLCS.addMessage(HTMLCS.NOTICE, top, 'Check that no component of the content flashes more than three times in any 1-second period, or that the size of any flashing area is sufficiently small.', 'G19,G176');

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle2_Guideline2_3_2_3_2 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['_top'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, top, 'Check that no component of the content flashes more than three times in any 1-second period.', 'G19');

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle2_Guideline2_2_2_2_1 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['meta'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        // Meta refresh testing under H76/F41. Fails if a non-zero timeout is provided.
        // NOTE: H76 only lists criterion 3.2.5, but F41 also covers refreshes to
        // same page (no URL content), which is covered by non-adjustable timeouts
        // in criterion 2.2.1.
        if (element.hasAttribute('http-equiv') === true) {
            if ((String(element.getAttribute('http-equiv'))).toLowerCase() === 'refresh') {
                if (/^[1-9]\d*/.test(element.getAttribute('content').toLowerCase()) === true) {
                    if (/url=/.test(element.getAttribute('content').toLowerCase()) === true) {
                        // Redirect.
                        HTMLCS.addMessage(HTMLCS.ERROR, element, 'Meta refresh tag used to redirect to another page, with a time limit that is not zero. Users cannot control this time limit.', 'F40.2');
                    } else {
                        // Just a refresh.
                        HTMLCS.addMessage(HTMLCS.ERROR, element, 'Meta refresh tag used to refresh the current page. Users cannot control the time limit for this refresh.', 'F41.2');
                    }
                }
            }//end if
        }//end if

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle2_Guideline2_2_2_2_2 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return [
            '_top',
            'blink'
        ];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        if (element === top) {
            HTMLCS.addMessage(HTMLCS.NOTICE, element, 'If any part of the content moves, scrolls or blinks for more than 5 seconds, or auto-updates, check that there is a mechanism available to pause, stop, or hide the content.', 'SCR33,SCR22,G187,G152,G186,G191');

            var elements = HTMLCS.util.getAllElements(top, '*');
            for (var i = 0; i < elements.length; i++) {
                var computedStyle = HTMLCS.util.style(elements[i]);

                if (computedStyle) {
                    if (/blink/.test(computedStyle['text-decoration']) === true) {
                        HTMLCS.addMessage(HTMLCS.WARNING, elements[i], 'Ensure there is a mechanism available to stop this blinking element in less than five seconds.', 'F4');
                    }
                }
            }//end for
        } else if (element.nodeName.toLowerCase() === 'blink') {
            HTMLCS.addMessage(HTMLCS.ERROR, element, 'Blink elements cannot satisfy the requirement that blinking information can be stopped within five seconds.', 'F47');
        }//end if

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle2_Guideline2_2_2_2_3 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['_top'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, element, 'Check that timing is not an essential part of the event or activity presented by the content, except for non-interactive synchronized media and real-time events.', 'G5');

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle2_Guideline2_2_2_2_4 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['_top'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, element, 'Check that all interruptions (including updates to content) can be postponed or suppressed by the user, except interruptions involving an emergency.', 'SCR14');

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle2_Guideline2_2_2_2_5 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['_top'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, element, 'If this Web page is part of a set of Web pages with an inactivity time limit, check that an authenticated user can continue the activity without loss of data after re-authenticating.', 'G105,G181');

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle2_Guideline2_4_2_4_1 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return [
            'iframe',
            'a',
            'area',
            '_top'
        ];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        if (element === top) {
            this.testGenericBypassMsg(top);
        } else {
            var nodeName = element.nodeName.toLowerCase();

            switch (nodeName) {
                case 'iframe':
                    this.testIframeTitle(element);
                break;

                case 'a':
                case 'area':
                    this.testSameDocFragmentLinks(element, top);
                break;
            }
        }
    },

    /**
     * Test for the presence of title attributes on the iframe element (technique H64).
     *
     * @param {DOMNode} element The element to test.
     *
     * @returns void
     */
    testIframeTitle: function(element)
    {
        var nodeName = element.nodeName.toLowerCase();

        if (nodeName === 'iframe') {
            var hasTitle = false;
            if (element.hasAttribute('title') === true) {
                if (element.getAttribute('title') && (/^\s+$/.test(element.getAttribute('title')) === false)) {
                    hasTitle = true;
                }
            }

            if (hasTitle === false) {
                HTMLCS.addMessage(HTMLCS.ERROR, element, 'Iframe element requires a non-empty title attribute that identifies the frame.', 'H64.1');
            } else {
                HTMLCS.addMessage(HTMLCS.NOTICE, element, 'Check that the title attribute of this element contains text that identifies the frame.', 'H64.2');
            }
        }//end if
    },

    /**
     * Throw a generic bypass blocks message.
     *
     * @param {DOMNode} top Top element of the testing source.
     *
     * @returns void
     */
    testGenericBypassMsg: function(top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, top, 'Ensure that any common navigation elements can be bypassed; for instance, by use of skip links, header elements, or ARIA landmark roles.', 'G1,G123,G124,H69');
    },

    /**
     * Test for document fragment links to IDs that do not exist.
     *
     * These are links of the form "<a href="#content">", where the ID "content" does
     * not exist. Area elements in image maps are also tested, as they are also
     * likely to contain these attributes.
     *
     * @param {DOMNode} element The element to test.
     * @param {DOMNode} top     Top element of the testing source.
     *
     * @returns void
     */
    testSameDocFragmentLinks: function(element, top)
    {
        if (element.hasAttribute('href') === true) {
            var href = element.getAttribute('href');
            href     = HTMLCS.util.trim(href);
            if ((href.length > 1) && (href.charAt(0) === '#')) {
                var id = href.substr(1);

                try {
                    var doc = top;
                    if (doc.ownerDocument) {
                        doc = doc.ownerDocument;
                    }

                    // First search for an element with the appropriate ID, then search for a
                    // named anchor using the name attribute.
                    var target = doc.getElementById(id);
                    if (target === null) {
                        var _doc         = HTMLCS.util.getElementWindow(top).document;
                        var doctype      = HTMLCS.util.getDocumentType(_doc);
                        var nameSelector = 'a';

                        if (doctype && doctype.indexOf('html5') === -1) {
                            nameSelector = '*';
                        }

                        target = doc.querySelector(nameSelector + '[name="' + id + '"]');
                    }

                    if ((target === null) || (HTMLCS.util.contains(top, target) === false)) {
                        if ((HTMLCS.isFullDoc(top) === true) || (top.nodeName.toLowerCase() === 'body')) {
                            HTMLCS.addMessage(HTMLCS.ERROR, element, 'This link points to a named anchor "' + id + '" within the document, but no anchor exists with that name.', 'G1,G123,G124.NoSuchID');
                        } else {
                            HTMLCS.addMessage(HTMLCS.WARNING, element, 'This link points to a named anchor "' + id + '" within the document, but no anchor exists with that name in the fragment tested.', 'G1,G123,G124.NoSuchIDFragment');
                        }
                    }
                } catch (ex) {
                }//end try
            }//end if
        }
    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle2_Guideline2_4_2_4_2 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['html'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        // Find a head first.
        var children = element.childNodes;
        var head     = null;

        for (var i = 0; i < children.length; i++) {
            if (children[i].nodeName.toLowerCase() === 'head') {
                head = children[i];
                break;
            }
        }

        if (head === null) {
            HTMLCS.addMessage(HTMLCS.ERROR, element, 'There is no head section in which to place a descriptive title element.', 'H25.1.NoHeadEl');
        } else {
            var children = head.childNodes;
            var title    = null;

            for (var i = 0; i < children.length; i++) {
                if (children[i].nodeName.toLowerCase() === 'title') {
                    title = children[i];
                    break;
                }
            }

            if (title === null) {
                HTMLCS.addMessage(HTMLCS.ERROR, head, 'A title should be provided for the document, using a non-empty title element in the head section.', 'H25.1.NoTitleEl');
            } else {
                if (/^\s*$/.test(title.innerHTML) === true) {
                    HTMLCS.addMessage(HTMLCS.ERROR, title, 'The title element in the head section should be non-empty.', 'H25.1.EmptyTitle');
                } else {
                    HTMLCS.addMessage(HTMLCS.NOTICE, title, 'Check that the title element describes the document.', 'H25.2');
                }
            }//end if
        }//end if

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle2_Guideline2_4_2_4_3 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['_top'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        if (element === top) {
            var tabIndexExists = top.querySelector('*[tabindex]');

            if (tabIndexExists) {
                HTMLCS.addMessage(HTMLCS.NOTICE, element, 'If tabindex is used, check that the tab order specified by the tabindex attributes follows relationships in the content.', 'H4.2');
            }
        }
    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle2_Guideline2_4_2_4_4 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['a'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        if (element.hasAttribute('title') === true) {
            HTMLCS.addMessage(HTMLCS.NOTICE, element, 'Check that the link text combined with programmatically determined link context, or its title attribute, identifies the purpose of the link.', 'H77,H78,H79,H80,H81,H33');
        } else {
            HTMLCS.addMessage(HTMLCS.NOTICE, element, 'Check that the link text combined with programmatically determined link context identifies the purpose of the link.', 'H77,H78,H79,H80,H81');
        }

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle2_Guideline2_4_2_4_5 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['_top'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, element, 'If this Web page is not part of a linear process, check that there is more than one way of locating this Web page within a set of Web pages.', 'G125,G64,G63,G161,G126,G185');

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle2_Guideline2_4_2_4_6 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['_top'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, element, 'Check that headings and labels describe topic or purpose.', 'G130,G131');

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle2_Guideline2_4_2_4_7 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['_top'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        // Fire this notice if there appears to be an input field or link on the page
        // (which will be just about anything). Links are important because they can
        // still be tabbed to.
        var inputField = top.querySelector('input, textarea, button, select, a');

        if (inputField !== null) {
            HTMLCS.addMessage(HTMLCS.NOTICE, top, 'Check that there is at least one mode of operation where the keyboard focus indicator can be visually located on user interface controls.', 'G149,G165,G195,C15,SCR31');
        }

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle2_Guideline2_4_2_4_8 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['link'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        var linkParentName = element.parentNode.nodeName.toLowerCase();

        // Check for the correct location. HTML4 states "it may only appear in the
        // HEAD element". HTML5 states it appears "wherever metadata content is
        // expected", which only includes the head element.
        if (linkParentName !== 'head') {
            HTMLCS.addMessage(HTMLCS.ERROR, element, 'Link elements can only be located in the head section of the document.', 'H59.1');
        }

        // Check for mandatory elements.
        if ((element.hasAttribute('rel') === false) || (!element.getAttribute('rel')) || (/^\s*$/.test(element.getAttribute('rel')) === true)) {
            HTMLCS.addMessage(HTMLCS.ERROR, element, 'Link element is missing a non-empty rel attribute identifying the link type.', 'H59.2a');
        }

        if ((element.hasAttribute('href') === false) || (!element.getAttribute('href')) || (/^\s*$/.test(element.getAttribute('href')) === true)) {
            HTMLCS.addMessage(HTMLCS.ERROR, element, 'Link element is missing a non-empty href attribute pointing to the resource being linked.', 'H59.2b');
        }
    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle2_Guideline2_4_2_4_9 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['a'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, element, 'Check that text of the link describes the purpose of the link.', 'H30');

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle3_Guideline3_1_3_1_1 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['html'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        if ((element.hasAttribute('lang') === false) && (element.hasAttribute('xml:lang') === false)) {
            // TODO: if we can tell whether it's HTML or XHTML, we should split this
            // into two - one asking for "lang", the other for "xml:lang".
            HTMLCS.addMessage(HTMLCS.ERROR, element, 'The html element should have a lang or xml:lang attribute which describes the language of the document.', 'H57.2');
        } else {
            if (element.hasAttribute('lang') === true) {
                var lang = element.getAttribute('lang');
                if (this.isValidLanguageTag(lang) === false) {
                    HTMLCS.addMessage(HTMLCS.ERROR, top, 'The language specified in the lang attribute of the document element does not appear to be well-formed.', 'H57.3.Lang');
                }
            }

            if (element.hasAttribute('xml:lang') === true) {
                var lang = element.getAttribute('xml:lang');
                if (this.isValidLanguageTag(lang) === false) {
                    HTMLCS.addMessage(HTMLCS.ERROR, top, 'The language specified in the xml:lang attribute of the document element does not appear to be well-formed.', 'H57.3.XmlLang');
                }
            }
        }

    },


    /**
     * Test for well-formed language tag as per IETF BCP 47.
     *
     * Note that this checks only for well-formedness, not whether the subtags are
     * actually on the registered subtags list.
     *
     * @param {String} langTag The language tag to test.
     *
     * @returns {Boolean} the result of the regex test.
     */
    isValidLanguageTag: function(langTag)
    {
        // Allow irregular or private-use tags starting with 'i' or 'x'.
        // Values after it are 1-8 alphanumeric characters.
        var regexStr = '^([ix](-[a-z0-9]{1,8})+)$|';

        // Core language tags - 2 to 8 letters
        regexStr += '^[a-z]{2,8}';

        // Extlang subtags - three letters, repeated 0 to 3 times
        regexStr += '(-[a-z]{3}){0,3}';

        // Script subtag - four letters, optional.
        regexStr += '(-[a-z]{4})?';

        // Region subtag - two letters for a country or a three-digit region; optional.
        regexStr += '(-[a-z]{2}|-[0-9]{3})?';

        // Variant subtag - either digit + 3 alphanumeric, or
        // 5-8 alphanumeric where it doesn't start with a digit; optional
        // but repeatable.
        regexStr += '(-[0-9][a-z0-9]{3}|-[a-z0-9]{5,8})*';

        // Extension subtag - one single alphanumeric character (but not "x"),
        // followed by at least one value of 2-8 alphanumeric characters.
        // The whole thing is optional but repeatable (for different extensions).
        regexStr += '(-[a-wy-z0-9](-[a-z0-9]{2,8})+)*';

        // Private use subtag, starting with an "x" and containing at least one
        // value of 1-8 alphanumeric characters. It must come last.
        regexStr += '(-x(-[a-z0-9]{1,8})+)?$';

        // Make a regex out of it, and make it all case-insensitive.
        var regex = new RegExp(regexStr, 'i');

        // Throw the correct lang code depending on whether this is a document
        // element or not.
        var valid = true;
        if (regex.test(langTag) === false) {
            valid = false;
        }

        return valid;
    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle3_Guideline3_1_3_1_2 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['_top'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        // Generic message for changes in language.
        HTMLCS.addMessage(HTMLCS.NOTICE, top, 'Ensure that any change in language is marked using the lang and/or xml:lang attribute on an element, as appropriate.', 'H58');

        // Alias the SC 3.1.1 object, which contains our "valid language tag" test.
        var sc3_1_1 = HTMLCS_WCAG2AAA_Sniffs_Principle3_Guideline3_1_3_1_1;

        // Note, going one element beyond the end, so we can test the top element
        // (which doesn't get picked up by the above query). Instead of going off the
        // cliff of the collection, the last loop (i === langEls.length) checks the
        // top element.
        var langEls = HTMLCS.util.getAllElements(top, '*[lang]');
        for (var i = 0; i <= langEls.length; i++) {
            if (i === langEls.length) {
                var langEl = top;
            } else {
                var langEl = langEls[i];
            }

            // Skip html nodes, they are covered by 3.1.1.
            // Also skip if the top element is the document element.
            if ((!langEl.documentElement) && (langEl.nodeName.toLowerCase() !== 'html')) {
                if (langEl.hasAttribute('lang') === true) {
                    var lang = langEl.getAttribute('lang');
                    if (sc3_1_1.isValidLanguageTag(lang) === false) {
                        HTMLCS.addMessage(HTMLCS.ERROR, langEl, 'The language specified in the lang attribute of this element does not appear to be well-formed.', 'H58.1.Lang');
                    }
                }

                if (langEl.hasAttribute('xml:lang') === true) {
                    var lang = langEl.getAttribute('xml:lang');
                    if (sc3_1_1.isValidLanguageTag(lang) === false) {
                        HTMLCS.addMessage(HTMLCS.ERROR, langEl, 'The language specified in the xml:lang attribute of this element does not appear to be well-formed.', 'H58.1.XmlLang');
                    }
                }
            }//end if
        }//end for
    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle3_Guideline3_1_3_1_3 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['_top'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, top, 'Check that there is a mechanism available for identifying specific definitions of words or phrases used in an unusual or restricted way, including idioms and jargon.', 'H40,H54,H60,G62,G70');

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle3_Guideline3_1_3_1_4 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['_top'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, top, 'Check that a mechanism for identifying the expanded form or meaning of abbreviations is available.', 'G102,G55,G62,H28,G97');

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle3_Guideline3_1_3_1_5 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['_top'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, top, 'Where the content requires reading ability more advanced than the lower secondary education level, supplemental content or an alternative version should be provided.', 'G86,G103,G79,G153,G160');

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle3_Guideline3_1_3_1_6 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['ruby'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        var rb = element.querySelectorAll('rb');
        var rt = element.querySelectorAll('rt');
        if (rt.length === 0) {
            // Vary the message depending on whether an rb element exists. If it doesn't,
            // the presumption is that we are using HTML5 that uses the body of the ruby
            // element for the same purpose (otherwise, assume XHTML 1.1 with rb element).
            if (rb.length === 0) {
                HTMLCS.addMessage(HTMLCS.ERROR, element, 'Ruby element does not contain an rt element containing pronunciation information for its body text.', 'H62.1.HTML5');
            } else {
                HTMLCS.addMessage(HTMLCS.ERROR, element, 'Ruby element does not contain an rt element containing pronunciation information for the text inside the rb element.', 'H62.1.XHTML11');
            }
        }

        var rp = element.querySelectorAll('rp');
        if (rp.length === 0) {
            // No "ruby parentheses" tags for those user agents that don't support
            // ruby at all.
            HTMLCS.addMessage(HTMLCS.ERROR, element, 'Ruby element does not contain rp elements, which provide extra punctuation to browsers not supporting ruby text.', 'H62.2');
        }
    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle3_Guideline3_2_3_2_1 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return [
            'input',
            'textarea',
            'button',
            'select'
        ];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, element, 'Check that a change of context does not occur when this input field receives focus.', 'G107');

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle3_Guideline3_2_3_2_2 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['form'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        var nodeName = element.nodeName.toLowerCase();

        if (nodeName === 'form') {
            this.checkFormSubmitButton(element);
        }
    },

    /**
     * Test for forms that don't have a submit button of some sort (technique H32).
     *
     * @param {DOMNode} form The form to test.
     */
    checkFormSubmitButton: function(form)
    {
        var ok = false;

        // Test for INPUT-based submit buttons. The type must be specified, as
        // the default for INPUT is text.
        var inputButtons = form.querySelectorAll('input[type=submit], input[type=image]');
        if (inputButtons.length > 0) {
            ok = true;
        } else {
            // Check for BUTTONs that aren't reset buttons, or normal buttons.
            // If they're blank or invalid, they are submit buttons.
            var buttonButtons    = form.querySelectorAll('button');
            var nonSubmitButtons = form.querySelectorAll('button[type=reset], button[type=button]');
            if (buttonButtons.length > nonSubmitButtons.length) {
                ok = true;
            }
        }//end if

        if (ok === false) {
            HTMLCS.addMessage(
                HTMLCS.ERROR,
                form,
                'This form does not contain a submit button, which creates issues for those who cannot submit the form using the keyboard. Submit buttons are INPUT elements with type attribute "submit" or "image", or BUTTON elements with type "submit" or omitted/invalid.',
                'H32.2'
            );
        }
    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle3_Guideline3_2_3_2_3 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['_top'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, top, 'Check that navigational mechanisms that are repeated on multiple Web pages occur in the same relative order each time they are repeated, unless a change is initiated by the user.', 'G61');

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle3_Guideline3_2_3_2_4 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['_top'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, top, 'Check that components that have the same functionality within this Web page are identified consistently in the set of Web pages to which it belongs.', 'G197');

    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle3_Guideline3_2_3_2_5 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['a'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        var nodeName = element.nodeName.toLowerCase();

        if (nodeName === 'a') {
            this.checkNewWindowTarget(element);
        }
    },

    /**
     * Test for links that open in new windows but don't warn users (technique H83).
     *
     * @param {DOMNode} link The link to test.
     */
    checkNewWindowTarget: function(link)
    {
        var hasTarget = link.hasAttribute('target');

        if (hasTarget === true) {
            var target = link.getAttribute('target') || '';
            if ((target === '_blank') && (/new window/i.test(link.innerHTML) === false)) {
                HTMLCS.addMessage(HTMLCS.WARNING, link, 'Check that this link\'s link text contains information indicating that the link will open in a new window.', 'H83.3');
            }
        }
    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle3_Guideline3_3_3_3_1 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['form'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, element, 'If an input error is automatically detected in this form, check that the item(s) in error are identified and the error(s) are described to the user in text.', 'G83,G84,G85');
    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle3_Guideline3_3_3_3_2 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['form'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        // Only the generic message will be displayed here. If there were problems
        // with input boxes not having labels, it will be pulled up as errors in
        // other Success Criteria (eg. 1.3.1, 4.1.2).
        HTMLCS.addMessage(HTMLCS.NOTICE, element, 'Check that descriptive labels or instructions (including for required fields) are provided for user input in this form.', 'G131,G89,G184,H90');
    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle3_Guideline3_3_3_3_3 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['form'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        // Only G177 (about providing suggestions) is flagged as a technique.
        // The techniques in 3.3.1 are also listed in this Success Criterion.
        HTMLCS.addMessage(HTMLCS.NOTICE, element, 'Check that this form provides suggested corrections to errors in user input, unless it would jeopardize the security or purpose of the content.', 'G177');
    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle3_Guideline3_3_3_3_4 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['form'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, element, 'If this form would bind a user to a financial or legal commitment, modify/delete user-controllable data, or submit test responses, ensure that submissions are either reversible, checked for input errors, and/or confirmed by the user.', 'G98,G99,G155,G164,G168.LegalForms');
    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle3_Guideline3_3_3_3_5 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['form'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, element, 'Check that context-sensitive help is available for this form, at a Web-page and/or control level.', 'G71,G184,G193');
    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle3_Guideline3_3_3_3_6 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['form'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        HTMLCS.addMessage(HTMLCS.NOTICE, element, 'Check that submissions to this form are either reversible, checked for input errors, and/or confirmed by the user.', 'G98,G99,G155,G164,G168.AllForms');
    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle4_Guideline4_1_4_1_1 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return [
            '_top'
        ];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        if (element === top) {
            var elsWithIds = HTMLCS.util.getAllElements(top, '*[id]');
            var usedIds    = {};

            for (var i = 0; i < elsWithIds.length; i++) {
                var id = elsWithIds[i].getAttribute('id');

                if (/^\s*$/.test(id) === true) {
                    continue;
                }

                if (usedIds[id] !== undefined) {
                    // F77 = "Failure of SC 4.1.1 due to duplicate values of type ID".
                    // Appropriate technique in HTML is H93.
                    HTMLCS.addMessage(HTMLCS.ERROR, elsWithIds[i], 'Duplicate id attribute value "' + id + '" found on the web page.', 'F77');
                } else {
                    usedIds[id] = true;
                }
            }
        }
    }
};


_global.HTMLCS_WCAG2AAA_Sniffs_Principle4_Guideline4_1_4_1_2 = {
    /**
     * Determines the elements to register for processing.
     *
     * Each element of the returned array can either be an element name, or "_top"
     * which is the top element of the tested code.
     *
     * @returns {Array} The list of elements.
     */
    register: function()
    {
        return ['_top'];

    },

    /**
     * Process the registered element.
     *
     * @param {DOMNode} element The element registered.
     * @param {DOMNode} top     The top element of the tested code.
     */
    process: function(element, top)
    {
        if (element === top) {
            var messages = this.processFormControls(top);
            for (var i = 0; i < messages.errors.length; i++) {
                HTMLCS.addMessage(HTMLCS.ERROR, messages.errors[i].element, messages.errors[i].msg, 'H91.' + messages.errors[i].subcode);
            }

            for (var i = 0; i < messages.warnings.length; i++) {
                HTMLCS.addMessage(HTMLCS.WARNING, messages.warnings[i].element, messages.warnings[i].msg, 'H91.' + messages.warnings[i].subcode);
            }

            this.addProcessLinksMessages(top);
        }//end if
    },

    addProcessLinksMessages: function(top)
    {
        var errors = this.processLinks(top);
        for (var i = 0; i < errors.empty.length; i++) {
            HTMLCS.addMessage(HTMLCS.WARNING, errors.empty[i], 'Anchor element found with an ID but without a href or link text. Consider moving its ID to a parent or nearby element.', 'H91.A.Empty');
        }

        for (var i = 0; i < errors.emptyWithName.length; i++) {
            HTMLCS.addMessage(HTMLCS.WARNING, errors.emptyWithName[i], 'Anchor element found with a name attribute but without a href or link text. Consider moving the name attribute to become an ID of a parent or nearby element.', 'H91.A.EmptyWithName');
        }

        for (var i = 0; i < errors.emptyNoId.length; i++) {
            HTMLCS.addMessage(HTMLCS.ERROR, errors.emptyNoId[i], 'Anchor element found with no link content and no name and/or ID attribute.', 'H91.A.EmptyNoId');
        }

        for (var i = 0; i < errors.noHref.length; i++) {
            HTMLCS.addMessage(HTMLCS.WARNING, errors.noHref[i], 'Anchor elements should not be used for defining in-page link targets. If not using the ID for other purposes (such as CSS or scripting), consider moving it to a parent element.', 'H91.A.NoHref');
        }

        for (var i = 0; i < errors.placeholder.length; i++) {
            HTMLCS.addMessage(HTMLCS.WARNING, errors.placeholder[i], 'Anchor element found with link content, but no href, ID or name attribute has been supplied.', 'H91.A.Placeholder');
        }

        for (var i = 0; i < errors.noContent.length; i++) {
            HTMLCS.addMessage(HTMLCS.ERROR, errors.noContent[i], 'Anchor element found with a valid href attribute, but no link content has been supplied.', 'H91.A.NoContent');
        }
    },

    processLinks: function(top)
    {
        var errors   = {
            empty: [],
            emptyWithName: [],
            emptyNoId: [],
            noHref: [],
            placeholder: [],
            noContent: []
        };

        var elements = HTMLCS.util.getAllElements(top, 'a:not([role="button"])');

        for (var el = 0; el < elements.length; el++) {
            var element = elements[el];

            var nameFound = false;
            var hrefFound = false;
            var content   = HTMLCS.util.getElementTextContent(element);

            if ((element.hasAttribute('title') === true) && (/^\s*$/.test(element.getAttribute('title')) === false)) {
                nameFound = true;
            } else if (/^\s*$/.test(content) === false) {
                nameFound = true;
            }

            if ((element.hasAttribute('href') === true) && (/^\s*$/.test(element.getAttribute('href')) === false)) {
                hrefFound = true;
            }

            if (hrefFound === false) {
                // No href. We don't want these because, although they are commonly used
                // to create targets, they can be picked up by screen readers and
                // displayed to the user as empty links. A elements are defined by H91 as
                // having an (ARIA) role of "link", and using them as targets are
                // essentially misusing them. Place an ID on a parent element instead.
                if (/^\s*$/.test(content) === true) {
                    // Also no content. (eg. <a id=""></a> or <a name=""></a>)
                    if (element.hasAttribute('id') === true) {
                        errors.empty.push(element);
                    } else if (element.hasAttribute('name') === true) {
                        errors.emptyWithName.push(element);
                    } else {
                        errors.emptyNoId.push(element);
                    }
                } else {
                    // Giving a benefit of the doubt here - if a link has text and also
                    // an ID, but no href, it might be because it is being manipulated by
                    // a script.
                    if ((element.hasAttribute('id') === true) || (element.hasAttribute('name') === true)) {
                        errors.noHref.push(element);
                    } else {
                        // HTML5 allows A elements with text but no href, "for where a
                        // link might otherwise have been placed, if it had been relevant".
                        // Hence, thrown as a warning, not an error.
                        errors.placeholder.push(element);
                    }
                }//end if
            } else {
                if (nameFound === false) {
                    // Href provided, but no content, title or valid aria label.
                    // We only fire this message when there are no images in the content.
                    // A link around an image with no alt text is already covered in SC
                    // 1.1.1 (test H30).
                    if (element.querySelectorAll('img').length === 0
                        && HTMLCS.util.hasValidAriaLabel(element) === false
                    ) {
                        errors.noContent.push(element);
                    }
                }//end if
            }//end if
        }//end for

        return errors;
    },

    processFormControls: function(top)
    {
        var elements = HTMLCS.util.getAllElements(top, 'button, fieldset, input, select, textarea, [role="button"]');
        var errors   = [];
        var warnings = [];

        var requiredNames = {
            button: ['@title', '_content', '@aria-label', '@aria-labelledby'],
            fieldset: ['legend', '@aria-label', '@aria-labelledby'],
            input_button: ['@value', '@aria-label', '@aria-labelledby'],
            input_text: ['label', '@title', '@aria-label', '@aria-labelledby'],
            input_file: ['label', '@title', '@aria-label', '@aria-labelledby'],
            input_password: ['label', '@title', '@aria-label', '@aria-labelledby'],
            input_checkbox: ['label', '@title', '@aria-label', '@aria-labelledby'],
            input_radio: ['label', '@title', '@aria-label', '@aria-labelledby'],
            input_image: ['@alt', '@title', '@aria-label', '@aria-labelledby'],
            select: ['label', '@title', '@aria-label', '@aria-labelledby'],
            textarea: ['label', '@title', '@aria-label', '@aria-labelledby']
        }

        var html5inputTypes = ['email', 'search', 'date', 'datetime-local', 'month', 'number', 'tel', 'time', 'url', 'week'];
        for (var i = 0, l = html5inputTypes.length; i < l; i++) {
            requiredNames['input_'+html5inputTypes[i]] = ['label', '@title', '@aria-label', '@aria-labelledby'];
        }

        var requiredValues = {
            select: 'option_selected'
        };

        for (var el = 0, ll = elements.length; el < ll; el++) {
            var element    = elements[el];
            var nodeName   = element.nodeName.toLowerCase();
            var msgSubCode = element.nodeName.substr(0, 1).toUpperCase() + element.nodeName.substr(1).toLowerCase();
            if (nodeName === 'input') {
                if (element.hasAttribute('type') === false) {
                    // If no type attribute, default to text.
                    nodeName += '_text';
                } else {
                    nodeName += '_' + element.getAttribute('type').toLowerCase();
                }

                // Treat all input buttons as the same
                if ((nodeName === 'input_submit') || (nodeName === 'input_reset')) {
                    nodeName = 'input_button';
                }

                // Get a format like "InputText".
                var msgSubCode = 'Input' + nodeName.substr(6, 1).toUpperCase() + nodeName.substr(7).toLowerCase();
            }//end if

            var matchingRequiredNames  = requiredNames[nodeName];
            var requiredValue = requiredValues[nodeName];

            // Any element that doesn't have specific handling must have content.
            if (!matchingRequiredNames && nodeName !== 'input_hidden') {
                matchingRequiredNames = ['_content'];
            }

            // Check all possible combinations of names to ensure that one exists.
            if (matchingRequiredNames) {
                for (var i = 0; i < matchingRequiredNames.length; i++) {
                    var requiredName = matchingRequiredNames[i];
                    if (requiredName === '_content') {
                        // Work with content.
                        var content = HTMLCS.util.getElementTextContent(element);
                        if (/^\s*$/.test(content) === false) {
                            break;
                        }
                    } else if (requiredName === 'label') {
                        // Label element. Re-use the label associating
                        // functions in SC 1.3.1.
                        var hasLabel = HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_3_1_3_1.testLabelsOnInputs(element, top, true);
                        if (hasLabel !== false) {
                            break;
                        }
                    } else if (requiredName.charAt(0) === '@') {
                        // Attribute.
                        requiredName = requiredName.substr(1, requiredName.length);

                        if ((requiredName === 'aria-label' || requiredName === 'aria-labelledby') && HTMLCS.util.hasValidAriaLabel(element)) {
                            break;
                        }

                        if ((element.hasAttribute(requiredName) === true) && (/^\s*$/.test(element.getAttribute(requiredName)) === false)) {
                            break;
                        }
                    } else {
                        // Sub-element contents.
                        var subEl = element.querySelector(requiredName);
                        if (subEl !== null) {
                            var content = HTMLCS.util.getElementTextContent(subEl);
                            if (/^\s*$/.test(content) === false) {
                                break;
                            }
                        }
                    }//end if
                }//end for

                if (i === matchingRequiredNames.length) {
                    var msgNodeType = nodeName + ' element';
                    if (nodeName.substr(0, 6) === 'input_') {
                        msgNodeType = nodeName.substr(6) + ' input element';
                    }

                    if (element.hasAttribute('role') && element.getAttribute('role') === 'button') {
                        msgNodeType = 'element has a role of "button" but';
                    }

                    var builtAttrs = matchingRequiredNames.slice(0, matchingRequiredNames.length);
                    for (var a = 0; a < builtAttrs.length; a++) {
                        if (builtAttrs[a] === '_content') {
                            builtAttrs[a] = 'element content';
                        } else if (builtAttrs[a].charAt(0) === '@') {
                            builtAttrs[a] = builtAttrs[a].substr(1) + ' attribute';
                        } else {
                            builtAttrs[a] = builtAttrs[a] + ' element';
                        }
                    }

                    var msg = 'This ' + msgNodeType + ' does not have a name available to an accessibility API. Valid names are: ' + builtAttrs.join(', ') + '.';
                    errors.push({
                        element: element,
                        msg: msg,
                        subcode: (msgSubCode + '.Name')
                    });
                }
            }//end if

            var valueFound = false;

            if (requiredValue === undefined) {
                // Nothing required of us.
                valueFound = true;
            } else if (requiredValue === '_content') {
                // Work with content.
                var content = HTMLCS.util.getElementTextContent(element);
                if (/^\s*$/.test(content) === false) {
                    valueFound = true;
                }
            } else if (requiredValue === 'option_selected') {
                // Select lists are recommended to have a selected Option element.
                if (element.hasAttribute('multiple') === false) {
                    var selected = element.querySelector('option[selected]');
                    if (selected !== null) {
                        valueFound = true;
                    }
                } else {
                    // Allow zero element selection to be valid where the SELECT
                    // element has been declared as a multiple selection.
                    valueFound = true;
                }
            } else if (requiredValue.charAt(0) === '@') {
                // Attribute.
                requiredValue = requiredValue.substr(1, requiredValue.length);
                if ((element.hasAttribute(requiredValue) === true)) {
                    valueFound = true;
                }
            }//end if

            // Check for valid aria labels.
            if (valueFound === false) {
                valuFound = HTMLCS.util.hasValidAriaLabel(element);
            }

            if (valueFound === false) {
                var msgNodeType = nodeName + ' element';
                if (nodeName.substr(0, 6) === 'input_') {
                    msgNodeType = nodeName.substr(6) + ' input element';
                }

                var msg = 'This ' + msgNodeType + ' does not have a value available to an accessibility API.';

                var builtAttr = '';
                var warning   = false;
                if (requiredValue === '_content') {
                    builtAttr = ' Add one by adding content to the element.';
                } else if (requiredValue === 'option_selected') {
                    // Change the message instead. The value is only undefined in HTML 4/XHTML 1;
                    // in HTML5 the first option in a single select dropdown is automatically selected.
                    // Because of this, it should also be sent out as a warning, not an error.
                    warning = true;
                    msg = 'This ' + msgNodeType + ' does not have an initially selected option.' + ' ' +
                        'Depending on your HTML version, the value exposed to an accessibility API may be undefined.';
                } else if (requiredValue.charAt(0) === '@') {
                    builtAttr = ' A value is exposed using the "' + requiredValue + '" attribute.';
                } else {
                    builtAttr = ' A value is exposed using the "' + requiredValue + '" element.';
                }

                msg += builtAttr;
                if (warning === false) {
                    errors.push({
                        element: element,
                        msg: msg,
                        subcode: (msgSubCode + '.Value')
                    });
                } else {
                    warnings.push({
                        element: element,
                        msg: msg,
                        subcode: (msgSubCode + '.Value')
                    });
                }
            }//end if
        }//end for

        return {
            errors: errors,
            warnings: warnings
        };
    }
};


_global.HTMLCS = new function()
{
    var _standards    = {};
    var _sniffs       = [];
    var _tags         = {};
    var _standard     = null;
    var _currentSniff = null;

    var _messages     = [];
    var _msgOverrides = {};

    /*
        Message type constants.
    */
    this.ERROR   = 1;
    this.WARNING = 2;
    this.NOTICE  = 3;

    /**
     * Loads the specified standard and run the sniffs.
     *
     * @param {String}      standard The name of the standard to load.
     * @param {String|Node} An HTML string or a DOM node object.
     * @param {Function}    The function that will be called when the testing is completed.
     */
    this.process = function(standard, content, callback, failCallback) {
        // Clear previous runs.
        _standards    = {};
        _sniffs       = [];
        _tags         = {};
        _standard     = null;

        if (!content) {
            return false;
        }

        if (_standards[_getStandardPath(standard)]) {
            HTMLCS.run(callback, content);
        } else {
            this.loadStandard(standard, function() {
                HTMLCS.run(callback, content);
            }, failCallback);
        }
    };

    /**
     * Loads the specified standard and its sniffs.
     *
     * @param {String}   standard The name of the standard to load.
     * @param {Function} callback The function to call once the standard is loaded.
     */
    this.loadStandard = function(standard, callback, failCallback) {
        if (!standard) {
            return false;
        }

        _includeStandard(standard, function() {
            _standard = standard;
            callback.call(this);
        }, failCallback);
    };

    /**
     * Runs the sniffs for the loaded standard.
     *
     * @param {Function}    callback The function to call once all sniffs are completed.
     * @param {String|Node} content  An HTML string or a DOM node object.
     */
    this.run = function(callback, content) {
        var element      = null;
        var loadingFrame = false;
        if (typeof content === 'string') {
            loadingFrame = true;
            var elementFrame = document.createElement('iframe');
            elementFrame.style.display = 'none';
            elementFrame = document.body.insertBefore(elementFrame, null);

            if (elementFrame.contentDocument) {
                element = elementFrame.contentDocument;
            } else if (element.contentWindow) {
                element = elementFrame.contentWindow.document;
            }

            elementFrame.load = function() {
                this.onreadystatechange = null;
                this.onload = null;

                if (HTMLCS.isFullDoc(content) === false) {
                    element = element.getElementsByTagName('body')[0];
                    var div = element.getElementsByTagName('div')[0];
                    if (div && (div.id === '__HTMLCS-source-wrap')) {
                        div.id  = '';
                        element = div;
                    }
                }

                var elements = HTMLCS.util.getAllElements(element);
                elements.unshift(element);
                _run(elements, element, callback);
            }

            // Satisfy IE which doesn't like onload being set dynamically.
            elementFrame.onreadystatechange = function() {
                if (/^(complete|loaded)$/.test(this.readyState) === true) {
                    this.onreadystatechange = null;
                    this.load();
                }
            }

            elementFrame.onload = elementFrame.load;

            if ((HTMLCS.isFullDoc(content) === false) && (content.indexOf('<body') === -1)) {
                element.write('<div id="__HTMLCS-source-wrap">' + content + '</div>');
            } else {
                element.write(content);
            }

            element.close();
        } else {
            element = content;
        }

        if (!element) {
            callback.call(this);
            return;
        }

        callback  = callback || function() {};
        _messages = [];

        // Get all the elements in the parent element.
        // Add the parent element too, which will trigger "_top" element codes.
        var elements = HTMLCS.util.getAllElements(element);
        elements.unshift(element);

        // Run the sniffs.
        if (loadingFrame === false) {
            _run(elements, element, callback);
        }
    };

    /**
     * Returns true if the content passed appears to be from a full document.
     *
     * With string content, we consider a full document as the presence of <html>,
     * or <head> + <body> elements. For an element, only the 'html' element (the
     * document element) is accepted.
     *
     * @param {String|Node} content An HTML string or a DOM node object.
     *
     * @returns {Boolean}
     */
    this.isFullDoc = function(content) {
        var fullDoc = false;
        if (typeof content === 'string') {
            if (content.toLowerCase().indexOf('<html') !== -1) {
                fullDoc = true;
            } else if ((content.toLowerCase().indexOf('<head') !== -1) && (content.toLowerCase().indexOf('<body') !== -1)) {
                fullDoc = true;
            }
        } else {
            // If we are the document, or the document element.
            if ((content.nodeName.toLowerCase() === 'html') || (content.documentElement)) {
                fullDoc = true;
            }
        }

        return fullDoc;
    }

    /**
     * Adds a message.
     *
     * @param {Number}  type    The type of the message.
     * @param {Node}    element The element that the message is related to.
     * @param {String}  msg     The message string.
     * @param {String}  code    Unique code for the message.
     * @param {Object}  [data]  Extra data to store for the message.
     */
    this.addMessage = function(type, element, msg, code, data) {
        code = _getMessageCode(code);

        _messages.push({
            type: type,
            element: element,
            msg: _msgOverrides[code] || msg,
            code: code,
            data: data
        });
    };

    /**
     * Returns all the messages for the last run.
     *
     * Return a copy of the array so the class variable doesn't get modified by
     * future modification (eg. splicing).
     *
     * @returns {Array} Array of message objects.
     */
    this.getMessages = function() {
        return _messages.concat([]);
    }, this.getMessagesJSON = function() {
        var messages = _messages.concat([]);
        var simplerMessages = [];
        messages.forEach(function(message, index, array) {
            simplerMessages.push({
                code: message.code,
                message: message.msg,
                type: message.type,
                elementString: message.element.outerHTML
            });
        });
        return JSON.stringify(simplerMessages);
    };

    /**
     * Runs the sniffs in the loaded standard for the specified element.
     *
     * @param {Node}     element    The element to test.
     * @param {Node}     topElement The top element of the processing.
     * @param {Function} [callback] The function to call once all tests are run.
     */
    var _run = function(elements, topElement, callback) {
        var topMsgs = [];
        while (elements.length > 0) {
            var element = elements.shift();

            if (element === topElement) {
                var tagName = '_top';
            } else {
                var tagName = element.tagName.toLowerCase();
            }

            // First check whether any "top" messages need to be shifted off for this
            // element. If so, dump off into the main messages.
            for (var i = 0; i < topMsgs.length;) {
                if (element === topMsgs[i].element) {
                    _messages.push(topMsgs[i]);
                    topMsgs.splice(i, 1);
                } else {
                    i++;
                }
            }//end for

            if (_tags[tagName] && _tags[tagName].length > 0) {
                _processSniffs(element, _tags[tagName].concat([]), topElement);

                // Save "top" messages, and reset the messages array.
                if (tagName === '_top') {
                    topMsgs   = _messages;
                    _messages = [];
                }
            }
        }//end while

        // Due to filtering of presentation roles for general sniffing these need to be handled
        // separately. The 1.3.1 sniff needs to run to detect any incorrect usage of the presentation
        // role.
        var presentationElems = topElement.querySelectorAll('[role="presentation"]');
        _currentSniff         = HTMLCS_WCAG2AAA_Sniffs_Principle1_Guideline1_3_1_3_1;
        [].forEach.call(presentationElems, function(element) {
            _currentSniff.testSemanticPresentationRole(element);
        });

        if (callback instanceof Function === true) {
            callback.call(this);
        }
    };

    /**
     * Process the sniffs.
     *
     * @param {Node}     element    The element to test.
     * @param {Array}    sniffs     Array of sniffs.
     * @param {Node}     topElement The top element of the processing.
     * @param {Function} [callback] The function to call once the processing is completed.
     */
    var _processSniffs = function(element, sniffs, topElement, callback) {
        while (sniffs.length > 0) {
            var sniff     = sniffs.shift();
            _currentSniff = sniff;

            if (sniff.useCallback === true) {
                // If the useCallback property is set:
                // - Process the sniff.
                // - Recurse into ourselves with remaining sniffs, with no callback.
                // - Clear out the list of sniffs (so they aren't run again), so the
                //   callback (if not already recursed) can run afterwards.
                sniff.process(element, topElement, function() {
                    _processSniffs(element, sniffs, topElement);
                    sniffs = [];
                });
            } else {
                // Process the sniff.
                sniff.process(element, topElement);
            }
        }//end while

        if (callback instanceof Function === true) {
            callback.call(this);
        }
    };

    /**
     * Includes the specified standard file.
     *
     * @param {String}   standard The name of the standard.
     * @param {Function} callback The function to call once the standard is included.
     * @param {Object}   options  The options for the standard (e.g. exclude sniffs).
     */
    var _includeStandard = function(standard, callback, failCallback, options) {
        if (standard.indexOf('http') !== 0) {
            standard = _getStandardPath(standard);
        }//end id

        // See if the ruleset object is already included (eg. if minified).
        var parts   = standard.split('/');
        var ruleSet = _global['HTMLCS_' + parts[(parts.length - 2)]];
        if (ruleSet) {
            // Already included.
            _registerStandard(standard, callback, failCallback, options);
        } else {
            _includeScript(standard, function() {
                // Script is included now register the standard.
                _registerStandard(standard, callback, failCallback, options);
            }, failCallback);
        }//end if
    };

    /**
     * Registers the specified standard and its sniffs.
     *
     * @param {String}   standard The name of the standard.
     * @param {Function} callback The function to call once the standard is registered.
     * @param {Object}   options  The options for the standard (e.g. exclude sniffs).
     */
    var _registerStandard = function(standard, callback, failCallback, options) {
        // Get the object name.
        var parts = standard.split('/');

        // Get a copy of the ruleset object.
        var oldRuleSet = _global['HTMLCS_' + parts[(parts.length - 2)]];
        var ruleSet    = {};

        for (var x in oldRuleSet) {
            if (oldRuleSet.hasOwnProperty(x) === true) {
                ruleSet[x] = oldRuleSet[x];
            }
        }

        if (!ruleSet) {
            return false;
        }

        _standards[standard] = ruleSet;

        // Process the options.
        if (options) {
            if (options.include && options.include.length > 0) {
                // Included sniffs.
                ruleSet.sniffs = options.include;
            } else if (options.exclude) {
                // Excluded sniffs.
                for (var i = 0; i < options.exclude.length; i++) {
                    var index = ruleSet.sniffs.find(options.exclude[i]);
                    if (index >= 0) {
                        ruleSet.sniffs.splice(index, 1);
                    }
                }
            }
        }//end if

        // Register the sniffs for this standard.
        var sniffs = ruleSet.sniffs.slice(0, ruleSet.sniffs.length);
        _registerSniffs(standard, sniffs, callback, failCallback);
    };

    /**
     * Registers the sniffs for the specified standard.
     *
     * @param {String}   standard The name of the standard.
     * @param {Array}    sniffs   List of sniffs to register.
     * @param {Function} callback The function to call once the sniffs are registered.
     */
    var _registerSniffs = function(standard, sniffs, callback, failCallback) {
        if (sniffs.length === 0) {
            callback.call(this);
            return;
        }

        // Include and register sniffs.
        var sniff = sniffs.shift();
        _loadSniffFile(standard, sniff, function() {
            _registerSniffs(standard, sniffs, callback, failCallback);
        }, failCallback);
    };

    /**
     * Includes the sniff's JS file and registers it.
     *
     * @param {String}        standard The name of the standard.
     * @param {String|Object} sniff    The sniff to register, can be a string or
     *                                 and object specifying another standard.
     * @param {Function}      callback The function to call once the sniff is included and registered.
     */
    var _loadSniffFile = function(standard, sniff, callback, failCallback) {
        if (typeof sniff === 'string') {
            var sniffObj = _getSniff(standard, sniff);
            var cb       = function() {
                _registerSniff(standard, sniff);
                callback.call(this);
            }

            // Already loaded.
            if (sniffObj) {
                cb();
            } else {
                _includeScript(_getSniffPath(standard, sniff), cb, failCallback);
            }
        } else {
            // Including a whole other standard.
            _includeStandard(sniff.standard, function() {
                if (sniff.messages) {
                    // Add message overrides.
                    for (var msg in sniff.messages) {
                        _msgOverrides[msg] = sniff.messages[msg];
                    }
                }

                callback.call(this);
            }, failCallback, {
                exclude: sniff.exclude,
                include: sniff.include
            });
        }
    };

    /**
     * Registers the specified sniff.
     *
     * @param {String} standard The name of the standard.
     * @param {String} sniff    The name of the sniff.
     */
    var _registerSniff = function(standard, sniff) {
        // Get the sniff object.
        var sniffObj = _getSniff(standard, sniff);
        if (!sniffObj) {
            return false;
        }

        // Call the register method of the sniff, it should return an array of tags.
        if (sniffObj.register) {
            var watchedTags = sniffObj.register();

            for (var i = 0; i < watchedTags.length; i++) {
                if (!_tags[watchedTags[i]]) {
                    _tags[watchedTags[i]] = [];
                }

                _tags[watchedTags[i]].push(sniffObj);
            }
        }

        _sniffs.push(sniffObj);
    };

    /**
     * Returns the path to the sniff file.
     *
     * @param {String} standard The name of the standard.
     * @param {String} sniff    The name of the sniff.
     *
     * @returns {String} The path to the JS file of the sniff.
     */
    var _getSniffPath = function(standard, sniff) {
        var parts = standard.split('/');
        parts.pop();
        var path = parts.join('/') + '/Sniffs/' + sniff.replace(/\./g, '/') + '.js';
        return path;
    };

    /**
     * Returns the path to a local standard.
     *
     * @param {String} standard The name of the standard.
     *
     * @returns {String} The path to the local standard.
     */
    var _getStandardPath = function(standard)
    {
        // Get the include path of a local standard.
        var scripts = document.getElementsByTagName('script');
        var path    = null;

        // Loop through all the script tags that exist in the document and find the one
        // that has included this file.
        for (var i = 0; i < scripts.length; i++) {
            if (scripts[i].src) {
                if (scripts[i].src.match(/HTMLCS\.js/)) {
                    // We have found our appropriate <script> tag that includes
                    // this file, we can extract the path.
                    path = scripts[i].src.replace(/HTMLCS\.js/,'');

                    // trim any trailing bits
                    path = path.substring(0, path.indexOf('?'));
                    break;
                }
            }
        }

        return path + 'Standards/' + standard + '/ruleset.js';

    };

    /**
     * Returns the sniff object.
     *
     * @param {String} standard The name of the standard.
     * @param {String} sniff    The name of the sniff.
     *
     * @returns {Object} The sniff object.
     */
    var _getSniff = function(standard, sniff) {
        var name = 'HTMLCS_';
        name    += _standards[standard].name + '_Sniffs_';
        name    += sniff.split('.').join('_');

        if (!_global[name]) {
            return null;
        }

        _global[name]._name = sniff;
        return _global[name];
    };

    /**
     * Returns the full message code.
     *
     * A full message code includes the standard name, the sniff name and the given code.
     *
     * @returns {String} The full message code.
     */
    var _getMessageCode = function(code) {
        code = _standard + '.' + _currentSniff._name + '.' + code;
        return code;
    };

    /**
     * Includes the specified JS file.
     *
     * @param {String}   src      The URL to the JS file.
     * @param {Function} callback The function to call once the script is loaded.
     */
    var _includeScript = function(src, callback, failCallback) {
        var script    = document.createElement('script');
        script.onload = function() {
            script.onload = null;
            script.onreadystatechange = null;
            callback.call(this);
        };

        script.onerror = function() {
            script.onload = null;
            script.onreadystatechange = null;
            if (failCallback) {
                failCallback.call(this);
            }
        };

        script.onreadystatechange = function() {
            if (/^(complete|loaded)$/.test(this.readyState) === true) {
                script.onreadystatechange = null;
                script.onload();
            }
        }

        script.src = src;

        if (document.head) {
            document.head.appendChild(script);
        } else {
            document.getElementsByTagName('head')[0].appendChild(script);
        }
    };
};

_global.HTMLCS.util = function() {
    var self = {};

    /**
     * Trim off excess spaces on either side.
     *
     * @param {String} string The string with potentially extraneous whitespace.
     *
     * @returns {String}
     */
    self.trim = function(string) {
        return string.replace(/^\s*(.*)\s*$/g, '$1');
    };

    /**
     * Returns true if the string is "empty" according to WCAG standards.
     *
     * We can test for whether the string is entirely composed of whitespace, but
     * WCAG standards explicitly state that non-breaking spaces (&nbsp;, &#160;)
     * are not considered "empty". So we need this function to filter out that
     * situation.
     *
     * @param {String} string The potentially empty string.
     *
     * @returns {Boolean}
     */
    self.isStringEmpty = function(string) {
        if (typeof string !== 'string') {
            return true;
        }

        var empty = true;

        if (string.indexOf(String.fromCharCode(160)) !== -1) {
            // Has an NBSP, therefore cannot be empty.
            empty = false;
        } else if (/^\s*$/.test(string) === false) {
            // Not spacing.
            empty = false;
        }

        return empty;
    };

    /**
     * Get the document type being tested.
     *
     * Possible values: html5, xhtml5, xhtml11, xhtml10, html401, html40
     * ... or empty string if it couldn't work out the doctype.
     *
     * This will only give the thumbs-up to the "strict" doctypes.
     *
     * @param {Document} The document being tested.
     *
     * @return {String}
     */
    self.getDocumentType = function(document)
    {
        var retval  = null;
        var doctype = document.doctype;
        if (doctype) {
            var doctypeName = doctype.name;
            var publicId    = doctype.publicId;
            var systemId    = doctype.systemId;

            if (doctypeName === null) {
                doctypeName = '';
            }

            if (systemId === null) {
                systemId = '';
            }

            if (publicId === null) {
                publicId = '';
            }

            if (doctypeName.toLowerCase() === 'html') {
                if (publicId === '' && systemId === '') {
                    retval = 'html5';
                } else if (publicId.indexOf('//DTD HTML 4.01') !== -1 || systemId.indexOf('w3.org/TR/html4/strict.dtd') !== -1) {
                    retval = 'html401';
                } else if (publicId.indexOf('//DTD HTML 4.0') !== -1 || systemId.indexOf('w3.org/TR/REC-html40/strict.dtd') !== -1) {
                    retval = 'html40';
                } else if (publicId.indexOf('//DTD XHTML 1.0 Strict') !== -1 && systemId.indexOf('w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd') !== -1) {
                    retval = 'xhtml10';
                } else if (publicId.indexOf('//DTD XHTML 1.1') !== -1 && systemId.indexOf('w3.org/TR/xhtml11/DTD/xhtml11.dtd') !== -1) {
                    retval = 'xhtml11';
                } if (systemId.indexOf('about:legacy-compat') !== -1) {
                    // Some tools don't like the lack of doctype for XHTML5 so permit
                    // an "about:legacy-compat" SYSTEM doctype.
                    if (document.contentType === 'application/xhtml+xml') {
                        var htmlElement = document.querySelector('html');
                        if (htmlElement.getAttribute('xmlns') === 'http://www.w3.org/1999/xhtml') {
                            retval = 'xhtml5';
                        }
                    }
                }
            }
        } else {
            // XHTML5 has no doctype (at all) normally, but it only counts if the
            // content type it was sent as is set correctly
            if (document.contentType === 'application/xhtml+xml') {
                var htmlElement = document.querySelector('html');
                if (htmlElement.getAttribute('xmlns') === 'http://www.w3.org/1999/xhtml') {
                    retval = 'xhtml5';
                }
            }
        }

        return retval;
    };//end getDocumentType()

    /**
     * Get the window object relating to the passed element.
     *
     * @param {Node|Document} element The element (or document) to pass.
     *
     * @returns {Window}
     */
    self.getElementWindow = function(element)
    {
        if (element.ownerDocument) {
            var doc = element.ownerDocument;
        } else {
            var doc = element;
        }

        var window = null;
        if (doc.defaultView) {
            window = doc.defaultView;
        } else {
            window = doc.parentWindow;
        }

        return window;

    };

    /**
     * Returns true if the element has a valid aria label.
     *
     * @param {Node} element The element we are checking.
     *
     * @return {Boolean}
     */
    self.hasValidAriaLabel = function(element)
    {
        var found = false;
        if (element.hasAttribute('aria-labelledby') === true) {
            // Checking aria-labelled by where the label exists AND it has text available
            // to an accessibility API.
            var labelledByIds = element.getAttribute('aria-labelledby').split(/\s+/);
            labelledByIds.forEach(function(id) {
                var elem = document.getElementById(id);
                if (elem) {
                    var text = self.getElementTextContent(elem);
                    if (/^\s*$/.test(text) === false) {
                        found = true;
                    }
                }
            });
        } else if (element.hasAttribute('aria-label') === true) {
            var text = element.getAttribute('aria-label');
            if (/^\s*$/.test(text) === false) {
                found = true;
            }
        }

        return found;
    };

    /**
     * Return the appropriate computed style object for an element.
     *
     * It's accessed in different ways depending on whether it's IE or not.
     *
     * @param {Node} element An element with style.
     *
     * @returns {Object}
     */
    self.style = function(element) {
        var computedStyle = null;
        var window        = self.getElementWindow(element);

        if (element.currentStyle) {
            computedStyle = element.currentStyle;
        } else if (window.getComputedStyle) {
            computedStyle = window.getComputedStyle(element, null);
        }

        return computedStyle;
    };

    /**
     * Return true if an element is hidden visually.
     *
     * If the computed style of an element cannot be determined for some reason,
     * it is presumed it is NOT hidden.
     *
     * @param {Node} element The element that is hiding, or not.
     *
     * @returns {Boolean}
     */
    self.isVisuallyHidden = function(element) {
        var hidden = false;

        // Do not point to elem if its hidden. Use computed styles.
        var style = self.style(element);
        if (style !== null) {
            if ((style.visibility === 'hidden') || (style.display === 'none')) {
                hidden = true;
            }

            if ((parseInt(style.left, 10) + parseInt(style.width, 10)) < 0) {
                hidden = true;
            }

            if ((parseInt(style.top, 10) + parseInt(style.height, 10)) < 0) {
                hidden = true;
            }
        }

        return hidden;
    };

    /**
     * Returns true if the element is deliberately hidden from Accessibility APIs using ARIA hidden.
     *
     * Not: This is separate to isAccessibilityHidden() due to a need to check specifically for aria hidden.
     *
     * @param {Node} element The element to check.
     *
     * @return {Boolean}
     */
    self.isAriaHidden = function(element) {
        do {
            // WAI-ARIA hidden attribute.
            if (element.hasAttribute('aria-hidden') && element.getAttribute('aria-hidden') === 'true') {
                return true;
            }
        } while (element = element.parentElement);

        return false;
    };


    /**
     * Returns true if the element is deliberately hidden from Accessibility APIs.
     *
     * @param {Node} element The element to check.
     *
     * @return {Boolean}
     */
    self.isAccessibilityHidden = function(element) {
        do {
            // WAI-ARIA presentation role.
            if (element.hasAttribute('role') && element.getAttribute('role') === 'presentation') {
                return true;
            }

            // WAI-ARIA hidden attribute.
            if (element.hasAttribute('aria-hidden') && element.getAttribute('aria-hidden') === 'true') {
                return true;
            }
        } while (element = element.parentElement);

        return false;
    };


    /**
     * Returns TRUE if the element is able to be focused .
     *
     * @param {Node} element DOM Node to test.
     *
     * @return {Boolean}
     */
    self.isFocusable = function(element)
    {
        var nodeName = element.nodeName.toLowerCase();
        if (self.isDisabled(element) === true) {
            return false;
        }

        if (self.isVisuallyHidden(element) === true) {
            return false;
        }

        // Form elements.
        if (/^(input|select|textarea|button|object)$/.test(nodeName)) {
            return true;
        }

        // Hyperlinks without empty hrefs are focusable.
        if (nodeName === 'a' && element.hasAttribute('href') && /^\s*$/.test(element.getAttribute('href')) === false) {
            return true;
        }

        return false;
    };

    /**
     * Returns TRUE if the element is able to be focused by keyboard tabbing.
     *
     * @param {Node} element DOM Node to test.
     *
     * @return {Boolean}
     */
    self.isKeyboardTabbable = function(element)
    {
        if (element.hasAttribute('tabindex') === true) {
            var index = element.getAttribute('tabindex');
            if (index === "-1") {
                return false;
            } else {
                return true;
            }
        }

        return self.isFocusable(element);
    };

    /**
     * Returns TRUE if the element is able to be accessed via the keyboard.
     *
     * @param {Node} element DOM Node to test.
     *
     * @return {Boolean}
     */
    self.isKeyboardNavigable = function(element)
    {
        if (element.hasAttribute('accesskey') && /^\s*$/.test(element.getAttribute('accesskey')) === false) {
            return true;
        }

        return self.isKeyboardTabbable(element);
    };

    /**
     * Return true if an element is disabled.
     *
     * If the computed style of an element cannot be determined for some reason,
     * it is presumed it is NOT hidden.
     *
     * @param {Node} element The element that is hiding, or not.
     *
     * @returns {Boolean}
     */
    self.isDisabled = function(element) {
        var disabled = false;

        // Do not point to elem if its hidden. Use computed styles.
        if ((element.disabled === true) || (element.getAttribute('aria-disabled') === 'true')) {
            disabled = true;
        }

        return disabled;
    };

    /**
     * Return true if an element is in a document.
     *
     * @param {Node} element The element that is in a doc, or not.
     *
     * @returns {Boolean}
     */
    self.isInDocument = function(element) {
        // Check whether the element is in the document, by looking up its
        // DOM tree for a document object.
        var parent = element.parentNode;
        while (parent && parent.ownerDocument) {
            parent = parent.parentNode;
        }//end while

        // If we didn't hit a document, the element must not be in there.
        if (parent === null) {
            return false;
        }

        return true;
    };

    /**
     * Returns all elements that are visible to the accessibility API.
     *
     * @param {Node}   element  The parent element to search.
     * @param {String} selector Optional selector to pass to
     *
     * @return {Array}
     */
    self.getAllElements = function(element, selector) {
        element      = element || document;
        selector     = selector || '*';
        var elements = Array.prototype.slice.call(element.querySelectorAll(selector));
        var visible  = elements.filter(function(elem) {
            return HTMLCS.util.isAccessibilityHidden(elem) === false;
        });

        // We shouldn't be testing elements inside the injected auditor code if it's present.
        var auditor = document.getElementById('HTMLCS-wrapper');
        if (auditor) {
            visible = visible.filter(function(elem) {
                return auditor.contains(elem) === false;
            });
        }

        return visible;
    };

    /**
     * Returns true if the passed child is contained by the passed parent.
     *
     * Uses either the IE contains() method or the W3C compareDocumentPosition()
     * method, as appropriate.
     *
     * @param {Node|Document} parent The parent element or document.
     * @param {Node|Document} child  The child.
     *
     * @returns {Boolean}
     */
    self.contains = function(parent, child) {
        var contained = false;

        // If the parent and the child are the same, they can't contain each
        // other.
        if (parent !== child) {
            if (!parent.ownerDocument) {
                // Parent is the document. Short-circuiting because contains()
                // doesn't exist on the document element.
                // We check whether the child can be contained, and whether the
                // child is in the same document as the parent.
                if ((child.ownerDocument) && (child.ownerDocument === parent)) {
                    contained = true;
                }
            } else {
                if ((parent.contains) && (parent.contains(child) === true)) {
                    contained = true;
                } else if ((parent.compareDocumentPosition) && ((parent.compareDocumentPosition(child) & 16) > 0)) {
                    contained = true;
                }
            }//end if
        }//end if

        return contained;
    };

    /**
     * Returns true if the table passed is a layout table.
     *
     * If the passed table contains headings - through the use of the th
     * element - HTML_CodeSniffer will assume it is a data table. This is in line
     * with most other online checkers.
     *
     * @param {Node} table The table to check.
     *
     * @returns {Boolean}
     */
    self.isLayoutTable = function(table) {
        var th = table.querySelector('th');
        if (th === null) {
            return true;
        }

        return false;
    };

    /**
     * Calculate the contrast ratio between two colours.
     *
     * Colours should be in rgb() or 3/6-digit hex format; order does not matter
     * (ie. it doesn't matter which is the lighter and which is the darker).
     * Values should be in the range [1.0, 21.0]... a ratio of 1.0 means "they're
     * exactly the same contrast", 21.0 means it's white-on-black or v.v.
     * Formula as per WCAG 2.0 definitions.
     *
     * @param {String} colour1 The first colour to compare.
     * @param {String} colour2 The second colour to compare.
     *
     * @returns {Number}
     */
    self.contrastRatio = function(colour1, colour2) {
        var ratio = (0.05 + self.relativeLum(colour1)) / (0.05 + self.relativeLum(colour2));
        if (ratio < 1) {
            ratio = 1 / ratio;
        }

        return ratio;
    };

    /**
     * Calculate relative luminescence for a colour in the sRGB colour profile.
     *
     * Supports rgb() and hex colours. rgba() also supported but the alpha
     * channel is currently ignored.
     * Hex colours can have an optional "#" at the front, which is stripped.
     * Relative luminescence formula is defined in the definitions of WCAG 2.0.
     * It can be either three or six hex digits, as per CSS conventions.
     * It should return a value in the range [0.0, 1.0].
     *
     * @param {String} colour The colour to calculate from.
     *
     * @returns {Number}
     */
    self.relativeLum = function(colour) {
        if (colour.charAt) {
            var colour = self.colourStrToRGB(colour);
        }

        var transformed = {};
        for (var x in colour) {
            if (colour[x] <= 0.03928) {
                transformed[x] = colour[x] / 12.92;
            } else {
                transformed[x] = Math.pow(((colour[x] + 0.055) / 1.055), 2.4);
            }
        }//end for

        var lum = ((transformed.red * 0.2126) + (transformed.green * 0.7152) + (transformed.blue * 0.0722));
        return lum;
    }

    /**
     * Convert a colour string to a structure with red/green/blue elements.
     *
     * Supports rgb() and hex colours (3 or 6 hex digits, optional "#").
     * rgba() also supported but the alpha channel is currently ignored.
     * Each red/green/blue element is in the range [0.0, 1.0].
     *
     * @param {String} colour The colour to convert.
     *
     * @returns {Object}
     */
    self.colourStrToRGB = function(colour) {
        colour = colour.toLowerCase();

        if (colour.substring(0, 3) === 'rgb') {
            // rgb[a](0, 0, 0[, 0]) format.
            var matches = /^rgba?\s*\((\d+),\s*(\d+),\s*(\d+)([^)]*)\)$/.exec(colour);
            colour = {
                red: (matches[1] / 255),
                green: (matches[2] / 255),
                blue: (matches[3] / 255)
            }
        } else {
            // Hex digit format.
            if (colour.charAt(0) === '#') {
                colour = colour.substr(1);
            }

            if (colour.length === 3) {
                colour = colour.replace(/^(.)(.)(.)$/, '$1$1$2$2$3$3');
            }

            colour = {
                red: (parseInt(colour.substr(0, 2), 16) / 255),
                green: (parseInt(colour.substr(2, 2), 16) / 255),
                blue: (parseInt(colour.substr(4, 2), 16) / 255)
            };
        }

        return colour;
    };

    /**
     * Convert an RGB colour structure to a hex colour.
     *
     * The red/green/blue colour elements should be on a [0.0, 1.0] scale.
     * Colours that can be converted into a three Hex-digit string will be
     * converted as such (eg. rgb(34,34,34) => #222). Others will be converted
     * to a six-digit string (eg. rgb(48,48,48) => #303030).
     *
     * @param {Object} colour Structure with "red", "green" and "blue" elements.
     *
     * @returns {String}
     */
    self.RGBtoColourStr = function(colour) {
        colourStr = '#';
        colour.red   = Math.round(colour.red * 255);
        colour.green = Math.round(colour.green * 255);
        colour.blue  = Math.round(colour.blue * 255);

        if ((colour.red % 17 === 0) && (colour.green % 17 === 0) && (colour.blue % 17 === 0)) {
            // Reducible to three hex digits.
            colourStr += (colour.red / 17).toString(16);
            colourStr += (colour.green / 17).toString(16);
            colourStr += (colour.blue / 17).toString(16);
        } else {
            if (colour.red < 16) {
                colourStr += '0';
            }
            colourStr += colour.red.toString(16);

            if (colour.green < 16) {
                colourStr += '0';
            }
            colourStr += colour.green.toString(16);

            if (colour.blue < 16) {
                colourStr += '0';
            }
            colourStr += colour.blue.toString(16);
        }

        return colourStr;
    };

    /**
     * Convert an RGB colour into hue-saturation-value.
     *
     * This is used for calculations changing the colour (for colour contrast
     * purposes) to ensure that the hue is maintained.
     * The parameter accepts either a string (hex or rgb() format) or a
     * red/green/blue structure.
     * The returned structure has hue, saturation, and value components: the
     * latter two are in the range [0.0, 1.0]; hue is in degrees,
     * range [0.0, 360.0).
     * If there is no saturation then hue is technically undefined.
     *
     * @param {String|Object} colour A colour to convert.
     *
     * @returns {Object}
     */
    self.sRGBtoHSV = function(colour) {
        // If this is a string, then convert to a colour structure.
        if (colour.charAt) {
            colour = self.colourStrToRGB(colour);
        }

        var hsvColour = {
            hue: 0,
            saturation: 0,
            value: 0
        };

        var maxColour = Math.max(colour.red, colour.green, colour.blue);
        var minColour = Math.min(colour.red, colour.green, colour.blue);
        var chroma    = maxColour - minColour;

        if (chroma === 0) {
            hsvColour.value = colour.red;
        } else {
            hsvColour.value = maxColour;
            if (maxColour === colour.red) {
                hsvColour.hue = ((colour.green - colour.blue) / chroma);
            } else if (maxColour === colour.green) {
                hsvColour.hue = (2.0 + ((colour.blue - colour.red) / chroma));
            } else {
                hsvColour.hue = (4.0 + ((colour.red - colour.green) / chroma));
            }//end if

            hsvColour.hue = (hsvColour.hue * 60.0);
            if (hsvColour.hue >= 360.0) {
                hsvColour.hue -= 360.0;
            }

            hsvColour.saturation = chroma / hsvColour.value;
        }//end if

        return hsvColour;
    };

    /**
     * Convert a hue-saturation-value structure into an RGB structure.
     *
     * The hue element should be a degree value in the region of [0.0, 360.0).
     * The saturation and value elements should be in the range [0.0, 1.0].
     * Use RGBtoColourStr to convert back into a hex colour.
     *
     * @param {Object} hsvColour A HSV structure to convert.
     *
     * @returns {Object}
     */
    self.HSVtosRGB = function(hsvColour) {
        var colour = {
            red: 0,
            green: 0,
            blue: 0
        };

        if (hsvColour.saturation === 0) {
            colour.red = hsvColour.value;
            colour.green = hsvColour.value;
            colour.blue = hsvColour.value;
        } else {
            var chroma      = hsvColour.value * hsvColour.saturation;
            var minColour   = hsvColour.value - chroma;
            var interHue    = hsvColour.hue / 60.0;
            var interHueMod = interHue - 2 * (Math.floor(interHue / 2));
            var interCol    = chroma * (1 - Math.abs(interHueMod - 1));

            switch(Math.floor(interHue)) {
                case 0:
                    colour.red   = chroma;
                    colour.green = interCol;
                break;

                case 1:
                    colour.green = chroma;
                    colour.red   = interCol;
                break;

                case 2:
                    colour.green = chroma;
                    colour.blue  = interCol;
                break;

                case 3:
                    colour.blue  = chroma;
                    colour.green = interCol;
                break;

                case 4:
                    colour.blue = chroma;
                    colour.red  = interCol;
                break;

                case 5:
                    colour.red  = chroma;
                    colour.blue = interCol;
                break;
            }//end switch

            colour.red   = (colour.red + minColour);
            colour.green = (colour.green + minColour);
            colour.blue  = (colour.blue + minColour);
        }//end if

        return colour;
    };

    /**
     * Gets the text contents of an element.
     *
     * @param {Node}    element           The element being inspected.
     * @param {Boolean} [includeAlt=true] Include alt text from images.
     *
     * @returns {String} The text contents.
     */
    self.getElementTextContent = function(element, includeAlt)
    {
        if (includeAlt === undefined) {
            includeAlt = true;
        }

        var element = element.cloneNode(true);
        var nodes  = [];
        for (var i = 0; i < element.childNodes.length; i++) {
            nodes.push(element.childNodes[i]);
        }

        var text = [element.textContent];
        while (nodes.length > 0) {
            var node = nodes.shift();

            // If it's an element, add any sub-nodes to the process list.
            if (node.nodeType === 1) {
                if (node.nodeName.toLowerCase() === 'img') {
                    // If an image, include the alt text unless we are blocking it.
                    if ((includeAlt === true) && (node.hasAttribute('alt') === true)) {
                        text.push(node.getAttribute('alt'));
                    }
                } else {
                    for (var i = 0; i < node.childNodes.length; i++) {
                        nodes.push(node.childNodes[i]);
                    }
                }
            } else if (node.nodeType === 3) {
                // Text node.
                text.push(node.nodeValue);
            }
        }

        // Push the text nodes together and trim.
        text = text.join('').replace(/^\s+|\s+$/g,'');
        return text;
    };


    /**
     * Find a parent node matching a selector.
     *
     * @param {DOMNode} node     Node to search from.
     * @param {String}  selector The selector to search.
     *
     * @return DOMNode|null
     */
    self.findParentNode = function(node, selector) {
        if (node && node.matches && node.matches(selector)) {
            return node;
        }

        while (node && node.parentNode) {
            node = node.parentNode;

            if (node && node.matches && node.matches(selector)) {
                return node;
            }
        }

        return null;
    };


    self.getChildrenForTable = function(table, childNodeName)
    {
        if (table.nodeName.toLowerCase() !== 'table') {
            return null;
        }

        var rows    = [];
        var allRows = table.getElementsByTagName(childNodeName);

        // Filter out rows that don't belong to this table.
        for (var i = 0, l = allRows.length; i<l; i++) {
            if (self.findParentNode(allRows[i], 'table') === table) {
                rows.push(allRows[i]);
            }
        }

        return rows;

    };


    /**
     * Test for the correct headers attributes on table cell elements.
     *
     * Return value contains the following elements:
     * - required (Boolean):   Whether header association at all is required.
     * - used (Boolean):       Whether headers attribute has been used on at least
     *                         one table data (td) cell.
     * - allowScope (Boolean): Whether scope is allowed to satisfy the association
     *                         requirement (ie. max one row/one column).
     * - correct (Boolean):    Whether headers have been correctly used.
     * - missingThId (Array):  Array of th elements without IDs.
     * - missingTd (Array):    Array of elements without headers attribute.
     * - wrongHeaders (Array): Array of elements where headers attr is incorrect.
     *                         Each is a structure with following keys: element,
     *                         expected [headers attr], actual [headers attr].
     *
     * @param {DOMNode} element Table element to test upon.
     *
     * @return {Object} The above return value structure.
     */
    self.testTableHeaders = function(element)
    {
        var retval = {
            required: true,
            used: false,
            correct: true,
            allowScope: true,
            missingThId: [],
            missingTd: [],
            wrongHeaders: []
        }

        var rows      = self.getChildrenForTable(element, 'tr');
        var tdCells   = {};
        var skipCells = [];

        // Header IDs already used.
        var headerIds = {
            rows: [],
            cols: []
        };
        var multiHeaders = {
            rows: 0,
            cols: 0
        }
        var missingIds = false;

        for (var rownum = 0; rownum < rows.length; rownum++) {
            var row    = rows[rownum];
            var colnum = 0;

            for (var item = 0; item < row.childNodes.length; item++) {
                var cell = row.childNodes[item];
                if (cell.nodeType === 1) {
                    // Skip columns that are skipped due to rowspan.
                    if (skipCells[rownum]) {
                        while (skipCells[rownum][0] === colnum) {
                            skipCells[rownum].shift();
                            colnum++;
                        }
                    }

                    var nodeName = cell.nodeName.toLowerCase();
                    var rowspan  = Number(cell.getAttribute('rowspan')) || 1;
                    var colspan  = Number(cell.getAttribute('colspan')) || 1;

                    // If rowspanned, mark columns as skippable in the following
                    // row(s).
                    if (rowspan > 1) {
                        for (var i = rownum + 1; i < rownum + rowspan; i++) {
                            if (!skipCells[i]) {
                                skipCells[i] = [];
                            }

                            for (var j = colnum; j < colnum + colspan; j++) {
                                skipCells[i].push(j);
                            }
                        }
                    }

                    if (nodeName === 'th') {
                        var id = (cell.getAttribute('id') || '');

                        // Save the fact that we have a missing ID on the header.
                        if (id === '') {
                            retval.correct = false;
                            retval.missingThId.push(cell);
                        }

                        if ((rowspan > 1) && (colspan > 1)) {
                            // Multi-column AND multi-row header. Abandon all hope,
                            // As it must span across more than one row+column
                            retval.allowScope = false;
                        } else if (retval.allowScope === true) {
                            // If we haven't had a th in this column (row) yet,
                            // record it. if we find another th in this column (row),
                            // record that has multi-ths. If we already have a column
                            // (row) with multi-ths, we cannot use scope.
                            if (headerIds.cols[colnum] === undefined) {
                                headerIds.cols[colnum] = 0;
                            }

                            if (headerIds.rows[rownum] === undefined) {
                                headerIds.rows[rownum] = 0;
                            }

                            headerIds.rows[rownum] += colspan;
                            headerIds.cols[colnum] += rowspan;
                        }//end if
                    } else if ((nodeName === 'td')) {
                        if ((cell.hasAttribute('headers') === true) && (/^\s*$/.test(cell.getAttribute('headers')) === false)) {
                            retval.used = true;
                        }
                    }//end if

                    colnum += colspan;
                }//end if
            }//end for
        }//end for

        for (var i = 0; i < headerIds.rows.length; i++) {
            if (headerIds.rows[i] > 1) {
                multiHeaders.rows++;
            }
        }

        for (var i = 0; i < headerIds.cols.length; i++) {
            if (headerIds.cols[i] > 1) {
                multiHeaders.cols++;
            }
        }

        if ((multiHeaders.rows > 1) || (multiHeaders.cols > 1)) {
            retval.allowScope = false;
        } else if ((retval.allowScope === true) && ((multiHeaders.rows === 0) || (multiHeaders.cols === 0))) {
            // If only one column OR one row header.
            retval.required = false;
        }//end if

        // Calculate expected heading IDs. If they are not there or incorrect, flag
        // them.
        var cells = HTMLCS.util.getCellHeaders(element);
        for (var i = 0; i < cells.length; i++) {
            var cell     = cells[i].cell;
            var expected = cells[i].headers;

            if (cell.hasAttribute('headers') === false) {
                retval.correct = false;
                retval.missingTd.push(cell);
            } else {
                var actual = (cell.getAttribute('headers') || '').split(/\s+/);
                if (actual.length === 0) {
                    retval.correct = false;
                    retval.missingTd.push(cell);
                } else {
                    actual = ' ' + actual.sort().join(' ') + ' ';
                    actual = actual.replace(/\s+/g, ' ').replace(/(\w+\s)\1+/g, '$1').replace(/^\s*(.*?)\s*$/g, '$1');
                    if (expected !== actual) {
                        retval.correct = false;
                        var val = {
                            element: cell,
                            expected: expected,
                            actual: (cell.getAttribute('headers') || '')
                        }
                        retval.wrongHeaders.push(val);
                    }
                }//end if
            }//end if
        }//end for

        return retval;
    };

    /**
     * Return expected cell headers from a table.
     *
     * Returns null if not a table.
     *
     * Returns an array of objects with two properties:
     * - cell (Object) - the TD element referred to,
     * - headers (String) - the normalised list of expected headers.
     *
     * Cells are returned in DOM order. This may mean cells in a tfoot (which
     * normally precedes tbody if used) would come before tbody cells.
     *
     * If there are missing IDs on relevant table header (th) elements, this
     * method won't complain about it - it will just return them as empty. Its
     * job is to take the IDs it can get, not to complain about it (see, eg. the
     * test in WCAG2's sniff 1_3_1). If there are no headers for a cell, it
     * won't be included.
     *
     * @param {Object} table The table to test.
     *
     * @returns {Array}
     */
    self.getCellHeaders = function(table) {
        if (typeof table !== 'object') {
            return null;
        } else if (table.nodeName.toLowerCase() !== 'table') {
            return null;
        }


        var rows       = self.getChildrenForTable(table, 'tr');
        var skipCells  = [];
        var headingIds = {
            rows: {},
            cols: {}
        };

        // List of cells and headers. Each item should be a two-property object:
        // a "cell" object, and a normalised string of "headers".
        var cells = [];

        // Now determine the row and column headers for the table.
        // Go through once, first finding the th's to load up the header names,
        // then finding the td's to dump them off.
        var targetNodeNames = ['th', 'td'];
        for (var k = 0; k < targetNodeNames.length; k++) {
            var targetNode = targetNodeNames[k];
            for (var rownum = 0; rownum < rows.length; rownum++) {
                var row    = rows[rownum];
                var colnum = 0;

                for (var item = 0; item < row.childNodes.length; item++) {
                    var thisCell = row.childNodes[item];
                    if (thisCell.nodeType === 1) {
                        // Skip columns that are skipped due to rowspan.
                        if (skipCells[rownum]) {
                            while (skipCells[rownum][0] === colnum) {
                                skipCells[rownum].shift();
                                colnum++;
                            }
                        }

                        var nodeName = thisCell.nodeName.toLowerCase();
                        var rowspan  = Number(thisCell.getAttribute('rowspan')) || 1;
                        var colspan  = Number(thisCell.getAttribute('colspan')) || 1;

                        // If rowspanned, mark columns as skippable in the following
                        // row(s).
                        if (rowspan > 1) {
                            for (var i = rownum + 1; i < rownum + rowspan; i++) {
                                if (!skipCells[i]) {
                                    skipCells[i] = [];
                                }

                                for (var j = colnum; j < colnum + colspan; j++) {
                                    skipCells[i].push(j);
                                }
                            }
                        }

                        if (nodeName === targetNode) {
                            if (nodeName === 'th') {
                                // Build up the cell headers.
                                var id = (thisCell.getAttribute('id') || '');

                                for (var i = rownum; i < rownum + rowspan; i++) {
                                    headingIds.rows[i] = headingIds.rows[i] || {
                                        first: colnum,
                                        ids: []
                                    };
                                    headingIds.rows[i].ids.push(id);
                                }

                                for (var i = colnum; i < colnum + colspan; i++) {
                                    headingIds.cols[i] = headingIds.cols[i] || {
                                        first: rownum,
                                        ids: []
                                    };
                                    headingIds.cols[i].ids.push(id);
                                }
                            } else if (nodeName === 'td') {
                                // Dump out the headers and cells.
                                var exp = [];
                                for (var i = rownum; i < rownum + rowspan; i++) {
                                    for (var j = colnum; j < colnum + colspan; j++) {
                                        if ((headingIds.rows[i]) && (j >= headingIds.rows[i].first)) {
                                            exp = exp.concat(headingIds.rows[i].ids);
                                        }

                                        if ((headingIds.cols[j]) && (i >= headingIds.cols[j].first)) {
                                            exp = exp.concat(headingIds.cols[j].ids);
                                        }
                                    }//end for
                                }//end for

                                if (exp.length > 0) {
                                    exp = ' ' + exp.sort().join(' ') + ' ';
                                    exp = exp.replace(/\s+/g, ' ').replace(/(\w+\s)\1+/g, '$1').replace(/^\s*(.*?)\s*$/g, '$1');
                                    cells.push({
                                        cell: thisCell,
                                        headers: exp
                                    });
                                }
                            }
                        }

                        colnum += colspan;
                    }//end if
                }//end for
            }//end for
        }//end for

        // Build the column and row headers that we expect.
        return cells;
    };

    /**
     * Get the previous sibling element.
     *
     * This is a substitute for previousSibling where there are text, comment and
     * other nodes between elements.
     *
     * If tagName is null, immediate is ignored and effectively defaults to true: the
     * previous element will be returned regardless of what it is.
     *
     * @param {DOMNode} element           Element to start from.
     * @param {String}  [tagName=null]    Only match this tag. If null, match any.
     *                                    Not case-sensitive.
     * @param {Boolean} [immediate=false] Only match if the tag in tagName is the
     *                                    immediately preceding non-whitespace node.
     *
     * @returns {DOMNode} The appropriate node or null if none is found.
     */
    self.getPreviousSiblingElement = function(element, tagName, immediate) {
        if (tagName === undefined) {
            tagName = null;
        }

        if (immediate === undefined) {
            immediate = false;
        }

        var prevNode = element.previousSibling;
        while (prevNode !== null) {
            if (prevNode.nodeType === 3) {
                if ((HTMLCS.util.isStringEmpty(prevNode.nodeValue) === false) && (immediate === true)) {
                    // Failed. Immediate node requested and we got text instead.
                    prevNode = null;
                    break;
                }
            } else if (prevNode.nodeType === 1) {
                // If this an element, we break regardless. If it's an "a" node,
                // it's the one we want. Otherwise, there is no adjacent "a" node
                // and it can be ignored.
                if ((tagName === null) || (prevNode.nodeName.toLowerCase() === tagName)) {
                    // Correct element, or we aren't picky.
                    break;
                } else if (immediate === true) {
                    // Failed. Immediate node requested and not correct tag name.
                    prevNode = null;
                    break;
                }

                break;
            }//end if

            prevNode = prevNode.previousSibling;
        }//end if

        return prevNode;
    };

    /**
     * Get the next sibling element.
     *
     * This is a substitute for nextSibling where there are text, comment and
     * other nodes between elements.
     *
     * If tagName is null, immediate is ignored and effectively defaults to true: the
     * next element will be returned regardless of what it is.
     *
     * @param {DOMNode} element           Element to start from.
     * @param {String}  [tagName=null]    Only match this tag. If null, match any.
     *                                    Not case-sensitive.
     * @param {Boolean} [immediate=false] Only match if the tag in tagName is the
     *                                    immediately following non-whitespace node.
     *
     * @returns {DOMNode} The appropriate node or null if none is found.
     */
    self.getNextSiblingElement = function(element, tagName, immediate) {
        if (tagName === undefined) {
            tagName = null;
        }

        if (immediate === undefined) {
            immediate = false;
        }

        var nextNode = element.nextSibling;
        while (nextNode !== null) {
            if (nextNode.nodeType === 3) {
                if ((HTMLCS.util.isStringEmpty(nextNode.nodeValue) === false) && (immediate === true)) {
                    // Failed. Immediate node requested and we got text instead.
                    nextNode = null;
                    break;
                }
            } else if (nextNode.nodeType === 1) {
                // If this an element, we break regardless. If it's an "a" node,
                // it's the one we want. Otherwise, there is no adjacent "a" node
                // and it can be ignored.
                if ((tagName === null) || (nextNode.nodeName.toLowerCase() === tagName)) {
                    // Correct element, or we aren't picky.
                    break;
                } else if (immediate === true) {
                    // Failed. Immediate node requested and not correct tag name.
                    nextNode = null;
                    break;
                }

                break;
            }//end if

            nextNode = nextNode.nextSibling;
        }//end if

        return nextNode;
    };

    return self;
}();
    // Expose globals.
    return _global;
}));
'use strict';

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

/* global HTMLCS, document */
/* eslint-disable no-console */
/*eslint no-unused-vars: 0*/

var output = function output(msg) {
  var typeName = 'UNKNOWN';

  switch (msg.type) {
    case HTMLCS.ERROR:
      {
        typeName = 'ERROR';
        break;
      }
    case HTMLCS.WARNING:
      {
        typeName = 'WARNING';
        break;
      }

    case HTMLCS.NOTICE:
      {
        typeName = 'NOTICE';
        break;
      }
  }

  var element = msg.element;
  var message = [typeName, msg.code, msg.msg, element.outerHTML, element.className, element.id].join('|');

  console.log(message);

  return message;
};

var Runner = function () {
  function Runner(options) {
    _classCallCheck(this, Runner);
  }

  _createClass(Runner, [{
    key: 'run',
    value: function run(standard) {
      var _this = this;

      var messages = [];

      // At the moment, it passes the whole DOM document.
      HTMLCS.process(standard, document, function () {
        messages = HTMLCS.getMessages();
        messages = messages.map(function (message) {
          return output(message);
        }, _this);
        console.log('done');
      });

      return messages;
    }
  }]);

  return Runner;
}();

var HTMLCS_RUNNER = new Runner();
HTMLCS.process(arguments[0], window.document);
return HTMLCS.getMessagesJSON();
})(arguments);