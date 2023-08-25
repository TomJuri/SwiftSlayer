package dev.macrohq.swiftslayer.command;

import dev.macrohq.swiftslayer.util.PlayerUtil;
import dev.macrohq.swiftslayer.util.Ref;
import dev.macrohq.swiftslayer.util.RenderUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import static dev.macrohq.swiftslayer.util.LogUtil.*;

public class Set extends CommandBase {
    BlockPos startPos = null;
    BlockPos endPos = null;
    @Override
    public int getRequiredPermissionLevel(){return 0;}
    @Override
    public String getCommandName() {
        return "set";
    }
    @Override
    public String getCommandUsage(ICommandSender sender) {
        return null;
    }
    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if(args.length>0) {
            switch (args[0]) {
                case "clear":
                    RenderUtil.filledBox.clear();
                    RenderUtil.markers.clear();
                    startPos = null;
                    endPos = null;
                    return;
                case "start":
                    startPos = PlayerUtil.getPosition();
                    RenderUtil.filledBox.add(startPos);
                    return;
                case "end":
                    endPos = PlayerUtil.getPosition();
                    RenderUtil.filledBox.add(endPos);
                    return;
            }
        }
        if(startPos!=null && endPos != null){
            log("hello");
            say("This is write.");
            error("This is error.");
            Ref.player().addChatMessage(new ChatComponentText("§c" + "hello"));
        }
    }
}
