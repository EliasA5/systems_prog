package bgu.spl.net.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.DataBase;

import java.nio.charset.StandardCharsets;

public class Stat extends Message{

    String[] usernames;

    public Stat(byte[] bytes){
        super(bytes);
        usernames = new String(bytes,2 ,bytes.length-2, StandardCharsets.UTF_8).split("\0");
    }

    @Override
    public boolean process(DataBase database, int connectionID, Connections<Message> connections){
        if(database.isLoggedIn(connectionID) == null) {
            connections.send(connectionID, new ERROR(opcode));
            return false;
        }
        byte[] info;
        for(String user : usernames){
            info = database.getLogStat(user);
            if(info == null)//TODO check if send error to all
                connections.send(connectionID, new ERROR(opcode));
            else
                connections.send(connectionID, new ACK(opcode, info));
        }
        return true;
    }
}
