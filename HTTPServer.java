import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class HTTPServer {
  public static void main(String[] args) {
    //Move out of a static context
    new HTTPServer();
  }

  public HTTPServer() {
    //The socket number we will connect to.
    //Sockets have to be below 2^16
    //So they can have a range of 0 to 65535.
    //Lower number are reserved, so we usually pick high numbers

    int socket = 5000;
    //Try with resources statement
    //Create a new server. This server will connect to our local network
    //at the given socket number.
    try (ServerSocket serverSocket = new ServerSocket(socket)) {
      System.out.println("Waiting for a client");

      //Loop forever waiting for connections
      //We can do this because we run our HTTP server code in a different thread.
      while (true) {
        //This is a blocking call. accept() will not return until a client connects to our socket.
        Socket clientSocket = serverSocket.accept();

        //Create an instance of Runnable. Passing this allows us to easily create threads in Java.
        Runnable runnable = () -> {
          System.out.println("A client connected");
          //Try with resource statement that lets us read from the socket (scanner) and write to the socket (PrintStream).
          try (Scanner scanner = new Scanner(clientSocket.getInputStream()); PrintStream out = new PrintStream(clientSocket.getOutputStream())) {

            //Get the header line
            String requestLine = scanner.nextLine();
            System.out.println("Request line " + requestLine);
            
            //Read lines from the client until we get a blank line.
            String line;
            do {
              line = scanner.nextLine();
              System.out.println(line);
              
            } while (line.trim().length() != 0); //We have a blank line if the line is empty

            //Respond with our own header line
            String responseLine = "HTTP/1.0 200 OK\n";

            //Then with the header response fields
            String[] responseHeaderFields = {"Content-Type: text/html;"};

            //Concatonate everything with the body of our response
            String response = responseLine + String.join("\n", Arrays.asList(responseHeaderFields)) + "\n\nHello, <b>internet 2</b>";
            out.println(response);
          } catch (IOException e) {
            e.printStackTrace();
          }
        };

        //Place the above code in a thread 
        Thread thread = new Thread(runnable);

        //Spawn the thread
        thread.start();
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
