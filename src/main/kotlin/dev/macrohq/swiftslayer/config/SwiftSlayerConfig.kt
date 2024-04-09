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
    name = "Slayer",
    category = "General",
    subcategory = "Slayers",
    options = ["Revenant Horror", "Tarantula Broodfather", "Sven Packmaster"/*, "Voidgloom Seraph"*/]
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
    options = ["Spirit Sceptre", "Melee", "Fire Veil Wand", "Right click Meele"]
  )
  var mobKillerWeapon = 0

  @Switch(
    name = "Ignore Mini bosses",
    category = "General",
    subcategory = "Slayer",
  )
  var ignoreMiniBosses = false

  @Dropdown(
    name = "BossKiller Weapon",
    category = "General",
    subcategory = "Slayer",
    options = ["Melee", "Hyperion"]
  )
  var bossKillerWeapon = 0

  @Slider(
    name = "Melee Weapon Slot",
    category = "General",
    subcategory = "Slayer",
    min = 1f,
    max = 9f
  )
  var meleeWeaponSlot = 1

  @Switch(
    name = "Use healing wand",
    category = "General",
    subcategory = "Support Items"
  )
  var useHealing = false

  @Slider(
    name = "Use healing at % health",
    category = "General",
    subcategory = "Support Items",
    min = 10f,
    max = 90f,
  )
  var useHealingAt = 25

  @Switch(
    name = "Use weird tuba",
    category = "General",
    subcategory = "Support Items"
  )
  var useTuba = false

  @Switch(
    name = "Deploy Power Orb",
    category = "General",
    subcategory = "Support Items"
  )
  var deployOrb = false

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
    name = "Autoslayer",
    category = "General",
    subcategory = "Slayer"
  )
  var autoSlayer = false

  @Switch(
    name = "Ungrab Mouse",
    category = "General",
    subcategory = "QOL"
  )
  var ungrabMouse = true

  @Switch(
    name = "Aotv",
    category = "General",
    subcategory = "QOL"
  )
  var useAOTV = false

  @HUD(name = "HUD", category = "HUD")
  var hud = SwiftSlayerHud()

  @Slider(
    name = "GUI Delay",
    category = "General",
    subcategory = "GUI Delays",
    description = "The delay between clicking during GUI macros (in milliseconds)",
    min = 250f,
    max = 2000f
  )
  var macroGuiDelay: Float = 400f
  @Slider(
    name = "Additional random GUI Delay",
    category = "General",
    subcategory = "GUI Delays",
    description = "The maximum random time added to the delay time between clicking during GUI macros (in milliseconds)",
    min = 0f,
    max = 2000f
  )
  var macroGuiDelayRandomness: Float = 350f


  // ==============================
  //    Pathfinder Debug Config
  // ==============================
  @Switch(
    name = "allowJump",
    category = "PF"
  )
  var allowJump = true


  @Switch(
    name = "holdSneak",
    category = "PF"
  )
  var holdSneak = true

  @Switch(
    name = "allowDiagonalAscend",
    category = "PF"
  )
  var allowDiagonalAscend = true


  @Switch(
    name = "allowDiagonalDescend",
    category = "PF"
  )
  var allowDiagonalDescend = true

  @Slider(
    name = "maxFallHeight",
    category = "PF",
    min = 1f,
    max = 256f
  )
  var maxFallHeight = 20

  fun getRandomGUIMacroDelay(): Long {
    return (macroGuiDelay + Math.random().toFloat() * macroGuiDelayRandomness).toLong()
  }

  init {
    initialize()
    registerKeyBind(toggleMacro) { macroManager.toggle() }
  }
}