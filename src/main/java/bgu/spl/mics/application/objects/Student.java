package bgu.spl.mics.application.objects;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    enum Degree {
        MSc, PhD
    }

    private final String name;
    private final String department;
    private final Degree status;
    private int publications = 0;
    private int papersRead = 0;
    private LinkedList<Model> models;

    public Student(String _name, String _department, String _status, LinkedList<Model> _models){
        name = _name;
        department = _department;
        status = Degree.valueOf(_status);
        models = _models;
    }

    public String getName(){
        return name;
    }

    public String getDepartment(){
        return department;
    }

    public String getStatus(){
        return status.toString();
    }

    public int getPublications(){
        return publications;
    }

    public int getPapersRead(){
        return papersRead;
    }

    public void incPublications(){
        publications++;
    }

    public void incPapersRead(LinkedList<Model> succModels){
        for(Model mod: succModels)
            incPapersRead(mod.getStudent());
    }

    public void incPapersRead(Student other){
        if(other != this)
            papersRead++;
    }

    public Iterator<Model> getModelsIterator(){
        return models.iterator();
    }
    public LinkedList<Model> getModels(){
        return models;
    }

}
