package de.iqwrwq.core;

import de.iqwrwq.config.Config;

import java.io.*;
import java.net.Socket;

public class Kernel {

    private Config config;
    private final ShipServerConnectionThread shipServerConnectionThread;


    public Kernel(){
        this.config = new Config("config/config.properties");
        this.shipServerConnectionThread = new ShipServerConnectionThread(config);
    }

    public void connect(){
        shipServerConnectionThread.start();
    }

    public static void main(String[] args) {
        Kernel core = new Kernel();
        core.connect();
    }
}
