package bgu.spl.net.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.DataBase;

import java.nio.charset.StandardCharsets;

public class Stat extends Message{

    String[] usernames;

    public Stat(byte[] bytes){
        super(bytes);
        String[] parsed = new String(bytes,2 ,bytes.length-2, StandardCharsets.UTF_8).split("\0");
        usernames = parsed[0].split("|");
    }

    @Override
    public boolean process(DataBase database, int connectionID, Connections<Message> connections){

        return false;
    }
}
