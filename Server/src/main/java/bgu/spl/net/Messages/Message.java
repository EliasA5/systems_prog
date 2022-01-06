package bgu.spl.net.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.DataBase;

import java.util.Arrays;

public abstract class Message {
    protected short opcode;
    protected static final byte[] zeroByte = new byte[]{0};
    protected static final byte[] endByte = new byte[]{';'};

    public Message(byte[] bytes){
        opcode = bytesToShort(bytes);
    }
    public Message(){}

    abstract public boolean process(DataBase database, int connectionID, Connections<Message> connections);
    public byte[] encode(){
        return endByte;
    }


    public short getOpcode(){
        return opcode;
    }
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

    public static byte[] concatAllBytes(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }
}
