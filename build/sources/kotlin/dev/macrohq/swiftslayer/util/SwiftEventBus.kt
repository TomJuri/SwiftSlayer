package dev.macrohq.swiftslayer.util

import me.kbrewster.eventbus.EventBus
import me.kbrewster.eventbus.invokers.LMFInvoker
import net.minecraftforge.fml.common.eventhandler.Event

object SwiftEventBus {
    val internalEventBus = EventBus(LMFInvoker())

    @JvmStatic
    fun register(obj: Any) = internalEventBus.register(obj)

    @JvmStatic
    fun unregister(obj: Any) = internalEventBus.unregister(obj)

    @JvmStatic
    fun post(event: Any) = internalEventBus.post(event)

    @JvmStatic
    inline fun <reified T> post(supplier: () -> T) = internalEventBus.post(supplier)

    @JvmStatic
    fun getSubscribedEvents(clazz: Class<*>) = internalEventBus.getSubscribedEvents(clazz)

}