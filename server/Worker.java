package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;


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
            Response response = new Response();
            switch (request.getType()) {
                case "get":
                    Map<String, String> dbMap = db.read();
                    if (dbMap.get(request.getKey()) == null) {
                        response.errorResponse(output);
                    } else {
                        response.getResponse(output, dbMap.get(request.getKey()));
                    }
                    break;
                case "delete":
                    if (db.delete(request.getKey()) == null) {
                        response.errorResponse(output);
                    } else {
                        response.okResponse(output);
                    }
                    break;
                case "set":
                    db.set(request.getKey(), request.getValue());
                    response.okResponse(output);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
