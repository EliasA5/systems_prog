package bgu.spl.net.impl.echo;

import bgu.spl.net.srv.Server;

public class EchoServer {

    public static void main(String[] args){
        Server.reactor(3, 7777, () -> new EchoProtocol(), LineMessageEncoderDecoder::new).serve();
    }

}
