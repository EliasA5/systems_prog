package bgu.spl.net.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.DataBase;

import java.nio.charset.StandardCharsets;

public class Block extends Message{

    String username;

    public Block(byte[] bytes){
        super(bytes);
        String[] parsed = new String(bytes,2 ,bytes.length-2, StandardCharsets.UTF_8).split("\0");
        username = parsed[0];
    }

    @Override
    public boolean process(DataBase database, int connectionID, Connections<Message> connections){
        boolean success;
        success = database.add_blocked(username, connectionID);
        if(success)
            connections.send(connectionID, new ACK(opcode, concatAllBytes(username.getBytes(StandardCharsets.UTF_8), zeroByte)));
        else
            connections.send(connectionID, new ERROR(opcode));
        return success;
    }

}
