package edu.escuelaing.arep;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class WebServer {
    public static final WebServer _instance = new WebServer();
    private static final String USER_AGENT = "Mozilla/5.0";

    private static WebServer getInstance(){

        return _instance;
    }
    private WebServer(){}

    public void start(String[] args, int port) throws IOException, URISyntaxException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        boolean running = true;
        while(running){
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            serveConnection(clientSocket);
        }
        serverSocket.close();
    }

    public void serveConnection(Socket clientSocket) throws IOException, URISyntaxException {
        OutputStream outputStream;
        outputStream = clientSocket.getOutputStream();
        PrintWriter out = new PrintWriter(outputStream, true);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        clientSocket.getInputStream()));
        String inputLine, outputLine;
        ArrayList<String> request = new ArrayList<String>();

        while ((inputLine = in.readLine()) != null) {
            //System.out.println("Received: " + inputLine);
            request.add(inputLine);
            if (!in.ready()) {
                break;
            }
        }

        try{
            String uriStr= request.get(0).split(" ")[1];
            System.out.println("uriStr: "+uriStr);
            URI resourceURI = new URI(uriStr);
            if(uriStr.equals("/clima")){
                out.println(computeDefaultResponse());
            }
            else{
                outputLine = getResource(resourceURI);
                out.println(outputLine);
            }
        }
        catch (Exception e){
            out.println(computeDefaultResponse());
        }
        out.close();
        in.close();
        clientSocket.close();
    }

    public String getResource(URI resourceURI) throws IOException {
        System.out.println("Received URI path: "+resourceURI.getPath());
        System.out.println("Received URI query: "+resourceURI.getQuery());
        return getTextResource(resourceURI.getPath());
    }

    public String getTextResource(String extent) throws IOException{
        String responseStr = "none";
        System.out.println("extención: "+extent);
        URL obj = new URL("https://api.openweathermap.org/data/2.5/weather?q=London&appid=8805fcd7d0439565c8dda8eb5eedbbad");
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        BufferedReader in = new BufferedReader(new InputStreamReader(
                con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        responseStr = response.toString();
        // print result
        System.out.println(responseStr);
        return responseStr;
    }

    public String typeExt(String path){
        String[] ext = path.split("\\.");
        return ext[1];
    }

    public String computeDefaultResponse(){
        String outputLine = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<meta charset=\"UTF-8\">"
                + "<title>Title of the document</title>\n"
                + "<script src=\"https://ajax.aspnetcdn.com/ajax/jQuery/jquery-3.4.1.min.js\"></script>"
                + "</head>"
                + "<body>"
                + "<h1>My Web Site</h1>"
                + "<br>"
                + "<input id=\"entrada\" type=\"text\" name=\"Ciudad\" placeholder=\"Ciudad\">"
                + "<button type=\"button\" class=\"button\" onclick=\"app.getIntraday($('#entrada').val())\">Click me</button>"
                + "<br>"
                + "<table>"
				+ "<tbody>"
				+ "<tr>"
				+	"<th> Climate </th>"
				+	"<td> null </td>"
				+"</tr>"
                +"<tr>"
				+	"<th> Description </th>"
				+	"<td> null </td>"
				+ "</tr>"
				+ "</tbody>"
                + "</table>"
                + "</body>"
                + "</html>";
        return outputLine;
    }
}
