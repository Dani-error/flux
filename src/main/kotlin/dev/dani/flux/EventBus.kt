package dev.dani.flux

import java.util.concurrent.ConcurrentHashMap


/*
 * Project: flux
 * Created at: 11/7/25 12:14
 * Created by: Dani-error
 */
class EventBus {

    private val listeners = ConcurrentHashMap<Class<out Event>, MutableList<ListenerData>>()

    fun register(listener: Any) {

    }

    inline fun <reified T : Event> register(
        noinline listener: (T) -> Unit,
        priority: Int = 0,
        ignoreCancelled: Boolean = false
    ) {

    }

    fun unregister(listener: Any) {
        listeners.values.forEach { list ->
            list.removeIf { it.instance == listener }
        }
    }

    fun < T : Event > post(event: T): T {
        listeners[event.javaClass]?.sortedByDescending { it.priority }?.forEach { data ->
            if (event is CancellableEvent && event.isCancelled && !data.ignoreCancelled) return@forEach

            data.lambda?.invoke(event)
                ?: data.method?.invoke(data.instance, event)
        }

        return event
    }

}