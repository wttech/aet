plugins {
    id("com.cognifide.aet.java-conventions")
    id("com.cognifide.aet.test-coverage")
    id("biz.aQute.bnd.builder")
}

dependencies {
    testImplementation("junit:junit:4.13.1")
    testImplementation("org.hamcrest:hamcrest-all:1.3")
    testImplementation("org.mockito:mockito-all:1.9.5")
    testImplementation("com.github.stefanbirkner:system-rules:1.18.0")
    projectCompile(project(":communication-api"))
    projectCompile(project(":datastorage-api"))
    projectCompile(project(":validation-api"))
    projectCompile(project(":validation"))
    implementation("org.mongodb:mongo-java-driver:3.8.0")
    implementation("org.slf4j:slf4j-api:1.7.7")
    implementation("org.apache.commons:commons-lang3:3.7")
    compileOnly("com.google.guava:guava:25.1-jre")
    compileOnly("commons-codec:commons-codec:1.11")
    compileOnly("org.osgi:org.osgi.service.component.annotations:1.3.0")
    compileOnly("org.osgi:org.osgi.annotation:6.0.0")
    compileOnly("org.osgi:org.osgi.service.metatype.annotations:1.3.0")
    compileOnly("com.google.code.gson:gson:2.8.5")
    compileOnly("org.osgi:org.osgi.compendium:4.2.0")
    compileOnly("commons-io:commons-io:2.6")
    compileOnly("org.apache.servicemix.bundles:org.apache.servicemix.bundles.quartz:2.3.0_2")
}

tasks.jar {
    manifest {
        attributes(
            Pair("Bundle-Vendor", "Cognifide Ltd."),
            Pair("Export-Package", "com.cognifide.aet.vs.metadata.*,com.cognifide.aet.vs.mongodb.*")
        )
    }
}

description = "AET :: Core :: Data Storage"
