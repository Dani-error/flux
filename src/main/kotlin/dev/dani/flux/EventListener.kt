package dev.dani.flux

import java.lang.reflect.Method


/*
 * Project: flux
 * Created at: 11/7/25 12:14
 * Created by: Dani-error
 */

/**
 * Marks a method as an event listener to be registered by the [EventBus].
 *
 * The method must have exactly one parameter of a type implementing [Event].
 *
 * @property priority The execution priority of this listener.
 * Higher values mean the listener is called earlier.
 * Default is 0.
 *
 * @property ignoreCancelled If true, the listener will be invoked even if the event
 * has been cancelled (for cancellable events).
 * Default is false.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Subscribe(
    val priority: Int = 0,
    val ignoreCancelled: Boolean = false
)

/**
 * Represents a function that handles an [Event].
 *
 * This is a listener callback type used internally by the [EventBus]
 * to invoke lambda-based event listeners.
 */
typealias Listener = (Event) -> Unit

/**
 * Represents metadata for a registered event listener.
 *
 * This encapsulates both reflection-based listener methods and lambda listeners.
 *
 * @property method the reflected method to invoke (nullable if using lambda).
 * @property instance the listener object instance owning the method (nullable for lambdas).
 * @property priority the priority of the listener (higher = earlier).
 * @property ignoreCancelled whether to ignore cancelled events.
 * @property lambda the lambda listener function (nullable if using method).
 */
data class ListenerData(
    val method: Method?,
    val instance: Any?,
    val priority: Int,
    val ignoreCancelled: Boolean,
    val lambda: Listener?
)

/**
 * Handle for a lambda listener registered on the [EventBus].
 *
 * Used to unregister lambda listeners when needed.
 *
 * @property data the internal listener data associated with this handle.
 */
data class ListenerHandle(val data: ListenerData)