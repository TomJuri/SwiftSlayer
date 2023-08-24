package dev.macrohq.swiftslayer.util;

public class Timer {

    private final long endTime;

    public Timer(long millis) {
        this.endTime = System.currentTimeMillis() + millis;
    }

    public boolean isDone() {
        return System.currentTimeMillis() >= endTime;
    }
}
