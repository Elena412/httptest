package ru.n1ks;

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

@SuppressWarnings("FieldCanBeLocal")
final class XMessage {
    private DocumentBuilderFactory dbf;
    private DocumentBuilder db;

    private String type = null;
    private String data = null;

    public String getData() {
        return data;
    }

    public String getType() {
        return type;
    }

    public XMessage(InputStream is) throws ParserConfigurationException, IOException, SAXException {
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
        dbf = DocumentBuilderFactory.newInstance();
        db = dbf.newDocumentBuilder();
        Main.showMessage(": try to parse...");
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

    public String getReplyEncoded() throws TransformerException, UnsupportedEncodingException {
        if (type == null || data == null)
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
        Main.showMessage("<----------------xml data begins--------------------");
        Main.showMessage(writer.toString());
        Main.showMessage("<----------------xml data ends----------------------");
        return URLEncoder.encode(writer.toString(), "UTF-8");
    }
}
