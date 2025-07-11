package dev.dani.flux

import java.util.concurrent.ConcurrentHashMap


/*
 * Project: flux
 * Created at: 11/7/25 12:14
 * Created by: Dani-error
 */

/**
 * A simple and flexible event bus system supporting both reflection-based and lambda-based listeners.
 *
 * The [EventBus] allows you to register event listeners using annotations or lambdas, post events,
 * and manage listener lifecycle (register/unregister).
 *
 * Supports cancellable events via the [CancellableEvent] interface.
 */
class EventBus {

    /**
     * Internal map of event types to their registered listeners.
     * Each event type maps to a list of [ListenerData] objects.
     */
    val listeners = ConcurrentHashMap<Class<out Event>, MutableList<ListenerData>>()

    /**
     * Registers an object instance containing methods annotated with [Subscribe].
     *
     * Each method must have exactly one parameter of a class that implements [Event].
     *
     * @param listener the object whose annotated methods will be registered.
     *
     * @throws FluxException if the method signature is invalid.
     */
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

    /**
     * Registers a lambda listener for the given event type [T].
     *
     * This is a more idiomatic Kotlin alternative to annotation-based listeners.
     *
     * @param priority controls the order in which listeners are called (higher = earlier).
     * @param ignoreCancelled if true, the listener will receive events even if they are cancelled.
     * @param listener the lambda function to invoke when an event of type [T] is posted.
     */
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

    /**
     * Unregisters all methods belonging to the given listener instance.
     *
     * @param listener the object whose registered listener methods should be removed.
     */
    fun unregister(listener: Any) {
        listeners.values.forEach { list ->
            list.removeIf { it.instance == listener }
        }
    }

    /**
     * Posts an event to all registered listeners.
     *
     * Listeners are called in descending order of priority. If the event is a [CancellableEvent]
     * and is already cancelled, listeners that do not ignore cancelled events will be skipped.
     *
     * @param event the event to post.
     * @return the same event instance, possibly modified or cancelled by listeners.
     */
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