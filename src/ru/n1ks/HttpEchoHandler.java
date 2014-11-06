package ru.n1ks;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import jdk.nashorn.internal.runtime.ParserException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class HttpEchoHandler implements HttpHandler {

    public void handle(HttpExchange t) throws IOException{
        String response;
        if (t.getRequestMethod().equals("POST")){
            Main.showMessage(": POST request from " + t.getRemoteAddress());
            try {
                InputStream is = t.getRequestBody();
                BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = br.readLine()) != null){
                    sb.append(line);
                }
                String xml = URLDecoder.decode(sb.toString(), "UTF-8");
                Main.showMessage(">----------------xml data begins--------------------");
                Main.showMessage(xml);
                Main.showMessage(">----------------xml data ends----------------------");
                is = new ByteArrayInputStream(xml.getBytes());
                response = xmlRead(is);
                Main.showMessage("<----------------xml data begins--------------------");
                Main.showMessage(response);
                Main.showMessage("<----------------xml data ends----------------------");
                response = URLEncoder.encode(response,"UTF-8");
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
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Main.showMessage(": try to parse...");
        Document xmlDoc = db.parse(xmldata);
        Main.showMessage(": ...done.");
        Node node = xmlDoc.getDocumentElement();
        NodeList nl = node.getChildNodes();
        String type = null;
        String data = null;
        for (int i = 0; i < nl.getLength(); i++){
            Node cnode = nl.item(i);
            if (cnode.getNodeName().equals("Type")){
                type = cnode.getTextContent();
            }
            if (cnode.getNodeName().equals("Data")){
                data = cnode.getTextContent();
            }
        }
        if (type == null || data == null)
            throw new ParserException("Invalid xml.");

        Document resDoc = db.newDocument();
        Element root = resDoc.createElement("Response");
        resDoc.appendChild(root);
        Element resData = resDoc.createElement("Data");
        resData.setTextContent(type + ": " + data);
        root.appendChild(resData);
        DOMSource domSource = new DOMSource(resDoc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(domSource, result);
        return writer.toString();
    }
}
