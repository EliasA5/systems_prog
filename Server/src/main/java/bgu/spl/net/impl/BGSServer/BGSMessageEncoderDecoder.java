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
        //TODO encode messages
        return new byte[1];
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
    }

    private Message popMessage() {
        short opcode = Message.bytesToShort(bytes);
        Message m;
        switch(opcode){
            case 1:
                m = new Register(bytes);
            break;
            case 2:
                m = new Login(bytes);
            break;
            case 3:
                m = new Logout(bytes);
            break;
            case 4:
                m = new Follow(bytes);
            break;
            case 5:
                m = new Post(bytes);
            break;
            case 6:
                m = new PrivateMessage(bytes);
            break;
            case 7:
                m = new Logstat(bytes);
            break;
            case 8:
                m = new Stat(bytes);
            break;
            case 12:
                m = new Block(bytes);
            break;
            default:
                m = null;
        }
        return m;
    }

}
