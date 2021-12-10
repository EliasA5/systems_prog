package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.CPUService;

import java.util.ArrayList;


/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {

    public CPU(int numOfCores){
        coresNum = numOfCores;
        CPUdata = new ArrayList<DataBatch>(2);
        cluster = Cluster.getInstance();
        serviceThread = new Thread(new CPUService(Integer.toString(coresNum), this));
    }

    public int getCoresNum(){
        return coresNum;
    }

    public void runService(){
        serviceThread.start();
    }
    public boolean isRunning(){
        return serviceThread.isAlive();
    }

    public boolean isBusy(){ return busy; }
    public void setTimeToProcess(DataBatch batch){
        String type = batch.getDataType();
        int ticks = type.equals("Images") ? 4 :
                    type.equals("Text") ? 2 :
                            1;
        timeToProcess = 32/coresNum * ticks;
    }
    public void resetCounter(){ counter = timeToProcess;}
    public void decrementCounter(){ counter--; }
    public int getCounter(){ return counter; }
    public void finishBatch(){
        busy = false;
        DataBatch batch = CPUdata.get(0);
        cluster.addDataBatchToGPU(batch.getOwnerGPU(), batch);
        cluster.incNumOfProcDataBatch();
        CPUdata.set(0, null);
    }
    public boolean addBatch(){
        if(CPUdata.get(0) != null)
            return false;
        DataBatch batch = cluster.getNextBatchCPU();
        if(batch == null)
            return false;
        busy = true;
        CPUdata.set(0, batch);
        setTimeToProcess(batch);
        resetCounter();
        return true;
    }
    public void incNumOfCPUTicks(){
        cluster.incNumOfCPUTicks();
    }
    private int counter;
    private int timeToProcess;
    private boolean busy;
    private int coresNum;
    private ArrayList<DataBatch> CPUdata; //Databatch
    private Cluster cluster;
    private Thread serviceThread;
}
