package dev.dani.flux


/*
 * Project: flux
 * Created at: 11/7/25 12:20
 * Created by: Dani-error
 */
class FluxException(
    message: String? = null,
    cause: Throwable? = null
) : RuntimeException(message, cause)