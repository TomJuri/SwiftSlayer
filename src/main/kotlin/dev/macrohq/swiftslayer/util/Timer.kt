package dev.macrohq.swiftslayer.util

class Timer(millis: Long) {
    private val endTime: Long
    init { endTime = System.currentTimeMillis() + millis }
    val isDone: Boolean
        get() = System.currentTimeMillis() >= endTime
}
