package bgu.spl.mics.application.objects;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    /**
     * Enum representing the Data type.
     */
    enum Type {
        Images, Text, Tabular
    }

    private Type type;
    private AtomicInteger processed;
    private final int size;

    public Data(String _type, int _size){
        size = _size;
        type = Type.valueOf(_type);
    }

    public int getSize() {
        return size;
    }

    public int getProcessed() {
        return processed.get();
    }
    public int remainingBatches(){return size - processed.get();}
    public void incProcessed(){
        int val;
        do{
            val = processed.get();
        }while(!processed.compareAndSet(val, val+1000));
    }

}
