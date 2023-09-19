package dev.macrohq.swiftslayer.macro

import dev.macrohq.swiftslayer.pathfinding.AStarPathfinder
import dev.macrohq.swiftslayer.util.*
import dev.macrohq.swiftslayer.util.Logger.info
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.util.BlockPos
import net.minecraftforge.client.event.RenderLivingEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import kotlin.math.sqrt

class Revenant {
    val MINIBOSSES =
        arrayOf("Revenant Sycophant", "Revenant Champion", "Deformed Revenant", "Atoned Champion", "Atoned Revenant")
    private var mob: EntityZombie? = null
    private var armorStandBlacklist = mutableListOf<EntityArmorStand>()
    private var found = false
    private var enabled = false
    private var state: State = State.STARTING
    private lateinit var angle: RotationUtil.Rotation

    enum class State {
        STARTING,
        STARTING_MOBKILLER,
        CHECKING_FOR_BOSS,
        WALKING_TO_BOSS,
        WALKING_TO_BOSS_VERIFY,
        LOOKING_AT_BOSS,
        LOOKING_AT_BOSS_VERIFY,
        KILLING_BOSS,
        KILLING_BOSS_VERIFY,
        BATPHONE,
        BATPHONE_VERIFY
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (!enabled) return
        when (state) {
            State.STARTING -> {
                state = State.STARTING_MOBKILLER
                if (ScoreboardUtil.getScoreboardLines().any { it.contains("Boss slain!") }
                    || !ScoreboardUtil.getScoreboardLines().any{ it.contains("Slayer Quest") }){
                    state = State.BATPHONE
                }
            }

            State.STARTING_MOBKILLER -> {
                mobKiller.enable()
                state = State.CHECKING_FOR_BOSS
            }

            State.CHECKING_FOR_BOSS -> {
                if (found) {
                    mobKiller.disable()
                    state = State.WALKING_TO_BOSS
                }
            }

            State.WALKING_TO_BOSS -> {
                state = State.LOOKING_AT_BOSS
                if (player.getDistanceToEntity(mob!!) !in 3.0..5.0) {
                    info("should walk")
                    RenderUtil.markers.clear()
                    RenderUtil.markers.add(blockToStandOn(mob!!))

                    PathingUtil.goto(blockToStandOn(mob!!))
                    state = State.WALKING_TO_BOSS_VERIFY
                }
            }

            State.WALKING_TO_BOSS_VERIFY -> {
                if (PathingUtil.isDone) {
                    info("walk done")
                    PathingUtil.stop()
                    state = State.LOOKING_AT_BOSS
                }
            }

            State.LOOKING_AT_BOSS -> {
                info("looking")
                lookAt(mob!!)
                state = State.LOOKING_AT_BOSS_VERIFY
            }

            State.LOOKING_AT_BOSS_VERIFY -> {
                info("look verify")
                if (lookDone(mob!!)) {
                    player.inventory.currentItem = SlayerUtil.getRevWeaponSlot()
                    state = State.KILLING_BOSS
                }
            }

            State.KILLING_BOSS -> {
                state = State.WALKING_TO_BOSS
                if (player.getDistanceToEntity(mob!!) in 3.0..5.0) {
                    useWeapon()
                    state = State.KILLING_BOSS_VERIFY
                }
            }

            State.KILLING_BOSS_VERIFY -> {
                if (player.getDistanceToEntity(mob!!) !in 3.0..5.0 && mob!!.health > 0) {
                    state = State.WALKING_TO_BOSS
                    return
                }
                if (mob!!.health > 0 && mob!!.isEntityAlive) {
                    state = State.KILLING_BOSS
                    return
                }
                mob = null
                found = false
                KeyBindUtil.stopClicking()
                gameSettings.keyBindSneak.setPressed(false)
                state = State.STARTING
            }

            State.BATPHONE -> {
                autoBatphone.enable()
                state = State.BATPHONE_VERIFY
            }

            State.BATPHONE_VERIFY -> {
                if (!autoBatphone.enabled) {
                    state = State.STARTING_MOBKILLER
                }
            }
        }
    }

    @SubscribeEvent
    fun onEntityRender(event: RenderLivingEvent.Pre<EntityLivingBase>) {
        if (!enabled) return
        if (state != State.CHECKING_FOR_BOSS) return
        if (event.entity in armorStandBlacklist) return
        if (event.entity is EntityArmorStand) {
            if (!event.entity.hasCustomName()) return
            val name = (event.entity as EntityArmorStand).displayName.unformattedText
            if (MINIBOSSES.any { name.contains(it) } || name.contains("Revenant Horror")) {
                if(SlayerUtil.getMiniBoss()!=null){
                    mob = SlayerUtil.getMiniBoss() as EntityZombie
                    armorStandBlacklist.clear()
                    armorStandBlacklist.add(event.entity as EntityArmorStand)
                }
                if(SlayerUtil.getBoss() != null){
                    mob = SlayerUtil.getBoss()!!.first as EntityZombie
                    armorStandBlacklist.clear()
                    armorStandBlacklist.add(SlayerUtil.getBoss()!!.second)
                }
                if (mob == null) return

                found = true
                RenderUtil.entites.remove(mob!!)
                RenderUtil.entites.add(mob!!)
            }
        }
    }

    fun enable() {
        info("enabling rev")
        enabled = true
        mob = null
        found = false
        state = State.STARTING
    }

    fun disable() {
        enabled = false
        RotationUtil.stop()
        PathingUtil.stop()
        KeyBindUtil.stopClicking()
    }

    private fun blockToStandOn(entity: EntityZombie): BlockPos{
        val parentPos = entity.position.down()
        val blocks = BlockUtil.neighbourGenerator(parentPos, 6, 1, 6)
        val betterBlocks = mutableListOf<BlockPos>()
        for(block in blocks){
            if(!AStarPathfinder.Node(block, null).isWalkable()) continue
            if(block.distanceSq(parentPos) !in 16.0..25.0) continue

            if(BlockUtil.isStairSlab(parentPos)) betterBlocks.add(block)
            else if(world.isBlockFullCube(parentPos)){
                if(block.y != parentPos.y && BlockUtil.isStairSlab(block)) betterBlocks.add(block)
                if(block.y == parentPos.y) betterBlocks.add(block)
            }
        }
        return betterBlocks.minBy { player.getDistanceSqToCenter(it) }
    }

    private fun getAngle(entity: EntityZombie) {
        angle = when (config.revWeapon) {
            0 -> RotationUtil.Rotation(player.rotationYaw, 90f)
            else -> AngleUtil.getAngles(entity.positionVector.addVector(0.0, 1.0, 0.0))
        }
    }

    private fun lookAt(entity: EntityZombie) {
        RotationUtil.stop()
        when (config.revWeapon) {
            0 -> RotationUtil.ease(RotationUtil.Rotation(player.rotationYaw, 90f), 200, override = true)
            else -> RotationUtil.lock(entity, 200, eyes = false, override = true)
        }
    }

    private fun lookDone(entity: EntityZombie): Boolean {
        getAngle(entity)
        val angleDiff =
            AngleUtil.getNeededChange(RotationUtil.Rotation(player.rotationYaw, player.rotationPitch), angle)
        return angleDiff.yaw < 5 && angleDiff.pitch < 5
    }

    private fun start(): Double {
        return when (config.revWeapon) {
            0 -> 0.0
            else -> 2.5
        }
    }

    private fun useWeapon() {
        when (config.revWeapon) {
            0 -> KeyBindUtil.rightClick(7)
            1 -> {
                gameSettings.keyBindSneak.setPressed(true)
                KeyBindUtil.leftClick(7)
                KeyBindUtil.rightClick(7)
            }
            else ->{
                gameSettings.keyBindSneak.setPressed(true)
                KeyBindUtil.leftClick(7)
            }
        }
    }
}