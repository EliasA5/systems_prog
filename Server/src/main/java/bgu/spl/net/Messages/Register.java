package bgu.spl.net.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.DataBase;

import java.nio.charset.StandardCharsets;

public class Register extends Message{
    String username;
    String password;
    String birthday;

    public Register(byte[] bytes){
        super(bytes);

        String[] parsed = new String(bytes,2 ,bytes.length-2, StandardCharsets.UTF_8).split("\0");
        username = parsed[0];
        password = parsed[1];
        birthday = parsed[2];
    }

    @Override
    public boolean process(DataBase database, int connectionID, Connections<Message> connections){
        boolean success = database.register(username, password, birthday);
        if(success)
            connections.send(connectionID, new ACK(opcode));
        else
            connections.send(connectionID, new ERROR(opcode));
        return success;
    }
}
