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
        String me = database.isLoggedIn(connectionID);
        if(me == null) {
            connections.send(connectionID, new ERROR(opcode));
            return false;
        }
        byte[] info;
        for(String user : usernames){
            info = database.getLogStat(me, user);
            if(info == null) {
                connections.send(connectionID, new ERROR(opcode));
                break;
            }
            else
                connections.send(connectionID, new ACK(opcode, info));
        }
        return true;
    }
}
