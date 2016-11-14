/*
 * Automated Exploratory Tests
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

/**
 * Grunt module implementing automation for Cognifide regular project
 * @module Grunt CogProject
 * @param  {Object} grunt
 * @return {void}
 */
module.exports = function (grunt) {
	'use strict';

	require('time-grunt')(grunt);

	var config = grunt.file.readJSON('settings.json');

	grunt.initConfig({

		/**
		 * Responsible establishing connection with server
		 */
		'connect': {
			'production': {
				options: {
					port: config.server.port,
					base: config.server.base,
					hostname: '0.0.0.0',
					livereload: true
				}
			},
			'build': {
				options: {
					port: config.server.port,
					base: config.server.base,
					hostname: '0.0.0.0'
				}
			}
		},

		'open': {
			'production': {
				path: 'http://localhost:' + config.server.port + '/' + config.server.root
			}
		},

		'jshint': {
			'production': {
				options: {
					/**
					 * Project specific rules are always put into external file
					 * @type {String}
					 */
					jshintrc: '.jshintrc'
				},
				files: {
					src: [
						// You need to include the files manually
						'Gruntfile.js',
						config.app.base + '/components/**/*.js',
						config.app.base + '/layout/**/**/*.js',
						config.app.base + '/services/**/*.js',
						config.app.base + '/app.config.js',
						config.app.base + '/app.module.js',
						config.app.base + '/app.route.js'
					]
				}
			}
		},

		'csslint': {
			options: {
				csslintrc: '.csslintrc'
			},
			strict: {
				options: {
					import: true
				},
				src: [config.assets.css + '/**/*.css']
			},
			lax: {
				options: {
					import: false
				},
				src: [config.assets.css + '/**/*.css']
			}
		},

		'concurrent': {
			'production': {
				tasks: ['compass', 'jshint', 'csslint']
			},
			'dist': ['compass']
		},

		'compass': {
			build: {
				options: {
					config: 'compass-config.rb',
					//sassDir: config.assets.base + config.assets.sass,
					sassDir: 'assets/sass',
					//cssDir: config.assets.base + config.assets.css,
					cssDir: 'assets/css',
					imagesDir: config.assets.base + config.assets.images,
					fontsDir: config.assets.base + config.assets.fonts,
					javascriptsDir: config.app.base + config.app.js,
					outputStyle: 'compact'
				}
			}
		},

		'watch': {

			'jshint': {
				files: ['Gruntfile.js', config.app.base + '/**/*.js'],
				tasks: ['jshint']
			},

			'jsmin': {
				options: {
					livereload: true
				},
				files: [config.app.base + '/**/*.js']
			},

			'css': {
				options: {
					livereload: true
				},
				files: [config.assets.base + config.assets.css + '/**/*.css'],
				tasks: ['csslint']
			},

			'sass': {
				options: {
					spawn: false,
					livereload: true
				},
				files: [config.assets.base + config.assets.sass + '/**/*.scss'],
				tasks: ['compass']
			},

			'htmlbuild': {
				files: config.app.base + '/scaffold/**/*.{htm,html}'
			},

			'html': {
				options: {
					livereload: true
				},
				files: ['*.html', config.app.base + '/**/*.html']
			}
		},
		'requirejs': {
			options: {
				'appDir': 'app',
				'dir': 'build',
				'mainConfigFile': 'app/app.config.js',
				'uglify2': {
					mangle: false
				},
				'optimize': 'uglify2',
				'normalizeDirDefines': 'skip',
				'skipDirOptimize': true,
				'removeCombined': true,
				'optimizeCss': 'skip'
			},
			centralizedAlmond: {
				options: {
					almond: true,
					replaceRequireScript: [{
						files: ['build/*.html'],
						module: 'common',
						modulePath: 'common'
					}],
					'modules': [{
						'name': 'common',
						'include': ['jquery',
							'lodash',
							'bootstrap'
						]
					}]
				}
			}
		},
		'bower': {
			install: {
				options: {
					targetDir: 'assets/libs/'
				}
			}
		}
	});

	/**
	 * Load all dependencies from package.json that starts with "grunt-*"
	 */
	require('matchdep').filterDev('grunt-*').forEach(grunt.loadNpmTasks);


	grunt.registerTask('server', [
		'connect:production',
		'open:production',
		'watch'
	]);

	grunt.registerTask('build', [
		'jshint',
		'csslint',
		'bower'
	]);

	grunt.registerTask('default', [
		'build',
		'compass:build',
		'server'
	]);
};