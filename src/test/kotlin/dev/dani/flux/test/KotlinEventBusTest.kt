package dev.dani.flux.test

import io.kotest.matchers.shouldBe
import dev.dani.flux.CancellableEvent
import dev.dani.flux.Event
import dev.dani.flux.EventBus
import kotlin.test.Test


/*
 * Project: flux
 * Created at: 11/7/25 12:26
 * Created by: Dani-error
 */
class KotlinEventBusTest {

    class TestEvent : Event

    class CancellableTestEvent : CancellableEvent {
        override var isCancelled: Boolean = false
    }

    @Test
    fun `lambda registration and event call`() {
        val bus = EventBus()
        var called = 0

        bus.register<TestEvent>
            { called++ }

        bus.post(TestEvent())

        called shouldBe 1
    }

    @Test
    fun `priority ordering`() {
        val bus = EventBus()
        val calls = mutableListOf<String>()

        bus.register<TestEvent>(priority = 1) { calls.add("low") }
        bus.register<TestEvent>(priority = 100) { calls.add("high") }

        bus.post(TestEvent())

        calls shouldBe listOf("high", "low")
    }

    @Test
    fun `cancellation respected`() {
        val bus = EventBus()
        var called = false


        bus.register<CancellableTestEvent>(ignoreCancelled = false)
            { called = true }

        val event = CancellableTestEvent().apply { isCancelled = true }
        bus.post(event)

        called shouldBe false
    }

    @Test
    fun `ignore cancelled respects parameter`() {
        val bus = EventBus()
        var called = false

        bus.register<CancellableTestEvent>(ignoreCancelled = true)
            { called = true }

        val event = CancellableTestEvent().apply { isCancelled = true }
        bus.post(event)

        called shouldBe true
    }

}