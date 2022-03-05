plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.0"
    id("application")
}
group = "me.kules"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.slack.api:bolt-jetty:1.20.0")
    implementation("org.litote.kmongo:kmongo:4.5.0")
    implementation("org.slf4j:slf4j-simple:1.7.36") // or logback-classic
    implementation("org.kodein.di:kodein-di:7.10.0")
}

application {
    mainClassName = "MyAppKt" // add "Kt" suffix for main function source file
}