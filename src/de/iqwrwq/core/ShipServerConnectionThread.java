package de.iqwrwq.core;

import de.iqwrwq.config.Config;

import java.io.*;
import java.net.Socket;
import java.util.regex.Pattern;

public class ShipServerConnectionThread extends Thread {

    private static final int DEFAULT_SHIP_ID = 40404;

    public String company;
    public String harbour;
    public Cargo cargo;
    public int id;
    public PrintWriter printWriter;

    private final Config config;
    private final SeaTradeServerConnectionThread seaTradeServerConnectionThread;

    public ShipServerConnectionThread(Config config) {
        this.config = config;
        this.seaTradeServerConnectionThread = new SeaTradeServerConnectionThread(config, this);
        this.id = DEFAULT_SHIP_ID;
    }

    public void chargeCompany(int amount) {
        printWriter.println("charge -> " + amount);
    }

    public void sendMessage(String code) {
        printWriter.println(code);
    }

    @Override
    public void run() {
        int allowedConnectionAttempts = Integer.parseInt(config.getProperty("ConnectionAttempts"));
        for (int connectionAttempt = 0; connectionAttempt <= allowedConnectionAttempts; connectionAttempt++) {

            try (Socket shipServerSocket = connectShipToShipServer()) {
                registerToShipServer(shipServerSocket);
                communicateWithShipServer(shipServerSocket);
            } catch (IOException e) {
                try {
                    System.out.println("Ship -> Error -> ConnectionError |  " + config.host + " | " + config.shipServerPort);
                    System.out.println("Ship -> Reconnect");
                    sleep(10000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

            }
            System.out.println("Ship -> ConnectionTimeout");
        }
    }

    public void setCargo(String cargo_info) {
        this.cargo = new Cargo(cargo_info);
    }

    private void registerToShipServer(Socket shipServerSocket) throws IOException {
        this.printWriter = new PrintWriter(shipServerSocket.getOutputStream(), true);
        System.out.println("Ship -> ShipServer -> register");
        printWriter.println("register");
    }

    private void communicateWithShipServer(Socket shipServerSocket) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(shipServerSocket.getInputStream()));

        while (!shipServerSocket.isClosed()) {
            handleServerRequest(shipServerSocket, bufferedReader);
        }
    }

    private void handleServerRequest(Socket shipServerSocket, BufferedReader bufferedReader) throws IOException {
        String shipServerResponse;

        while (bufferedReader.ready()) {
            shipServerResponse = bufferedReader.readLine();
            System.out.println("ShipServer -> " + shipServerResponse);
            switch (shipServerResponse.split(" -> ")[0]) {
                case "registered" -> {
                    setId(Integer.parseInt(shipServerResponse.split(" -> ")[1].split(Pattern.quote(" | "))[0]));
                    System.out.println(shipServerResponse);
                    setHarbour(shipServerResponse.split(" -> ")[1].split(Pattern.quote(" | "))[1]);
                    setCompany(shipServerResponse.split(" -> ")[1].split(Pattern.quote(" | "))[2]);

                    printWriter.println("InitiateSeaTradeConnection");
                    seaTradeServerConnectionThread.start();
                }
                case "Error" -> {
                    System.out.println("Ship -> closing | LimitExceeded");
                    shipServerSocket.close();
                }
                case "move" -> {
                    seaTradeServerConnectionThread.printWriter.println("moveto:" + shipServerResponse.split(Pattern.quote(" -> "))[1]);
                }
                case "load" -> {
                    if (shipServerResponse.split(" ").length >= 2) {
                        seaTradeServerConnectionThread.printWriter.println("loadcargo:" + shipServerResponse.split(" ")[1]);
                    } else {
                        seaTradeServerConnectionThread.printWriter.println("loadcargo");
                    }
                }
                case "unload" -> {
                    seaTradeServerConnectionThread.printWriter.println("unloadcargo");
                }
                case "cargoinfo" -> {
                    if (cargo != null) {
                        printWriter.println(cargo.toString);
                    } else {
                        printWriter.println("noCargo");
                    }
                }
                case "removed" -> {
                    shipServerSocket.close();
                    System.out.println("Ship -> closing | ServerClosed");
                }
                default -> {
                    shipServerSocket.close();
                    System.out.println("Ship -> closing | UnknownError");
                }
            }
        }
    }

    private Socket connectShipToShipServer() throws IOException {
        Socket socket = new Socket(config.host, config.shipServerPort);
        System.out.println("Ship -> ShipServer -> connected");
        return socket;
    }

    private void setCompany(String name) {
        this.company = name;
    }

    private void setId(int id) {
        this.id = id;
        System.out.println("Ship -> setId | " + id);
    }

    private void setHarbour(String harbour) {
        this.harbour = harbour;
    }
}
