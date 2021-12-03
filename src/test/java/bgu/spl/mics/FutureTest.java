package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class FutureTest {
    Future<Integer> fut;
    @BeforeEach
    void setUp() {
        fut = new Future<>();
    }

    @Test
    void get() {
        assertFalse(fut.isDone());
        fut.resolve(1);
        fut.get();
        assertTrue(fut.isDone());
    }

    @Test
    void resolve() {
        assertFalse(fut.isDone());
        fut.resolve(1);
        assertTrue(fut.isDone());
        assertEquals(1, fut.get());
    }

    @Test
    void isDone() {
        assertFalse(fut.isDone());
        fut.resolve(1);
        assertTrue(fut.isDone());
    }

    @Test
    void testGet() {
        assertFalse(fut.isDone());
        assertNull(fut.get(100, TimeUnit.MILLISECONDS));
        fut.resolve(1);
        assertNotNull(fut.get(100, TimeUnit.MILLISECONDS));
        assertTrue(fut.isDone());
    }
}