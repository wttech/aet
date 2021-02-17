plugins {
    id("com.cognifide.aet.java-conventions")
	id("net.idlestate.gradle-duplicate-classes-check")
    id("biz.aQute.bnd.builder")
}

dependencies {
    testImplementation("junit:junit:4.11")
    testImplementation("org.hamcrest:hamcrest-all:1.3")
    testImplementation("org.mockito:mockito-all:1.9.5")
    projectCompile(project(":jobs-api"))
    projectCompile(project(":communication-api"))
    projectCompile(project(":datastorage-api"))
    projectCompile(project(":communication"))
    projectCompile(project(":proxy"))
    projectCompile(project(":selenium"))
    compileOnly("com.google.guava:guava:25.1-jre")
    compileOnly("org.apache.activemq:activemq-osgi:5.15.2") {
        exclude("com.google.guava", "guava")
    }
    compileOnly("org.osgi:org.osgi.core:4.3.0")
    compileOnly("org.osgi:org.osgi.service.component.annotations:1.3.0")
    compileOnly("org.osgi:org.osgi.annotation:6.0.0")
    compileOnly("org.osgi:org.osgi.service.metatype.annotations:1.3.0")
    compileOnly("javax.validation:validation-api:1.1.0.Final")
    compileOnly("org.apache.httpcomponents:fluent-hc:4.5.5")
    compileOnly("org.apache.httpcomponents:httpclient:4.4")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
}

tasks.jar {
    manifest {
        attributes(
                Pair("Bundle-Vendor", "Cognifide Ltd."),
                Pair("Import-Package", "javax.annotation;resolution:=optional,*"),
                Pair("Export-Package", "com.cognifide.aet.worker.*")
        )
    }
}

description = "AET :: Core :: Worker"
