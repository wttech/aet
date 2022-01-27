plugins {
    id("com.cognifide.aet.java-conventions")
    id("com.cognifide.aet.test-coverage") apply false
    id("org.nosphere.apache.rat") version "0.7.0"
    id("pl.allegro.tech.build.axion-release") version "1.12.1"
    id("de.undercouch.download") version "4.1.1"
}

scmVersion {
    tag.prefix = ""
}

project.version = scmVersion.version

subprojects {
    project.version = rootProject.version
}

defaultTasks("deployLocal")

tasks.rat {
    // general
    excludes.add("**/LICENSE")
    excludes.add("**/NOTICE")
    excludes.add(".travis.yml")
    excludes.add("eclipse-java-google-style.xml")
    excludes.add("intellij-java-google-style.xml")
    excludes.add("**/*.md")
    excludes.add("**/*.jar")
    excludes.add("**/*.war")
    excludes.add("**/*.zip")
    excludes.add("**/*.json")
    excludes.add("**/*.svg")

    // Eclipse files
    excludes.add("**/.project")
    excludes.add("**/.classpath")

    // IntelliJ files
    excludes.add("**/.idea/**")
    excludes.add("**/*.iml")

    // mvn files
    excludes.add("**/target/**")
    excludes.add("**/pom.xml")

    // gradle files
    excludes.add("**/build/**")
    excludes.add("**/*.gradle.kts")
    excludes.add("**/*.gradle")
    excludes.add("**/.gradle/**")
    excludes.add("**/gradle.properties")
    excludes.add("**/gradle-wrapper.properties")

    // dev environment
    excludes.add("**/dev-env/**")

    // sample-site
    excludes.add("**/integration-tests/sample-site/src/main/resources/mock/**/*.html")
    excludes.add("**/integration-tests/sample-site/src/main/webapp/assets/demo_files/accessibility/bootstrap.css")
    excludes.add("**/integration-tests/sample-site/src/main/webapp/assets/demo_files/bootstrap.css")
    excludes.add("**/integration-tests/sample-site/src/main/webapp/assets/demo_files/bootstrap.min.js")
    excludes.add("**/integration-tests/sample-site/src/main/webapp/assets/demo_files/bootswatch.min.css")
    excludes.add("**/integration-tests/sample-site/src/main/webapp/assets/demo_files/combined.js")
    excludes.add("**/integration-tests/sample-site/src/main/webapp/assets/demo_files/ie10-viewport-bug-workaround.js")
    excludes.add("**/integration-tests/sample-site/src/main/webapp/assets/demo_files/jquery.min.js")
    excludes.add("**/integration-tests/sample-site/src/main/webapp/assets/snippets/change-bg-snippet.js")
    excludes.add("**/integration-tests/sample-site/src/main/webapp/assets/secured/change-bg-snippet.js")

    // jobs
    excludes.add("**/core/jobs/src/test/resources/mock/**/*.html")

    // report
    excludes.add("**/report/src/main/webapp/node/**")
    excludes.add("**/report/src/main/webapp/.csslintrc")
    excludes.add("**/report/src/main/webapp/.jsbeautifyrc")
    excludes.add("**/report/src/main/webapp/.jshintrc")
    excludes.add("**/report/src/main/webapp/.babelrc")
    excludes.add("**/report/src/main/webapp/assets/libs/**")
    excludes.add("**/report/src/main/webapp/assets/js/**")
    excludes.add("**/report/src/test/jasmine/lib/**")
    excludes.add("**/report/src/main/webapp/node_modules/**")
    excludes.add("**/report/src/main/webapp/.sass-cache/**")
    excludes.add("**/report/src/main/webapp/assets/fonts/**")
    excludes.add("**/report/src/main/webapp/assets/img/**")
    excludes.add("**/report/src/main/webapp/assets/icons/**")
    excludes.add("**/report/src/main/webapp/assets/css/**")

    // documentation
    excludes.add("**/documentation/src/main/node/**")
    excludes.add("**/documentation/src/main/node_modules/**")

    // misc
    excludes.add("**/misc/plugins-report.txt")
    excludes.add("**/misc/dependencies-report.txt")
    excludes.add("**/vagrant/.vagrant/**")
    excludes.add("**/vagrant/Berksfile.lock")
    excludes.add("**/vagrant/cookbooks/**")

    // worker
    excludes.add("**/core/worker/firefox/chrome.manifest")
}

tasks["rat"].outputs.upToDateWhen { false }
tasks["build"].dependsOn(tasks["rat"])

val packages = "${project(":zip").buildDir}/packages"
val dockerRoot = "../aet-docker"

val coreBundles = "${dockerRoot}/core-bundles"
val coreConfigs = "${dockerRoot}/core-configs"
val coreFeatures = "${dockerRoot}/core-features"

tasks.register<Copy>("deployBundles") {
    verifySetup(coreBundles)
    dependsOn(project(":zip").tasks["packageBundles"])
    from("${packages}/bundles")
    into(coreBundles)
}

tasks.register<Copy>("deployConfigs") {
    verifySetup(coreConfigs)
    dependsOn(project(":zip").tasks["packageConfigs"])
    from("${packages}/configs")
    into(coreConfigs)
}

tasks.register<Copy>("deployFeatures") {
    verifySetup(coreFeatures)
    dependsOn(project(":zip").tasks["packageFeatures"])
    from("${packages}/features")
    into(coreFeatures)
}

tasks.register("deployLocal") {
    dependsOn(tasks["deployBundles"])
    dependsOn(tasks["deployConfigs"])
    dependsOn(tasks["deployFeatures"])
}

fun verifySetup(path: String) {
    if (!File(path).exists()) {
        logger.error("Directory $path must exist")
        throw ProjectConfigurationException(configErrorMsg(), configErrorThrowable(requiredDirs()))
    }
}

fun requiredDirs() = listOf(coreBundles, coreConfigs, coreFeatures)

fun configErrorMsg() =
        """
            Incorrect local environment setup.
            Please refer to https://github.com/malaskowski/aet-docker
            for a guide of setting up the environment.
        """.trimIndent()

fun configErrorThrowable(requiredDirs: List<String>) =
        Throwable("At least one of the directories: $requiredDirs does not exist")