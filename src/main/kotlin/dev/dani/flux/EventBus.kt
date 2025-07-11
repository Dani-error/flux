package dev.dani.flux

import java.util.concurrent.ConcurrentHashMap


/*
 * Project: flux
 * Created at: 11/7/25 12:14
 * Created by: Dani-error
 */
class EventBus {

    val listeners = ConcurrentHashMap<Class<out Event>, MutableList<ListenerData>>()

    @Suppress("UNCHECKED_CAST")
    fun register(listener: Any) {
        listener.javaClass.declaredMethods
            .filter { it.isAnnotationPresent(Subscribe::class.java) }
            .forEach { method ->
                val annotation = method.getAnnotation(Subscribe::class.java)
                val paramType = method.parameters.firstOrNull()?.type
                    ?: throw FluxException("Listener method must have exactly one parameter representing the event type")

                // Add runtime check for safety
                if (!Event::class.java.isAssignableFrom(paramType)) {
                    throw FluxException("Listener method parameter must be a subclass of Event: found $paramType")
                }

                val eventType = paramType as Class<out Event>

                val data = ListenerData(
                    method,
                    listener,
                    annotation.priority,
                    annotation.ignoreCancelled,
                    null
                )

                listeners.computeIfAbsent(eventType) { mutableListOf() }.add(data)
            }
    }

    inline fun <reified T : Event> register(
        priority: Int = 0,
        ignoreCancelled: Boolean = false,
        noinline listener: (T) -> Unit,
    ) {
        val data = ListenerData(
            method = null,
            instance = null,
            priority = priority,
            ignoreCancelled = ignoreCancelled,
            lambda = { event -> listener(event as T) }
        )

        listeners.computeIfAbsent(T::class.java) { mutableListOf() }.add(data)
    }

    fun unregister(listener: Any) {
        listeners.values.forEach { list ->
            list.removeIf { it.instance == listener }
        }
    }

    fun < T : Event > post(event: T): T {
        listeners[event.javaClass]?.sortedByDescending { it.priority }?.forEach { data ->
            if (event is CancellableEvent && event.isCancelled && !data.ignoreCancelled) return@forEach

            if (data.lambda != null) {
                data.lambda.invoke(event)
            } else if (data.method != null) {
                val wasAccessible = data.method.canAccess(data.instance)

                data.method.isAccessible = true
                data.method.invoke(data.instance, event)
                data.method.isAccessible = wasAccessible
            }
        }

        return event
    }

}