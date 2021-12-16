package bgu.spl.mics.application.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

public class GPUTest{
    GPU gpu0;
    GPU gpu1;
    GPU gpu2;
    Student student;
    Model model;

    @BeforeEach
    public void setUp() {
        gpu0 = new GPU("RTX3090");
        gpu1 = new GPU("RTX2080");
        gpu2 = new GPU("GTX1080");
        student = new Student("Stu", "Computer Science", "MSc");
        model = new Model("Model 0", "Images", 1000, student);
        LinkedList<Model> models = new LinkedList<>();
        models.add(model);
        student.addModels(models);
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
        assertNull(gpu0.getModel());
        gpu0.setModel(model);
        assertEquals(gpu0.getModel(), model);
    }

    @Test
    public void getType() {
        assertEquals(gpu0.getType().name(), "RTX3090");
        assertEquals(gpu1.getType().name(), "RTX2080");
        assertEquals(gpu2.getType().name(), "GTX1080");
    }

    @Test
    public void trainModel(){
        assertFalse(gpu0.isBusy());
        assertNull(gpu0.getModel());
        gpu0.trainModel(model);
        assertTrue(gpu0.isBusy());
        assertNotNull(gpu0.getModel());
        assertEquals(gpu0.getModel().getStatus(), "Training");
    }

    @Test
    public void incrementCurrentBatches(){
        gpu0.trainModel(model);
        assertEquals(gpu0.getCurrentNumOfBatches(), 0);
        assertTrue(gpu0.incrementCurrentBatches());
        assertEquals(gpu0.getCurrentNumOfBatches(), 1);
        assertFalse(gpu0.incrementCurrentBatches());
    }

    @Test
    public void finishTrainingModel(){
        gpu0.trainModel(model);
        assertEquals(gpu0.getModel().getStatus(), "Training");
        assertTrue(gpu0.isBusy());
        Model m = gpu0.finishTrainingModel();
        assertFalse(gpu0.isBusy());
        assertNull(gpu0.getModel());
        assertEquals(m.getStatus(), "Trained");
    }


}