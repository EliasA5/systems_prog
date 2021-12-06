package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {

    private Data data;
    private int start_index;
    private GPU ownerGPU;

    public DataBatch(Data _data, int _start_index , GPU _ownerGPU){
        data = _data;
        start_index = _start_index;
        ownerGPU = _ownerGPU;
    }
    public DataBatch(){}


}
