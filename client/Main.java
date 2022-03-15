package client;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;


public class Main {
    public static final String ADDRESS = "127.0.0.1";
    public static final int PORT = 23456;

    @Parameter(names = "-t", description = "Type of the request")
    private String requestType = "";

    @Parameter(names = "-i", description = "Index of the cell")
    private String cellIndex = "";

    @Parameter(names = "-m", description = "Value to save in DB")
    private String valueToSave = "";

    public static void main(String[] args) {
        Main main = new Main();
        JCommander.newBuilder()
                .addObject(main)
                .build()
                .parse(args);
        main.run();
    }

    public void run() {
        try (Socket socket = new Socket(InetAddress.getByName(ADDRESS), PORT);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {
            System.out.println("Client started!");
            String command = String.format("%s %s %s", requestType, cellIndex, valueToSave).trim();
            output.writeUTF(command);
            System.out.println("Sent: " + command);
            String msgReceived = input.readUTF();
            System.out.println("Received: " + msgReceived);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
