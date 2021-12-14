package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {
    private String name;
    private Data data;
    private Student student;
    private Status status = Status.PreTrained;
    private Results result = Results.None;
    enum Status{
        PreTrained, Training, Trained, Tested
    }
    enum Results{
        None, Good, Bad
    }

    public Model(String _name, String _type, int size, Student _student){
        name = _name;
        data = new Data(_type, size);
        student = _student;
    }
    public Model(){}

    public void setResult(String res){
        result = Results.valueOf(res);
    }
    public void setStatus(String stat){
        status = Status.valueOf(stat);
    }
    public String getName(){
        return name;
    }
    public Data getData(){
        return data;
    }
    public Student getStudent(){
        return student;
    }
    public String getResult(){
        return result.toString();
    }
    public String getStatus(){
        return status.toString();
    }
    public String isPublished(){
        return result == Results.Good ? "Published" : "Failed";
    }
    public boolean isTrained(){
        return status == Status.Tested;
    }
}
