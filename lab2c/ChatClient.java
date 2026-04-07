import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class ChatClient {
    BufferedReader in;
    PrintWriter out;
    JFrame frame = new JFrame("Chat App");
    JTextField textField = new JTextField(40);
    JTextArea messageArea = new JTextArea(8, 40);

    public ChatClient() {
        // Layout GUI
        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, "North");
        frame.getContentPane().add(new JScrollPane(messageArea), "Center");
        frame.pack();

        // Add Listeners
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());
                textField.setText("");
            }
        });
    }

    private void run() throws IOException {
        // Connect to the server
        String serverAddress = "127.0.0.1"; // Localhost
        Socket socket = new Socket(serverAddress, 12345);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Enable typing
        textField.setEditable(true);

        // Process all messages from server
        while (true) {
            String line = in.readLine();
            if (line == null) {
                break;
            }
            messageArea.append(line + "\n");
        }
    }

    public static void main(String[] args) throws Exception {
        ChatClient client = new ChatClient();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }
}