package server;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main {
    public static final String ADDRESS = "127.0.0.1";
    public static final int PORT = 23456;

    public static void main(String[] args) {
        Map<String, String> db = new HashMap<>();
        try (ServerSocket server = new ServerSocket(PORT, 50, InetAddress.getByName(ADDRESS))) {
            System.out.println("Server started!");
            while (true) {
                try (Socket socket = server.accept();
                     DataInputStream input = new DataInputStream(socket.getInputStream());
                     DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {

                    String requestJson = input.readUTF();
                    Request request = makeRequestFromJson(requestJson);
                    Map<String, String> response = new LinkedHashMap<>();
                    switch (request.getType()) {
                        case "exit":
                            okResponse(response, output);
                            System.exit(0);
                        case "get":
                            if (db.get(request.getKey()) == null) {
                                errorResponse(response, output);
                            } else {
                                response.put("value", db.get(request.getKey()));
                                okResponse(response, output);
                            }
                            break;
                        case "delete":
                            if (db.remove(request.getKey()) == null) {
                                errorResponse(response, output);
                            } else {
                                okResponse(response, output);
                            }
                            break;
                        case "set":
                            db.put(request.getKey(), request.getValue());
                            okResponse(response, output);
                            break;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Request makeRequestFromJson(String requestJson) {
        Gson gson = new Gson();
        return gson.fromJson(requestJson, Request.class);
    }

    public static String makeJsonResponse(Map<String, String> response) {
        Gson gson = new Gson();
        return gson.toJson(response);
    }

    public static void okResponse(Map<String, String> response, DataOutputStream output) throws IOException {
        response.put("response", "OK");
        output.writeUTF(makeJsonResponse(response));
    }

    public static void errorResponse(Map<String, String> response, DataOutputStream output) throws IOException {
        response.put("response", "ERROR");
        response.put("reason", "No such key");
        output.writeUTF(makeJsonResponse(response));
    }

}
