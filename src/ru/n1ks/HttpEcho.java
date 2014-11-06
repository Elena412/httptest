package ru.n1ks;

public class HttpEcho extends Thread {
    public volatile boolean isAlive = false;

    public HttpEcho(){
        super();
        isAlive = true;
        start();
    }

    @Override
    public void run(){

        isAlive = false;
    }
}
