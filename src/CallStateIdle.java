import AudioClasses.AudioStreamUDP;

import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * Created by Martin on 2017-10-01.
 */
public class CallStateIdle extends CallState
{
    public CallStateIdle(){
    }

    //INVITE
    public CallState inviteReceived(AudioStreamUDP stream, PrintWriter out){
        //This is an outgoing signal:
        return new CallStateAccepting();
    }

    //Send two invites
    public CallState callE(PrintWriter out){
        try {
            out.println("INVITE "+ Inet4Address.getLocalHost().getHostAddress());
            out.println("INVITE "+ Inet4Address.getLocalHost().getHostAddress());
            return new CallStateCalling();

        } catch (UnknownHostException e) {
            System.out.println("Local IP error");
            return new CallStateIdle();
        }
    }

    public CallState call(PrintWriter out){
        try {
            out.println("INVITE "+ Inet4Address.getLocalHost().getHostAddress());

            return new CallStateCalling();

        } catch (UnknownHostException e) {
            System.out.println("Local IP error");
            return new CallStateIdle();
        }
    }

    public void printState(){
        System.out.println("State: Idle");
    }
}
