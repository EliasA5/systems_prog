package bgu.spl.mics;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class FutureTest{
    Future<Integer> fut;
    @BeforeEach
    public void setUp() {
        fut = new Future<>();
    }

    @Test
    public void get() {
        assertFalse(fut.isDone());
        fut.resolve(1);
        fut.get();
        assertTrue(fut.isDone());
    }

    @Test
    public void resolve() {
        assertFalse(fut.isDone());
        fut.resolve(1);
        assertTrue(fut.isDone());
        assertEquals(1, (int)fut.get());
    }

    @Test
    public void isDone() {
        assertFalse(fut.isDone());
        fut.resolve(1);
        assertTrue(fut.isDone());
    }

    @Test
    public void testGet() {
        assertFalse(fut.isDone());
        assertNull(fut.get(100, TimeUnit.MILLISECONDS));
        fut.resolve(1);
        assertNotNull(fut.get(100, TimeUnit.MILLISECONDS));
        assertTrue(fut.isDone());
    }
}