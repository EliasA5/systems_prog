package bgu.spl.mics;
import bgu.spl.mics.example.services.*;
import bgu.spl.mics.example.messages.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MessageBusImplTest{
    MessageBusImpl bus;
    ExampleEventHandlerService handler;
    ExampleBroadcastListenerService handlerB;
    @BeforeEach
    public void setUp() {
        bus = MessageBusImpl.getInstance();
        ExampleEventHandlerService handler = new ExampleEventHandlerService("handler", new String[]{"1"});
        ExampleBroadcastListenerService handlerB = new ExampleBroadcastListenerService("handlerB", new String[]{"1"});
    }

    @Test
    public void subscribeEvent() {
        assertFalse(bus.isSubscribedEvent(ExampleEvent.class, handler));
        bus.register(handler);
        bus.subscribeEvent(ExampleEvent.class, handler);
        assertTrue(bus.isSubscribedEvent(ExampleEvent.class, handler));
    }

    @Test
    public void subscribeBroadcast() {
        assertFalse(bus.isSubscribedBroadcast(ExampleBroadcast.class, handlerB));
        bus.register(handlerB);
        bus.subscribeBroadcast(ExampleBroadcast.class, handlerB);
        assertTrue(bus.isSubscribedBroadcast(ExampleBroadcast.class, handlerB));
    }

    @Test
    public void complete() {
        ExampleEvent event = new ExampleEvent("tester");
        Future<String> res = bus.sendEvent(event);
        assertFalse(res.isDone());
        bus.complete(event, event.getSenderName());
        assertTrue(res.isDone());
        assertEquals(res.get(), event.getSenderName());
    }

    @Test
    public void sendBroadcast() {
        bus.register(handlerB);
        bus.subscribeBroadcast(ExampleBroadcast.class, handlerB);
        ExampleBroadcast broadcast = new ExampleBroadcast("1"){};
        bus.sendBroadcast(broadcast);
        try{
            assertEquals(bus.awaitMessage(handlerB), broadcast);
        }
        catch(InterruptedException e){}

    }

    @Test
    public void sendEvent() {
        bus.register(handlerB);
        bus.subscribeEvent(ExampleEvent.class, handlerB);
        ExampleEvent event = new ExampleEvent("1"){};
        bus.sendEvent(event);
        try{
            assertEquals(bus.awaitMessage(handlerB), event);
        }
        catch(InterruptedException e){}

    }

    @Test
    public void register(){
        assertFalse(bus.isRegistered(handler));
        bus.register(handler);
        assertTrue(bus.isRegistered(handler));
    }

    @Test
    public void unregister() {
        bus.register(handler);
        assertTrue(bus.isRegistered(handler));
        bus.unregister(handler);
        assertFalse(bus.isRegistered(handler));
    }

    @Test
    public void awaitMessage() {
        bus.register(handler);
        bus.subscribeEvent(ExampleEvent.class, handler);
        ExampleEvent event = new ExampleEvent("1"){};
        bus.sendEvent(event);
        try{
            assertEquals(bus.awaitMessage(handler), event);
        }
        catch(InterruptedException e){
            return;
        }

        bus.unregister(handler);
        bus.sendEvent(event);
        try{
            bus.awaitMessage(handler);
        }
        catch(InterruptedException e){
            assertTrue(true);
            return;
        }
        catch(IllegalStateException e){
            assertTrue(true);
            return;
        }
        assertFalse(true);
    }
    @AfterEach
    public void tearDown(){
        bus.unregister(handler);
        bus.unregister(handlerB);
    }
}