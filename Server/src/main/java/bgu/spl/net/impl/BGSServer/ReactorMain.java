package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.srv.Server;

public class ReactorMain {
    public static void main(String[] args){
        if(args.length != 2) {
            System.out.println("Usage: ReactorMain port num_threads");
            return;
        }
        int port = Integer.parseInt(args[0]);
        int num_threads = Integer.parseInt(args[1]);
        DataBase base = new DataBase();
        Server.reactor(num_threads, port, () -> new BGSProtocol(base), BGSMessageEncoderDecoder::new).serve();
    }
}
