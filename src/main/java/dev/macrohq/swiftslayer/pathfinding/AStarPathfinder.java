package dev.macrohq.swiftslayer.pathfinding;

import dev.macrohq.swiftslayer.util.Logger;
import dev.macrohq.swiftslayer.util.Ref;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AStarPathfinder {
    private final Node startNode;
    private final Node endNode;
    private final List<Node> openNodes = new ArrayList<>();
    private final List<Node> closedNodes = new ArrayList<>();

    public AStarPathfinder(BlockPos start, BlockPos end) {
        startNode = new Node(start, null);
        endNode = new Node(end, null);
    }

    public List<BlockPos> findPath(int iterations) {
        startNode.calculateCost(startNode, endNode);
        openNodes.add(startNode);
        for (int i = 0; i < iterations; i++) {
            Node currentNode = openNodes.stream().min(Comparator.comparingDouble(Node::getFCost)).orElse(null);
            if(currentNode == null) return new ArrayList<>();
            if (currentNode.getPosition().equals(endNode.getPosition())) return reconstructPath(currentNode);
            openNodes.remove(currentNode);
            closedNodes.add(currentNode);
            for (Node node : currentNode.getNeighbours()) {
                node.calculateCost(startNode, endNode);
                if (!node.isIn(openNodes) && !node.isIn(closedNodes)) openNodes.add(node);
            }
        }
        return new ArrayList<>();
    }

    private List<BlockPos> reconstructPath(Node end) {
        List<BlockPos> path = new ArrayList<>();
        Node currentNode = end;
        while (currentNode != null) {
            path.add(0, currentNode.getPosition());
            currentNode = currentNode.getParent();
        }
        return path;
    }

    @RequiredArgsConstructor
    private static class Node {
        @Getter
        private final BlockPos position;
        @Getter
        private final Node parent;

        private float gCost = Float.MAX_VALUE;
        private float hCost = Float.MAX_VALUE;

        public float calculateYaw() {
            float yaw = 0;
            if (parent != null) {
                double dX = parent.getPosition().getX() - position.getX();
                double dZ = parent.getPosition().getZ() - position.getZ();
                yaw = MathHelper.wrapAngleTo180_float((float) -Math.toDegrees(Math.atan2(dX, dZ)));
            }
            return yaw;
        }

        public void calculateCost(Node start, Node end) {
            gCost = (float) start.getPosition().distanceSq(position) + (parent != null ? Math.abs(calculateYaw() - parent.calculateYaw()) / 360 : 0);
            hCost = (float) position.distanceSq(end.getPosition());
        }

        public float getFCost() {
            return gCost + hCost;
        }

        public List<Node> getNeighbours() {
            List<Node> neighbourNodes = new ArrayList<>();
            int x = position.getX() - 1;
            int y = position.getY() - 1;
            int z = position.getZ() - 1;
            for (int x1 = 0; x1 < 3; x1++) {
                for (int y1 = 0; y1 < 3; y1++) {
                    for (int z1 = 0; z1 < 3; z1++) {
                        BlockPos position = new BlockPos(x + x1, y + y1, z + z1);
                        Node newNode = new Node(position, this);
                        if (newNode.isWalkable()) neighbourNodes.add(newNode);
                    }
                }
            }
            return neighbourNodes;
        }

        public boolean isWalkable() {
            boolean collision = false;

            if (this.parent != null && (this.parent.position.getX() != position.getX() && this.parent.position.getZ() != position.getZ())) {
                collision = !(Ref.world().isAirBlock(position.add(1, 1, 0))
                        && Ref.world().isAirBlock(position.add(-1, 1, 0))
                        && Ref.world().isAirBlock(position.add(0, 1, 1))
                        && Ref.world().isAirBlock(position.add(0, 1, -1)));
            }

            return allowedBlocks.contains(Ref.world().getBlockState(position.up().up()).getBlock())
                    && allowedBlocks.contains(Ref.world().getBlockState(position.up().up()).getBlock())
                    && Ref.world().getBlockState(position).getBlock().getMaterial().isSolid()
                    && !collision;
        }

        public boolean isIn(List<Node> nodes){
            return nodes.stream().anyMatch(node -> this.position.equals(node.position));
        }
    }

    private static List<Block> allowedBlocks = new ArrayList<Block>() {{
            add(Blocks.air);
            add(Blocks.tallgrass);
            add(Blocks.double_plant);
            add(Blocks.yellow_flower);
            add(Blocks.red_flower);
            add(Blocks.vine);
            add(Blocks.redstone_wire);
            add(Blocks.snow_layer);
            add(Blocks.cocoa);
            add(Blocks.end_portal);
            add(Blocks.tripwire);
            add(Blocks.web);
            add(Blocks.leaves);
            add(Blocks.flower_pot);
            add(Blocks.wooden_pressure_plate);
            add(Blocks.stone_pressure_plate);
            add(Blocks.redstone_torch);
            add(Blocks.lever);
            add(Blocks.stone_button);
            add(Blocks.wooden_button);
            add(Blocks.carpet);
            add(Blocks.standing_sign);
            add(Blocks.wall_sign);
            add(Blocks.rail);
            add(Blocks.detector_rail);
            add(Blocks.activator_rail);
            add(Blocks.golden_rail);
        }};
}
