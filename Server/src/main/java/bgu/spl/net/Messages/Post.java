package bgu.spl.net.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.DataBase;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.TreeSet;

public class Post extends Message{
    String content;

    public Post(byte[] bytes){
        super(bytes);
        String[] parsed = new String(bytes,2 ,bytes.length-2, StandardCharsets.UTF_8).split("\0");
        content = parsed[0];
    }

    @Override
    public boolean process(DataBase database, int connectionID, Connections<Message> connections){
        //TODO CHECK POST ON LOGGED OUT USER
        String sending_user = database.isLoggedIn(connectionID);
        if(sending_user == null){
            connections.send(connectionID, new ERROR(opcode));
        }
        String filtered_content = database.filter(content);
        NOTIFICATION noti = new NOTIFICATION((byte) 1, sending_user, filtered_content);
        String[] users_in_content = database.find_users_in_content(content);
        String[] followers = database.getFollowers(connectionID);
        TreeSet<String> toSend = new TreeSet<>();
        toSend.addAll(Arrays.asList(users_in_content));
        toSend.addAll(Arrays.asList(followers));
        int userConnectionID;
        for(String user : toSend){
            userConnectionID = database.send(user, noti);
            if(userConnectionID != -1)
                connections.send(userConnectionID, noti);
        }
        database.increment_post(sending_user);
        connections.send(connectionID, new ACK(opcode));
        return true;
    }
}
