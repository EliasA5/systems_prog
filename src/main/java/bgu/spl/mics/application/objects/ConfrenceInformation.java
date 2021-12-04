package bgu.spl.mics.application.objects;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private final String name;
    private final int date;

    public ConfrenceInformation(String _name, int _date){
        name = _name;
        date = _date;
    }

    public int getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

}
