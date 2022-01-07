package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.Messages.Message;

import java.time.Year;
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
    private String[] filtered_words = {"war", "trump"};


    public DataBase(){
        userPass = new ConcurrentHashMap<>();
        followers = new ConcurrentHashMap<>();
        blocked = new ConcurrentHashMap<>();
        loggedIn = new ConcurrentHashMap<>();
        toSend = new ConcurrentHashMap<>();
    }

    public boolean logIn(String username, String password, int connectionID, byte captcha){
        if(captcha == 0)
            return false;
        String logged = null;
        if(isLoggedIn(username) == -1 && userPass.getOrDefault(username, new String[]{""})[0].equals(password))
            logged = loggedIn.putIfAbsent(connectionID, username);
        else
            return false;
        return logged == null;
    }

    public boolean logOut(int connectionID){
        return loggedIn.remove(connectionID) != null;
    }

    public boolean register(String username, String password, String birthday){
        return userPass.putIfAbsent(username, new String[]{password, birthday, "0"}) == null;
    }

    public boolean add_follower(String toFollow, int connectionID){
        String me = loggedIn.get(connectionID);
        if(me != null && isRegistered(toFollow) && !isBlocked(me, toFollow)) {
            followers.compute(me, (key, val) -> {
                if (val == null)
                    return new ConcurrentHashMap<>();
                else
                    return val;
            }).put(toFollow, true);
        }
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
            followers.getOrDefault(me, new ConcurrentHashMap<>()).remove(toBlock);
            followers.getOrDefault(toBlock, new ConcurrentHashMap<>()).remove(me);
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
                toSend.compute(user, (key, val) -> {
                    if (val == null)
                        return new ConcurrentLinkedQueue<>();
                    else
                        return val;
                }).add(m);
        }
        return id;
    }

    public ConcurrentLinkedQueue<Message> getSendQueue(String username){
        return toSend.getOrDefault(username, new ConcurrentLinkedQueue<>());
    }

    private int isLoggedIn(String username){ //returns -1 if user is not logged in, if logged in returns connectionID of connections
        for(Map.Entry<Integer, String> entry: loggedIn.entrySet())
            if(entry.getValue().equals(username))
                return entry.getKey();
        return -1;
    }

    public String isLoggedIn(int connectionID){
        return loggedIn.getOrDefault(connectionID, null);
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
            users.add(user.trim().substring(1));
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

    public int get_num_following(String username){
        return followers.getOrDefault(username, new ConcurrentHashMap<>()).size();
    }

    public boolean increment_post(String username){
        String[] info = userPass.get(username);
        if(info != null)
            info[2] = Integer.toString(Integer.parseInt(info[2])+1);
        else
            return false;
        return true;
    }

    public String[] getFollowers(int connectionID){
        String me = loggedIn.get(connectionID);
        if(me == null)
            return new String[0];
        return followers.getOrDefault(me, new ConcurrentHashMap<>()).keySet().toArray(new String[0]);
    }

    public String[] getWhoFollowMe(int connectionID){
        String me = loggedIn.get(connectionID);
        ConcurrentLinkedQueue<String> q = new ConcurrentLinkedQueue<>();
        followers.forEachEntry(1, (entry) -> {
            if (entry.getValue().get(me) != null)
                q.add(entry.getKey());
        });
        return q.toArray(new String[0]);
    }

    public boolean isRegistered(String username){
        return userPass.containsKey(username);
    }

    public boolean isAFollowingB(String A, String B){
        return followers.getOrDefault(B, new ConcurrentHashMap<>()).containsKey(A);
    }

    public ConcurrentLinkedQueue<byte[]> getLogStats(){
        ConcurrentLinkedQueue<byte[]> result = new ConcurrentLinkedQueue<>();
        loggedIn.forEachValue(1, (user) ->{
            String[] info = userPass.getOrDefault(user, null);
            if(info == null)
                return;
            short age = (short) (Year.now().getValue() - Integer.parseInt(info[1].substring(6,10)));
            short num_posts = (short) Integer.parseInt(info[2]);
            short num_followers = (short) get_num_followers(user);
            short num_following = (short) get_num_following(user);
            result.add(Message.concatAllBytes(Message.shortToBytes(age), Message.shortToBytes(num_posts), Message.shortToBytes(num_followers), Message.shortToBytes(num_following)));
        });
        return result;
    }
    public byte[] getLogStat(String username){
        String[] info = userPass.get(username);
        if(info == null)
            return null;
        short age = (short) (Year.now().getValue() - Integer.parseInt(info[1].substring(6,10)));
        short num_posts = (short) Integer.parseInt(info[2]);
        short num_followers = (short) get_num_followers(username);
        short num_following = (short) get_num_following(username);
        return Message.concatAllBytes(Message.shortToBytes(age), Message.shortToBytes(num_posts), Message.shortToBytes(num_followers), Message.shortToBytes(num_following));
    }

}
