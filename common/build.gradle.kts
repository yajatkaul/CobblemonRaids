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

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    modImplementation("com.cobblemon:mod:${property("cobblemon_version")}") { isTransitive = false }

    testImplementation("org.junit.jupiter:junit-jupiter-api:${property("junit_version")}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${property("junit_version")}")

    implementation("org.spongepowered:mixin:0.8.5")
    modImplementation("dev.architectury:architectury:${property("architectury_api_version")}")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}