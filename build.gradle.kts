plugins {
	id("com.diffplug.spotless") version "6.18.0"
}

group = "com.staticoyster"
version = "0.0.1-SNAPSHOT"

repositories {
	mavenCentral()
}

// Define tasks for Python modules:
tasks.register<Exec>("ruffCheck") {
	group = "verification"
	description = "Run Ruff lint"

	commandLine("ruff", "check", ".")
}

tasks.register<Exec>("ruffFormatCheck") {
	group = "verification"
	description = "Run Ruff format check to see if a formatting is needed"

	commandLine("ruff", "format", "--check", ".")
}

// The global check task will run spotless for Java, ruff for Python:
tasks.named("check") {
	dependsOn("spotlessCheck", "ruffCheck", "ruffFormatCheck")
}

spotless {
	isEnforceCheck = true
	java {
		target("backend/**/*.java")
		palantirJavaFormat("2.28.0")
		targetExclude("**/backend/build/generated/**")
	}
}