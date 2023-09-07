package dev.macrohq.swiftslayer.macro

import dev.macrohq.swiftslayer.util.*
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.util.Vec3
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class VoidgloomKiller {

    private var enabled = false
    private lateinit var target: EntityArmorStand
    private var state = State.HIT
    lateinit var lasers: Pair<Vec3, Vec3>
    var lastLaser = 0L
    var timer = Timer(0)

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if(!enabled) return
        if (determineState() != state) {
            state = determineState()
            gameSettings.keyBindSneak.setPressed(false)
            gameSettings.keyBindForward.setPressed(false)
            gameSettings.keyBindBack.setPressed(false)
            RotationUtil.stop()
        }
        when(state) {
            State.HIT -> {
                RotationUtil.easeToEntity(target, 250, true)
                if(player.getDistanceSqToEntity(target) < 10.0) gameSettings.keyBindBack.setPressed(true) else gameSettings.keyBindBack.setPressed(false)
                if(player.worldObj.loadedEntityList.filterIsInstance<EntityZombie>().any { it.getDistanceSqToEntity(target) > 4.0 }) return
                InventoryUtil.holdItem("Reaper Scythe")
                KeyBindUtil.rightClick()
            }

            State.DAMAGE -> {
                RotationUtil.easeToEntity(target, 250, true)
                if(player.getDistanceSqToEntity(target) > 3.0) gameSettings.keyBindForward.setPressed(true) else gameSettings.keyBindForward.setPressed(false)
                if (timer.isDone) {
                    gameSettings.keyBindSneak.setPressed(true)
                    timer = Timer(100)
                    KeyBindUtil.leftClick()
                    return
                }
                gameSettings.keyBindSneak.setPressed(false)
            }

            State.LASER -> {
                if (player.getDistance(lasers.first.xCoord, lasers.first.yCoord, lasers.first.zCoord) > 5.0
                    || player.getDistance(lasers.second.xCoord, lasers.second.yCoord, lasers.second.zCoord) > 5.0
                    || lasers.first.subtract(lasers.second).crossProduct(player.positionVector.subtract(lasers.second))
                        .lengthVector() / lasers.first.subtract(lasers.second).lengthVector() > 5.0
                ) {
                    KeyBindUtil.jump()
                }
            }
        }
    }

    fun enable(target: EntityArmorStand) {
        if (enabled) return
        enabled = true
        this.target = target
        Logger.info("VoidgloomKiller enabled")
    }

    fun disable() {
        if (!enabled) return
        enabled = false
        Logger.info("VoidgloomKiller disabled")
    }

    private fun determineState() : State {
        if(target.name.contains("Hits"))
            return State.HIT
        if(::lasers.isInitialized && lastLaser + 100 > System.currentTimeMillis())
            return State.LASER
        return State.DAMAGE
    }

    enum class State {
        HIT,
        DAMAGE,
        LASER,
    }
}