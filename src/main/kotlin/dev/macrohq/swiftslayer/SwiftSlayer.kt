package dev.macrohq.swiftslayer

import cc.polyfrost.oneconfig.utils.commands.CommandManager
import dev.macrohq.swiftslayer.command.PathfindTest
import dev.macrohq.swiftslayer.command.SwiftSlayerCommand
import dev.macrohq.swiftslayer.config.SwiftSlayerConfig
import dev.macrohq.swiftslayer.feature.AutoBatphone
import dev.macrohq.swiftslayer.feature.Failsafe
import dev.macrohq.swiftslayer.feature.SupportItem
import dev.macrohq.swiftslayer.feature.Tracker
import dev.macrohq.swiftslayer.gui.AuthFailedDisplay
import dev.macrohq.swiftslayer.macro.EndermanBossKiller
import dev.macrohq.swiftslayer.macro.GenericBossKiller
import dev.macrohq.swiftslayer.macro.MacroManager
import dev.macrohq.swiftslayer.macro.MobKiller
import dev.macrohq.swiftslayer.macro.Revenant
import dev.macrohq.swiftslayer.pathfinding.PathExecutor
import dev.macrohq.swiftslayer.util.AuthUtil
import dev.macrohq.swiftslayer.util.RenderUtil
import dev.macrohq.swiftslayer.util.RotationUtil
import dev.macrohq.swiftslayer.util.mc
import net.minecraft.util.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import okhttp3.OkHttpClient
import okhttp3.Request
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

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

@Mod(modid = "swiftslayer", name = "SwiftSlayer", version = "%%VERSION%%")
class SwiftSlayer {
    companion object {
        @Mod.Instance("swiftslayer")
        lateinit var instance: SwiftSlayer private set
    }

    lateinit var pathExecutor: PathExecutor private set
    lateinit var config: SwiftSlayerConfig private set
    lateinit var mobKiller: MobKiller private set
    lateinit var endermanBossKiller: EndermanBossKiller private set
    lateinit var autoBatphone: AutoBatphone private set
    lateinit var macroManager: MacroManager private set
    lateinit var genericBossKiller: GenericBossKiller private set
    lateinit var revenant: Revenant private set
    lateinit var tracker: Tracker private set
    var removeLater: BlockPos? = null

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        try {
            val realData: String = AuthUtil.getHWID() + "|" + System.currentTimeMillis() + "|" + mc.session.playerID
            val publicKey: RSAPublicKey =
                AuthUtil.getPublicKey("MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAuemWyx6Ijqgud4UQRARSkc+ysXtH2n0EJj+dyEIInNhvniRKWtU28kqeFp9JIRNVGqkxt5r9R2WQ7DX+Gtv37dXcS+52RqyVMrPtwfHbCVNWi/kI9YXROoAn7+MdQZ20PATLR97DtbtMSMiu2/1J3xPtQMxlOnIGzAyuiHjUHcCL5bOEsDNdetJurVGPqyJKghk4ZwX5EqQ+BeWIszZ2EdOKZikFXnwWGk3vJC5hZwf4xnNm0oyLnDJrEH5huRBiNlqgp4NfsZQwBjcMg8hKmV41QxCnxQ/yWaa3fz9gVfDg/bWyKWWZLXUAty3pFyBEtyC/BdMhZTy9D8uZuhsZMWeYtGCKNcavp48z9zJTbP9sGaEHu/asmuLUT5/1/2lvuNmgxDxz676D19lSADc7QCs91H3YI5FuZ9LFlCSsoFuOUj9KLKxPpeX+V1Wr3Ei8K3ReDA90KduvP37womCWtxLBe6t9hgLzDdwVnac4i9BklC8SBCDsu/ADs7VmWk1GnLf70S6Kti8Fn9TCh46Go18D28CR99RcgzrylCN/g9p12ZRVKZQEHwHW8Bk6rJMER84VGK0Uj+UIr+zvDgX8x1wExTr7ZM3700qJfJP1rgc5s94AFLzfsd6ciipFI76FZT3GIyfH/EcF77awigCdXtHbmnTxgm3l8GhD/iS4P7ECAwEAAQ==")
            val privateKey: RSAPrivateKey = AuthUtil.getPrivateKey(
                "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"
            )
            val fakeAndRealData = StringBuilder()
            for (i in 0..4) {
                if (i == 3) {
                    fakeAndRealData.append(AuthUtil.rsaEncrypt(realData.toByteArray(), publicKey))
                } else {
                    fakeAndRealData.append(
                        AuthUtil.rsaEncrypt(
                            AuthUtil.generateRandomString(realData.length).toByteArray(), publicKey
                        )
                    )
                }
                if (i < 4) {
                    fakeAndRealData.append("-")
                }
            }
            val url =
                "https://macrohq.dev/api/Q6W2ecZlZ3InKEEriO7onlQ1i1vAVCC9ipOF4qwsfNIK1xg6Z80NZTgbXbOJ?DvigquT6UAztO5IdMb0iIex6skfcHrPs8lDeTCxLfzG98n0AdNWeIN2VX3Kd=$fakeAndRealData"
            val request = Request.Builder().url(url)
                .addHeader("User-Agent", "TOM-CLIENT-JAVA8-Shipping-g1vJHlyarpLgba7Fu3SoUm5dGkoDHeI5f8QIFur7").build()
            val client = OkHttpClient()
            val response = client.newCall(request).execute()
            if (response.body() != null) {
                val decoded: String = AuthUtil.rsaDecrypt(response.body()!!.string(), privateKey)
                val split = decoded.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if ("success" == split[0] && AuthUtil.getHWID() == split[1] && mc.session.playerID.equals(split[2]) && System.currentTimeMillis() - split[3].toLong() < 20000) {
                    config = SwiftSlayerConfig()
                    pathExecutor = PathExecutor()
                    mobKiller = MobKiller()
                    endermanBossKiller = EndermanBossKiller()
                    autoBatphone = AutoBatphone()
                    macroManager = MacroManager()
                    genericBossKiller = GenericBossKiller()
                    revenant = Revenant()
                    tracker = Tracker()
                    MinecraftForge.EVENT_BUS.register(this)
                    MinecraftForge.EVENT_BUS.register(pathExecutor)
                    MinecraftForge.EVENT_BUS.register(mobKiller)
                    MinecraftForge.EVENT_BUS.register(autoBatphone)
                    MinecraftForge.EVENT_BUS.register(macroManager)
                    MinecraftForge.EVENT_BUS.register(genericBossKiller)
                    MinecraftForge.EVENT_BUS.register(Failsafe())
                    MinecraftForge.EVENT_BUS.register(revenant)
                    MinecraftForge.EVENT_BUS.register(tracker)
                    MinecraftForge.EVENT_BUS.register(SupportItem())
                    CommandManager.register(PathfindTest())
                    CommandManager.register(SwiftSlayerCommand())
                    return
                }
            }
        } catch (ignored: Exception) {
            ignored.printStackTrace()
            throw AuthFailedDisplay(ignored.message ?: "eeee")
        }
        throw AuthFailedDisplay("eee")
    }

    fun isTrackerInitialized() = ::tracker.isInitialized

    @SubscribeEvent
    fun onRenderWorldLast(event: RenderWorldLastEvent) {
        // this is here because im not sure if objects can have events cuz they are kinda static
        RotationUtil.onRenderWorldLast()
        RenderUtil.onRenderWorldLast(event)
    }
}
