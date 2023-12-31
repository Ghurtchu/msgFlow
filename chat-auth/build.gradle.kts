import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  java
  application
  id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.arevadze"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "4.4.5"
val junitJupiterVersion = "5.9.1"

val mainVerticleName = "com.arevadze.chat_auth.MainVerticle"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
  mainClass.set(launcherClassName)
}

dependencies {
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-core")
  implementation("io.vertx:vertx-web:4.2.1")
  implementation("io.netty:netty-resolver-dns-native-macos:4.1.72.Final:osx-aarch_64")
  implementation("io.vertx:vertx-jdbc-client:4.4.5")
  implementation ("javax.persistence:javax.persistence-api:2.2")
  implementation ("org.hibernate:hibernate-core:5.5.6.Final") // Use the appropriate version
  implementation ("org.postgresql:postgresql:42.2.24")
  implementation("org.jetbrains:annotations:24.0.0")
  implementation("org.slf4j:slf4j-api:2.0.7")
  implementation("io.vertx:vertx-config:4.4.6")
  implementation("io.jsonwebtoken:jjwt-api:0.11.5")
  implementation("io.jsonwebtoken:jjwt-gson:0.11.5")
  runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
  runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
  implementation("io.vertx:vertx-auth-jwt:4.4.5")
  implementation("edu.vt.middleware:vt-password:3.1.2")

  compileOnly ("org.projectlombok:lombok:1.18.22") // Use the latest version
  annotationProcessor ("org.projectlombok:lombok:1.18.22")
  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")

}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Verticle" to mainVerticleName))
  }
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

tasks.withType<JavaExec> {
  args = listOf("run", mainVerticleName, "--redeploy=$watchForChange", "--launcher-class=$launcherClassName", "--on-redeploy=$doOnChange")
}
