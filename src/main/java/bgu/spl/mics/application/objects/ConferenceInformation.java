package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import bgu.spl.mics.application.services.ConferenceService;
/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConferenceInformation {

    private final String name;
    private final int date;
    private final LinkedList<Model> successfulModels = new LinkedList<>();
    public ConferenceInformation(String _name, int _date){
        name = _name;
        date = _date;
        serviceThread = new Thread(new ConferenceService(_name, this));
    }

    public void runService(){
        serviceThread.start();
    }
    public boolean isRunning(){
        return serviceThread.isAlive();
    }
    private final Thread serviceThread;

    public int getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public void addModelIfSuccessful(Model mod){
        if(mod.getResult().equals("Good"))
            successfulModels.add(mod);
    }

    public LinkedList<Model> getSuccessfulModels(){
        return successfulModels;
    }

}
