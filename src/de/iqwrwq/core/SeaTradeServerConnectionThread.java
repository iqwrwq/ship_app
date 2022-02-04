package de.iqwrwq.core;

import de.iqwrwq.config.Config;

import java.io.*;
import java.net.Socket;
import java.util.regex.Pattern;

public class SeaTradeServerConnectionThread extends Thread {

    public String companyName;
    public PrintWriter printWriter;
    private Config config;
    private ShipServerConnectionThread shipServerConnectionThread;
    private Socket socket;

    public SeaTradeServerConnectionThread(Config config, ShipServerConnectionThread shipServerConnectionThread) {
        this.config = config;
        this.shipServerConnectionThread = shipServerConnectionThread;
    }

    @Override
    public void run() {
        try  {
            this.socket = new Socket(config.host, config.seaTradeServerPort);
            this.printWriter = new PrintWriter(socket.getOutputStream(), true);
            printWriter.println("launch:" + shipServerConnectionThread.company + ":" + shipServerConnectionThread.harbour + ":Ship" + shipServerConnectionThread.id + (int) (Math.random() * (999 - 1 + 1) + 1));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String seaTradeResponse;
            while (!socket.isClosed()) {
                while (bufferedReader.ready()) {
                    seaTradeResponse = bufferedReader.readLine();
                    System.out.println("SeaTrade -> " + seaTradeResponse);
                    switch (seaTradeResponse.split(":")[0]) {
                        case "launched" -> {
                            shipServerConnectionThread.sendMessage("LaunchedOnSeaTrade");
                            shipServerConnectionThread.chargeCompany(-Integer.parseInt(seaTradeResponse.split(":")[2]));
                        }
                        case "moved" -> {
                            shipServerConnectionThread.chargeCompany(-Integer.parseInt(seaTradeResponse.split(":")[2]));
                        }
                        case "loaded" -> {
                            shipServerConnectionThread.setCargo(seaTradeResponse.split(Pattern.quote(":"))[1]);
                            shipServerConnectionThread.printWriter.println("loaded -> " + shipServerConnectionThread.cargo.toString);
                        }
                        case "unloaded" -> {
                            shipServerConnectionThread.chargeCompany(Integer.parseInt(seaTradeResponse.split(Pattern.quote(":"))[1]));
                            shipServerConnectionThread.printWriter.println("unload");
                        }
                        case "reached" -> {
                            shipServerConnectionThread.printWriter.println("reach -> " + seaTradeResponse.split(Pattern.quote(":"))[1]);
                        }
                        case "error" -> {
                            shipServerConnectionThread.printWriter.println(seaTradeResponse);
                        }
                        default -> {
                            System.out.println(seaTradeResponse);
                        }


                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Cannot Connect to " + config.host + "with port " + config.seaTradeServerPort);
            e.printStackTrace();
        }
    }

    public void exit(){
        try {
            printWriter.println("exit");
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCompanyName(String name) {
        this.companyName = name;
    }
}
