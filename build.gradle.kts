plugins {
    kotlin("jvm") version "1.6.10"
    application
    // id("bump-plugin") version "1.0.0"
}

group = "com.sirekanyan"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.telegram:telegrambots:5.7.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.37.3")
    implementation("org.postgresql:postgresql:42.3.3")
    implementation("org.slf4j:slf4j-simple:1.7.36")
    implementation("com.github.f4b6a3:uuid-creator:4.5.0")
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

