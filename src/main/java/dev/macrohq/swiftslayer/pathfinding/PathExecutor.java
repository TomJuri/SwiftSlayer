package dev.macrohq.swiftslayer.pathfinding;

import cc.polyfrost.oneconfig.config.annotations.KeyBind;
import cc.polyfrost.oneconfig.libs.checker.units.qual.Angle;
import com.mojang.realmsclient.dto.PlayerInfo;
import dev.macrohq.swiftslayer.util.*;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import scala.actors.threadpool.Arrays;

import java.util.ArrayList;
import java.util.List;

public class PathExecutor {

    private List<BlockPos> path;
    private int index = 0;
    private boolean running = false;


    public void executePath(List<BlockPos> inputPath) {
        if (running) return;
        path = inputPath;
        index = 1;
        running = true;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!running) return;
        if (index >= path.size()) {
            running = false;
            return;
        }
        BlockPos next = path.get(index);
        if (next == null) {
            running = false;
            return;
        }
        if (PlayerUtil.getPosition().distanceSq(next) <= 0.1) {
            index++;
            return;
        }
        //RotationUtil.ease(new RotationUtil.Rotation(-90, 0), 1);
        movePlayer(next);
    }

    private void movePlayer(BlockPos next) {
        FakePlayer f = new FakePlayer();
        MovementInput m = new MovementInput();
        m.moveForward = 1;
        f.onLivingUpdate1(m);
        Logger.info("Calc: " + f.motionX + " " + f.motionZ);
        Logger.info("Real: " + Ref.player().motionX + " " + Ref.player().motionZ);
    }

    private List<KeyBinding[]> getCombinations() {
        List<KeyBinding[]> combinations = new ArrayList<>();
        KeyBinding[] forward = { Ref.gameSettings().keyBindForward };
        KeyBinding[] forwardLeft = { Ref.gameSettings().keyBindForward, Ref.gameSettings().keyBindLeft };
        KeyBinding[] forwardRight = { Ref.gameSettings().keyBindForward, Ref.gameSettings().keyBindRight };
        KeyBinding[] left = { Ref.gameSettings().keyBindLeft };
        KeyBinding[] back = { Ref.gameSettings().keyBindBack };
        KeyBinding[] backLeft = { Ref.gameSettings().keyBindBack, Ref.gameSettings().keyBindLeft };
        KeyBinding[] backRight = { Ref.gameSettings().keyBindBack, Ref.gameSettings().keyBindRight };
        KeyBinding[] right = { Ref.gameSettings().keyBindRight };
        combinations.add(forward);
        combinations.add(forwardLeft);
        combinations.add(forwardRight);
        combinations.add(left);
        combinations.add(back);
        combinations.add(backLeft);
        combinations.add(backRight);
        combinations.add(right);
        return combinations;
    }

    private Tuple<Float, Float> getVelocityForCombination(MovementInput combination) {
        float f4 = Ref.player().worldObj.getBlockState(
                new BlockPos(MathHelper.floor_double(Ref.player().posX),
                        MathHelper.floor_double(Ref.player().getEntityBoundingBox().minY) - 1,
                        MathHelper.floor_double(Ref.player().posZ))).getBlock().slipperiness * 0.91F;
        float f = 0.16277136F / (f4 * f4 * f4);
        float f5 = (float) (0.699999988079071D * f);
        return new Tuple<>(combination.moveForward * f5, combination.moveStrafe * f5);
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        RotationUtil.onRenderWorldLast();
    }
}
