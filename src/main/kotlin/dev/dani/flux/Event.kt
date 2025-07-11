package dev.dani.flux


/*
 * Project: flux
 * Created at: 11/7/25 12:12
 * Created by: Dani-error
 */

/**
 * Base marker interface for all events posted through the [EventBus].
 *
 * Implement this interface to create a custom event type.
 */
interface Event

/**
 * An event that can be cancelled, preventing further propagation
 * to listeners that do not ignore cancelled events.
 *
 * Use this interface for events where you want to allow handlers
 * to stop the event from continuing.
 *
 * @property isCancelled whether the event is cancelled or not
 */
interface CancellableEvent : Event {

    /**
     * Indicates whether the event is currently cancelled.
     * If true, and the listener has not opted to ignore cancellations,
     * the listener will not be notified of the event.
     */
    var isCancelled: Boolean

}