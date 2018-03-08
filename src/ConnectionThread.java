import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionThread extends Thread {

    private Socket clientSocket;
    private Socket serverSocket;

    ConnectionThread(Socket clientSocket, Socket serverSocket) {
        this.clientSocket = clientSocket;
        this.serverSocket = serverSocket;
    }

    public void run() {
        try {
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter outToClient = new PrintWriter(clientSocket.getOutputStream());

            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter outToServer = new PrintWriter(clientSocket.getOutputStream());

            String line, response;

            while ((line = inFromClient.readLine()) != null) {
                outToServer.println(line);
                outToServer.flush();

                response = inFromServer.readLine();

                if (response != null) {
                    outToClient.println(response);
                    outToClient.flush();
                }
            }

            clientSocket.close();
            serverSocket.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
