package de.iqwrwq.core;

import java.io.*;
import java.net.Socket;

public class ShipClient {

    private OutputStream outputStream;
    private InputStream inputStream;
    private PrintStream printStream;

    public ShipClient(String host, int port){
        try(Socket socket = new Socket(host, port)) {
            this.outputStream = socket.getOutputStream();
            this.printStream = new PrintStream(outputStream, true);


            this.inputStream = socket.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            while(bufferedReader.ready()){
                System.out.println(bufferedReader.readLine());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
