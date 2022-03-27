package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static server.Main.*;

public class Worker implements Runnable {
    Socket socket;
    Resource db;


    public Worker(Socket socket, Resource db) {
        this.socket = socket;
        this.db = db;
    }

    @Override
    public void run() {
        try  {
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
//            try {
//                Thread.sleep(20000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            String requestJson = input.readUTF();
            Request request = gson.fromJson(requestJson, Request.class);
            Map<String, String> response = new HashMap<>();
            if ("exit".equals(request.getType())) {
                okResponse(response, output);
                System.exit(0);
            }

            switch (request.getType()) {
                case "get":
                    Map<String, String> dbMap = db.read();
                    if (dbMap.get(request.getKey()) == null) {
                        errorResponse(response, output);
                    } else {
                        response.put("value", dbMap.get(request.getKey()));
                        okResponse(response, output);
                    }
                    break;
                case "delete":
                    if (db.delete(request.getKey()) == null) {
                        errorResponse(response, output);
                    } else {
                        okResponse(response, output);
                    }
                    break;
                case "set":
                    db.set(request.getKey(), request.getValue());
//                    dbMap.put(request.getKey(), request.getValue());
                    okResponse(response, output);
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
