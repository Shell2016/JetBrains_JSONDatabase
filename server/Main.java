package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static final String ADDRESS = "127.0.0.1";
    public static final int PORT = 23456;

    public static void main(String[] args) {
        String[] db = new String[1000];
        Arrays.fill(db, "");
        try (ServerSocket server = new ServerSocket(PORT, 50, InetAddress.getByName(ADDRESS))) {
            System.out.println("Server started!");
            while (true) {
                try (Socket socket = server.accept();
                     DataInputStream input = new DataInputStream(socket.getInputStream());
                     DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {

                    String command = input.readUTF();
                    List<String> commandList = Arrays.asList(command.split(" "));
                    String requestType = commandList.get(0);
                    int cellIndex;
                    switch (requestType) {
                        case "exit":
                            output.writeUTF("OK");
                            System.exit(0);
                        case "get":
                            cellIndex = Integer.parseInt(commandList.get(1));
                            if (cellIndex < 1 || cellIndex > 1000 || db[cellIndex - 1].equals("")) {
                                output.writeUTF("ERROR");
                            } else {
                                output.writeUTF(db[cellIndex - 1]);
                            }
                            break;
                        case "delete":
                            cellIndex = Integer.parseInt(commandList.get(1));
                            if (cellIndex < 1 || cellIndex > 1000) {
                                output.writeUTF("ERROR");
                            } else {
                                db[cellIndex  - 1] = "";
                                output.writeUTF("OK");
                            }
                            break;
                        case "set":
                            cellIndex = Integer.parseInt(commandList.get(1));
                            if (cellIndex < 1 || cellIndex > 1000) {
                                output.writeUTF("ERROR");
                                break;
                            }
                            StringBuilder sb = new StringBuilder();
                            for (String word : commandList.subList(2, commandList.size())) {
                                sb.append(word);
                                sb.append(" ");
                            }
                            db[cellIndex - 1] = sb.toString().trim();
                            output.writeUTF("OK");
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

}
