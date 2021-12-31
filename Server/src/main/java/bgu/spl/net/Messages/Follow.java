package bgu.spl.net.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.DataBase;

import java.nio.charset.StandardCharsets;

import bgu.spl.net.impl.BGSServer.DataBase;

import java.nio.charset.StandardCharsets;

public class Follow extends Message{

    String username;
    Boolean follow;
    public Follow(byte[] bytes){
        super(bytes);
        String[] parsed = new String(bytes,3 ,bytes.length-3, StandardCharsets.UTF_8).split("\0");
        username = parsed[0];
        follow = bytes[2] == 0;
    }

    @Override
    public boolean process(DataBase database, int connectionID, Connections<Message> connections){

        return false;
    }
}
