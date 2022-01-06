package bgu.spl.net.Messages;


import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.DataBase;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Login extends Message{
    String username;
    String password;
    byte captcha;

    public Login(byte[] bytes){
        super(bytes);
        String[] parsed = new String(bytes,2 ,bytes.length-2, StandardCharsets.UTF_8).split("\0");
        username = parsed[0];
        password = parsed[1];
        captcha = bytes[bytes.length-1];
    }

    @Override
    public boolean process(DataBase database, int connectionID, Connections<Message> connections){
        boolean success = database.logIn(username, password, connectionID, captcha);
        if(success) {
            connections.send(connectionID, new ACK(opcode));
            ConcurrentLinkedQueue<Message> toSend = database.getSendQueue(username);
            Message mToSend;
            while(!toSend.isEmpty()){
                mToSend = toSend.poll();
                connections.send(connectionID, mToSend);
            }

        }
        else
            connections.send(connectionID, new ERROR(opcode));
        return success;
    }
}
