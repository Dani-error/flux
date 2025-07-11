package dev.dani.flux


/*
 * Project: flux
 * Created at: 11/7/25 12:12
 * Created by: Dani-error
 */
interface Event

interface CancellableEvent : Event {
    var isCancelled: Boolean
}