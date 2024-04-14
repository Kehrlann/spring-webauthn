plugins {
    java
    id("org.springframework.boot") version "3.2.4"
    id("io.spring.javaformat") version "0.0.41"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "wf.garnier"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    mavenLocal()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("example:webauthn:0.0.1-DGARNIER")

    runtimeOnly("org.postgresql:postgresql")

    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")

    implementation("com.webauthn4j:webauthn4j-core:0.22.1.RELEASE")

    implementation("com.sshtools:two-slices:0.9.3") // notifier
    implementation("net.java.dev.jna:jna:5.14.0") // notifier

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.test {
    useJUnitPlatform()
}

tasks.compileJava {
    options.isDeprecation = true
    options.compilerArgs.add("-parameters")
}
