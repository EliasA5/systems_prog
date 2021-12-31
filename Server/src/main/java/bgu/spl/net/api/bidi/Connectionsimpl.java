package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Connectionsimpl<T> implements Connections<T>{

    private ConcurrentHashMap<Integer, ConnectionHandler<T>> CHMap;
    private AtomicInteger id;

    public Connectionsimpl(){
        CHMap = new ConcurrentHashMap<>();
        id = new AtomicInteger(0);
    }

    @Override
    public boolean send(int connectionId, T msg){
        ConnectionHandler<T> handler = CHMap.get(connectionId);
        if(handler == null)
            return false;
        handler.send(msg);
        return true;
    }

    @Override
    public void broadcast(T msg){
        CHMap.forEachValue(1, (val) -> val.send(msg));
    }

    @Override
    public void disconnect(int connectionId){
        CHMap.remove(connectionId);
    }

    public int register(ConnectionHandler<T> handler){
        int val;
        do{
            val = id.get();
        }while(!id.compareAndSet(val, val+1));
        CHMap.put(val, handler);
        return val;
    }

    public void clear(){ //used for testing
        CHMap.clear();
    }
}
