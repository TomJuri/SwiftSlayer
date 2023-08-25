package dev.macrohq.swiftslayer.pathfinding;

import dev.macrohq.swiftslayer.util.Logger;
import dev.macrohq.swiftslayer.util.Ref;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
                if (!openNodes.contains(node) && !closedNodes.contains(node)) openNodes.add(node);
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
                        if (isWalkable()) neighbourNodes.add(newNode);
                    }
                }
            }
            return neighbourNodes;
        }

        public boolean isWalkable() {
            return Ref.world().isAirBlock(this.position.add(0, 2, 0))
                    && Ref.world().isAirBlock(this.position.add(0, 1, 0))
                    && Ref.world().getBlockState(this.position).getBlock().getMaterial().isSolid();
        }
    }

}
