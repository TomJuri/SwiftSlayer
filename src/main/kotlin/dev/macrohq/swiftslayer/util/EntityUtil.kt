package dev.macrohq.swiftslayer.util

import net.minecraft.entity.EntityLiving
import net.minecraft.entity.monster.EntityCaveSpider
import net.minecraft.init.Blocks
import kotlin.math.abs

object EntityUtil {

    // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ //
    // dont touch unless u are known as .ducklett //
    // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ //

    /*


              ..................  ......        ....... ......    ....................
              .#@*.#@@@@@@@@.#@=..-%@@@#..    ..:#@@@@%-..#@#.  ..+@#.*@@@@@%:.*@@@@%#:.
              .#@*.:--*@%--:.*@-.*@@:.:+..    .#@%=::=@@%.-@@-. .-@@:.#@#::::. #@#::+@@-
              .#@*.  .*@%.  ..-..*@@*:..     .=@@:. ..:@@-.#@%...%@+..#@#..... #@#..-@@-
              .#@*.  .*@%.      ..:@@@@%..  ..*@%.    :%@+..@@+.-@%...#@@@@@*. #@@@@@%:.
              .#@*.  .*@%.      .....:%@%.  ..=@@:.  .-@@-..=@%:%@-...#@#      #@#.:@@*.
              .#@*.  .*@%.      .*+-:=@@*.    .%@@=--+@@*....%@@@*. ..#@%----..#@#..-@@=..
              .*%+. ..=%*.      .-#%%%#:       .-#%%%%*.    .:%%#:. ..+%%%%%#:.*%*. .+%%..



                                          ...:-=#%%@@@@@@@@@#*:.
                                      .=%@@*+:...           .*@@%:
                                  .*@*-:                        .=#-
                                :##:                               .++:
                             .-@#.                                   .##..
                            :@+.                                       .*%.
                           -@:     .=++==--:::::...:=**=:                -@:
                          =%:                                             :@:
                        .%#.              ...-*##*+:..                     =%.
                        **.           =%*=:...      .-*%=::::::.           :@:
                      .*#.       :+=:.                           .....      #+
                      :@.       .-#%###%%=.                 :=+++-.         =*
                      =#      :@%.       :*.            ..-@-    .#@=.      ==
                      -@                  .#=           =*         .-=      #=
                      :@:::--===---::::----++:          %:    .--=====--:.  =+.
                     :@@@#-::. .::-====-::=++#@#=+++=--=+%@%#*+-:.   .:-=+#@@@@@-
                     :@@@=                    @@*:.:.:@#                   :@@@@:
                     .@@#.  .*                @*      %@                   :@%@@
                      #@%.  .=%#+-:.  :-+@-   @=      #@: :%#=:.    .-#%.  -@#@-
                      :@@-        :::::.      @=      -@=     .-===-:.    .*@*@-
                      :%@*                    @-      .%*                 .%%.%:
                      :%=@#::-+***#####%%%@@@@=.       :%@%%%%%*-::::::::::%* #-
                      :%.=*******+=======---:.                 .:-----------. *-
                      :%                                                      *-
                      :%                   +%.             .                 =%.
                      :%               -##@#.             .-%*.              =#
                      :@              .%=                    *#              =#
                      -*               +@:              .::.:@=              +%.
                      -*                :@@@@%%=       +@@@@%:                %-
                      *=                                                      +*-+=.
                     =@:                                                      :@:
                   :*@-:=.                                                    .#@+..
                   .*@*.                                                     :*#@:
                  .*%@=+-                                                   :=+*@#
                .-=#=@-:=-.                                               .:=+=@==
               .. :=*%%@%%=.                                             .#=@%@*+-
                   =++@%.%=.                                             -= :%@=
                   ..+#@@#.-=                                             #.-%--
                      :%@-+-                                            .=#%*#
                      .#@*+%-.          .:%@@@@@@@@@@@####%@%:.           .+@*..
                      .*@@+#%:        +%@*=::......:::====*#@@@:       :%*=#@@:.
                      -@=%%*.=:                               ..        .*+%##%.
                ..-#@@@%+*@*#==.                                      .=#*@@:*@=
           .-#@@@*:   #:  *#..                                        :#@@@+ :##.
    -%@@%*---::.      #: .:%@-::                                      .*@@-.. ***@%+-.
#%%*=.                =+ .+-@*.                                     .=+%@*-   **   .=#@@*:
                       =*...=%#*=                                   -=@@**  :.%*       :#@@=.
                        -%-#.*%*                                   :@*@%+.:%:+@-         .:#@*
                         .%-%-#@-                                 :%*%@-.#=.+@=.             :*@%*-.
                          .#+%=#%-.                               :@@**#++-%%:                   .=#
                            -%=%@@=                               :%%:  -%*.
                             -@:%@*                              .*@=   ..
                              .#@@%-*-               ::           :@:=*.  .=+*#%%%#+.
                                =@@#-+#+=....:  :+ .-.:+..=.   .@*%@%=            .-+*%@#.
                                .#@@#::=.=%+  .*:++=+:* %-.*=*+=+*@@=
                                  :%@*#.-:*.  *+-#:=:%.::* :##+:=#@@.
                                  ..=@%=%#=.+-**#+:+:*=#=#+*#+#@%@=.
                                    :-:%%@#=*:-#=*-*.*##:*:-*%@%:.
                                    ::.+:#.-@@@%@@@@@%%@=%@@#:#=                                    */



    private const val distanceNormalizationFactor: Int = 80
    private  const val rotationNormalizationFactor: Int = 360
    private const val yDistanceNormalizationFactor: Int = 80
    private const val rotationWeight: Double = 0.309
    private const val yChangeWeight: Double = 1.3
    private const val distanceWeight: Double = 2.0


    private fun getMobCost(entity: EntityLiving): Double {


   /*     val normalizedYaw: Double = abs(AngleUtil.yawTo360(mc.thePlayer.rotationYaw) - AngleUtil.yawTo360(Target(entity).getAngle().yaw)).toDouble() /  rotationNormalizationFactor
        val normalizedDistance: Double = BlockUtil.getXZDistance(player.position, entity.position) / distanceNormalizationFactor
        var normalizedYChange: Double = 0.0
        if(abs(player.posY - entity.posY) > 3) {
            normalizedYChange = (abs(player.posY - entity.posY) - 3) / yDistanceNormalizationFactor
        } */

        /* return (normalizedYaw * rotationWeight) + (normalizedDistance * distanceWeight) + (normalizedYChange * yChangeWeight) */
        var distanceCost = BlockUtil.getXZDistance(player.position, entity.position)

        if(abs(player.position.y - entity.posY) > 3) {
            distanceCost *= 5
        }
        if(!player.canEntityBeSeen(entity)) distanceCost *= 5
        return distanceCost.toDouble()
    }

    fun getMobs(entityClass: Class<out EntityLiving>): List<EntityLiving> {

        val entities = world.getLoadedEntityList().asSequence()
            .filterIsInstance(entityClass)
            .filter { it !is EntityCaveSpider }
            .filter { it.maxHealth > 250 }
           .filter { world.getBlockState(it.getStandingOnCeil()).block != Blocks.air }
           .filter { AngleUtil.getAngles(it).pitch < 60 && AngleUtil.getAngles(it).pitch > -60 }
            .filter { !SlayerUtil.isBoss(it) }
            .filter { !SlayerUtil.isMiniBoss(it) }
            .sortedBy{ getMobCost(it) }
            .toMutableList()
        if (!config.ignoreMiniBosses && SlayerUtil.getMiniBoss() != null)
            entities.add(0, SlayerUtil.getMiniBoss()!!)

        return entities
    }



}
