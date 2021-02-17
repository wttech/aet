plugins {
    id("com.cognifide.aet.java-conventions")
	id("com.cognifide.aet.test-coverage")
    id("biz.aQute.bnd.builder")
}

dependencies {
    testImplementation("junit:junit:4.11")
    testImplementation("org.hamcrest:hamcrest-all:1.3")
    testImplementation("org.mockito:mockito-all:1.9.5")
    testImplementation("com.github.stefanbirkner:system-rules:1.18.0")
    projectCompile(project(":communication-api"))
    projectCompile(project(":datastorage-api"))
    projectCompile(project(":accessibility-report"))
    implementation("org.apache.commons:commons-lang3:3.7")
    compileOnly("org.osgi:org.osgi.compendium:4.2.0")
    compileOnly("org.ops4j.pax.web:pax-web-runtime:1.1.17")
    compileOnly("com.google.guava:guava:25.1-jre")
    compileOnly("commons-io:commons-io:2.6")
    compileOnly("javax.validation:validation-api:1.1.0.Final")
    compileOnly("org.apache.geronimo.specs:geronimo-servlet_2.5_spec:1.2")
    compileOnly("com.google.code.gson:gson:2.8.5")
    compileOnly("org.osgi:org.osgi.service.component.annotations:1.3.0")
    compileOnly("org.osgi:org.osgi.annotation:6.0.0")
    compileOnly("org.osgi:org.osgi.service.metatype.annotations:1.3.0")
    compileOnly("org.freemarker:freemarker:2.3.29")
    compileOnly("org.slf4j:slf4j-api:1.7.7")
    compileOnly("org.osgi:org.osgi.core:4.3.0")
}

tasks.jar {
    manifest {
        attributes(
            Pair("Bundle-Vendor", "Cognifide Ltd."),
            Pair("Export-Package", "com.cognifide.aet.rest.*")
        )
    }
}

description = "AET :: Rest Endpoint"
