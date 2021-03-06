plugins {
    id("com.cognifide.aet.java-conventions")
    id("com.cognifide.aet.test-coverage")
}

dependencies {
    projectCompile(project(":communication-api"))
    projectCompile(project(":client-core"))
    implementation("org.apache.maven:maven-plugin-api:2.0")
    implementation("org.apache.maven:maven-project:2.2.1")
    implementation("org.codehaus.plexus:plexus-utils:3.0.8")
    implementation("com.google.guava:guava:30.1.1-jre")
    implementation("org.apache.commons:commons-lang3:3.3.2")
    implementation("commons-io:commons-io:2.4")
    implementation("com.jcabi:jcabi-log:0.12.2")
    implementation("org.slf4j:slf4j-api:1.7.10")
    implementation("org.slf4j:slf4j-log4j12:1.7.10")
    implementation("org.simpleframework:simple-xml:2.7.1")
    implementation("javax.validation:validation-api:1.1.0.Final")
    implementation("org.hibernate:hibernate-validator:4.3.2.Final")
    testImplementation("junit:junit:4.13.1")
    testImplementation("org.hamcrest:hamcrest-all:1.3")
    testImplementation("org.mockito:mockito-all:1.9.5")
    compileOnly("org.apache.maven.plugin-tools:maven-plugin-annotations:3.2")
}

description = "AET :: Client :: Maven Plugin"
