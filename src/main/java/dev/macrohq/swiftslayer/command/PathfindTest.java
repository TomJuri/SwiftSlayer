package dev.macrohq.swiftslayer.command;

import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand;
import dev.macrohq.swiftslayer.pathfinding.AStarPathfinder;
import dev.macrohq.swiftslayer.util.PlayerUtil;
import dev.macrohq.swiftslayer.util.Ref;
import dev.macrohq.swiftslayer.util.RenderUtil;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.util.BlockPos;

import java.util.List;

@Command(value = "pathfindtest")
public class PathfindTest {

    @Main
    private void main() {
        AStarPathfinder a = new AStarPathfinder(Ref.removeLater1, Ref.removeLater2);
        List<BlockPos> path = a.findPath(2000);
        RenderUtil.filledBox.addAll(path);
    }

    @SubCommand
    private void setstart() {
        RenderUtil.filledBox.add(PlayerUtil.getPosition());
        Ref.removeLater1 = PlayerUtil.getPosition();
    }

    @SubCommand
    private void setend() {
        RenderUtil.filledBox.add(PlayerUtil.getPosition());
        Ref.removeLater2 = PlayerUtil.getPosition();
    }

}
