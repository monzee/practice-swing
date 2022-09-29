allprojects {
    tasks {
        withType<JavaCompile> {
            options.compilerArgs.add("-Xlint:deprecation")
        }
    }
}

