package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.Messages.*;
import bgu.spl.net.api.MessageEncoderDecoder;

import java.util.Arrays;

public class BGSMessageEncoderDecoder implements MessageEncoderDecoder<Message> {
    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;


    @Override
    public Message decodeNextByte(byte nextByte) {
        if (nextByte == ';') {
            return popMessage();
        }

        pushByte(nextByte);
        return null; //not a line yet
    }

    @Override
    public byte[] encode(Message message) {
        return message.encode();
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
    }

    private Message popMessage() {
        short opcode = Message.bytesToShort(bytes);
        byte[] messageBytes = new byte[len];
        System.arraycopy(bytes, 0, messageBytes, 0, len);
        //TODO remove debugging
        System.out.print("Received msg in bytes: ");
        for(byte i : messageBytes)
            System.out.print(i);
        System.out.println();

        Message m;
        switch(opcode){
            case 1:
                m = new Register(messageBytes);
            break;
            case 2:
                m = new Login(messageBytes);
            break;
            case 3:
                m = new Logout(messageBytes);
            break;
            case 4:
                m = new Follow(messageBytes);
            break;
            case 5:
                m = new Post(messageBytes);
            break;
            case 6:
                m = new PrivateMessage(messageBytes);
            break;
            case 7:
                m = new Logstat(messageBytes);
            break;
            case 8:
                m = new Stat(messageBytes);
            break;
            case 12:
                m = new Block(messageBytes);
            break;
            default:
                m = null;
        }
        len = 0;
        return m;
    }

}
