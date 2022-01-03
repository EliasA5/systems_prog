package bgu.spl.net.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.DataBase;

import java.nio.charset.StandardCharsets;

public class NOTIFICATION extends Message{

    private byte type;
    private String username;
    private String content;

    public NOTIFICATION(byte[] bytes){
        super(bytes);
    }
    public NOTIFICATION(byte _type, String _username, String _content){
        opcode = 9;
        type = _type;
        username = _username;
        content = _content;
    }

    public boolean process(DataBase database, int connectionID, Connections<Message> connections){
        return false;
    }

    @Override
    public byte[] encode(){
        return concatAllBytes(shortToBytes(opcode), new byte[]{type}, username.getBytes(StandardCharsets.UTF_8), zeroByte, content.getBytes(StandardCharsets.UTF_8), zeroByte, endByte);
    }
}
