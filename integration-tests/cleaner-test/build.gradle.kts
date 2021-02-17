plugins {
    id("com.cognifide.aet.java-conventions")
	id("net.idlestate.gradle-duplicate-classes-check")
}

configurations {
    testCompile {
        extendsFrom(configurations.compileOnly.get())
    }
}

dependencies {
    testImplementation("junit:junit:4.11")
    testImplementation("org.mockito:mockito-all:1.9.5")
    testImplementation("org.hamcrest:hamcrest-all:1.3")
    testImplementation("com.googlecode.zohhak:zohhak:1.1.1")
    testImplementation("com.google.code.gson:gson:2.8.5")
    testImplementation("de.bwaldvogel:mongo-java-server:1.12.0")
    testImplementation("org.mongodb:mongo-java-driver:3.8.0")
    testImplementation("org.apache.sling:org.apache.sling.testing.osgi-mock.junit4:2.4.6")
    projectCompile(project(":communication-api"))
    projectCompile(project(":cleaner"))
    projectCompile(project(":datastorage-api"))
    projectCompile(project(":datastorage"))
    compileOnly("org.osgi:org.osgi.core:4.3.0")
    compileOnly("org.osgi:org.osgi.compendium:4.2.0")
    compileOnly("com.google.guava:guava:25.1-jre")
    compileOnly("org.apache.commons:commons-lang3:3.7")
    compileOnly("org.apache.servicemix.bundles:org.apache.servicemix.bundles.quartz:2.3.0_2")
    compileOnly("org.apache.camel:camel-core:2.24.0")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
}

description = "AET :: Integration Tests :: Cleaner Test"
