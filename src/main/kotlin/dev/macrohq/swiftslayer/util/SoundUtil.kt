package dev.macrohq.swiftslayer.util

import javax.sound.sampled.AudioSystem
import javax.sound.sampled.FloatControl
import kotlin.math.ln

object SoundUtil {
    fun playSound(file: String, volumePercent: Int) {
        Thread {
            val clip = AudioSystem.getClip()
            val inputStream = AudioSystem.getAudioInputStream(SoundUtil::class.java.getResource(file))
            clip.open(inputStream)
            val volumeControl = clip.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
            val volumePercentage = volumePercent.toFloat() / 100f
            val dB = (ln(volumePercentage.toDouble()) / ln(10.0) * 20.0).toFloat()
            volumeControl.value = dB
            clip.start()
        }.start()
    }
}