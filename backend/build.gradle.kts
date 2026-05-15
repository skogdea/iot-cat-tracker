plugins {
    java
    checkstyle
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.flywaydb.flyway") version "7.15.0"
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.3.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.17.1")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb:3.3.5")
    implementation("com.mailgun:mailgun-java:1.1.3")

    annotationProcessor("org.immutables:value:2.9.3")
    compileOnly("org.immutables:value:2.9.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.opentable.components:otj-pg-embedded:1.1.0")
    testImplementation("org.flywaydb:flyway-core:10.17.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    runtimeOnly("org.flywaydb:flyway-database-postgresql:10.17.0")
    runtimeOnly("org.postgresql:postgresql")
}

checkstyle {
    toolVersion = "10.12.4"
    configDirectory.set(file("linter/checkstyle"))
    // Point to the specific linter config file within that directory at the root level:
    configFile = rootProject.file("linter/checkstyle/checkstyle.xml")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<Checkstyle>("checkstyleMain").configure {
    source = fileTree("backend/src/main/java")
}
tasks.named<Checkstyle>("checkstyleTest").configure {
    source = fileTree("backend/src/test/java")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release = 17
}

val checkstylePublicTask = tasks.register("checkstyle") {
    group = "verification"
    description = "Runs all Checkstyle checks."
}

flyway {
    url = "jdbc:postgresql://localhost:5432/iot_cat_tracker_db"
    user = "postgres"
    password = "postgres"
    schemas = arrayOf("iot_cat_tracker_schema")
}

tasks.withType<Checkstyle>().forEach { checkstyleTask ->
    checkstylePublicTask { dependsOn(checkstyleTask) }
}

// build.gradle.kts
tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveBaseName.set("iot-cat-tracker")
}