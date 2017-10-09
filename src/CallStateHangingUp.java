import AudioClasses.AudioStreamUDP;

import java.io.PrintWriter;

/**
 * Created by Martin on 2017-10-01.
 */
public class CallStateHangingUp extends CallStateUncallable {

    public CallStateHangingUp(){

    }

    public CallState okReceived(AudioStreamUDP stream, PrintWriter out){
        //Send ack
        if(stream!= null)stream.close();
        out.close();
        return new CallStateIdle();
    }


    public void printState() {System.out.println("State: Hanging up");}
}
