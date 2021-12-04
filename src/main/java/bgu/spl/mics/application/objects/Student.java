package bgu.spl.mics.application.objects;

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

    public Student(String _name, String _department, String _status){
        name = _name;
        department = _department;
        status = Degree.valueOf(_status);
    }

    public String getName(){
        return name;
    }

    public String getDepartment(){
        return department;
    }

    public Degree getStatus(){
        return status;
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
    public void incPapersRead(Student other){
        if(other != this)
            papersRead++;
    }
}
