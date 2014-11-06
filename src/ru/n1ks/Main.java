package ru.n1ks;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

class Main {
    private static final PrintWriter pw = new PrintWriter(System.out,true);

    static synchronized public void showMessage(String msg){
        pw.println(msg);
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        showMessage("Welcome to HTTP Server");
        showMessage("---------------------------------------------------");
        showMessage("Debug information below; x for exit");
        showMessage("---------------------------------------------------");
        XHttpServer server = new XHttpServer(19910);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        for (;;) {
            String str = br.readLine();
            if (str.toLowerCase().equals("x")) {
                break;
            }
        }
        showMessage("---------------------------------------------------");
        showMessage("Server stopping...");
        server.serverStop();
        server.join();
        showMessage("...server stopped.");
        showMessage("Bye.");
    }
}
