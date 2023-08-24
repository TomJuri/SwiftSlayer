package dev.macrohq.swiftslayer.util;

import javax.sound.sampled.*;

public class SoundUtil {
    public static void playSound(String file, int volumePercent) {
        new Thread(() -> {
            try {
                Clip clip = AudioSystem.getClip();
                AudioInputStream inputStream = AudioSystem.getAudioInputStream(SoundUtil.class.getResource(file));
                clip.open(inputStream);
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float volumePercentage = (float) volumePercent / 100f;
                float dB = (float) (Math.log(volumePercentage) / Math.log(10.0) * 20.0);
                volumeControl.setValue(dB);
                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
