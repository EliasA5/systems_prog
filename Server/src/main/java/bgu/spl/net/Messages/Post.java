package bgu.spl.net.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.DataBase;

import java.nio.charset.StandardCharsets;

public class Post extends Message{
    String content;

    public Post(byte[] bytes){
        super(bytes);
        String[] parsed = new String(bytes,2 ,bytes.length-2, StandardCharsets.UTF_8).split("\0");
        content = parsed[0];

    }

    @Override
    public boolean process(DataBase database, int connectionID, Connections<Message> connections){

        return false;
    }
}
