import AudioClasses.AudioStreamUDP;

import java.io.PrintWriter;

/**
 * Created by Martin on 2017-10-01.
 */
public class CallStateCalling extends CallStateUncallable {

    public CallStateCalling(){

    }

    public CallState busyReceived(){
        System.out.println("The intended client was busy: ");
        return this;
    }

    public CallState torReceived(AudioStreamUDP stream, PrintWriter out, String remoteAddress,int remotePort){
        //Send ack
        out.println("ACK " + stream.getLocalPort());
        return new CallStateInCall(stream, out, remoteAddress, remotePort);
    }


    public void printState() {System.out.println("State: Calling");}
}
