package de.iqwrwq.core;

import de.iqwrwq.config.Config;

import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionThread extends Thread{
    private Config config;
    public PrintWriter printWriter;
    private Socket socket;

}
