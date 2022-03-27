package server;

import com.google.gson.Gson;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {
    public static final String ADDRESS = "127.0.0.1";
    public static final int PORT = 23456;
    public static Gson gson = new Gson();
    public static final String DB_PATH = "./JSON Database/task/src/server/data/db.json";

    public static void main(String[] args) {
        ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
//        File dbFile = new File(DB_PATH);
//        String dbJson = "";
//        try {
//            dbJson = new String(Files.readAllBytes(dbFile.toPath()));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Type type = new TypeToken<HashMap<String, String>>(){}.getType();
//        Map<String, String> dbMap = gson.fromJson(dbJson, type);


        ExecutorService executor = Executors.newCachedThreadPool();

        try (ServerSocket server = new ServerSocket(PORT, 50, InetAddress.getByName(ADDRESS))) {
            System.out.println("Server started!");
            while (true) {
                try {
                    Socket socket = server.accept();
                    System.out.println("New client connected");
                    Resource db = new Resource(rwl);
                    executor.execute(new Worker(socket, db));

                } catch (IOException e) {
                    e.printStackTrace();
                }

//                try (Socket socket = server.accept();
//                     DataInputStream input = new DataInputStream(socket.getInputStream());
//                     DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {
//
//                    String requestJson = input.readUTF();
//                    Request request = gson.fromJson(requestJson, Request.class);
//                    Map<String, String> response = new HashMap<>();
//                    switch (request.getType()) {
//                        case "exit":
//                            okResponse(response, output);
//                            System.exit(0);
//                        case "get":
//                            if (dbMap.get(request.getKey()) == null) {
//                                errorResponse(response, output);
//                            } else {
//                                response.put("value", dbMap.get(request.getKey()));
//                                okResponse(response, output);
//                            }
//                            break;
//                        case "delete":
//                            if (dbMap.remove(request.getKey()) == null) {
//                                errorResponse(response, output);
//                            } else {
//                                okResponse(response, output);
//                            }
//                            break;
//                        case "set":
//                            dbMap.put(request.getKey(), request.getValue());
//                            okResponse(response, output);
//                            break;
//                    }
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static Request makeRequestFromJson(String requestJson) {
//        return gson.fromJson(requestJson, Request.class);
//    }

//    public static String makeJsonResponse(Map<String, String> response) {
//        return gson.toJson(response);
//    }

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
