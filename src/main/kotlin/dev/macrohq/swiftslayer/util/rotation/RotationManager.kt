package dev.macrohq.swiftslayer.util.rotation



import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.util.BlockPos
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

    var currentThread: Thread = Thread {}

    fun rotateTo(target: Rotation) {
        if (currentThread.isAlive) return

        val player = Minecraft.getMinecraft().thePlayer
        val current = Rotation(player.rotationYaw, player.rotationPitch)

        // Do not forget to change this to something better!
        val msPD = 1f
        val yawControlPoints: List<Float> = Arrays.asList(0f, 1f)
        val pitchControlPoints: List<Float> = Arrays.asList(0f, 1f)

        val totalTime: Float = (abs(target.yaw - current.yaw) + abs(target.pitch - current.pitch)) * msPD
        val rotationPath: MutableList<Rotation> = ArrayList()
        var t = 1 / totalTime
        while (t < 1) {
            rotationPath.add(RotationMath.getInstance().calculateBezierPath(yawControlPoints, pitchControlPoints, t))
            t += 1 / totalTime
        }
        currentThread = Thread {
            for (rotation in rotationPath) {
                val difference = Rotation((target.yaw - current.yaw), (target.pitch - current.pitch)
                )

                difference.yaw = (difference.yaw + 180) % 360 - 180
                difference.pitch = (difference.pitch + 180) % 360 - 180

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

    fun rotateTo(entity: Entity) {
        val rotation: Rotation = RotationMath.getInstance().calculateNeededRotation(
            Minecraft.getMinecraft().thePlayer.position,
            entity.position.add(0.5, entity.height.toDouble() - 1.75, 0.5)
        )
        rotateTo(rotation)
    }

    fun rotateTo(pos: BlockPos?) {
        val rotation: Rotation = RotationMath.getInstance().calculateNeededRotation(Minecraft.getMinecraft().thePlayer.position, pos!!)
        rotateTo(rotation)
    }
}