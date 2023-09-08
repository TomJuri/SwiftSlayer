package dev.macrohq.swiftslayer.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.Dropdown
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType

class SwiftSlayerConfig : Config(Mod("SwiftSlayer", ModType.SKYBLOCK), "swiftslayer.json") {
    init { initialize() }
    val macro = 0

    @Dropdown(
        name = "Slayer",
        category = "General",
        subcategory = "Slayer",
        options = ["Revenant Horror", "Tarantula Broodfather", "Sven Packmaster", "Voidgloom Seraph"]
    )
    var slayer: Int = 0

    @Dropdown(
        name = "Tier",
        category = "General",
        subcategory = "Slayer",
        options = ["1", "2", "3", "4", "5"]
    )
    var slayerTier: Int = 0

    @Dropdown(
        name = "MobKiller Weapon",
        category = "General",
        subcategory = "Slayer",
        options = ["Spirit Sceptre", "Aspect of the Dragons"]
    )
    var mobkillerWeapon = 0
}