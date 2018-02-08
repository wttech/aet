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
define(['angularAMD'], function (angularAMD) {
    'use strict';
    angularAMD.directive('aetSidepanelResizer', SidepanelResizerDirective);

    function SidepanelResizerDirective() {
        var directive = {
            restrict: 'AE',
            scope: {
                'type': '@'
            },
            link: linkFunc
        };

        return directive;

        function linkFunc($scope, $element) {
            var $body = $('body');
            var $content = $('.main');
            var $sidepanel = $('.aside');

            var isSidepanelResized = false;
            var INIT_SIDEPANEL_WIDTH = 350;
            var pageX = INIT_SIDEPANEL_WIDTH;

            $element.on('mousedown', function (e) {
                e.preventDefault();

                isSidepanelResized = true;
            });

            $body.on('mousemove', function (e) {
                if (isSidepanelResized) {
                    if(e.pageX >= INIT_SIDEPANEL_WIDTH && e.pageX <= $(window).width()/2) {
                        pageX = e.pageX;
                    }
                    updateWidth(pageX);
                    e.preventDefault();
                }
            });

            $body.on('mouseup', function () {
                isSidepanelResized = false;
            });

            function updateWidth(newWidth) {
                var newContentWidth = $content.width() + parseInt($content.css('left')) - newWidth;

                $sidepanel.css('width', newWidth);
                $content.css('left', newWidth);
                $content.css('width', newContentWidth);
            }
        }
    }
});
