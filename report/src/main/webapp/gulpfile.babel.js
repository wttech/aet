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
import gulp             from 'gulp';
import browserify       from 'browserify';
import babelify         from 'babelify';
import source           from 'vinyl-source-stream';
import buffer           from 'vinyl-buffer';
import uglify           from 'gulp-uglify';
import sourceMaps       from 'gulp-sourcemaps';
import concat           from 'gulp-concat';
import glob             from 'glob';
import sass             from 'gulp-sass';
import cleanCSS         from 'gulp-clean-css';
import browserSync      from 'browser-sync';
import jshint           from 'gulp-jshint';
import csslint          from 'gulp-csslint';
import jshintSummary    from 'jshint-summary';
import bower            from 'gulp-bower';



gulp.task("parseSCSS", ["installLibs"], () => {
    gulp.src('./assets/sass/*.scss')
    .pipe(sourceMaps.init())
    .pipe(sass())
    .pipe(concat('main.css'))
    .pipe(cleanCSS())
    .pipe(sourceMaps.write('./'))
    .pipe(gulp.dest('./assets/css'))
    .pipe(browserSync.reload({stream:true}));
});

//this task is kinda useless now, but may come handy in the future
gulp.task('parseJS', () => {
    const files = glob.sync('./app/*.js');
    return browserify({entries: files, debug: true})
        .transform("babelify", { presets: ["es2015"] })
        .bundle()
        .pipe(source('bundle.js'))
        .pipe(buffer())
        .pipe(sourceMaps.init())
        .pipe(uglify())
        .pipe(sourceMaps.write('./'))
        .pipe(gulp.dest('./dist/js'))
        .pipe(browserSync.reload({stream:true}));
});

gulp.task('lintJS', function() {
    return gulp.src('./app/**/*.js')
        .pipe(jshint('.jshintrc'))
        .pipe(jshint.reporter('jshint-summary', {
        verbose: true,
        reasonCol: 'cyan,bold'
        }));
});

gulp.task('installLibs', function() {
    return bower({ directory: 'assets/libs'});
})

gulp.task('lintCSS', function() {
    gulp.src('./assets/css/main.css')
      .pipe(csslint())
      .pipe(csslint.formatter());
});

gulp.task('watch', ['parseSCSS', 'lintJS'], () => {

    browserSync.init({
        port: 9000,
        server: {
            baseDir: "./",
        }
    });

    gulp.watch('./assets/sass/*.scss', ['parseSCSS']);
    gulp.watch('./app/**/*.js', ['lintJS']).on('change', browserSync.reload);
});


gulp.task('default',['watch']);
gulp.task('build', ['installLibs', 'parseSCSS', 'lintJS']);

