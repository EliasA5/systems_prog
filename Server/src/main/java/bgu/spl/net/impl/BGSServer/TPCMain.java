package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.srv.Server;

public class TPCMain {

    public static void main(String[] args){
        if(args.length != 1) {
            System.out.println("Usage: ReactorMain port num_threads");
            return;
        }
        int port = Integer.parseInt(args[0]);
        DataBase base = new DataBase();
        Server.threadPerClient(port, () -> new BGSProtocol(base), BGSMessageEncoderDecoder::new).serve();
    }

}
