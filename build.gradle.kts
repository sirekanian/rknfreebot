plugins {
    kotlin("jvm") version "1.8.21"
    application
    id("org.sirekanyan.version-checker") version "1.0.6"
}

group = "com.sirekanyan"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.telegram:telegrambots:6.5.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")
    implementation("org.postgresql:postgresql:42.6.0")
    implementation("org.slf4j:slf4j-simple:2.0.7")
    implementation("com.github.f4b6a3:uuid-creator:5.3.2")
}

application {
    mainClass.set("com.sirekanyan.rknfreebot.Main")
    if (hasProperty("debug")) {
        applicationDefaultJvmArgs = listOf("-Ddebug")
    }
}

distributions {
    main {
        contents {
            from("bot.properties")
            from("data/coupon.webp") { into("data") }
        }
    }
}

tasks {
    compileKotlin {
        compilerOptions {
            allWarningsAsErrors.set(true)
        }
    }
}
