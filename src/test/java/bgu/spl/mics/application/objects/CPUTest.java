package bgu.spl.mics.application.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CPUTest {
    CPU cpu;
    GPU gpu;
    Data dataImage;
    Data dataText;
    Data dataTabular;
    DataBatch dataBatch;
    @BeforeEach
    public void setUp() {
        cpu = new CPU(4);
        dataImage = new Data("Image", 1000);
        dataText = new Data("Text", 1000);
        dataTabular = new Data("Tabular", 1000);
        gpu = new GPU("RTX3090");
    }

    @Test public void setTimeToProcess(){
        dataBatch = new DataBatch(dataImage, 0, gpu);
        cpu.setTimeToProcess(dataBatch);
        cpu.resetCounter();
        assertEquals(cpu.getCounter(), 32/cpu.getCoresNum() * 4);

        dataBatch = new DataBatch(dataText, 0, gpu);
        cpu.setTimeToProcess(dataBatch);
        cpu.resetCounter();
        assertEquals(cpu.getCounter(), 32/cpu.getCoresNum() * 2);

        dataBatch = new DataBatch(dataTabular, 0, gpu);
        cpu.setTimeToProcess(dataBatch);
        cpu.resetCounter();
        assertEquals(cpu.getCounter(), 32/cpu.getCoresNum() * 1);
    }

    @Test
    public void addBatch(){
        assertFalse(cpu.isBusy());
        assertFalse(cpu.addBatch());
        dataBatch = new DataBatch(dataTabular, 0, gpu);
        Cluster.getInstance().addDataBatchToCPU(dataBatch);
        assertTrue(cpu.addBatch());
        assertTrue(cpu.isBusy());
        assertEquals(cpu.getCounter(), 32/cpu.getCoresNum() * 1);
    }

    @Test
    public void finishBatch(){
        dataBatch = new DataBatch(dataTabular, 0, gpu);
        Cluster.getInstance().addDataBatchToCPU(dataBatch);
        cpu.addBatch();
        assertTrue(cpu.finishBatch());
        assertFalse(cpu.isBusy());
    }

}