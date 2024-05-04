package dev.macrohq.swiftslayer.util.rotation



import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.Entity
import net.minecraft.util.Vec3
import java.util.*
import kotlin.math.abs


class RotationManager {
    companion object {
        private var instance: RotationManager? = null
        fun getInstance(): RotationManager {
            if (instance == null) {
                instance = RotationManager()
            }
            return instance!!
        }
    }

    var defaultMSPD: Float = 3f
    var currentThread: Thread = Thread {}

    fun rotateTo(target: Rotation, msPD: Float = defaultMSPD) {
        if (currentThread.isAlive) return
        val stack = Thread.currentThread().stackTrace[3]

        //Logger.error(stack.className + "." + stack.methodName)
        val player = Minecraft.getMinecraft().thePlayer
        val current = Rotation(player.rotationYaw, player.rotationPitch)

        // Do not forget to change this to something better!
        //val msPD = msPD
        val yawControlPoints: List<Float> = Arrays.asList(0f, 0.55f, 1f)
        val pitchControlPoints: List<Float> = Arrays.asList(0f, 0.55f, 1f)

        val difference = Rotation((target.yaw - current.yaw), (target.pitch - current.pitch)
        )
        difference.yaw = (difference.yaw + 180) % 360 - 180
        difference.pitch = (difference.pitch + 180) % 360 - 180

        val totalTime = (abs(difference.yaw) + abs(difference.pitch)) * msPD
        val rotationPath: MutableList<Rotation> = ArrayList()
        var t = 1 / totalTime
        while (t < 1) {
            rotationPath.add(RotationMath.getInstance().calculateBezierPath(yawControlPoints, pitchControlPoints, t))
            t += 1 / totalTime
        }
        currentThread = Thread {
            for (rotation in rotationPath) {

                Minecraft.getMinecraft().thePlayer.rotationYaw = current.yaw + difference.yaw * rotation.yaw
                Minecraft.getMinecraft().thePlayer.rotationPitch = current.pitch + difference.pitch * rotation.pitch

                try {
                    Thread.sleep((totalTime / rotationPath.size).toLong())

                } catch (e: InterruptedException) {
                    throw RuntimeException(e)
                }
            }
        }
        currentThread.start()
    }

    fun rotateTo(entity: Entity, msPD: Float = defaultMSPD) {
        val player: EntityPlayerSP = Minecraft.getMinecraft().thePlayer
        val rotation: Rotation = RotationMath.getInstance().calculateNeededRotation(Vec3(player.posX, player.posY, player.posZ), Vec3(entity.posX, entity.posY  , entity.posZ))
        rotateTo(rotation, msPD)
    }

    fun rotateTo(pos: Vec3, msPD: Float = defaultMSPD) {
        val player: EntityPlayerSP = Minecraft.getMinecraft().thePlayer
        val rotation: Rotation = RotationMath.getInstance().calculateNeededRotation(Vec3(player.posX, player.posY, player.posZ), pos)
        rotateTo(rotation, msPD)
    }
}