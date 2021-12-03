package bgu.spl.mics.application.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CPUTest {
    CPU cpu;
    DataBatch data;
    @BeforeEach
    void setUp() {
        cpu = new CPU(4);
        data = new DataBatch();
    }

    @Test
    void getCoresNum() {
        assertEquals(4, cpu.getCoresNum());
    }

    @Test
    void getCPUdata() {
        assertNull(cpu.peekCPUdata());
        cpu.setData(data);
        assertNotNull(cpu.getCPUdata());
    }

    @Test
    void setData() {
        assertNull(cpu.peekCPUdata());
        cpu.setData(data);
        assertNotNull(cpu.peekCPUdata());
    }

    @Test
    void runService() {
        assertFalse(cpu.isRunning());
        cpu.runService();
        assertTrue(cpu.isRunning());
    }

}