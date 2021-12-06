package bgu.spl.mics.application.objects;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Statistics {

    private AtomicInteger numOfGPUTicks = new AtomicInteger(0);
    private AtomicInteger numOfCPUTicks = new AtomicInteger(0);
    private AtomicInteger numOfProcDataBatch = new AtomicInteger(0);
    private ConcurrentLinkedQueue<String> modelNames = new ConcurrentLinkedQueue<>();

    public Statistics(){}

    public int getNumOfGPUTicks(){
        return numOfGPUTicks.get();
    }

    public int getNumOfCPUTicks() {
        return numOfCPUTicks.get();
    }

    public int getNumOfProcDataBatch() {
        return numOfProcDataBatch.get();
    }

    public String[] getModelNames(){
        return modelNames.toArray(new String[0]);
    }

    public void incNumOfGPUTicks(){
        int val;
        do{
            val = numOfGPUTicks.get();
        }while(!numOfGPUTicks.compareAndSet(val, val+1));
    }

    public void incNumOfCPUTicks(){
        int val;
        do{
            val = numOfCPUTicks.get();
        }while(!numOfCPUTicks.compareAndSet(val, val+1));
    }

    public void incNumOfProcDataBatch(){
        int val;
        do{
            val = numOfProcDataBatch.get();
        }while(!numOfProcDataBatch.compareAndSet(val, val+1));
    }

    public void addModelName(String name){
        modelNames.add(name);
    }

}
