package de.iqwrwq.config;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Config extends Properties {
    public String host;
    public int shipServerPort;
    public int seaTradeServerPort;
    public String name;

    public Config(String path){
        try {
            this.load(new FileReader(path));
            this.host = getProperty("host");
            this.shipServerPort = Integer.parseInt(getProperty("shipServerPort"));
            this.seaTradeServerPort = Integer.parseInt(getProperty("seaTradeServerPort"));
            this.name = getProperty("name");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
