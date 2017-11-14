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

module.exports = function (grunt) {
    require('matchdep').filterAll('grunt-*').forEach(grunt.loadNpmTasks);

    var pkgJson = require('./package.json');
    var releaseDirectory = 'wiki/releases/' + pkgJson.version;
    var releaseFile = releaseDirectory + '/Documentation-' + pkgJson.version + '.md';

    grunt.initConfig({
        copy: {
            assets: {
                expand: true,
                cwd: 'wiki',
                src: 'assets/**',
                dest: releaseDirectory
            }
        },
        json_generator: {
            markdown: {
                dest: 'markdown.json',
                options: {
                    build: releaseFile,
                    files: [ 'DocumentationTemplate.md' ]
                }
            }
        }
    });

    grunt.registerTask('markdown-compile', 'Compiles markdown', function() {
        var done = this.async();

        var fs = require('fs-extra');

        /* This is synchronous function that checks whether specified directory exists and creates it if it
           doesn't exist. It is required here since the markdown-include package produces error if target
           directory doesn't exist.
        */
        fs.ensureDirSync(releaseDirectory);

        var markdownInclude = require('markdown-include');
        markdownInclude.compileFiles('markdown.json').then(function (data) {
            done();
        });
        setTimeout(function() {
            done();
        }, 5000);
    });

    grunt.registerTask('build', ['json_generator', 'markdown-compile', 'copy']);
};