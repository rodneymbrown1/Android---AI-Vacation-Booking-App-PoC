import org.gradle.testing.jacoco.tasks.JacocoReport

// Shared Jacoco wiring for modules that ship unit tests. Applied via
// `apply(from = "$rootDir/config/gradle/jacoco.gradle.kts")` rather than
// duplicated per module. No coverage-floor gate is enforced yet (see
// SUGGESTIONS.md) — this only produces the HTML/XML report CI uploads.
apply(plugin = "jacoco")

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    val fileFilter = listOf(
        "**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*",
        "**/*Test*.*", "android/**/*.*", "**/*_Hilt*.*", "**/Hilt_*.*",
        "**/*_Factory.*", "**/*_MembersInjector.*", "**/*Module_*Factory.*", "**/di/*.*"
    )
    val kotlinClasses = fileTree("${layout.buildDirectory.get()}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }

    classDirectories.setFrom(files(kotlinClasses))
    sourceDirectories.setFrom(files("$projectDir/src/main/java"))
    executionData.setFrom(
        fileTree(layout.buildDirectory.get()) {
            include(
                "jacoco/testDebugUnitTest.exec",
                "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec"
            )
        }
    )
}
