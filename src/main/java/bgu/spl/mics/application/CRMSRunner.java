package bgu.spl.mics.application;






import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.TimeService;
import com.google.gson.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) {
        String path = args[0];
        Gson gson = new Gson();

        ArrayList<Student> students = new ArrayList<>();
        ArrayList<GPU> gpus = new ArrayList<>();
        ArrayList<CPU> cpus = new ArrayList<>();
        ArrayList<ConferenceInformation> conferences = new ArrayList<>();
        try{
            Reader reader = Files.newBufferedReader(Paths.get(path));
            JsonObject input = gson.fromJson(reader, JsonObject.class);

            JsonArray inputStudents = input.get("Students").getAsJsonArray();
            JsonObject studentJson;
            JsonArray modelsJsonArray;
            JsonObject modelJson;
            LinkedList<Model> models;
            Student student;
            for(int i = 0; i < inputStudents.size(); i++) {
                models = new LinkedList<>();
                studentJson = inputStudents.get(i).getAsJsonObject();
                student = new Student(studentJson.get("name").getAsString(), studentJson.get("department").getAsString(), studentJson.get("status").getAsString());
                modelsJsonArray = studentJson.get("models").getAsJsonArray();
                for(int j = 0; j < modelsJsonArray.size(); j++){
                    modelJson = modelsJsonArray.get(j).getAsJsonObject();
                    models.add(new Model(modelJson.get("name").getAsString(), modelJson.get("type").getAsString(), modelJson.get("size").getAsInt(), student));
                }
                student.addModels(models);
                students.add(student);
            }

            JsonArray inputGPUS = input.get("GPUS").getAsJsonArray();
            for(int i = 0; i < inputGPUS.size(); i++)
                gpus.add(new GPU(inputGPUS.get(i).getAsString()));


            JsonArray inputCPUS = input.get("CPUS").getAsJsonArray();
            for(int i = 0; i < inputCPUS.size(); i++)
                cpus.add(new CPU(inputCPUS.get(i).getAsInt()));

            JsonArray inputConferences = input.get("Conferences").getAsJsonArray();
            JsonObject conference;
            for(int i = 0; i < inputConferences.size(); i++) {
                conference = inputConferences.get(i).getAsJsonObject();
                conferences.add(new ConferenceInformation(conference.get("name").getAsString(), conference.get("date").getAsInt()));
            }
            int Speed = input.get("TickTime").getAsInt();
            int Duration = input.get("Duration").getAsInt();

            Thread time = new Thread(new TimeService(Speed, Duration));
            for(GPU gpu : gpus)
                gpu.runService();
            for(CPU cpu : cpus)
                cpu.runService();
            for(ConferenceInformation conference1 : conferences)
                conference1.runService();
            for(Student student1 : students)
                student1.runService();
            time.start();

            time.join();
            Thread.sleep(50);
            String stats = Cluster.getInstance().stats();
            stats += "\n";
            for(ConferenceInformation conference1 : conferences)
                stats += conference1.toString();
            stats += "\n";
            for(Student student1 : students)
                stats += student1.toString() + "\n";
            BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
            writer.write(stats);
            writer.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        catch(JsonSyntaxException e){
            e.printStackTrace();
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }


    }
}
