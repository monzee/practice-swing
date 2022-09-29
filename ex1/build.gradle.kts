plugins {
    id("practice.swing.java-library-conventions")
}

dependencies {
    api(project(":utilities"))
}

tasks {
    withType<JavaCompile> {
        options.compilerArgs.add("-Xlint:deprecation")
    }
}
