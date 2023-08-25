package dev.macrohq.swiftslayer.pathfinding;

import com.mojang.realmsclient.dto.PlayerInfo;
import dev.macrohq.swiftslayer.util.Logger;
import dev.macrohq.swiftslayer.util.PlayerUtil;
import dev.macrohq.swiftslayer.util.Ref;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

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
        if (PlayerUtil.getPosition().distanceSq(next) <= 0.3) {
            index++;
            return;
        }


        double motionX = (next.getX() + 0.5 - Ref.player().posX);
        double motionY = (next.getY() + 0.5 - Ref.player().posY);
        double motionZ = (next.getZ() + 0.5 - Ref.player().posZ);

        // Calculate the distance to the target position
        double distance = Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);

        double speedBlocksPerSecond = 5.6; // Speed in blocks per second (adjust as needed)
        double speedMultiplier = 0.05;

        // Calculate the speed adjustment based on the desired speed and the distance
        double adjustedSpeed = Math.min(distance, speedBlocksPerSecond * speedMultiplier);

        // Normalize the motion components
        motionX /= distance;
        motionZ /= distance;

        // Apply the calculated motion to the Ref.player()'s velocity
        Ref.player().motionX = motionX * adjustedSpeed;
        Ref.player().motionZ = motionZ * adjustedSpeed;

        // Update the Ref.player()'s position based on the new velocity
    }

}
