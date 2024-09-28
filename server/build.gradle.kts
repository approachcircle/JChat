plugins {
    id("java")
}

group = "net.approachcircle"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("io.javalin:javalin:6.3.0")
    implementation("org.slf4j:slf4j-simple:2.0.16")
}

tasks.test {
    useJUnitPlatform()
}