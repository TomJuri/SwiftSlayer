package dev.macrohq.swiftslayer.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import cc.polyfrost.oneconfig.libs.universal.UKeyboard
import dev.macrohq.swiftslayer.util.macroManager


class SwiftSlayerConfig : Config(Mod("SwiftSlayer", ModType.SKYBLOCK), "swiftslayer.json") {

    @Dropdown(
        name = "Slayer type",
        category = "General",
        subcategory = "Slayer",
        options = ["Revenant Horror", "Tarantula Broodfather", "Sven Packmaster"/*, "Voidgloom Seraph"*/]
    )
    var slayer = 0

    @Dropdown(
        name = "Slayer tier",
        category = "General",
        subcategory = "Slayer",
        options = ["1", "2", "3", "4", "5"]
    )
    var slayerTier = 4

    @Dropdown(
        name = "Weapon type for mobs",
        category = "General",
        subcategory = "Slayer",
        options = ["Hyperion", "Ranged", "Melee"]
    )
    var mobKillerWeapon = 0

    @Slider(
        name = "'Ranged' range",
        category = "General",
        subcategory = "Slayer",
        min = 1f,
        max = 15f,
        step = 1
    )
    var rangedRange = 1

    @Slider(
        name = "'Ranged' cooldown (in seconds)",
        category = "General",
        subcategory = "Slayer",
        min = 0f,
        max = 25f,
        step = 1
    )
    var rangedCooldown = 1

    @Dropdown(
        name = "Weapon type for boss",
        category = "General",
        subcategory = "Slayer",
        options = ["Melee", "Hyperion"]
    )
    var bossKillerWeapon = 0

    @Switch(
        name = "Ignore mini bosses",
        category = "General",
        subcategory = "Slayer",
    )
    var ignoreMiniBosses = false

    @Switch(
        name = "Keep killing",
        category = "General",
        subcategory = "Slayer"
    )
    var autoSlayer = false

    @Slider(
        name = "Melee weapon slot",
        category = "General",
        subcategory = "Slayer",
        min = 1f,
        max = 9f,
        step = 1
    )
    var meleeWeaponSlot = 1

    @Switch(
        name = "Ungrab mouse",
        category = "General",
        subcategory = "Miscellaneous"
    )
    var ungrabMouse = true

    @Switch(
        name = "Enable advanced settings",
        category = "General",
        subcategory = "Miscellaneous",
        description = "All settings are configured optimally by default. Only adjust advanced settings if you have a clear understanding of their impact."
    )
    var showAdvanced = false

    @KeyBind(
        name = "Toggle macro",
        category = "General",
        subcategory = "Miscellaneous",
    )
    var toggleMacro = OneKeyBind(UKeyboard.KEY_X)

    @KeyBind(
        name = "Open config",
        category = "General",
        subcategory = "Miscellaneous",
    )
    var openConfig = OneKeyBind(UKeyboard.KEY_SEMICOLON)

    @Slider(
        name = "Failsafe volume",
        category = "General",
        subcategory = "Miscellaneous",
        min = 0f,
        max = 100f,
    )
    var failsafeVolume = 100

    @Slider(
        name = "Use healing at % health",
        category = "General",
        subcategory = "Advanced",
        min = 10f,
        max = 80f,
    )
    var useHealingAt = 70


    @Slider(
        name = "Gui delay",
        category = "General",
        subcategory = "Advanced",
        min = 250f,
        max = 2500f,
    )
    var macroGuiDelay: Float = 250f

    @Slider(
        name = "Additional random gui delay",
        category = "General",
        subcategory = "Advanced",
        min = 0f,
        max = 2500f,
    )
    var macroGuiDelayRandomness: Float = 250f
    fun getRandomGUIMacroDelay(): Long {
        return (macroGuiDelay + Math.random().toFloat() * macroGuiDelayRandomness).toLong()
    }



    @Dropdown(
        name = "Boss killer movement",
        category = "General",
        subcategory = "Advanced",
        options = ["Find corner", "Walk back"]
    )
    var movementType = 0

    @Switch(
        name = "Debug Mode",
        category = "General",
        subcategory = "Advanced",
    )
    var debugMode = false

    @HUD(name = "HUD", category = "HUD")
    var hud = SwiftSlayerHud()


    init {
        initialize()
        registerKeyBind(toggleMacro) { macroManager.toggle() }
        registerKeyBind(openConfig) { openGui() }

        hideIf("ignored") { !showAdvanced }
        hideIf("useHealingAt") { !showAdvanced }
        hideIf("macroGuiDelay") { !showAdvanced }
        hideIf("macroGuiDelayRandomness") { !showAdvanced }
        hideIf("movementType") { !showAdvanced }
        hideIf("debugMode") { !showAdvanced }

        hideIf("rangedRange") { mobKillerWeapon != 1}
        hideIf("rangedCooldown") { mobKillerWeapon != 1}
    }
}