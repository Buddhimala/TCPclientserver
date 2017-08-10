import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Client implements Runnable {
  private static Socket clsoc = null;
  private static PrintStream p = null;
  private static Scanner scanner = null;
  private static Scanner input1 = null;
  private int number1;
  private int number2;
  private int temp;

  public static void main(String[] args) {
    int port = 2000;
    String host = "localhost";
    int number1;
    int number2;
    int temp;
    if (args.length < 2) {
      System.out.println("Usage: java TCPClient running on host=" + host + ", portNumber=" + port);
    } else {
      host = args[0];
      port = Integer.valueOf(args[1]).parseInt(" ");
    }
    try {
      clsoc = new Socket(host,port); //connection with the server
      scanner = new Scanner(clsoc.getInputStream()); //to get server respone
      p = new PrintStream(clsoc.getOutputStream()); //Pass scanned values from client to server
      input1 = new Scanner(System.in);
    } catch (UnknownHostException e) {
      System.out.println("Don't know about the host");
    } catch (IOException e) {
      System.out.println("Couldn't get I/O for the connection to host");
    }

    /*After initializing, now start passing data from/to the socket created */
    new Thread(new Client()).start();
    System.out.println("Type 0 as second number to quit");
  }

  @Override
    public void run() {
    try {
      do {
        System.out.println("Enter your first number:");
        number1 = input1.nextInt();
        System.out.println("Enter your second number:");
        number2 = input1.nextInt();
        p.println(number1);
        p.println(number2);
        temp = scanner.nextInt();//to store server response
        System.out.println("The result:");
        System.out.println(temp);
        System.out.println("\n");
      } while (number1 != 0 && number2 != 0);
    } catch (NoSuchElementException ne) {
      System.out.println("\nClosing Connection ....");
    } finally {
      try {
        System.out.println("Connection Closed.");
        clsoc.close();
      } catch (IOException e) {
        System.out.println("Unable to close connection");
        System.out.println(e);

      }
    }
  }
}
