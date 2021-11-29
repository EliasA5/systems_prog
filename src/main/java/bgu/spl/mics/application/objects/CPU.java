package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.CPUService;
import java.util.Vector;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {

    public CPU(int numOfCores){
        coresNum = numOfCores;
        CPUdata = new Vector<>();
        cluster = Cluster.getInstance();
        service = new CPUService(Integer.toString(coresNum));
    }

    public int getCoresNum(){
        return coresNum;
    }

    public Vector<DataBatch> getCPUdata(){
        return CPUdata;
    }

    public void setData(DataBatch data){
        //TODO: choose container
        CPUdata = new Vector<>();
    }
    public void runService(){
        service.run();
    }
    private int coresNum;
    private Vector<DataBatch> CPUdata; //Databatch
    private Cluster cluster;
    private CPUService service;
}
