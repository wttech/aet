plugins {
    id("myproject.jacoco-aggregation")
}

dependencies {
    implementation(project(":accessibility-report"))
    implementation(project(":validation"))
    implementation(project(":runner"))
    implementation(project(":rest-endpoint"))
    implementation(project(":datastorage-api"))
    implementation(project(":cleaner"))
    implementation(project(":validation-api"))
    implementation(project(":sample-site"))
    implementation(project(":client-core"))
    implementation(project(":test-executor"))
    implementation(project(":w3chtml5validator"))
    implementation(project(":zip"))
    implementation(project(":jobs-api"))
    implementation(project(":report"))
    implementation(project(":aet-maven-plugin"))
    implementation(project(":cleaner-test"))
    implementation(project(":datastorage"))
    implementation(project(":selenium"))
    implementation(project(":proxy"))
    implementation(project(":worker"))
    implementation(project(":communication"))
    implementation(project(":communication-api"))
    implementation(project(":jobs"))
}
