import AudioClasses.AudioStreamUDP;

import java.io.PrintWriter;

/**
 * Created by Martin on 2017-10-01.
 */
public class CallStateAccepting extends CallStateUncallable {

    public CallStateAccepting(){}


    //tor
    public CallState acceptCall(AudioStreamUDP stream, PrintWriter out) {
        out.println("TOR "+ stream.getLocalPort());
        return this;
    }

    public CallState ackReceived(AudioStreamUDP stream, PrintWriter out, String remoteAddress,int remotePort){
        return new CallStateInCall(stream, out, remoteAddress, remotePort);
    }


    //ERROR
    public CallState declineCall() {
        return new CallStateIdle();
    }

    public void printState() {System.out.println("State: Handling incomming call");}
}
