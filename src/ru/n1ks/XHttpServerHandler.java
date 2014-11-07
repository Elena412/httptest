package ru.n1ks;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;

final class XHttpServerHandler implements HttpHandler {

    public void handle(HttpExchange t) throws IOException{
        String response;
        if (t.getRequestMethod().equals("POST")){
            Main.showMessage(": POST request from " + t.getRemoteAddress());
            try {
                InputStream is = t.getRequestBody();
                response = xmlRead(is);
                t.sendResponseHeaders(200,response.length());
                Main.showMessage(": valid data from " + t.getRemoteAddress());
            } catch (ParserConfigurationException | SAXException | TransformerException e) {
                response = e.getMessage();
                Main.showMessage(": invalid data from " + t.getRemoteAddress());
                t.sendResponseHeaders(400, response.length());
            }
        } else {
            Main.showMessage(": " + t.getRequestMethod() + " request from " + t.getRemoteAddress());
            response = "Bad method; POST only allowed";
            t.sendResponseHeaders(400, response.length());
        }
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
        Main.showMessage(": response for " + t.getRemoteAddress() + " send.");
    }

    private String xmlRead(InputStream xmldata) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        XMessage message = new XMessage(xmldata);
        String ret = message.getReplyEncoded();
        return ret;
    }
}
