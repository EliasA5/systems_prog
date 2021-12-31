package bgu.spl.net.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.DataBase;

public class Logstat extends Message{

    public Logstat(byte[] bytes){
        super(bytes);
    }

    @Override
    public boolean process(DataBase database, int connectionID, Connections<Message> connections){

        return false;
    }

}
