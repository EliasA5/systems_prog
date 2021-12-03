package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.CPUService;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {

    public CPU(int numOfCores){
        coresNum = numOfCores;
        CPUdata = new ArrayBlockingQueue<DataBatch>(1);
        cluster = Cluster.getInstance();
        serviceThread = new Thread(new CPUService(Integer.toString(coresNum)));
    }

    public int getCoresNum(){
        return coresNum;
    }

    public DataBatch getCPUdata(){
        try{return CPUdata.take();}
        catch(InterruptedException e){
            return null;
        }
    }
    public DataBatch peekCPUdata(){
        return CPUdata.peek();
    }
    public void setData(DataBatch data){
        CPUdata.add(data);
    }
    public void runService(){
        serviceThread.start();
    }

    public boolean isRunning(){
        return serviceThread.isAlive();
    }
    private int coresNum;
    private ArrayBlockingQueue<DataBatch> CPUdata; //Databatch
    private Cluster cluster;
    private Thread serviceThread;
}
