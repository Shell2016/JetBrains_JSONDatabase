package server;

import com.google.gson.Gson;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Response {
    private Map<String, String> response = new LinkedHashMap<>();

    public void okResponse(DataOutputStream output) throws IOException {
        response.put("response", "OK");
        output.writeUTF(new Gson().toJson(response));
    }

    public void errorResponse(DataOutputStream output) throws IOException {
        response.put("response", "ERROR");
        response.put("reason", "No such key");
        output.writeUTF(new Gson().toJson(response));
    }

    public void getResponse(DataOutputStream output, String key) throws  IOException {
        response.put("response", "OK");
        response.put("value", key);
        output.writeUTF(new Gson().toJson(response));
    }
}
