import com.google.javascript.jscomp.CompilerOptions.LanguageMode;
import org.gradlewebtools.minify.JsMinifyTask;

plugins {
    id("com.cognifide.aet.java-conventions")
    id("com.cognifide.aet.test-coverage")
    id("biz.aQute.bnd.builder")
    id("org.gradlewebtools.minify") version "1.1.0"
}


dependencies {
    testImplementation("junit:junit:4.13.1")
    testImplementation("org.hamcrest:hamcrest-all:1.3")
    testImplementation("org.mockito:mockito-all:1.9.5")
    testImplementation("com.googlecode.zohhak:zohhak:1.1.1")
    testImplementation("xmlunit:xmlunit:1.2")
    testImplementation("uk.co.jemos.podam:podam:3.6.0.RELEASE")
    testImplementation("com.googlecode.catch-exception:catch-exception:1.2.0")
    testImplementation("org.skyscreamer:jsonassert:1.3.0")
    testImplementation("org.slf4j:slf4j-simple:1.7.7")
    testImplementation("org.assertj:assertj-core:3.11.1")
    implementation("com.googlecode.java-diff-utils:diffutils:1.3.0")
    implementation("org.apache.commons:commons-lang3:3.7")
    implementation("org.jsoup:jsoup:1.11.3")
    compileOnly("org.osgi:org.osgi.service.component.annotations:1.3.0")
    compileOnly("org.osgi:org.osgi.annotation:6.0.0")
    compileOnly("org.osgi:org.osgi.service.metatype.annotations:1.3.0")
    compileOnly("org.osgi:org.osgi.core:4.3.0")
    compileOnly("org.apache.httpcomponents:fluent-hc:4.5.5")
    compileOnly("com.google.code.gson:gson:2.8.5")
    compileOnly("org.slf4j:slf4j-api:1.7.7")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    projectCompile(project(":communication-api"))
    projectCompile(project(":jobs-api"))
    projectCompile(project(":datastorage-api"))
    projectCompile(project(":validation-api"))
    projectCompile(project(":proxy"))
    projectCompile(project(":selenium"))
    projectCompile(project(":w3chtml5validator"))
}

tasks.register<JsMinifyTask>("minifyHtmlcs") {
    srcDir = project.file("src/main/resources/collectors/accessibility")
    dstDir = project.file("${buildDir}/resources/main/collectors/accessibility")
    options {
        languageOut = LanguageMode.ECMASCRIPT5
    }
}

tasks.processResources {
    dependsOn(tasks["minifyHtmlcs"])
}

tasks.jar {
    manifest {
        attributes(
                Pair("Bundle-Vendor", "Cognifide Ltd."),
                Pair("Import-Package", "javax.annotation;resolution:=optional,*"),
                Pair("Export-Package", "com.cognifide.aet.job.common.*,collectors.*")
        )
    }
}

description = "AET :: Core :: Jobs"
