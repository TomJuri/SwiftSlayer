package dev.macrohq.swiftslayer.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.Dropdown
import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import cc.polyfrost.oneconfig.libs.universal.UKeyboard
import dev.macrohq.swiftslayer.util.macroManager

class SwiftSlayerConfig : Config(Mod("SwiftSlayer", ModType.SKYBLOCK), "swiftslayer.json") {
    @Dropdown(
        name = "Slayer",
        category = "General",
        subcategory = "Slayers",
        options = ["Revenant Horror", "Tarantula Broodfather", "Sven Packmaster", "Voidgloom Seraph"]
    )
    var slayer = 0

    @Dropdown(
        name = "Tier",
        category = "General",
        subcategory = "Slayers",
        options = ["1", "2", "3", "4", "5"]
    )
    var slayerTier = 4

    @Dropdown(
        name = "MobKiller Weapon",
        category = "General",
        subcategory = "Slayer",
        options = ["Spirit Sceptre", "Aspect of the Dragons"]
    )
    var mobKillerWeapon = 0

    @Dropdown(
        name = "BossKiller Weapon",
        category = "General",
        subcategory = "Slayer",
        options = ["Hyperion", "Melee"]
    )
    var bossKillerWeapon = 0

    @Slider(
        name = "Failsafe volume",
        category = "General",
        subcategory = "Failsafe",
        min = 0f,
        max = 100f,
        step = 5
    )
    val failsafeVolume = 100

    @KeyBind(
        name = "Toggle Macro",
        category = "General",
        subcategory = "Macro"
    )
    var toggleMacro = OneKeyBind(UKeyboard.KEY_X)

    @Switch(
        name = "Debug Mode",
        category = "General",
        subcategory = "Debug"
    )
    var debugMode = false

    @Switch(
        name = "Use Batphone",
        category = "General",
        subcategory = "Slayer"
    )
    var useBatphone = false

    init {
        initialize()
        registerKeyBind(toggleMacro) { macroManager.toggle() }
    }
}