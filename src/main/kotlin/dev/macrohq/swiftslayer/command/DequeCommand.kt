package dev.macrohq.swiftslayer.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand
import dev.macrohq.swiftslayer.util.LockRotationUtil


@Command("deque")
class DequeCommand{

   @SubCommand
   fun pri() {
       System.out.println(LockRotationUtil.getInstance().lastYaws.toString())
   }
}
