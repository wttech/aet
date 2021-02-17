plugins {
    id("com.cognifide.aet.java-conventions")
	id("com.cognifide.aet.test-coverage")
    id("biz.aQute.bnd.builder")
}

dependencies {
    projectCompile(project(":communication-api"))
    compileOnly("com.google.guava:guava:25.1-jre")
    compileOnly("javax.validation:validation-api:1.1.0.Final")
    compileOnly("org.hibernate:hibernate-validator:4.3.2.Final")
    compileOnly("org.jboss.logging:jboss-logging:3.3.2.Final")
    compileOnly("com.google.code.gson:gson:2.8.5")
}

tasks.jar {
    manifest {
        attributes(
            Pair("Bundle-Vendor", "Cognifide Ltd."),
            Pair("Export-Package", "com.cognifide.aet.vs.*")
        )
    }
}

description = "AET :: API :: Data Storage API"
