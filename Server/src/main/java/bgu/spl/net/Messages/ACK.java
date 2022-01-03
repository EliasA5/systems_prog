package bgu.spl.net.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.DataBase;

import java.nio.charset.StandardCharsets;

public class ACK extends Message{

    private byte[] ack_opcode = shortToBytes((short) 10);
    private byte[] options;

    public ACK(byte[] bytes){
        super(bytes);
    }
    public ACK(short _opcode){
        opcode = _opcode;
        options = new byte[0];
    }
    public ACK(short _opcode, byte[] _options){
        opcode = _opcode;
        options = _options;
    }

    public boolean process(DataBase database, int connectionID, Connections<Message> connections){
        return false;
    }

    @Override
    public byte[] encode(){
        return concatAllBytes(ack_opcode, shortToBytes(opcode), options, endByte);
    }
}
