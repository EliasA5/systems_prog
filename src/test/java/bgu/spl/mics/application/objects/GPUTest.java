package bgu.spl.mics.application.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GPUTest{
    GPU gpu0;
    GPU gpu1;
    GPU gpu2;

    @BeforeEach
    public void setUp() {
        gpu0 = new GPU("RTX3090");
        gpu1 = new GPU("RTX2080");
        gpu2 = new GPU("GTX1080");
    }

    @Test
    public void setModel() {
        Model mod = new Model();
        assertNull(gpu0.getModel());
        gpu0.setModel(mod);
        assertNotNull(gpu0.getModel());
    }

    @Test
    public void getModel() {
        Model mod = new Model();
        assertNull(gpu0.getModel());
        gpu0.setModel(mod);
        assertEquals(gpu0.getModel(), mod);
    }

    @Test
    public void getType() {
        assertEquals(gpu0.getType().name(), "RTX3090");
        assertEquals(gpu1.getType().name(), "RTX2080");
        assertEquals(gpu2.getType().name(), "GTX1080");
    }

    @Test
    public void runService(){
        assertFalse(gpu0.isRunning());
        gpu0.runService();
        assertTrue(gpu0.isRunning());
    }

}