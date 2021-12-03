package bgu.spl.mics.application.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import junit.framework.TestCase;

import static org.junit.jupiter.api.Assertions.*;

public class CPUTest extends TestCase{
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
    public void getCPUdata() {
        assertNull(cpu.peekCPUdata());
        cpu.setData(data);
        assertNotNull(cpu.getCPUdata());
    }

    @Test
    public void setData() {
        assertNull(cpu.peekCPUdata());
        cpu.setData(data);
        assertNotNull(cpu.peekCPUdata());
    }

    @Test
    public void runService() {
        assertFalse(cpu.isRunning());
        cpu.runService();
        assertTrue(cpu.isRunning());
    }

}