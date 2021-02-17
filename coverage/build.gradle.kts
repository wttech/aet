tasks.register("make") {
    dependsOn(rootProject.getTasksByName("jacocoTestReport", true))
}