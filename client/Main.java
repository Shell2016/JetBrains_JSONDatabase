package client;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


public class Main {
    public static final String ADDRESS = "127.0.0.1";
    public static final int PORT = 23456;

    @Parameter(names = "-t", description = "Type of the request")
    private String requestType;

    @Parameter(names = "-k", description = "Index of the cell")
    private String key;

    @Parameter(names = "-v", description = "Value to save in DB")
    private String value;

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
            Map<String, String> request = new HashMap<>();
            request.put("type", requestType);
            request.put("key", key);
            if (value != null) {
                request.put("value", value);
            }
            Gson gson = new Gson();
            String requestJson = gson.toJson(request);
            output.writeUTF(requestJson);
            System.out.println("Sent: " + requestJson);
            String msgReceived = input.readUTF();
            System.out.println("Received: " + msgReceived);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
