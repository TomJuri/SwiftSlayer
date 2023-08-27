package dev.macrohq.swiftslayer.command;

import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand;
import dev.macrohq.swiftslayer.SwiftSlayer;
import dev.macrohq.swiftslayer.pathfinding.AStarPathfinder;
import dev.macrohq.swiftslayer.util.*;
import net.minecraft.util.BlockPos;

import java.util.List;

@Command(value = "pathfindtest", aliases = {"pft"})
public class PathfindTest {

    @Main
    private void main() {
        RenderUtil.lines.clear();
        AStarPathfinder astar = new AStarPathfinder(Ref.removeLater1, Ref.removeLater2);
        List<BlockPos> path = astar.findPath(10000);
       if(path.isEmpty()) {
           Logger.error("No path found!");
           return;
       }
        RenderUtil.lines.addAll(path);
//        SwiftSlayer.getInstance().pathExecutor.executePath(path);
    }

    @SubCommand
    private void start() {
        RenderUtil.filledBox.add(PlayerUtil.getStandingPosition());
        Ref.removeLater1 = PlayerUtil.getStandingPosition();
    }

    @SubCommand
    private void end() {
        RenderUtil.filledBox.add(PlayerUtil.getStandingPosition());
        Ref.removeLater2 = PlayerUtil.getStandingPosition();
    }

    @SubCommand
    private void clear(){
        RenderUtil.filledBox.clear();
        RenderUtil.markers.clear();
        RenderUtil.lines.clear();
        Ref.removeLater1 = null;
        Ref.removeLater2 = null;
    }

}
