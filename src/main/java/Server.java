import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Server {
  private static ServerSocket srvsoc = null;
  private static Socket clsoc = null;
  private static final int maxclients = 50;
  static final Clientthread[] threads = new Clientthread[maxclients];
  public static final Queue<Socket> queue = new LinkedList<Socket>();

  public static void main(String[] args) {
    int port = 2000;
    if (args.length < 1) {
      System.out.println("Usage: java MultiThreadServer <portNumber>\n"
                        + "Now using port number=" + port);
    } else {
      port = Integer.valueOf(args[0]).parseInt(" ");
    }
    try {
      srvsoc = new ServerSocket(port); //port number on which server socket is binded
    } catch (IOException ioex) {
      System.out.println("Unable to connect to port");
      System.exit(1);
    }
    /*create a client socket for each connection and pass it to a new client thread*/
    while (true) {
      try {
        clsoc = srvsoc.accept(); //to accept the input from srvsocket
        int i = 0;
        for (i = 0;i < maxclients;i++) {
          if (threads[i] == null) {
            (threads[i] = new Clientthread(clsoc,threads)).start();
            break;
          }
        }
        if (i == maxclients) {
          DataOutputStream p = new DataOutputStream(clsoc.getOutputStream());
          p.writeChars("Server too busy. Try later.");
          clsoc.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

}

class Clientthread extends Thread {
  private Scanner input = null;
  private PrintStream ps = null;
  private Socket clsoc = null;
  private final Clientthread[] threads;
  private int maxclients;
  private int number1;
  private int number2;

  public Clientthread(Socket clsoc, Clientthread[] threads) {
    this.clsoc = clsoc;
    this.threads = threads;
    maxclients = threads.length;
  }

  public void run() {
    int maxclients = this.maxclients;
    Clientthread[] threads = this.threads;
    //Server newserver = new Server();

    try {
      input = new Scanner(clsoc.getInputStream()); //recieve input stream from the client
      ps = new PrintStream(clsoc.getOutputStream()); //Pass server response to the client
      number1 = input.nextInt();
      number2 = input.nextInt();

      while (number1 != 0 && number2 != 0) {
        int temp = number1 + number2;
        ps.println(temp);
        number1 = input.nextInt();
        number2 = input.nextInt();
      }
      //clean up the thread so that another task can be assigned to it
      synchronized (this) {
        for (int i = 0; i < maxclients; i++) {
          if (threads[i] == this) {
            threads[i] = null;
          }
        }
      }
      if (Server.queue.isEmpty() == false) {
        clsoc = Server.queue.peek();
        for (int i = 0; i < maxclients; i++) {
          if (threads[i] == null) {
            (threads[i] = new Clientthread(clsoc, Server.threads)).start();
            break;
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        System.out.println("Closing connection ...");
        clsoc.close();
      } catch (IOException ie) {
        System.out.println("Unable to close connection");
        //System.exit(1);
      }
    }
  }
}
