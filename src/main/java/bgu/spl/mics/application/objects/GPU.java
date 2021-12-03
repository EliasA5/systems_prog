package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.GPUService;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    public GPU(String ty){
        type = Type.valueOf(ty);
        cluster = Cluster.getInstance();
        serviceThread = new Thread(new GPUService(ty));
        switch(type){
            case RTX3090:
                timeToTrain = 1;
                maxNumOfBatches = 32;
                break;
            case RTX2080:
                timeToTrain = 2;
                maxNumOfBatches = 16;
                break;
            case GTX1080:
                timeToTrain = 4;
                maxNumOfBatches = 8;
                break;
        }
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public Model getModel(){
        return model;
    }

    public Type getType(){
        return type;
    }

    public void runService(){
        serviceThread.start();
    }
    public boolean isRunning(){
        return serviceThread.isAlive();
    }

    enum Type {RTX3090, RTX2080, GTX1080}
    private int timeToTrain;
    private int numOfBatches;
    private int maxNumOfBatches;
    private Model model;
    private Cluster cluster;
    private Type type;
    private Thread serviceThread;

}
