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
        busy = false;
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
        serviceThread = new Thread(new GPUService(ty, this));
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
    public void incNumOfGPUTicks(){
        cluster.incNumOfGPUTicks();
    }

    public void runService(){
        serviceThread.start();
    }
    public boolean isRunning(){
        return serviceThread.isAlive();
    }
    public int getMaxNumOfBatches(){
        return maxNumOfBatches;
    }
    public int getCurrentNumOfBatches(){ return numOfBatches; }
    public int getNumOfTrainedBatches(){ return numOfTrainedBatches; }
    public int getCounter(){ return counter; }
    public DataBatch getNextBatch(){ return cluster.getNextBatchGPU(this); }
    public void resetCounter(){ counter = timeToTrain; }
    public void decrementCounter(){ counter--; }
    public void incrementCurrentBatches(){
        Data data = model.getData();
        if(currentIndToSend != data.getSize()) {
            cluster.addDataBatchToCPU(new DataBatch(data, currentIndToSend, this));
            numOfBatches++;
            currentIndToSend += 1000;
        }
    }
    public Model finishTrainingModel(){
        Model m = model;
        busy = false;
        model = null;
        return m;
    }
    public void incrementCurrentTrainedBatches(){
        numOfTrainedBatches++;
    }
    public void decrementCurrentNumOfBatches(){
        numOfBatches--;
    }
    public void trainModel(Model mod){
        busy = true;
        model = mod;
        numOfBatches = 0;
        currentIndToSend = 0;
    }

    public boolean isBusy(){
        return busy;
    }
    public void addModelName(Model mod){
        cluster.addModelName(mod.getName());
    }
    enum Type {RTX3090, RTX2080, GTX1080}
    private int currentIndToSend;
    private boolean busy;
    private int timeToTrain;
    private int counter;
    private int numOfBatches;
    private int numOfTrainedBatches;
    private int maxNumOfBatches;
    private Model model;
    private Cluster cluster;
    private Type type;
    private Thread serviceThread;

}
