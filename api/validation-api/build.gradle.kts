plugins {
    id("com.cognifide.aet.java-conventions")
	id("net.idlestate.gradle-duplicate-classes-check")
    id("biz.aQute.bnd.builder")
}

dependencies {
    compileOnly("org.apache.commons:commons-lang3:3.7")
}

tasks.jar {
    manifest {
        attributes(
            Pair("Bundle-Vendor", "Cognifide Ltd."),
            Pair("Export-Package", "com.cognifide.aet.validation")
        )
    }
}

description = "AET :: API :: Validation API"
