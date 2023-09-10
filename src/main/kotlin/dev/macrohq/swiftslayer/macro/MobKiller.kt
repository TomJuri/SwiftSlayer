package dev.macrohq.swiftslayer.macro

import dev.macrohq.swiftslayer.util.*
import dev.macrohq.swiftslayer.util.Logger.info
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.monster.EntityZombie
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import kotlin.math.abs

class MobKiller {
    private var blacklist = mutableListOf<EntityLiving>()
    var enabled = false
        private set
    private var state: State = State.NONE
    private var targetEntity: EntityLiving? = null
    private var ticks: Int = 0
    private var stuckCounter: Int = 0
    private var lookTimer: Int = 0
    private lateinit var angle: RotationUtil.Rotation

    private enum class State {
        NONE,
        STARTING,
        FINDING,
        PATHFINDING,
        PATHFINDING_VERIFY,
        LOOKING,
        LOOKING_VERIFY,
        KILLING,
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (player == null || world == null) return
        if (!enabled) return
        ticks++
        if (player.lastTickPosition().add(0, -1, 0) == player.getStandingOnFloor()) {
            stuckCounter++
        } else stuckCounter = 0

        when (state) {
            State.STARTING -> {
                state = State.FINDING
                return
            }

            State.FINDING -> {
                info("finding")
                RenderUtil.entites.clear()
                if (ticks >= 60) {
                    blacklist.clear()
                    ticks = 0
                }
                val targetEntityList = EntityUtil.getMobs(EntityZombie::class.java, 32000).toMutableList()
                targetEntityList.removeAll(blacklist)

                if (targetEntityList.isEmpty()) return

                targetEntity = targetEntityList[0]
                RenderUtil.entites.add(targetEntity as EntityLiving)
                state = State.PATHFINDING
            }

            State.PATHFINDING -> {
                info("pathfinding")
                PathingUtil.goto(targetEntity!!.position.down())
                state = State.PATHFINDING_VERIFY
            }

            State.PATHFINDING_VERIFY -> {
                info("path verif")
                if (PathingUtil.hasFailed() || (targetEntity)!!.health <= 0 || stuckCounter >= 40) {
                    PathingUtil.stop()
                    stuckCounter = 0
                    blacklist.add(targetEntity!!)
                    state = State.FINDING
                    return
                }
                if ((PathingUtil.isDone || player.getDistanceToEntity(targetEntity) < attackDistance()) && player.canEntityBeSeen(targetEntity)) {
                    stop()
                    state = State.LOOKING
                }
                return
            }

            State.LOOKING -> {
                info("looking")
                lookAtEntity(targetEntity!!)
                state = State.LOOKING_VERIFY
                return
            }

            State.LOOKING_VERIFY -> {
                info("look verif")
                if (lookTimer++ >= 40) state = State.LOOKING
                if (lookDone()) {
                    RotationUtil.stop()
                    holdWeapon()
                    state = State.KILLING
                }
            }

            State.KILLING -> {
                info("kill")
                useWeapon()
                blacklist.add(targetEntity as EntityLiving)
                state = State.FINDING
            }

            else -> {}
        }
    }

    fun enable() {
        enabled = true
        state = State.STARTING
    }

    fun disable() {
        enabled = false
        PathingUtil.stop()
        RotationUtil.stop()
        state = State.NONE
        Logger.error("Disabling")
    }

    private fun lookAtEntity(entity: EntityLiving){
        angle = angleForWeapon(entity)
        when (config.mobKillerWeapon) {
            0, 1 -> RotationUtil.ease(angle, 100)
            2 -> RotationUtil.lock(entity, 200, false)
        }
    }

    private fun angleForWeapon(entity: EntityLiving): RotationUtil.Rotation {
        return when (config.mobKillerWeapon) {
            0 -> RotationUtil.Rotation(AngleUtil.getAngles(targetEntity!!).yaw, 45f)
            1, 2 -> AngleUtil.getAngles(entity.positionVector.addVector(0.0, 1.0, 0.0))
            else -> RotationUtil.Rotation(0f, 0f)
        }
    }

    private fun useWeapon() {
        when (config.mobKillerWeapon) {
            0,2 -> KeyBindUtil.rightClick()
            1 -> KeyBindUtil.leftClick()
            else -> {}
        }
    }

    private fun attackDistance(): Int {
        return when (config.mobKillerWeapon) {
            0 -> 6
            1 -> 3
            2 -> 15
            else -> 6
        }
    }

    private fun holdWeapon() {
        when (config.mobKillerWeapon) {
            0 -> InventoryUtil.holdItem("Spirit Sceptre")
            1 -> InventoryUtil.holdItem("Aspect of the Dragons")
            2 -> InventoryUtil.holdItem("Frozen Scythe")
        }
    }

    private fun stop() {
        when (config.mobKillerWeapon) {
            0 -> {}
            1, 2 -> PathingUtil.stop()
        }
    }

    private fun lookDone(): Boolean {
        val yawDiff = abs(AngleUtil.yawTo360(player.rotationYaw) - AngleUtil.yawTo360(angle.yaw))
        val pitchDiff = abs(mc.thePlayer.rotationPitch - angle.pitch)
        when (config.mobKillerWeapon) {
            0 -> return pitchDiff < 2
            1 -> return yawDiff < 10 && pitchDiff < 5
            2 -> return yawDiff < 3 && pitchDiff < 3
        }
        return true
    }
}