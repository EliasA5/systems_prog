package bgu.spl.net.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.DataBase;

public abstract class Message {
    private short opcode;
    private static final byte zeroByte = 0;

    public Message(byte[] bytes){
        opcode = bytesToShort(bytes);
    }
    //TODO implement process for each message
    abstract boolean process(DataBase database, int connectionID, Connections<Message> connections);
    //public abstract byte[] encode();

    public static short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    public static byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }
}
