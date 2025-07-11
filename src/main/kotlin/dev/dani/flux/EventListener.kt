package dev.dani.flux

import java.lang.reflect.Method


/*
 * Project: flux
 * Created at: 11/7/25 12:14
 * Created by: Dani-error
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Subscribe(
    val priority: Int = 0,
    val ignoreCancelled: Boolean = false
)

typealias Listener = (Event) -> Unit

data class ListenerData(
    val method: Method?,
    val instance: Any?,
    val priority: Int,
    val ignoreCancelled: Boolean,
    val lambda: Listener?
)
