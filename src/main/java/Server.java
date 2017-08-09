import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Server {
    private static ServerSocket srvsoc=null;
    private static Socket clsoc=null;
    private static final int maxclients=50;
    public static final clientThread[] threads=new clientThread[maxclients];
    public static final Queue<Socket> queue = new LinkedList<Socket>();
    public static void main(String[] args) {
        int port=2000;
        if (args.length < 1) {
            System.out.println("Usage: java MultiThreadChatServer <portNumber>\n"
                    + "Now using port number=" + port);
        } else {
            port = Integer.valueOf(args[0]).intValue();
        }

        try{
            srvsoc=new ServerSocket(port); //port number on which server socket is binded
        }catch(IOException ioex){
            System.out.println("Unable to connect to port");
            System.exit(1);
        }
        /*create a client socket for each connection and pass it to a new client thread*/
        while(true){
            try{
                clsoc=srvsoc.accept(); //to accept the input from srvsocket
                int i=0;
                for(i=0;i<maxclients;i++){
                    if(threads[i]==null){
                        (threads[i]=new clientThread(clsoc,threads)).start();
                        break;
                    }
                }if(i==maxclients){
                    PrintStream p = new PrintStream(clsoc.getOutputStream());
                    p.println("Server too busy. Try later.");
                    clsoc.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

}

class clientThread extends Thread{
    private Scanner input=null;
    private PrintStream p=null;
    private Socket clsoc=null;
    private final clientThread[] threads;
    private int maxclients,number1,number2;

    public clientThread(Socket clsoc,clientThread[] threads){
        this.clsoc=clsoc;
        this.threads=threads;
        maxclients=threads.length;
    }
    public void run(){
        int maxclients=this.maxclients;
        clientThread[] threads=this.threads;
        Server newserver=new Server();

        try{
            input=new Scanner(clsoc.getInputStream()); //recieve input stream from the client
            p=new PrintStream(clsoc.getOutputStream()); //Pass server response to the client
            number1=input.nextInt();
            number2=input.nextInt();

            while (number1 != 0 && number2 != 0){
                int temp=number1+number2;
                p.println(temp);
                number1=input.nextInt();
                number2=input.nextInt();
            }
            //clean up the thread so that another task can be assigned to it
            synchronized (this) {
                for (int i = 0; i < maxclients; i++) {
                    if (threads[i] == this) {
                        threads[i] = null;
                    }
                }
            }
            if(newserver.queue.isEmpty()==false) {
                clsoc = newserver.queue.peek();
                for (int i = 0; i < maxclients; i++) {
                    if (threads[i] == null) {
                        (threads[i] = new clientThread(clsoc, Server.threads)).start();
                        break;
                    }
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        finally{
            try{
                System.out.println("Closing connection ...");
                clsoc.close();
            }catch(IOException ie){
                System.out.println("Unable to close connection");
                System.exit(1);
            }
        }
    }
}
