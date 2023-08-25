package dev.macrohq.swiftslayer.pathfinding;

import dev.macrohq.swiftslayer.util.AngleUtil;
import dev.macrohq.swiftslayer.util.Ref;
import dev.macrohq.swiftslayer.util.WorldUtil;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AStarPathfinder {
    List<Node> openNodes = new ArrayList<>();
    List<Node> closedNodes = new ArrayList<>();
    Node startNode = null;
    Node endNode = null;

    public List<BlockPos> findPath(BlockPos start, BlockPos end, int iter){
        startNode = new Node(start);
        endNode = new Node(end);
        calculateCost(startNode);
        openNodes.add(startNode);

        for(int i = 0; i++<iter;){
            Node currentNode = nodeWithLowestCost(openNodes);
            if(currentNode.getPosition().equals(end)){
                return reconstructPath(currentNode);
            }

            openNodes.remove(currentNode);
            closedNodes.add(currentNode);

            for(Node node: neighbourNodes(currentNode)){
                calculateCost(node);
                if(!Node.includes(openNodes, node) && !Node.includes(closedNodes, node)){
                    openNodes.add(node);
                }
            }
        }
        return new ArrayList<>();
    }

    void calculateCost(Node node){
        node.setGCost(WorldUtil.distanceBetweenBlockPos(startNode.getPosition(), node.getPosition()));
        node.setHCost(WorldUtil.distanceBetweenBlockPos(node.getPosition(), endNode.getPosition()));
    }

    List<Node> neighbourNodes(Node node){
        List<Node> neighbourNodes = new ArrayList<Node>();
        int X = node.getPosition().getX() - 1;
        int Y = node.getPosition().getY() - 1;
        int Z = node.getPosition().getZ() - 1;
        for(int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    BlockPos position = new BlockPos(X + x, Y + y, Z + z);
                    Node newNode = new Node(position);
                    newNode.setParentNode(node);
                    if(node.isValid()){
                        neighbourNodes.add(newNode);
                    }
                }
            }
        }
        return neighbourNodes;
    }

    Node nodeWithLowestCost(List<Node> openNodes){
        openNodes.sort(Comparator.comparingDouble(Node::getFCost));
        return openNodes.get(0);
    }

    List<BlockPos> reconstructPath(Node end){
        List<BlockPos> path = new ArrayList<>();
        Node currentNode = end;
        while(currentNode!=null){
            path.add(0, currentNode.getPosition());
            currentNode = currentNode.getParentNode();
        }
        return path;
    }
}

class Node{
    private Node parentNode;
    private float gCost = Float.MAX_VALUE;
    private float hCost = Float.MAX_VALUE;
    private float yaw = 0;
    private BlockPos position = null;

    Node(BlockPos block){
        this.position = block;
    }

    public Node getParentNode(){return this.parentNode;}
    public void setParentNode(Node parentNode){this.parentNode = parentNode;}

    public float getGCost(){return this.gCost;}
    public void setGCost(float gCost){
        calculateAngle();
        float extraCost = (parentNode != null) ? Math.abs(this.yaw - this.parentNode.yaw)/360 : 0;
        this.gCost = gCost + extraCost;
    }

    public float getHCost(){return this.hCost;}
    public void setHCost(float hCost){this.hCost = hCost;}

    public float getFCost(){return gCost + hCost;}

    public BlockPos getPosition(){return this.position;}

    void calculateAngle(){
        if(parentNode==null) return;
        double dX = this.parentNode.getPosition().getX()-this.position.getX();
        double dZ = this.parentNode.getPosition().getZ()-this.position.getZ();
        this.yaw = AngleUtil.get360Yaw((float)-Math.toDegrees(Math.atan2(dX,dZ)));
    }

    public static boolean includes(List<Node> nodes, Node node){
        for(Node inode: nodes){
            if(inode.getPosition().equals(node.getPosition())){
                return true;
            }
        }
        return false;
    }

    public boolean isValid(){
        return Ref.mc().theWorld.isAirBlock(this.position.add(0,2,0))
                && Ref.mc().theWorld.isAirBlock(this.position.add(0,1,0))
                && Ref.mc().theWorld.getBlockState(this.position).getBlock().getMaterial().isSolid();
    }
}
