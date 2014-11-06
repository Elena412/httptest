package ru.n1ks;

import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;

public class Main {
    private static PrintWriter pw = new PrintWriter(System.out,true);

    static synchronized public void showMessage(String msg){
        pw.println(msg);
    }

    public static void main(String[] args) throws IOException {

        showMessage("Welcome to HTTP Echo Server");
        showMessage("-----------------------------------");
        HttpServer server = HttpServer.create(new InetSocketAddress(19910),0);
        server.createContext("/httpecho", new HttpEchoHandler());
        server.setExecutor(null);
        showMessage("server ready for starting...");
        server.start();
        showMessage("...server started.");
        showMessage("-----------------------------------");
        showMessage("Debug information below; x for exit");
        showMessage("-----------------------------------");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        for (;;) {
            String str = br.readLine();
            if (str.toLowerCase().equals("x")) {
                break;
            }
        }
        showMessage("-----------------------------------");
        showMessage("Server stopping...");
        server.stop(3);
        showMessage("...server stopped.");
        showMessage("Bye.");
    }
}
