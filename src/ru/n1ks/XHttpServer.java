package ru.n1ks;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

@SuppressWarnings("SynchronizeOnNonFinalField")
final class XHttpServer extends Thread {
    private volatile Boolean isAlive = false;

    private HttpServer server = null;

    public XHttpServer(int port) throws IOException {
        super();
        server = HttpServer.create(new InetSocketAddress(port),0);
        server.createContext("/xmlgate", new XHttpServerHandler());
        server.setExecutor(null);
        Main.showMessage("XHttpServer ready for starting...");
        start();
        Main.showMessage("...XHttpServer started.");
    }

    @Override
    public void finalize() throws Throwable {
        super.finalize();
        if (server != null)
            server.stop(0);
    }

    public void serverStop(){
        synchronized (isAlive) {
            isAlive.notifyAll();
        }
    }

    public void run(){
        isAlive = true;
        server.start();
        try {
            synchronized (isAlive) {
                isAlive.wait();
            }
        } catch (InterruptedException ignored) {}
        server.stop(3);
        server = null;
        isAlive = false;
    }
}
