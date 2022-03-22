package de.iqwrwq.core;

import de.iqwrwq.config.Config;

public class Core {

    private Config config;
    private final ShipServerConnectionThread shipServerConnectionThread;

    public Core(){
        this.config = new Config("config/config.properties");
        this.shipServerConnectionThread = new ShipServerConnectionThread(config);
    }

    public void connect(){
        shipServerConnectionThread.start();
    }

    public static void main(String[] args) {
        Core core = new Core();
        core.connect();
    }
}
