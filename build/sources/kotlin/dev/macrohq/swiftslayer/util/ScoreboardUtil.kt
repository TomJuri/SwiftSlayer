package dev.macrohq.swiftslayer.util

import com.google.common.collect.Iterables
import com.google.common.collect.Lists
import net.minecraft.scoreboard.Score
import net.minecraft.scoreboard.ScorePlayerTeam
import net.minecraft.util.StringUtils
import java.util.stream.Collectors

object ScoreboardUtil {
  fun getScoreboardLines(): List<String> {
    val lines = mutableListOf<String>()
    val scoreboard = mc.theWorld.scoreboard ?: return lines
    val objective = scoreboard.getObjectiveInDisplaySlot(1) ?: return lines
    var scores = scoreboard.getSortedScores(objective)
    val list = scores.stream()
      .filter { input: Score? ->
        input != null && input.playerName != null && !input.playerName
          .startsWith("#")
      }
      .collect(Collectors.toList())
    scores = if (list.size > 15) {
      Lists.newArrayList(Iterables.skip(list, scores.size - 15))
    } else {
      list
    }
    for (score in scores) {
      val team = scoreboard.getPlayersTeam(score.playerName)
      lines.add(ScorePlayerTeam.formatPlayerName(team, score.playerName))
    }
    val newlines = mutableListOf<String>()
    lines.forEach {
      newlines.add(StringUtils.stripControlCodes(it))
    }
    return newlines
  }
}