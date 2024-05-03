package dev.macrohq.swiftslayer

import cc.polyfrost.oneconfig.utils.commands.CommandManager
import dev.macrohq.swiftslayer.codecPathfinder.Pathfinder.Pathfinder
import dev.macrohq.swiftslayer.codecPathfinder.Pathfinder.dependencies.CodecPathexecutor
import dev.macrohq.swiftslayer.command.DirectionTest
import dev.macrohq.swiftslayer.command.LockTest
import dev.macrohq.swiftslayer.command.PathfindTest
import dev.macrohq.swiftslayer.command.SwiftSlayerCommand
import dev.macrohq.swiftslayer.config.SwiftSlayerConfig
import dev.macrohq.swiftslayer.event.GameEventHandler
import dev.macrohq.swiftslayer.feature.AutoBatphone
import dev.macrohq.swiftslayer.feature.Failsafe
import dev.macrohq.swiftslayer.feature.SupportItem
import dev.macrohq.swiftslayer.feature.Tracker
import dev.macrohq.swiftslayer.feature.implementation.AutoRotation
import dev.macrohq.swiftslayer.macro.EndermanBossKiller
import dev.macrohq.swiftslayer.macro.GenericBossKiller
import dev.macrohq.swiftslayer.macro.MacroManager
import dev.macrohq.swiftslayer.macro.Revenant
import dev.macrohq.swiftslayer.pathfinding.PathExecutor
import dev.macrohq.swiftslayer.util.*
import dev.macrohq.swiftslayer.util.movement.helper.BlockStateAccessor
import dev.macrohq.swiftslayer.util.movement.helper.player.IPlayerContext
import dev.macrohq.swiftslayer.util.movement.helper.player.PlayerContext
import dev.macrohq.swiftslayer.util.rotation.RotationManager
import me.kbrewster.eventbus.Subscribe
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.GameSettings
import net.minecraft.util.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.gameevent.TickEvent


/* fun main() {
    val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
    keyPairGenerator.initialize(4096)
    val keyPair = keyPairGenerator.generateKeyPair()
    println("Public Key 1: " + Base64.getEncoder().encodeToString(keyPair.public.encoded)).toString()
    println("Private Key 1: " + Base64.getEncoder().encodeToString(keyPair.private.encoded)).toString()

    val keyPairGenerator0 = KeyPairGenerator.getInstance("RSA")
    keyPairGenerator0.initialize(4096)
    val keyPair0 = keyPairGenerator0.generateKeyPair()
    println("Public Key 2: " + Base64.getEncoder().encodeToString(keyPair0.public.encoded)).toString()
    println("Private Key 2: " + Base64.getEncoder().encodeToString(keyPair0.private.encoded)).toString()
}*/
object SwiftSlayer {

  lateinit var pathExecutor: PathExecutor private set
  lateinit var config: SwiftSlayerConfig private set
  lateinit var endermanBossKiller: EndermanBossKiller private set
  lateinit var autoBatphone: AutoBatphone private set
  lateinit var macroManager: MacroManager private set
  lateinit var genericBossKiller: GenericBossKiller private set
  lateinit var revenant: Revenant private set
  lateinit var tracker: Tracker private set
  private lateinit var lockTest: LockTest
  var removeLater: BlockPos? = null
  lateinit var pathFinder: Pathfinder
  lateinit var  pathExec: CodecPathexecutor
  lateinit var  slayerUtil: SlayerUtil

  fun init() {
    config = SwiftSlayerConfig()
    pathExecutor = PathExecutor()
    endermanBossKiller = EndermanBossKiller()
    autoBatphone = AutoBatphone()
    macroManager = MacroManager()
    genericBossKiller = GenericBossKiller()
    revenant = Revenant()
    tracker = Tracker()
    lockTest = LockTest()
    pathFinder = Pathfinder()
    pathExec = CodecPathexecutor()
    slayerUtil = SlayerUtil
    SwiftEventBus.register(this)
    SwiftEventBus.register(pathExecutor)
    SwiftEventBus.register(autoBatphone)
    SwiftEventBus.register(macroManager)
    SwiftEventBus.register(genericBossKiller)
    SwiftEventBus.register(Failsafe())
    SwiftEventBus.register(revenant)
    SwiftEventBus.register(tracker)
    SwiftEventBus.register(lockTest)
    SwiftEventBus.register(DirectionTest())
    SwiftEventBus.register(SupportItem())
    SwiftEventBus.register(slayerUtil)
    CommandManager.register(PathfindTest())
    CommandManager.register(SwiftSlayerCommand())
    CommandManager.register(DirectionTest())



    // New Structure
    //FeatureManager.getInstance().loadFeatures().forEach(SwiftEventBus::register)
    SwiftEventBus.register(AutoRotation)
    SwiftEventBus.register(GameEventHandler(this))
  }

  fun isTrackerInitialized() = ::tracker.isInitialized

  @Subscribe
  fun onRenderWorldLast(event: RenderWorldLastEvent) {
    // this is here because im not sure if objects can have events cuz they are kinda static
    RotationUtil.onRenderWorldLast()
    RenderUtil.onRenderWorldLast(event)
    KeyBindUtil.onRenderWorldLast()
  }

  @Subscribe
  fun onChat(event: TickEvent.ClientTickEvent) {
    if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak)) RotationManager.getInstance().rotateTo(BlockPos(44, 3, 245))
  }

  // Hellow
  val mc: Minecraft = Minecraft.getMinecraft()
  val playerContext: IPlayerContext = PlayerContext(this, mc)
  var bsa: BlockStateAccessor? = null
}