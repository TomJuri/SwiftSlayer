package dev.macrohq.swiftslayer.pathfinding;

import dev.macrohq.swiftslayer.mixin.EntityPlayerSPInvoker;
import dev.macrohq.swiftslayer.util.Ref;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;
import net.minecraft.world.EnumDifficulty;

import java.util.List;

public class FakePlayer {

    public double motionX = 0;
    public double motionZ = 0;
    private float moveForward = 0;
    private float moveStrafing = 0;
    private float aiMoveSpeed = 0;

    public void onLivingUpdate1(MovementInput movementInput) {
        moveForward = movementInput.moveForward;
        moveStrafing = movementInput.moveStrafe;
        EntityPlayerSP player = Ref.player();
        this.pushOutOfBlocks(player.posX - (double) player.width * 0.35D, player.getEntityBoundingBox().minY + 0.5D, player.posZ + (double) player.width * 0.35D);
        this.pushOutOfBlocks(player.posX - (double) player.width * 0.35D, player.getEntityBoundingBox().minY + 0.5D, player.posZ - (double) player.width * 0.35D);
        this.pushOutOfBlocks(player.posX + (double) player.width * 0.35D, player.getEntityBoundingBox().minY + 0.5D, player.posZ - (double) player.width * 0.35D);
        this.pushOutOfBlocks(player.posX + (double) player.width * 0.35D, player.getEntityBoundingBox().minY + 0.5D, player.posZ + (double) player.width * 0.35D);
        onLivingUpdate2();
    }

    public void onLivingUpdate2() {
        EntityPlayerSP player = Ref.player();
        onLivingUpdate3();
        IAttributeInstance iattributeinstance = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
        if (!player.worldObj.isRemote) iattributeinstance.setBaseValue(player.capabilities.getWalkSpeed());
        aiMoveSpeed = (float) iattributeinstance.getAttributeValue();
    }

    public void onLivingUpdate3() {
        if (Math.abs(motionX) < 0.005D) motionX = 0.0D;
        if (Math.abs(motionZ) < 0.005D) motionZ = 0.0D;
        moveStrafing *= 0.98F;
        moveForward *= 0.98F;
        this.moveEntityWithHeading(this.moveStrafing, this.moveForward);
    }

    public void moveEntityWithHeading(float strafe, float forward) {
        float f5;
        float f4 = Ref.player().worldObj.getBlockState(new BlockPos(MathHelper.floor_double(Ref.player().posX), MathHelper.floor_double(Ref.player().getEntityBoundingBox().minY) - 1, MathHelper.floor_double(Ref.player().posZ))).getBlock().slipperiness * 0.91F;
        float f = 0.16277136F / (f4 * f4 * f4);
        f5 = Ref.player().getAIMoveSpeed() * f;
        this.moveFlying(strafe, forward, f5);
        f4 = 0.91F;
        f4 = Ref.player().worldObj.getBlockState(new BlockPos(MathHelper.floor_double(Ref.player().posX), MathHelper.floor_double(Ref.player().getEntityBoundingBox().minY) - 1, MathHelper.floor_double(Ref.player().posZ))).getBlock().slipperiness * 0.91F;
        this.motionX *= f4;
        this.motionZ *= f4;
    }

    private void pushOutOfBlocks(double x, double y, double z) {
        EntityPlayerSP player = Ref.player();
        BlockPos blockpos = new BlockPos(x, y, z);
        double d0 = x - (double) blockpos.getX();
        double d1 = z - (double) blockpos.getZ();
        int entHeight = Math.max((int) Math.ceil(player.height), 1);
        boolean inTranslucentBlock = !((EntityPlayerSPInvoker) player).invokeIsHeadspaceFree(blockpos, entHeight);
        if (inTranslucentBlock) {
            int i = -1;
            double d2 = 9999.0D;
            if (((EntityPlayerSPInvoker) player).invokeIsHeadspaceFree(blockpos.west(), entHeight) && d0 < d2) {
                d2 = d0;
                i = 0;
            }
            if (((EntityPlayerSPInvoker) player).invokeIsHeadspaceFree(blockpos.east(), entHeight) && 1.0D - d0 < d2) {
                d2 = 1.0D - d0;
                i = 1;
            }
            if (((EntityPlayerSPInvoker) player).invokeIsHeadspaceFree(blockpos.north(), entHeight) && d1 < d2) {
                d2 = d1;
                i = 4;
            }
            if (((EntityPlayerSPInvoker) player).invokeIsHeadspaceFree(blockpos.south(), entHeight) && 1.0D - d1 < d2)
                i = 5;
            float f = 0.1F;
            if (i == 0) this.motionX = -f;
            if (i == 1) this.motionX = f;
            if (i == 4) this.motionZ = -f;
            if (i == 5) this.motionZ = f;
        }
    }

    public void moveFlying(float strafe, float forward, float friction) {
        float f = strafe * strafe + forward * forward;
        if (f >= 1.0E-4F) {
            f = MathHelper.sqrt_float(f);
            if (f < 1.0F) {
                f = 1.0F;
            }
            f = friction / f;
            strafe *= f;
            forward *= f;
            float f1 = MathHelper.sin(Ref.player().rotationYaw * 3.1415927F / 180.0F);
            float f2 = MathHelper.cos(Ref.player().rotationYaw * 3.1415927F / 180.0F);
            this.motionX += strafe * f2 - forward * f1;
            this.motionZ += forward * f2 + strafe * f1;
        }
    }
}
