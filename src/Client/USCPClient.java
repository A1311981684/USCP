package Client;

import Utils.DPRunnable;
import Utils.DataPackage;
import Utils.Sender;
import Utils.log;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Client construction：（Similar to Server's）
 * 1、Socket socket = new Socket(String serverIP, int serverPort);//Connect to the specific server
 * 2、BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(),
 * StandardCharsets.UTF_8));// UTF-8: encoding method, optional
 * 3、String temp；
 * 4、while((temp = br.readLine()) != null) {  // Blocked an read message
 * //temp is the message received from another Socket(in this case, that would be the server)
 * //TODO Write your code here
 * }
 */
public class USCPClient {
    private Socket socket;          //Client socket
    private String serverIp;        //ServerIp
    private int serverPort;         //Server Port
    private DPRunnable runnable;    //What to do with received string data

    //Construct a USCP client: pass in Server address, port number and task for received data
    public USCPClient(String serverIP, int serverPort, DPRunnable dpRunnable) {
        this.serverIp = serverIP;
        this.serverPort = serverPort;
        this.runnable = dpRunnable;
        initialize();
    }

    //Close connection
    public void Close() {
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method uses parameters to create a Socket , and sets up for receiving message
     */
    private void initialize() {
        new Thread(() -> {
            try {
                //Connect to Server
                this.socket = new Socket(this.serverIp, this.serverPort);
                //Create a BufferedReader for message receiving
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(),
                        StandardCharsets.UTF_8));
                //Temp for message data
                String temp;
                log.Println("Server successfully Connected!");
                //'temp = bufferedReader.readLine()' will be blocked until a new line is read
                while ((temp = bufferedReader.readLine()) != null) {
                    //Package the data
                    DataPackage dp = new DataPackage(temp.trim(), this.socket);
                    //Go to do something with data received
                    this.runnable.SetDp(dp);
                    this.runnable.run();
                }
            } catch (IOException e) { //Error happened, attempt to close Socket
                try {
                    if (this.socket != null)
                        this.socket.close();
                    log.Println("Socket closed.");
                } catch (IOException e1) {
                    log.Println("Socket is null.");
                }
            }
        }).start();
    }

    //Send message
    public void Send(String message) {
        //Wait for a moment in case that the Socket is not ready
        int retry = 0;
        while (this.socket == null) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                retry++;
                if (retry == 50) {
                    log.Println("Can'not create a valid connection to server");
                    return;
                }
            } catch (InterruptedException e) {
                //Sleep error, normally won't happen
                e.printStackTrace();
            }
        }
        //Use Sender's SendMessage method to Send Message
        Sender.SendMesage(this.socket, message);
    }
}
