package bgu.spl.net.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.DataBase;

import java.nio.charset.StandardCharsets;

public class PrivateMessage extends Message {
    String username;
    String content;
    String date;

    public PrivateMessage(byte[] bytes){
        super(bytes);
        String[] parsed = new String(bytes,2 ,bytes.length-2, StandardCharsets.UTF_8).split("\0");
        username = parsed[0];
        content = parsed[1];
        date = parsed[2];
    }

    @Override
    public boolean process(DataBase database, int connectionID, Connections<Message> connections){
        String sending_user = database.isLoggedIn(connectionID);
        if(sending_user == null || !database.isRegistered(username) || !database.isAFollowingB(username, sending_user)){
            connections.send(connectionID, new ERROR(opcode));
            return false;
        }
        String filtered_content = database.filter(content);
        NOTIFICATION noti = new NOTIFICATION((byte) 0, sending_user, filtered_content);
        int userConnectionID = database.send(username, noti);
        if(userConnectionID != -1)
            connections.send(userConnectionID, noti);
        connections.send(connectionID, new ACK(opcode));
        return true;
    }

}
