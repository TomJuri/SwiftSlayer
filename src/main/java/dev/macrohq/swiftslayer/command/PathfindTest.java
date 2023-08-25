package dev.macrohq.swiftslayer.command;

import cc.polyfrost.oneconfig.libs.checker.units.qual.A;
import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand;
import dev.macrohq.swiftslayer.pathfinding.AStarPathfinder;
import dev.macrohq.swiftslayer.util.Logger;
import dev.macrohq.swiftslayer.util.PlayerUtil;
import dev.macrohq.swiftslayer.util.Ref;
import dev.macrohq.swiftslayer.util.RenderUtil;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.util.BlockPos;

import javax.xml.soap.Node;
import java.util.List;

@Command(value = "pathfindtest")
public class PathfindTest {

    @Main
    private void main() {
        RenderUtil.lines.clear();
        AStarPathfinder astar = new AStarPathfinder(Ref.removeLater1, Ref.removeLater2);
        RenderUtil.lines.addAll(astar.findPath(2000));
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

    @SubCommand
    private void clear(){
        RenderUtil.filledBox.clear();
        RenderUtil.markers.clear();
        RenderUtil.lines.clear();
        Ref.removeLater1 = null;
        Ref.removeLater2 = null;
    }

}
