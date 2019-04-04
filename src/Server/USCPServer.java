package Server;

import Utils.DPRunnable;
import Utils.DataPackage;
import Utils.Sender;
import Utils.log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * 一个Socket通信服务端的建立：
 * 1、ServerSocket serverSocket = new ServerSocket(8888); //在本地(127.0.0.1或者说是localhost的8888端口开启Server)
 * 2、Socket clientSocket = serverSocket.accept(); //ServerSocket的accept()函数会造成程序的阻塞，直到有Client的Socket连接进来才会往下执行
 * 3、接收消息：BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())) //用来读取clientSocket里面传输过来的数据
 * 4、String temp；
 * 5、while((temp = br.readLine()) != null) {  //temp在br读到数据时（即不为null）成为有效数据，此步骤也会阻塞
 * //temp就是接收到的缓冲内容转换为字符串的结果
 * //TODO Write your code here
 * }
 */
public class USCPServer {
    private boolean SERVER_ENABLE;      //控制Server是否工作，即等待监听新的Client连接
    private int port;                   //用于Server开启监听的端口号
    private DPRunnable dpRunnable;      //定义如何处理接受的通讯数据
    private ServerSocket serverSocket;  //USCPServer端的Socket

    //构造函数，创建一个Server时传入的必要参数:
    //       Port：在那个端口开启Server监听
    // DPRunnable：在自定义的DPRunnable里面指定处理业务
    public USCPServer(int port, DPRunnable runnable) {
        this.port = port;
        this.dpRunnable = runnable;
        initialize();
    }

    //初始化ServerSocket
    private void initialize() {
        try {
            //开始在port处的ServerSocket
            this.serverSocket = new ServerSocket(this.port);
            SERVER_ENABLE = true;
        } catch (IOException e) {
            log.Println(e.getMessage());
        }
    }

    //开启监听,等待Client的连接
    public void StartListen() {
        log.Println("服务端已开启...");
        while (SERVER_ENABLE) {
            try {
                Socket s = serverSocket.accept();               //等待Client连接
                new ServerSubThread(s, this.dpRunnable).start();//为了让多个Client都可以连接进入此Server，就要每来一个Client就分配一个线程给这个Client
            } catch (IOException e) {
                SERVER_ENABLE = false;
                log.Println(e.getMessage());
            }
        }
    }

    //停止Server
    public void StopServer() {
        SERVER_ENABLE = false;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            log.Println(e.getMessage());
        }
    }

    //这个Class是一个线程类，new 一个就是一个新的线程，主要操作业务在run()内完成
    private class ServerSubThread extends Thread {
        private Socket clientSocket;//连入的Client的Socket
        DataPackage dp;             //自定义的数据对象类，包含了String型、以及Client的Socket
        DPRunnable runnable;        //自定义的Runnable,一个接口，implement了这个接口的Class对象作为参数传进来，

        //构造函数需要告诉Class是谁连了进来(通过socket告诉)，以及怎么处理通信(通过DPRunnable告诉)
        private ServerSubThread(Socket socket, DPRunnable runnable) {
            this.clientSocket = socket;
            this.runnable = runnable;
        }

        @Override
        public void run() {
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream(),
                        StandardCharsets.UTF_8));   //缓冲读取器，读取Socket缓冲中的内容

                String temp;    //声明临时消息变量
                //这里的bufferedReader.readLine()会阻塞，直到读到换行符，然后才判断是不是为null，正常情况下，读到东西后是不会为null的，
                //所以while循环会一直进行
                while ((temp = bufferedReader.readLine()) != null) {
                    //把接收到的数据包装进入数据包里面，丢到runnable中进行处理
                    this.dp = new DataPackage(temp.trim(), this.clientSocket);
                    this.runnable.SetDp(this.dp);
                    this.runnable.run();
                }
            } catch (IOException e) {
                //出现错误尝试关闭连接
                if (this.clientSocket != null) {
                    try {
                        this.clientSocket.close();
                    } catch (IOException ex) {
                        log.Println(e.getMessage());
                    }
                }
                log.Println(e.getMessage());
            }

        }

    }
}
