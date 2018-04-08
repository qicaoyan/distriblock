package com.ming.distriblock.startup;

import com.ming.distriblock.io.Server;

/**
 * Created by xueming on 2018/4/6.
 */
public class StartUp {
    public static void main(String[] args){
        int port = 3500;
        if(args != null && args.length > 0){
            port = Integer.parseInt(args[0]);
        }
        Server server = new Server(port);
        server.run();
    }
}
