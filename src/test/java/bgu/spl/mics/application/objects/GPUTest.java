package bgu.spl.mics.application.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GPUTest {
    GPU gpu0;
    GPU gpu1;
    GPU gpu2;

    @BeforeEach
    void setUp() {
        gpu0 = new GPU("RTX3090");
        gpu1 = new GPU("RTX2080");
        gpu2 = new GPU("GTX1080");
    }

    @Test
    void setModel() {
        Model mod = new Model();
        assertNull(gpu0.getModel());
        gpu0.setModel(mod);
        assertNotNull(gpu0.getModel());
    }

    @Test
    void getModel() {
    }

    @Test
    void getType() {
    }

    @Test
    void runService() {
    }
}