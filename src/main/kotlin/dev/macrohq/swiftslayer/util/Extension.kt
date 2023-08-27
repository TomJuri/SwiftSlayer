package dev.macrohq.swiftslayer.util

import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.settings.KeyBinding
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3

fun EntityPlayerSP.getStandingOn() = position.down()
fun KeyBinding.setPressed(pressed: Boolean) = KeyBinding.setKeyBindState(keyCode, pressed)
fun BlockPos.toVec3() = Vec3(x.toDouble() + 0.5, y.toDouble() + 0.5, z.toDouble() + 0.5)