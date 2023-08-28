package dev.macrohq.swiftslayer.util

import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3

fun EntityLivingBase.getStandingOn() = BlockPos(posX, posY - 1, posZ)
fun KeyBinding.setPressed(pressed: Boolean) = KeyBinding.setKeyBindState(keyCode, pressed)
fun BlockPos.toVec3() = Vec3(x.toDouble() + 0.5, y.toDouble() + 0.5, z.toDouble() + 0.5)
fun BlockPos.toVec3Top(): Vec3 = toVec3().addVector(0.0,0.5,0.0)