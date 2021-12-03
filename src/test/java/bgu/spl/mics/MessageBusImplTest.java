package bgu.spl.mics;
import bgu.spl.mics.example.services.*;
import bgu.spl.mics.example.messages.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {
    MessageBusImpl bus;
    @BeforeEach
    void setUp() {
        bus = MessageBusImpl.getInstance();
    }

    @Test
    void subscribeEvent() {
        ExampleEventHandlerService handler = new ExampleEventHandlerService("handler 1", new String[]{"1"});
        bus.register(handler);
        bus.subscribeEvent(ExampleEvent.class, handler);
        Message message = new Message(){};
        ExampleEvent event = new ExampleEvent("sender 1");
        bus.sendEvent(event);
        try{
            message = bus.awaitMessage(handler);
        }
        catch(InterruptedException e){};

        assertEquals(message, event);
    }

    @Test
    void subscribeBroadcast() {
        ExampleBroadcastListenerService handler_1 = new ExampleBroadcastListenerService("handler 1", new String[]{"1"});
        ExampleBroadcastListenerService handler_2 = new ExampleBroadcastListenerService("handler 2", new String[]{"1"});
        bus.register(handler_1);
        bus.register(handler_2);
        bus.subscribeBroadcast(ExampleBroadcast.class, handler_1);
        bus.subscribeBroadcast(ExampleBroadcast.class, handler_2);
        Message message_1 = new Message(){};
        Message message_2 = new Message(){};
        Broadcast broadcast = new Broadcast() {};
        bus.sendBroadcast(broadcast);
        try{
            message_1 = bus.awaitMessage(handler_1);
            message_2 = bus.awaitMessage(handler_2);
        }
        catch(InterruptedException e){};

        assertEquals(message_1, broadcast);
        assertEquals(message_2, broadcast);
    }

    @Test
    void complete() {
        ExampleEvent event = new ExampleEvent("tester");
        Future<String> res = bus.sendEvent(event);
        assertFalse(res.isDone());
        bus.complete(event, event.getSenderName());
        assertTrue(res.isDone());
        assertEquals(res.get(), event.getSenderName());
    }

    @Test
    void sendBroadcast() {
        ExampleBroadcastListenerService handler = new ExampleBroadcastListenerService("handler 1", new String[]{"1"});
        bus.register(handler);
        bus.subscribeBroadcast(ExampleBroadcast.class, handler);
        ExampleBroadcast broadcast = new ExampleBroadcast("1"){};
        bus.sendBroadcast(broadcast);
        try{
            assertEquals(bus.awaitMessage(handler), broadcast);
        }
        catch(InterruptedException e){}

    }

    @Test
    void sendEvent() {
        ExampleEventHandlerService handler = new ExampleEventHandlerService("handler 1", new String[]{"1"});
        bus.register(handler);
        bus.subscribeEvent(ExampleEvent.class, handler);
        ExampleEvent event = new ExampleEvent("1"){};
        bus.sendEvent(event);
        try{
            assertEquals(bus.awaitMessage(handler), event);
        }
        catch(InterruptedException e){}

    }

    @Test
    void register() {
        ExampleEventHandlerService handler = new ExampleEventHandlerService("handler 1", new String[]{"1"});
        bus.register(handler);
        bus.subscribeEvent(ExampleEvent.class, handler);
        ExampleEvent event = new ExampleEvent("1"){};
        bus.sendEvent(event);
        try{
            assertEquals(bus.awaitMessage(handler), event);
        }
        catch(InterruptedException e){}

    }

    @Test
    void unregister() {
        ExampleEventHandlerService handler = new ExampleEventHandlerService("handler 1", new String[]{"1"});
        ExampleEvent event = new ExampleEvent("1"){};
        bus.sendEvent(event);
        try{
        bus.awaitMessage(handler);
        }
        catch(InterruptedException e){}
        catch(IllegalStateException e){
            assertEquals(true,true);
            return;
        }
        assertEquals(true, false);
    }

    @Test
    void awaitMessage() {
        ExampleEventHandlerService handler = new ExampleEventHandlerService("handler 1", new String[]{"1"});
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
        catch(InterruptedException e){}
        catch(IllegalStateException e){
            assertTrue(true);
            return;
        }
        assertEquals(true, false);

    }
}