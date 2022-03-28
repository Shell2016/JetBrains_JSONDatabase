package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static server.Main.errorResponse;
import static server.Main.okResponse;

public class Worker implements Runnable {
    Resource db;
    DataOutputStream output;
    Request request;

    public Worker(Resource db, DataOutputStream output, Request request) {
        this.output = output;
        this.db = db;
        this.request = request;
    }

    @Override
    public void run() {
        try  {
            Map<String, String> response = new HashMap<>();
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
                    okResponse(response, output);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
