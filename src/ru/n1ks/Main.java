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
        showMessage("Debug information below; x for exit, y for send");
        showMessage("---------------------------------------------------");
        XHttpServer server = new XHttpServer(19910);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        for (;;) {
            String str = br.readLine();
            if (str.toLowerCase().equals("x")) {
                break;
            }
            if (str.toLowerCase().equals("y")){
                showMessage("Enter URL:");
                String url = br.readLine();
                showMessage("Enter method:");
                String method = br.readLine();
                showMessage("Enter data:");
                String data = br.readLine();
                try {
                    XHttpClient client = new XHttpClient(url, method, data);
                    String ret = client.request();
                    showMessage("---------------------------------------------------");
                    showMessage("RET " + ret);
                    showMessage("---------------------------------------------------");
                } catch (IOException e){
                    showMessage("ERR: " + e.getMessage());
                }

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
