plugins {
    id("com.cognifide.aet.java-conventions")
    id("com.cognifide.aet.test-coverage")
    id("war")
}

dependencies {
    implementation("taglibs:standard:1.1.2")
    implementation("javax.servlet:jstl:1.2")
    testImplementation("junit:junit:4.11")
    implementation("javax.servlet:javax.servlet-api:3.0.1")
}

description = "AET :: Integration Tests :: Sample Site"
