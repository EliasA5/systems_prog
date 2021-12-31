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

        return false;
    }

}
