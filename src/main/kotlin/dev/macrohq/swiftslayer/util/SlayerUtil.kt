package dev.macrohq.swiftslayer.util

import dev.macrohq.swiftslayer.SwiftSlayer
import dev.macrohq.swiftslayer.feature.helper.Target
import dev.macrohq.swiftslayer.macro.bossKiller.RevBossKiller
import dev.macrohq.swiftslayer.macro.mobKillers.RevMobKiller
import me.kbrewster.eventbus.Subscribe
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.monster.*
import net.minecraft.entity.passive.EntityWolf
import net.minecraft.util.StringUtils
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

object SlayerUtil {

  private val bosses = listOf("Revenant Horror", "Atoned Horror", "Tarantula Broodfather", "Sven Packmaster", "Voidgloom Seraph", "Inferno Demonlord")
  private val miniBosses = listOf("Revenant Sycophant", "Revenant Champion", "Deformed Revenant", "Atoned Champion", "Atoned Revenant", "Tarantula Vermin", "Tarantula Beast", "Mutant Tarantula", "Pack Enforcer", "Sven Follower", "Sven Alpha", "Voidling Devotee", "Voidling Radical", "Voidcrazed Maniac", "Flare Demon", "Kindleheart Demon", "Burningsoul Demon")
  private val bossTypes = listOf(EntityZombie::class, EntitySpider::class, EntityWolf::class, EntityEnderman::class, EntityBlaze::class)
  private val fakeBossTypes = listOf(EntitySkeleton::class)

  fun getActive(): Pair<Slayer, SlayerTier>? {
    for (boss in bosses) {
      for (line in ScoreboardUtil.getScoreboardLines()) {
        // rev has some weird invisible emoji in the name don't remove this replace thingy
        if (!line.replace("\uD83D\uDC79", "").contains(boss)) continue
        return Pair(Slayer.valueOf(boss.uppercase().replace(" ", "_")), SlayerTier.entries.first { "$line ".contains(" ${it.name} ") })
      }
    }
    return null
  }

  fun getState() = if (ScoreboardUtil.getScoreboardLines()
      .any { it.contains("Slay the boss!") }
  ) SlayerState.BOSS_ALIVE else if (ScoreboardUtil.getScoreboardLines().any { it.contains("Boss slain!") }) SlayerState.BOSS_DEAD else null

  fun getMiniBoss(): EntityLiving? {
    val closestMiniBossArmorStand = player.worldObj.loadedEntityList.filterIsInstance<EntityArmorStand>().filter { isMiniBoss(it) }
      .minByOrNull { it.getDistanceToEntity(player) }
      ?: return null
    val entity = player.worldObj.loadedEntityList.filter { it::class.java == getMobClass() }.minByOrNull { it.getDistanceToEntity(closestMiniBossArmorStand) }
    if (entity != null) return entity as EntityLiving
    return null
  }

  fun getBoss(): Pair<EntityLiving, EntityArmorStand>? {
    val spawnedBy = player.worldObj.loadedEntityList.firstOrNull {
      StringUtils.stripControlCodes(it.name).contains("Spawned by: ${player.name}")
    }
    if (spawnedBy != null) {
      val boss = player.worldObj.loadedEntityList.filter { it::class in bossTypes }.minByOrNull { it.getDistanceToEntity(spawnedBy) }
      val bossArmorStand = player.worldObj.loadedEntityList.filterIsInstance<EntityArmorStand>().filter { isBoss(it) }.minByOrNull { it.getDistanceToEntity(boss) }
      if (bossArmorStand != null && boss != null) return Pair(boss as EntityLiving, bossArmorStand)
    }
    return null
  }

  fun getFakeBoss(): EntityLiving? {
    if(player.worldObj.loadedEntityList.filter { it::class in fakeBossTypes }.isNotEmpty()) {
      val boss = player.worldObj.loadedEntityList.filter { it::class in fakeBossTypes }[0]
      return boss as EntityLiving
    } else {
      return null
    }



  }

  fun isBoss(entity: EntityArmorStand) = bosses.any { StringUtils.stripControlCodes(entity.name).contains(it) }
  fun isBoss(entity: EntityLiving): Boolean {
    val bossArmorStands = player.worldObj.loadedEntityList.filterIsInstance<EntityArmorStand>().filter { isBoss(it) }
    bossArmorStands.forEach { bossArmorStand ->
      val closest = player.worldObj.loadedEntityList.minByOrNull { it.getDistanceToEntity(bossArmorStand) }
      if (closest == entity) return true
    }
    return false
  }

  fun isMiniBoss(entity: EntityArmorStand) = miniBosses.any { StringUtils.stripControlCodes(entity.name).contains(it) }
  fun isMiniBoss(entity: EntityLiving): Boolean {
    val miniBossArmorStands = player.worldObj.loadedEntityList.filterIsInstance<EntityArmorStand>().filter { isMiniBoss(it) }
    miniBossArmorStands.forEach { miniBossArmorStand ->
      val closest = player.worldObj.loadedEntityList.minByOrNull { it.getDistanceToEntity(miniBossArmorStand) }
      if (closest == entity) return true
    }
    return false
  }

  fun getSlayerName(): String? {
    return when (config.slayer) {
      0 -> "Revenant Horror"
      1 -> "Tarantula Broodfather"
      2 -> "Sven Packmaster"
      3 -> "Voidgloom Seraph"
      else -> null
    }
  }


  fun getSlayerSlot(): Int {
    return InventoryUtil.getSlotInGUI(getSlayerName()!!)
  }

  fun getMobClass(): Class<out EntityLiving> = when (config.slayer) {
    0 -> EntityZombie::class.java
    1 -> EntitySpider::class.java
    2 -> EntityWolf::class.java
    3 -> EntityEnderman::class.java
    else -> EntityZombie::class.java
  }

  fun getTierSlot(): Int {
    return when (config.slayerTier) {
      0 -> InventoryUtil.getSlotInGUI("${getSlayerName()} I")
      1 -> InventoryUtil.getSlotInGUI("${getSlayerName()} II")
      2 -> InventoryUtil.getSlotInGUI("${getSlayerName()} III")
      3 -> InventoryUtil.getSlotInGUI("${getSlayerName()} IV")
      4 -> InventoryUtil.getSlotInGUI("${getSlayerName()} V")
      else -> -1
    }
  }

  fun getTier(): String? {
    return when (config.slayerTier) {
      0 -> "I"
      1 -> "II"
      2 -> "III"
      3 -> "IV"
      4 -> "V"
      else -> null
    }
  }

  enum class Slayer {
    REVENANT_HORROR,
    TARANTULA_BROODFATHER,
    SVEN_PACKMASTER,
    VOIDGLOOM_SERAPH
  }

  enum class SlayerTier {
    I,
    II,
    III,
    IV,
    V
  }

  enum class SlayerState {
    BOSS_ALIVE,
    BOSS_DEAD,
  }

  @Subscribe
  fun onTick(event: ClientTickEvent) {
    if(!macroManager.enabled || RevBossKiller.getInstance().enabled) return



    if(getState() == SlayerState.BOSS_ALIVE) {
      PathingUtil.stop()
      if(RevMobKiller.getInstance().lastTargetPos != null) {
        val target = RevMobKiller.getInstance().lastTargetPos!!.add(0, 1, 0)
        val time = SwiftSlayer.config.calculateRotationTime(
          SwiftSlayer.config.calculateDegreeDistance(AngleUtil.yawTo360(mc.thePlayer.rotationYaw).toDouble(), mc.thePlayer.rotationPitch.toDouble(), AngleUtil.yawTo360(
            Target(target).getAngle().yaw).toDouble(), Target(target).getAngle().pitch.toDouble()))
        }

      RevMobKiller.getInstance().disable()
      RevBossKiller.getInstance().enable()
    }
  }
}