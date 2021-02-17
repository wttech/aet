plugins {
    id("com.cognifide.aet.java-conventions")
	id("net.idlestate.gradle-duplicate-classes-check")
    id("biz.aQute.bnd.builder")
}

dependencies {
    testImplementation("junit:junit:4.11")
    testImplementation("org.hamcrest:hamcrest-all:1.3")
    testImplementation("org.mockito:mockito-all:1.9.5")
    projectCompile(project(":communication-api"))
    implementation("javax.jms:jms:1.1")
    implementation("org.slf4j:slf4j-api:1.7.7")
    compileOnly("org.osgi:org.osgi.service.component.annotations:1.3.0")
    compileOnly("org.osgi:org.osgi.annotation:6.0.0")
    compileOnly("org.osgi:org.osgi.service.metatype.annotations:1.3.0")
    compileOnly("org.apache.activemq:activemq-osgi:5.15.2")
    compileOnly("org.apache.felix:org.apache.felix.configadmin:1.8.16")
}

tasks.jar {
    manifest {
        attributes(
            Pair("Bundle-Vendor", "Cognifide Ltd."),
            Pair("Export-Package", "com.cognifide.aet.queues.*")
        )
    }
}

description = "AET :: Core :: Communication"
