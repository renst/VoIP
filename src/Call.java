import AudioClasses.AudioStreamUDP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Martin on 2017-10-01.
 */
public class Call {
    public static void main(String[] args) {
        CallHandler ch = new CallHandler();
        ServerSocket serverSocket = null;
        boolean busy = false;

        int port = 4545;
        while (serverSocket == null) {
            try {
                serverSocket = new ServerSocket(port);
                System.out.println("Listening on port " + port);
            } catch (IOException e) {
                System.out.println("Could not listen on the port: " + port++);
            }
        }

        //Take commands from user
        Thread userHandler = new Thread(new userHandler(ch));
        userHandler.start();
        Socket sock = null;
        try {
        while (true) {

                sock = serverSocket.accept();

                //If we are not busy create thread to handle client
                if (!ch.isBusy()) {
                    ch.setOut(new PrintWriter(sock.getOutputStream(), true));
                    Thread clientHandler = new Thread(new clientHandler(sock, ch));
                    clientHandler.start();
                } else {
                    new PrintWriter(sock.getOutputStream(), true).println("BUSY");
                    Thread.sleep(200);
                    sock.close();
                    //System.out.println("Cant be called ATM");
                }
            }
        }
        catch (Exception e) {
            System.out.println("An error with the connection occurred");
        }finally {
            try {
                sock.close();
            } catch (IOException e1) {
                System.out.println("Couldn't shut down connection properly");
            }
        }
    }
}

class userHandler extends Thread {
    public BufferedReader in;
    CallHandler ch;

    public userHandler(CallHandler ch) {
        this.ch = ch;
        in = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void run() {
        String[] input;
        String choice;
        System.out.println();
        System.out.println("What happens to the call?");
        System.out.println("Call <ip> <port> to start call");
        System.out.println("Bye to end call.");
        System.out.println("Answer to answer call");

        try {
            while (true) {
                input = in.readLine().split(" ");
                choice = input[0].toLowerCase();
                switch (choice) {
                    case "call":
                        try {
                            if (ch.isBusy()) {
                                System.out.println("Can't call right now, call handler is busy");
                                break;
                            }
                            if (input.length != 3) {
                                System.out.println("Expected: call <ip> <port>");
                                break;
                            }
                            InetAddress serverAddr = InetAddress.getByName(input[1]);
                            Socket sock = new Socket(serverAddr, Integer.parseInt(input[2]));

                            ch.setRemoteAddr(input[1]);
                            ch.setOut(new PrintWriter(sock.getOutputStream(), true));
                            ch.processNextEvent(CallHandler.CallEvent.USER_INVITE);
                            Thread clientHandler = new Thread(new clientHandler(sock, ch));
                            clientHandler.start();

                            break;
                        } catch (Exception e) {
                            System.out.println("Couldn't call the user error with connection");
                            break;
                        }
                    case "calle":
                        try {
                            if (ch.isBusy()) {
                                System.out.println("Can't call right now, call handler is busy");
                                break;
                            }
                            if (input.length != 3) {
                                System.out.println("Expected: call <ip> <port>");
                                break;
                            }
                            InetAddress serverAddr = InetAddress.getByName(input[1]);
                            Socket sock = new Socket(serverAddr, Integer.parseInt(input[2]));

                            ch.setRemoteAddr(input[1]);
                            ch.setOut(new PrintWriter(sock.getOutputStream(), true));
                            ch.processNextEvent(CallHandler.CallEvent.USER_INVITEE);
                            Thread clientHandler = new Thread(new clientHandler(sock, ch));
                            clientHandler.start();

                            break;
                        } catch (Exception e) {
                            System.out.println("Couldn't call the user error with connection");
                            break;
                        }
                    case "bye":
                        ch.processNextEvent(CallHandler.CallEvent.USER_BYE);
                        break;
                    case "answer":
                        ch.processNextEvent(CallHandler.CallEvent.ACCEPT);
                        break;
                    case "decline":
                        ch.processNextEvent(CallHandler.CallEvent.DECLINE);
                        break;
                    default:
                        System.out.println("Unknown command");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("closing input");
    }
}

class clientHandler implements Runnable {

    private PrintWriter out;
    private BufferedReader in;
    Socket sock;
    CallHandler ch;

    public clientHandler(Socket sock, CallHandler ch) {
        this.sock = sock;
        this.ch = ch;
    }

    @Override
    public void run() {
        AudioStreamUDP stream;
        String input;
        String choice;

        try {
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out = new PrintWriter(sock.getOutputStream(), true);
            //Setup audio stream on receivers end
            stream = new AudioStreamUDP();
            ch.setStream(stream);

            while ((input = in.readLine()) != null) {
                choice = input.split(" ")[0].toLowerCase();

                switch (choice) {
                    case "invite":
                        ch.setRemoteAddr(input.split(" ")[1]);
                        ch.processNextEvent(CallHandler.CallEvent.INVITE);
                        break;
                    case "bye":
                        ch.processNextEvent(CallHandler.CallEvent.BYE);
                        break;
                    case "ok":
                        ch.processNextEvent(CallHandler.CallEvent.OK);
                        break;
                    case "ack":
                        ch.processNextEvent(CallHandler.CallEvent.ACK);
                        break;
                    case "tor":
                        ch.setRemotePort(Integer.parseInt(input.split(" ")[1]));
                        ch.processNextEvent(CallHandler.CallEvent.TOR);
                        break;
                    case "busy":
                        ch.processNextEvent(CallHandler.CallEvent.BUSY);
                        break;
                    default:
                        System.out.println("Unknown message, closing connection " + choice);
                        break;
                }
                ch.printState();

            }
        } catch (IOException e) {
            ch.processNextEvent(CallHandler.CallEvent.ERROR);
        } finally {
            closeConnection();
        }
    }

    private void closeConnection() {
        try {
            sock.close();
            ch.processNextEvent(CallHandler.CallEvent.ERROR);
            System.out.println("Connection closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



