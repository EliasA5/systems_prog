package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.Messages.*;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

public class BGSProtocol implements BidiMessagingProtocol<Message> {
    private DataBase database;

    private Connections<Message> connections;
    private int connectionId;
    private boolean shouldTerminate = false;

    public BGSProtocol(DataBase _database){
        database = _database;
    }
    @Override
    public void start(int connectionId, Connections<Message> connections){
        this.connectionId = connectionId;
        this.connections = connections;
    }
    @Override
    public boolean shouldTerminate(){return shouldTerminate;}

    @Override
    public void process(Message message){

    }

}
