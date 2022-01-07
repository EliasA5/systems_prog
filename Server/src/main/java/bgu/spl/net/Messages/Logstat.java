package bgu.spl.net.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.DataBase;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Logstat extends Message{

    public Logstat(byte[] bytes){
        super(bytes);
    }

    @Override
    public boolean process(DataBase database, int connectionID, Connections<Message> connections){
        String me = database.isLoggedIn(connectionID);
        if(me == null) {
            connections.send(connectionID, new ERROR(opcode));
            return false;
        }
        ConcurrentLinkedQueue<byte[]> stats = database.getLogStats(me);
        for(byte[] stat: stats)
            connections.send(connectionID, new ACK(opcode, stat));
        return true;
    }

}
