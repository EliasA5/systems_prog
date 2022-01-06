package bgu.spl.net.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.DataBase;

public class ERROR extends Message{
    private static final short error_opcode = 11;

    public ERROR(byte[] bytes){
        super(bytes);
    }
    public ERROR(short _opcode){
        opcode = _opcode;
    }

    public boolean process(DataBase database, int connectionID, Connections<Message> connections){
        return false;
    }

    @Override
    public byte[] encode(){
        byte[] error = shortToBytes(error_opcode);
        byte[] opcode_bytes = shortToBytes(opcode);
        return concatAllBytes(error, opcode_bytes, endByte);
    }
}
