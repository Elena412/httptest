package ru.n1ks;

import jdk.nashorn.internal.runtime.ParserException;
import org.json.simple.*;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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

final class XMap {

}

@SuppressWarnings("FieldCanBeLocal")
final class XMessage {
    private DocumentBuilderFactory dbf;
    private DocumentBuilder db;

    private boolean parsed = false;

    /* xml in */
    private String type = null;
    private String data = null;

    /* json in */
    private String status = null;
    private String position = null;
    private String sizeX = null;
    private String sizeY = null;
    private String squareSize = null;
    private XMap map = null;

    private void XMLParse(InputStream is) throws ParserConfigurationException, IOException, SAXException {
        dbf = DocumentBuilderFactory.newInstance();
        db = dbf.newDocumentBuilder();
        Main.showMessage(": try to parse xml...");
        Document xmlDoc = db.parse(is);
        Main.showMessage(": ...done.");
        Node node = xmlDoc.getDocumentElement();
        NodeList nl = node.getChildNodes();
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

    }

    public void JSONParse(InputStream is) throws IOException, ParseException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null){
            sb.append(line);
        }
        JSONParser parser = new JSONParser();
        JSONObject doc = (JSONObject) parser.parse(sb.toString());

        /*TODO try to parse STATUS ret*/

    }

    public XMessage(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null){
            sb.append(line);
        }
        String xml = URLDecoder.decode(sb.toString(), "UTF-8");
        Main.showMessage(">--------------------data begins--------------------");
        Main.showMessage(xml);
        Main.showMessage(">--------------------data ends----------------------");
        is = new ByteArrayInputStream(xml.getBytes());
        try {
            XMLParse(is);
        } catch (ParserConfigurationException | SAXException e) {
            Main.showMessage(e.getMessage());
            return;
        }
        parsed = true;
    }

    public String getReplyEncoded() throws TransformerException, UnsupportedEncodingException {
        if (!parsed)
            return null;
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
        Main.showMessage("<--------------------data begins--------------------");
        Main.showMessage(writer.toString());
        Main.showMessage("<--------------------data ends----------------------");
        return URLEncoder.encode(writer.toString(), "UTF-8");
    }
}
