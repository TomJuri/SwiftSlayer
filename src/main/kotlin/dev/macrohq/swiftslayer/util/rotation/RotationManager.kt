package dev.macrohq.swiftslayer.util.rotation



import dev.macrohq.swiftslayer.util.Logger
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.util.BlockPos
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

    var currentThread: Thread = Thread {}

    fun rotateTo(target: Rotation) {
        if (currentThread.isAlive) return

        val player = Minecraft.getMinecraft().thePlayer
        val current = Rotation(player.rotationYaw, player.rotationPitch)

        // Do not forget to change this to something better!
        val msPD = 4f
        val yawControlPoints: List<Float> = listOf(0f, .3f, 1f)
        val pitchControlPoints: List<Float> = listOf(0f, .3f, 1f)

        val totalTime: Float = (abs(target.yaw - current.yaw) + abs(target.pitch - current.pitch)) * msPD
        val rotationPath: MutableList<Rotation> = ArrayList()
        var t = 1 / totalTime
        while (t < 1) {
            rotationPath.add(RotationMath.getInstance().calculateBezierPath(yawControlPoints, pitchControlPoints, t))
            t += 1 / totalTime
        }
        currentThread = Thread {
            Logger.info(rotationPath.size)
            for (rotation in rotationPath) {
                Logger.info("here")
                Minecraft.getMinecraft().thePlayer.rotationYaw =
                    current.yaw + (target.yaw - current.yaw) * rotation.yaw
                Minecraft.getMinecraft().thePlayer.rotationPitch =
                    current.pitch + (target.pitch - current.pitch) * rotation.pitch

                try {
                    Thread.sleep((totalTime / rotationPath.size).toLong())
                } catch (e: InterruptedException) {
                    throw RuntimeException(e)
                }
            }
        }
        currentThread.start()
    }

    fun rotateTo(entity: Entity) {
        val rotation: Rotation = RotationMath.getInstance().calculateNeededRotation(
            Minecraft.getMinecraft().thePlayer.position,
            entity.position.add(0.0, entity.height - 0.5, 0.0)
        )
        rotateTo(rotation)
    }

    fun rotateTo(pos: BlockPos?) {
        val rotation: Rotation = RotationMath.getInstance().calculateNeededRotation(Minecraft.getMinecraft().thePlayer.position, pos!!)
        rotateTo(rotation)
    }
}