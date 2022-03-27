package server;

import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static server.Main.gson;

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
            dbMap = gson.fromJson(dbJson, type);
        } finally {
            rwl.readLock().unlock();
        }
        return dbMap;
    }

    public void set(String key, String value) {
        File dbFile = new File(Main.DB_PATH);
        String dbJson = "";
        Map<String, String> dbMap;
        rwl.writeLock().lock();
        try {
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
            dbMap.put(key, value);
            dbJson = gson.toJson(dbMap);
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
        String dbJson = "";
        Map<String, String> dbMap;
        rwl.writeLock().lock();
        try {
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
            String result = dbMap.remove(key);
            if (result == null) {
                return null;
            } else {
                dbJson = gson.toJson(dbMap);
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


}
