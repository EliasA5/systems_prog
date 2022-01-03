package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.Messages.Message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataBase {
    private ConcurrentHashMap<String, String[]> userPass;
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> followers;
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> blocked;
    private ConcurrentHashMap<Integer, String> loggedIn; //Username, connectionID
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<Message>> toSend;
    private String[] filtered_words;

    public boolean logIn(String username, String password, int connectionID){
        String logged = null;
        if(userPass.get(username)[0].equals(password))
            logged = loggedIn.putIfAbsent(connectionID, username);
        else
            return false;
        return logged == null;
    }

    public boolean logOut(String username, int connectionID){
        return loggedIn.remove(connectionID, username);
    }

    public boolean register(String username, String password, String birthday){
        return userPass.putIfAbsent(username, new String[]{password, birthday}) == null;
    }

    public boolean add_follower(String toFollow, int connectionID){
        String me = loggedIn.get(connectionID);
        if(me != null && userPass.get(toFollow) != null && isBlocked(me, toFollow))
            followers.compute(me, (key, val) -> {
               if(val == null)
                   return new ConcurrentHashMap<>();
               else
                   return val;
            }).put(toFollow, true);
        else
            return false;
        return true;
    }

    public boolean isBlocked(String me, String other){
        ConcurrentHashMap<String, Boolean> myBlocked = blocked.get(me);
        ConcurrentHashMap<String, Boolean> otherBlocked = blocked.get(other);
        if((myBlocked != null && myBlocked.get(other) != null) || (otherBlocked != null && otherBlocked.get(me) != null))
            return true;
        return false;
    }

    public boolean add_blocked(String toBlock, int connectionID){
        String me = loggedIn.get(connectionID);
        if(me != null && userPass.get(toBlock) != null) {
            blocked.compute(me, (key, val) -> {
                if (val == null)
                    return new ConcurrentHashMap<>();
                else
                    return val;
            }).put(toBlock, true);
            followers.get(me).remove(toBlock);
            followers.get(toBlock).remove(me);
        }
        else
            return false;
        return true;
    }

    public boolean remove_follower(String toUnfollow, int connectionID){
        String me = loggedIn.get(connectionID);
        if(me != null)
            followers.compute(me, (key, value) -> {
                if(value != null)
                    value.remove(toUnfollow);
                return value;
            });
        else
            return false;
        return true;
    }

    public boolean remove_blocked(String toUnblock, int connectionID){
        String me = loggedIn.get(connectionID);
        if(me != null)
            blocked.compute(me, (key, value) -> {
                if(value != null)
                    value.remove(toUnblock);
                return value;
            });
        else
            return false;
        return true;
    }

    public int send(String user, Message m) {
        int id = -1; //if this functions returns -1, do nothing as the message will be sent later when the user logs in
        if (userPass.get(user) != null) {
            id = isLoggedIn(user);
            if(id == -1)
                toSend.compute(user, (key, value) -> {
                    if (value != null)
                        value = new ConcurrentLinkedQueue<>();
                    return value;
                }).add(m);
        }
        return id;
    }

    private int isLoggedIn(String username){
        for(Map.Entry<Integer, String> entry: loggedIn.entrySet())
            if(entry.getValue().equals(username))
                return entry.getKey();
        return -1;
    }

    public String filter(String content){
        String filtered_content = content;
        for(String filtered_word : filtered_words)
            filtered_content = filtered_content.replaceAll(filtered_word, "<filtered>");
        return filtered_content;
    }

    public String[] find_users_in_content(String content){
        Pattern MY_USER = Pattern.compile("@.*?(\\s|\\z)");
        Matcher m = MY_USER.matcher(content);
        ConcurrentLinkedQueue<String> users = new ConcurrentLinkedQueue<>();
        String user;
        while(m.find()) {
            user = m.group();
            users.add(user.substring(1));
        }
        return users.toArray(new String[0]);
    }

    public int get_num_followers(String username){
        int num_followers = followers.reduceValuesToInt(1, (val) -> {
            if(val.get(username) != null)
                return 1;
            else return 0;
                }, 0, (x, y) -> x+y);
        return num_followers;
    }

}
