package dev.macrohq.swiftslayer.gui

import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.GuiErrorScreen
import net.minecraftforge.fml.client.CustomModLoadingErrorDisplayException

class AuthFailedDisplay(private val s: String) : CustomModLoadingErrorDisplayException() {
    override fun initGui(guiErrorScreen: GuiErrorScreen, fontRenderer: FontRenderer) {}
    override fun drawScreen(guiErrorScreen: GuiErrorScreen, fontRenderer: FontRenderer, i: Int, j: Int, f: Float) {
        guiErrorScreen.drawCenteredString(fontRenderer, "Authentication for SwiftSlayer failed!", guiErrorScreen.width / 2, 20, 16711680)
      guiErrorScreen.drawString(fontRenderer, "Error: $s", 20, 40, 16711680)
      guiErrorScreen.drawString(fontRenderer, "Possible solutions are:", 20, 50, 16777215)
      guiErrorScreen.drawString(fontRenderer, "- Make sure you are on the account you registered.", 60, 60, 16777215)
      guiErrorScreen.drawString(fontRenderer, "- Reset your HWID using /resethwid on the Discord Server.", 60, 70, 16777215)
      guiErrorScreen.drawString(fontRenderer, "- Check that you have a working Internet connection.", 60, 80, 16777215)
      guiErrorScreen.drawString(fontRenderer, "- If all options above fail, contact a staff member in the Discord Server.", 60, 90, 16777215)
      guiErrorScreen.drawString(fontRenderer, "Press ALT+F4 to quit.", 20, 110, 16711680)
    }
}
