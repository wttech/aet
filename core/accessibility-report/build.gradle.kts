plugins {
    id("com.cognifide.aet.java-conventions")
    id("com.cognifide.aet.test-coverage")
    id("org.jetbrains.kotlin.jvm") version ("1.4.21-2")
    id("biz.aQute.bnd.builder")
}

dependencies {
    implementation("org.apache.poi:ooxml-schemas:1.4")
    implementation("org.jetbrains:annotations:19.0.0")
    projectCompile(project(":communication-api"))
    projectCompile(project(":datastorage-api"))
    compileOnly("org.osgi:org.osgi.service.component.annotations:1.3.0")
    compileOnly("org.osgi:org.osgi.annotation:6.0.0")
    compileOnly("org.osgi:org.osgi.service.metatype.annotations:1.3.0")
    compileOnly("org.slf4j:slf4j-api:1.7.7")
    compileOnly("org.mongodb:mongo-java-driver:3.8.0")
    compileOnly("com.google.code.gson:gson:2.8.5")
    compileOnly("commons-io:commons-io:2.6")
    compileOnly("org.apache.commons:commons-text:1.8")
    compileOnly("org.apache.commons:commons-collections4:4.4")
    compileOnly("org.apache.poi:poi-ooxml:4.1.2")
    compileOnly("org.jetbrains.kotlin:kotlin-osgi-bundle:1.3.72")
}

tasks.jar {
    manifest {
        attributes(
            Pair("Bundle-Vendor", "Cognifide Ltd."),
            Pair("Import-Package", "javax.annotation;resolution:=optional,*"),
            Pair("Export-Package", "com.cognifide.aet.accessibility.report.service.*")
        )
    }
}

description = "AET :: Core :: Accessibility Report"
