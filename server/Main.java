package server;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {
    public static final String ADDRESS = "127.0.0.1";
    public static final int PORT = 23456;
    public static Gson gson = new Gson();
//    короткий адрес для проверки jetbrains, длинный для своего компа
    public static final String DB_PATH = System.getProperty("user.dir") + "/src/server/data/db.json";
//    public static final String DB_PATH = System.getProperty("user.dir") + "/JSON Database/task/src/server/data/db.json";

    public static void main(String[] args) {
        ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
        Resource db = new Resource(rwl);
        ExecutorService executor = Executors.newFixedThreadPool(4);

        try {
            ServerSocket server = new ServerSocket(PORT, 50, InetAddress.getByName(ADDRESS));
            System.out.println("Server started!");
            while (true) {
                try {
                    Socket socket = server.accept();
                    DataInputStream input = new DataInputStream(socket.getInputStream());
                    DataOutputStream output = new DataOutputStream(socket.getOutputStream());

                    String requestJson = input.readUTF();
                    Request request = gson.fromJson(requestJson, Request.class);
                    Map<String, String> response = new HashMap<>();
                    if ("exit".equals(request.getType())) {
                        okResponse(response, output);
                        socket.close();
                        server.close();
                        System.exit(0);
                    }
                    executor.submit(new Worker(db, output, request));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void okResponse(Map<String, String> response, DataOutputStream output) throws IOException {
        response.put("response", "OK");
        output.writeUTF(gson.toJson(response));
    }

    public static void errorResponse(Map<String, String> response, DataOutputStream output) throws IOException {
        response.put("response", "ERROR");
        response.put("reason", "No such key");
        output.writeUTF(gson.toJson(response));
    }

}
