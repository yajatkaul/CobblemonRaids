plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
}

architectury {
    common("neoforge", "fabric")
}

loom {
    silentMojangMappingsLicense()

    accessWidenerPath.set(file("src/main/resources/cobblemon_raids.accesswidener"))
}

repositories {
    maven("https://maven.impactdev.net/repository/development/")
    maven("https://maven.nucleoid.xyz")
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    modImplementation("com.cobblemon:mod:${property("cobblemon_version")}") { isTransitive = false }

    testImplementation("org.junit.jupiter:junit-jupiter-api:${property("junit_version")}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${property("junit_version")}")

    modImplementation("club.minnced:discord-webhooks:${property("discord_webhooks_version")}")

    modImplementation("ca.landonjw.gooeylibs:api:${property("gooeylibs_version")}")

    modImplementation("eu.pb4:polymer-core:${property("polymer_version")}")
    include("eu.pb4:polymer-core:${property("polymer_version")}")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}