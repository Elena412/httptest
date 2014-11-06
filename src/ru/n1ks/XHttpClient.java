package ru.n1ks;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

final class XHttpClient {
    private String data;
    private HttpURLConnection c = null;

    public XHttpClient(String url,String httpMethod,String data) throws IOException {
        URL url1 = new URL(url);
        c = (HttpURLConnection) url1.openConnection();
        c.setRequestMethod(httpMethod);
        if (httpMethod.equals("POST")){
            c.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
        }
        c.setDoOutput(true);
        c.setRequestProperty("User-Agent","Mozilla/5.0");
        this.data = URLEncoder.encode(data,"UTF-8");
        Main.showMessage("----------------out data begins--------------------");
        Main.showMessage(this.data);
        Main.showMessage("----------------out data ends----------------------");
    }

    public String request() throws IOException {
        DataOutputStream ds = new DataOutputStream(c.getOutputStream());
        ds.writeBytes(data);
        ds.flush();
        ds.close();
        Main.showMessage("^ sending HTTP request...");
        int retCode = c.getResponseCode();
        Main.showMessage("^ ...response code " + retCode);
        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null){
            sb.append(line);
        }
        br.close();
        String response = sb.toString();
        try {
            response = URLDecoder.decode(sb.toString(), "UTF-8");
        } catch (IllegalArgumentException e){
            Main.showMessage("ERR: " + e.getMessage());
        }
        Main.showMessage("----------------out data begins--------------------");
        Main.showMessage(response);
        Main.showMessage("----------------out data ends----------------------");
        return response;
    }
}
