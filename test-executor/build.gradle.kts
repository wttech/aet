plugins {
    id("com.cognifide.aet.java-conventions")
    id("com.cognifide.aet.test-coverage")
    id("biz.aQute.bnd.builder")
}

dependencies {
    testImplementation("junit:junit:4.13.1")
    testImplementation("org.hamcrest:hamcrest-all:1.3")
    testImplementation("org.mockito:mockito-all:1.9.5")
    projectCompile(project(":communication-api"))
    projectCompile(project(":datastorage-api"))
    projectCompile(project(":rest-endpoint"))
    implementation("javax.jms:jms:1.1")
    implementation("com.google.guava:guava:25.1-jre")
    implementation("org.simpleframework:simple-xml:2.7.1")
    implementation("org.slf4j:slf4j-api:1.7.7")
    compileOnly("org.osgi:org.osgi.compendium:4.2.0")
    compileOnly("org.ops4j.pax.web:pax-web-runtime:1.1.17")
    compileOnly("commons-io:commons-io:2.6")
    compileOnly("commons-fileupload:commons-fileupload:1.3.3")
    compileOnly("org.apache.commons:commons-lang3:3.7")
    compileOnly("javax.validation:validation-api:1.1.0.Final")
    compileOnly("org.apache.geronimo.specs:geronimo-servlet_2.5_spec:1.2")
    compileOnly("com.google.code.gson:gson:2.8.5")
    compileOnly("org.apache.activemq:activemq-osgi:5.15.2")
    compileOnly("org.osgi:org.osgi.service.component.annotations:1.3.0")
    compileOnly("org.osgi:org.osgi.annotation:6.0.0")
    compileOnly("org.osgi:org.osgi.service.metatype.annotations:1.3.0")
    compileOnly("org.slf4j:slf4j-api:1.7.7")
}

tasks.jar {
    manifest {
        attributes(
            Pair("Bundle-Vendor", "Cognifide Ltd."),
            Pair("Export-Package", "")
        )
    }
}

description = "AET :: Test Executor"
