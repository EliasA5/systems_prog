package bgu.spl.net.srv.bidi;


import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.srv.Reactor;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NonBlockingConnectionHandler<T> implements ConnectionHandler<T> {

    private static final int BUFFER_ALLOCATION_SIZE = 1 << 13; //8k
    private static final ConcurrentLinkedQueue<ByteBuffer> BUFFER_POOL = new ConcurrentLinkedQueue<>();

    private final BidiMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Queue<ByteBuffer> writeQueue = new ConcurrentLinkedQueue<>();
    private final SocketChannel chan;
    private final Reactor reactor;

    public NonBlockingConnectionHandler(MessageEncoderDecoder _encdec, BidiMessagingProtocol _protocol, SocketChannel _chan, Reactor _reactor){
        encdec = _encdec;
        protocol = _protocol;
        chan = _chan;
        reactor = _reactor;
    }

    @Override
    public void send(T msg){

    }

    @Override
    public void close(){

    }

    public Runnable continueRead(){
        return () -> {};
    }

    public void continueWrite(){

    }
}
