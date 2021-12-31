package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.Messages.Message;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataBase {
    private ConcurrentHashMap<String, String> userPass;
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> followers;
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> blocked;
    private ConcurrentHashMap<String, Integer> loggedIn; //Username, connectionID
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<Message>> toSend;

}
