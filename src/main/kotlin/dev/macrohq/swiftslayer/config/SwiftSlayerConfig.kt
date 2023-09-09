package dev.macrohq.swiftslayer.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.Dropdown
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType

class SwiftSlayerConfig : Config(Mod("SwiftSlayer", ModType.SKYBLOCK), "swiftslayer.json") {
    init { initialize() }

    @Dropdown(
        name = "Slayer",
        category = "General",
        subcategory = "Slayers",
        options = ["Revenant Horror", "Tarantula Broodfather", "Sven Packmaster", "Voidgloom Seraph"]
    )
    val slayer: Int = 0

    @Dropdown(
        name = "Tier",
        category = "General",
        subcategory = "Slayers",
        options = ["1", "2", "3", "4", "5"]
    )
    val slayerTier: Int = 0

    @Slider(
        name = "Failsafe volume",
        category = "General",
        subcategory = "Failsafe",
        min = 0f,
        max = 100f,
        step = 5
    )
    val failsafeVolume = 100

}