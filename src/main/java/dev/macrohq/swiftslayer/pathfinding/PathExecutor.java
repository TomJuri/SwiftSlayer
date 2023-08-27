package dev.macrohq.swiftslayer.pathfinding;

import cc.polyfrost.oneconfig.config.annotations.KeyBind;
import dev.macrohq.swiftslayer.util.*;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

public class PathExecutor {
    private List<BlockPos> path;
    private int index = 0;
    public boolean running = false;
    public float directionYaw = 0f;

    public void executePath(List<BlockPos> inputPath) {
        if (running) return;
        path = inputPath;
        index = 1;
        running = true;
        directionYaw = Ref.player().rotationYaw;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if(Ref.world()==null || Ref.player()==null) return; // :angiest:
        if (!running) return;

        BlockPos current = path.get(index);
        if (PlayerUtil.getStandingPosition().distanceSqToCenter(current.getX() + 0.5, current.getY(), current.getZ() + 0.5) <= 1) {
            if (++index >= path.size()) {
                running = false;
                KeyBindUtil.setPressed(Ref.gameSettings().keyBindSprint, false);
                KeyBindUtil.setPressed(Ref.gameSettings().keyBindForward, false);
                KeyBindUtil.setPressed(Ref.gameSettings().keyBindJump, false);
                return;
            }
            current = path.get(index);
        }

        movePlayer(current);
    }

    public void disable(){
        running = false;
    }

    private void movePlayer(BlockPos current) {
        RotationUtil.Rotation rotation = RotationUtil.getAngles(new Vec3(current.getX() + 0.5, current.getY() + 2.0, current.getZ() + 0.5));
        RotationUtil.ease(rotation, 500);
        directionYaw = rotation.getYaw();
        KeyBindUtil.setPressed(Ref.gameSettings().keyBindSprint, true);
        KeyBindUtil.setPressed(Ref.gameSettings().keyBindForward, true);
        KeyBindUtil.setPressed(Ref.gameSettings().keyBindJump, current.getY() > Ref.player().posY - 1);
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        RotationUtil.onRenderWorldLast();
    }
}
