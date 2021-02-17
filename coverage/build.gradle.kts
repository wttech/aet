plugins {
    id("com.cognifide.aet.java-conventions")
	id("com.cognifide.aet.test-coverage")
}

tasks.register("make") {
    dependsOn(rootProject.getTasksByName("jacocoTestReport", true))
}