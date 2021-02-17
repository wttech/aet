plugins {
    jacoco
}

afterEvaluate {
    tasks.withType<JacocoReport> {
        dependsOn(tasks.getByName("test"))
        reports {
            xml.isEnabled = true
            xml.destination = file("${coverageRoot(project)}/report.xml")

            html.isEnabled = true
            html.destination = file("${coverageRoot(project)}/htmlReport")
        }
    }
}

fun coverageRoot(project: Project): String {
    return "${project(":coverage").buildDir}/${project.name}"
}