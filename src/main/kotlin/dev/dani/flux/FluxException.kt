package dev.dani.flux


/*
 * Project: flux
 * Created at: 11/7/25 12:20
 * Created by: Dani-error
 */

/**
 * Custom exception type for errors related to the Flux event system.
 *
 * Thrown when an invalid listener method is registered, an event is malformed,
 * or any internal issue occurs during event processing.
 *
 * @param message the detail message describing the exception.
 * @param cause the cause of the exception, used for exception chaining.
 */
class FluxException(
    message: String? = null,
    cause: Throwable? = null
) : RuntimeException("[Flux] $message", cause)