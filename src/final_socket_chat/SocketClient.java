package final_socket_chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public class SocketClient {
    HashMap<String, DataOutputStream> clients;
    private ServerSocket ServerSocket = null;

    public static void main(String[] args){
        new SocketClient().start();
    }

    public SocketClient(){
        clients = new HashMap<String,DataOutputStream>();
        Collections.synchronizedMap(clients);
    }

    private void start() {
        int port = 5001;
        Socket socket = null;
        try {
            ServerSocket = new ServerSocket(port);
            System.out.println("접속대기중");
            while (true) {
                socket = ServerSocket.accept();
                InetAddress ip = socket.getInetAddress();
                System.out.println(ip + " conneted");
                new MultiThread(socket).start();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    class MultiThread extends Thread{
        Socket socket =  null;

        String mac = null;
        String msg = null;

        DataInputStream input;
        DataOutputStream output;

        public MultiThread(Socket socket){
            this.socket = socket;
            try{
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
            }catch(IOException e){
            }
        }

        public void run(){
            try{
                mac = input.readUTF();
                System.out.println("Mac address : "+ mac);
                clients.put(mac, output);
                sendMsg(mac + " 접속");

                while(input != null){
                    try{
                        String temp = input.readUTF();
                        sendMsg(temp);
                        System.out.println(temp);
                    }catch(IOException e){
                        sendMsg("No message");
                        break;
                    }
                }
            }catch(IOException e){
                System.out.println(e);
            }
        }

        private void sendMsg(String msg){
            Iterator<String> it = clients.keySet().iterator();

            while(it.hasNext()){
                try{
                    OutputStream dos = clients.get(it.next());
                    DataOutputStream output = new DataOutputStream(dos);
                    output.writeUTF(msg);
                }catch(IOException e){
                    System.out.println(e);
                }
            }
        }
    }
}
