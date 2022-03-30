package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Resource {
    private final ReentrantReadWriteLock rwl;

    public Resource(ReentrantReadWriteLock rwl) {
        this.rwl = rwl;
    }

    public Map<String, String> read() {
        File dbFile = new File(Main.DB_PATH);
        String dbJson = "";
        Map<String, String> dbMap;
        rwl.readLock().lock();
        try {
            try {
                dbJson = new String(Files.readAllBytes(dbFile.toPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Type type = new TypeToken<HashMap<String, String>>(){}.getType();
            dbMap = new Gson().fromJson(dbJson, type);
            if (dbMap == null)
                return new HashMap<>();
        } finally {
            rwl.readLock().unlock();
        }
        return dbMap;
    }

    public void set(String key, String value) {
        File dbFile = new File(Main.DB_PATH);
        String dbJson;
        Map<String, String> dbMap;
        rwl.writeLock().lock();
        try {
            dbMap = getMap(dbFile);
            dbMap.put(key, value);
            dbJson = new Gson().toJson(dbMap);
            try (FileWriter writer = new FileWriter(dbFile)) {
                writer.write(dbJson);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            rwl.writeLock().unlock();
        }
    }

    public String delete(String key) {
        File dbFile = new File(Main.DB_PATH);
        String dbJson;
        Map<String, String> dbMap;
        rwl.writeLock().lock();
        try {
            dbMap = getMap(dbFile);
            String result = dbMap.remove(key);
            if (result == null) {
                return null;
            } else {
                dbJson = new Gson().toJson(dbMap);
                try (FileWriter writer = new FileWriter(dbFile)) {
                    writer.write(dbJson);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
            }
        } finally {
            rwl.writeLock().unlock();
        }
    }

    private Map<String, String> getMap(File dbFile) {
        Gson gson = new Gson();
        Map<String, String> dbMap;
        String dbJson = "";
        try {
            dbJson = new String(Files.readAllBytes(dbFile.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Type type = new TypeToken<HashMap<String, String>>(){}.getType();
        dbMap = gson.fromJson(dbJson, type);
        if (dbMap == null) {
            dbMap = new HashMap<>();
        }
        return dbMap;
    }


}
