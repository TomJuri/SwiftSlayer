package dev.macrohq.swiftslayer.macro

import dev.macrohq.swiftslayer.pathfinding.AStarPathfinder
import dev.macrohq.swiftslayer.util.*
import net.minecraft.client.renderer.entity.Render
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.pathfinding.PathFinder
import net.minecraft.util.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import java.util.concurrent.CompletableFuture


class BossSpawner {

    private var enabled = false
    private var state = State.GOTO_MOB
    private var target: EntityLiving? = null
    private var condition = { true }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if(!enabled || !condition()) return
        when(state) {
            State.GOTO_MOB -> {
                target = TargetingUtil.getBestMob(EntityZombie::class.java)
                RenderUtil.entites.add(target!!)
                if(target == null) {
                    Logger.error("No mobs found")
                    disable()
                    return
                }
                val path = AStarPathfinder(target!!.getStandingOn(), player.getStandingOn()).findPath(5000)
                if(path.isEmpty()) {
                    Logger.error("No path found")
                    disable()
                    return
                }
                pathExecutor.executePath(path)
                condition = { !pathExecutor.running}
            }

            State.ROTATE_TO_MOB -> {
                RotationUtil.ease(AngleUtil.getAngles(target!!), 100)
                disable()
                return
            }

            State.KILL_MOB -> {

            }
        }
        state = State.entries[(state.ordinal + 1) % State.entries.size]
    }

    fun enable(entity: Class<out EntityLiving>) {
        enabled = true
    }

    fun disable() {
        enabled = false
    }

    private enum class State {
        GOTO_MOB,
        ROTATE_TO_MOB,
        KILL_MOB,
    }

}
