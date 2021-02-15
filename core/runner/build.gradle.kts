plugins {
    id("com.cognifide.aet.java-conventions")
    id("biz.aQute.bnd.builder")
}

dependencies {
    testImplementation("junit:junit:4.13.1")
    testImplementation("org.hamcrest:hamcrest-all:1.3")
    testImplementation("org.mockito:mockito-all:1.9.5")
    testImplementation("com.googlecode.zohhak:zohhak:1.1.1")
    testImplementation("commons-io:commons-io:2.6")
    implementation("com.google.guava:guava:25.1-jre")
    projectCompile(project(":communication-api"))
    projectCompile(project(":datastorage-api"))
    projectCompile(project(":communication"))
    compileOnly("com.google.code.gson:gson:2.8.5")
    compileOnly("commons-io:commons-io:2.6")
    compileOnly("org.apache.commons:commons-lang3:3.7")
    compileOnly("org.apache.activemq:activemq-osgi:5.15.2")
    compileOnly("javax.validation:validation-api:1.1.0.Final")
    compileOnly("org.osgi:org.osgi.service.component.annotations:1.3.0")
    compileOnly("org.osgi:org.osgi.annotation:6.0.0")
    compileOnly("org.osgi:org.osgi.service.metatype.annotations:1.3.0")
}

tasks.jar {
    manifest {
        attributes(
                Pair("Bundle-Vendor", "Cognifide Ltd."),
                Pair("Export-Package", "com.cognifide.aet.runner*")
        )
    }
}

description = "AET :: Core :: Runner"
