import java.io.*;
import java.net.*;

public class WebDownloader {
    public static void main(String[] args){
        String hostname = "www.google.com";
        int port = 80;
        
        try (Socket socket = new Socket(hostname, port)){
            // Create output stream to send request
            OutputStream out = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(out, true);

            writer.println("GET / HTTP/1.1");
            writer.println("Host: " + hostname);
            writer.println("");
            writer.println("");

            // Create input stream to read response
            InputStream in = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            System.out.println("--- Response from Server ---");
            while((line = reader.readLine()) != null){
                System.out.println(line);
            }
        }catch (UnknownHostException ex){
            System.out.println("Server not found: " + ex.getMessage());
    }catch (IOException ex){
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}