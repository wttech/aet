plugins {
    id("com.cognifide.aet.java-conventions")
	id("com.cognifide.aet.test-coverage")
    id("biz.aQute.bnd.builder")
}

dependencies {
    testImplementation("junit:junit:4.11")
    projectCompile(project(":communication-api"))
    projectCompile(project(":datastorage-api"))
    projectCompile(project(":selenium"))
    implementation("com.google.guava:guava:25.1-jre")
    compileOnly("org.apache.commons:commons-lang3:3.7")
    compileOnly("org.apache.httpcomponents:fluent-hc:4.5.5")
    compileOnly("org.codehaus.jackson:jackson-mapper-asl:1.9.13")
}

tasks.jar {
    manifest {
        attributes(
            Pair("Bundle-SymbolicName", project.name),
            Pair("Bundle-Name", project.name),
            Pair("Bundle-Vendor", "Cognifide Ltd."),
            Pair("Export-Package", "com.cognifide.aet.job.api.*,org.browsermob.core.*")
        )
    }
}

description = "AET :: API :: Jobs API"
