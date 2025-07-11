package dev.dani.flux.test;

import dev.dani.flux.CancellableEvent;
import dev.dani.flux.Event;
import dev.dani.flux.EventBus;
import dev.dani.flux.ListenerHandle;
import dev.dani.flux.Subscribe;
import kotlin.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Project: flux
 * Created at: 11/7/25 12:34
 * Created by: Dani-error
 *
 * Unit tests for {@link EventBus}, verifying listener registration,
 * unregistration, priority ordering, cancellation behaviour,
 * and Java-compatible lambda registration.
 */
public class JavaEventBusTest {

    private static class TestEvent implements Event { }

    private static class CancellableTestEvent implements CancellableEvent {

        private boolean cancelled = false;

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }
    }

    private static class JavaListener {

        int called = 0;

        @Subscribe(priority = 5)
        public void onTest(TestEvent event) { called++; }

        boolean cancelledSeen = false;

        @Subscribe(priority = 1, ignoreCancelled = true)
        public void onCancel(CancellableTestEvent event) {
            cancelledSeen = event.isCancelled();
        }

    }

    private EventBus bus;
    private JavaListener listener;

    @BeforeEach
    void setup() {
        bus = new EventBus();
        listener = new JavaListener();
    }

    @Test
    void testRegistrationAndEventCall() {
        bus.register(listener);
        bus.post(new TestEvent());

        assertEquals(1, listener.called);
    }

    @Test
    void testUnregister() {
        bus.register(listener);
        bus.unregister(listener);

        bus.post(new TestEvent());

        assertEquals(0, listener.called);
    }

    @Test
    void testPriority() {
        StringBuilder builder = new StringBuilder();

        Object high = new Object() {
            @Subscribe(priority = 100)
            public void on(TestEvent event) { builder.append("H"); }
        };

        Object low = new Object() {
            @Subscribe(priority = 1)
            public void on(TestEvent event) { builder.append("L"); }
        };

        bus.register(high);
        bus.register(low);

        bus.post(new TestEvent());

        assertEquals("HL", builder.toString());
    }

    @Test
    void testCancellation() {
        bus.register(listener);

        CancellableTestEvent event = new CancellableTestEvent();
        event.setCancelled(true);

        bus.post(event);

        assertTrue(listener.cancelledSeen);
    }

    @Test
    void testLambdaRegistrationAndUnregister() {
        AtomicBoolean called = new AtomicBoolean(false);

        ListenerHandle handle = bus.register(TestEvent.class, event -> {
            called.set(true);
        });

        bus.post(new TestEvent());
        assertTrue(called.get());

        // Reset and unregister
        called.set(false);
        bus.unregister(handle);
        bus.post(new TestEvent());
        assertFalse(called.get());
    }
}