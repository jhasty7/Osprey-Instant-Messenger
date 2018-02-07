
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

class ClientListener implements Runnable {

    //server handler
    private Server myServer;

    //server variables and objects
    private ServerSocket serverSocket;
    private int PORT_NUMBER = 45566;
    private boolean isServerReady = false;

    ClientListener(Server server) {
        this.myServer = server;
        try {
            serverSocket = new ServerSocket(PORT_NUMBER);
        }
        catch (IOException ex) {
        }
    }

    @Override
    public void run() {
        
        try {
            isServerReady = true;
            myServer.writeToConsole("Listening on port " + PORT_NUMBER);
            do {
                Socket client = serverSocket.accept();
                myServer.writeToConsole("Connect received from" + client.getInetAddress());
                /*
                // pass the client over to the login/register and the control of the server
                LoginOrRegisterClient login = new LoginOrRegisterClient(client,myServer);
                
                Thread nt = new Thread(login);
                nt.start();
                */
                ClientHandler clientHandler = new ClientHandler(client, myServer);
                new Thread(clientHandler).start();
                
            } while (isServerReady);
        }
        catch (IOException e) {
            myServer.writeToConsole("Exception in PacketListener class");
            isServerReady = false;
        }
        
        myServer.stopServer();

    }

}
