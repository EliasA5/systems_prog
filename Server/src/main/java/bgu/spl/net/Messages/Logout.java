package bgu.spl.net.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.DataBase;

import java.nio.charset.StandardCharsets;

public class Logout extends Message{

    public Logout(byte[] bytes){
        super(bytes);
    }

    @Override
    public boolean process(DataBase database, int connectionID, Connections<Message> connections){
        boolean success = database.logOut(connectionID);
        if(success)
            connections.send(connectionID, new ACK(opcode));
        else
            connections.send(connectionID, new ERROR(opcode));
        return success;
    }
}
