package bgu.spl.net.impl.echo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class EchoClient {

    public static void main(String[] args) throws IOException {

        if (args.length == 0) {
            args = new String[]{"localhost", "hello"};
        }

        if (args.length < 2) {
            System.out.println("you must supply two arguments: host, message");
            System.exit(1);
        }

        //BufferedReader and BufferedWriter automatically using UTF-8 encoding
        try (Socket sock = new Socket(args[0], 7777);
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
             BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()))) {
            String[] finalArgs = args;
            Thread t1 = new Thread (() -> {
                try {
                    System.out.println("sending message to server");
                    out.write(finalArgs[1]);
                    out.write('\n');
                    out.flush();
                    out.write("bye");
                    out.write('\n');
                    out.flush();
                }
                catch(IOException e){e.printStackTrace();}
            });

            Thread t2 = new Thread (() -> {
                try {
                    System.out.println("awaiting response");
                    String line = in.readLine();
                    System.out.println("message from server: " + line);
                }
                catch(IOException e){e.printStackTrace();}
            });
            t1.start();
            t2.start();
            t1.join();
            t2.join();
        }
        catch(InterruptedException e) { e.printStackTrace();}
    }
}
