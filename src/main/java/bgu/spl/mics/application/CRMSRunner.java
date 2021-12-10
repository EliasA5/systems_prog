package bgu.spl.mics.application;






import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) {
        Input input = new Input();
        String path = args[0];
        Gson gson = new Gson();
        String tojson = gson.toJson(input);
        try{
            Reader reader = Files.newBufferedReader(Paths.get(path));
            input = gson.fromJson(reader, Input.class);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        System.out.println("hi");
    }
}
