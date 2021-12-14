package bgu.spl.mics.application.objects;


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

    private final Type type;
    private final int size;

    public Data(String _type, int _size){
        size = _size;
        type = Type.valueOf(_type);
    }

    public int getSize() {
        return size;
    }

    public String getType(){
        return type.toString();
    }


}
