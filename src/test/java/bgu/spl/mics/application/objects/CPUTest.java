package bgu.spl.mics.application.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CPUTest{
    CPU cpu;
    DataBatch data;
    @BeforeEach
    public void setUp() {
        cpu = new CPU(4);
        data = new DataBatch();
    }

    @Test
    public void getCoresNum() {
        assertEquals(4, cpu.getCoresNum());
    }


    @Test
    public void runService() {
        assertFalse(cpu.isRunning());
        cpu.runService();
        assertTrue(cpu.isRunning());
    }

}