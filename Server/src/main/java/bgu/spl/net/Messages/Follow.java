package bgu.spl.net.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.DataBase;

import java.nio.charset.StandardCharsets;

import bgu.spl.net.impl.BGSServer.DataBase;

import java.nio.charset.StandardCharsets;

public class Follow extends Message{

    String username;
    Boolean follow;
    byte[] followByte;
    public Follow(byte[] bytes){
        super(bytes);
        String[] parsed = new String(bytes,3 ,bytes.length-3, StandardCharsets.UTF_8).split("\0");
        username = parsed[0];
        followByte  = new byte[1];
        followByte[0] = bytes[2];
        follow = bytes[2] == 0;
    }

    @Override
    public boolean process(DataBase database, int connectionID, Connections<Message> connections){
        boolean success;
        if(follow)
            success = database.add_follower(username, connectionID);
        else
            success = database.remove_follower(username, connectionID);
        if(success)
            connections.send(connectionID, new ACK(opcode, concatAllBytes(followByte, username.getBytes(StandardCharsets.UTF_8), zeroByte)));
        else
            connections.send(connectionID, new ERROR(opcode));
        return success;
    }
}
