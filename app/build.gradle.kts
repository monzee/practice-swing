/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    id("practice.swing.java-application-conventions")
}

dependencies {
    implementation("org.apache.commons:commons-text")
    implementation(project(":ex1"))
    implementation(project(":ex2"))
}

application {
    // Define the main class for the application.
    mainClass.set("practice.swing.app.App")
}
