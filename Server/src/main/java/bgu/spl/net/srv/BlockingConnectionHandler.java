package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connectionsimpl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {

    private final BidiMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected;



    public BlockingConnectionHandler(Socket _sock, MessageEncoderDecoder<T> _encdec, BidiMessagingProtocol<T> _protocol){
        sock = _sock;
        protocol = _protocol;
        encdec = _encdec;
    }

    @Override
    public void send(T msg){
        //TODO check if out != null is needed or connected == true
        if(msg != null && connected)
            try{
                out.write(encdec.encode(msg));
                out.flush();
            } catch(IOException e){
                e.printStackTrace();
            }
    }

    @Override
    public void run(){
        try(Socket sock = this.sock){
            int read;
            connected = true;
            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());

            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                T nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    protocol.process(nextMessage);
                }
            }

        } catch (IOException e){
            e.printStackTrace();
        }

    }

    @Override
    public void close() throws IOException{
        connected = false;
        sock.close();
    }

    public void startProtocol(int connectionID, Connectionsimpl<T> connections){
        protocol.start(connectionID, connections);
    }

}
