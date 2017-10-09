import AudioClasses.AudioStreamUDP;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;

/**
 * Created by Martin on 2017-10-01.
 */
public class CallStateInCall extends CallStateUncallable
{

    public CallStateInCall(AudioStreamUDP stream, PrintWriter out, String remoteAddress,int remotePort){
        startCall(stream, remoteAddress, remotePort);
    }


    public void startCall(AudioStreamUDP stream, String remoteAddress,int remotePort){
        try {
            InetAddress address = InetAddress.getByName(remoteAddress);
            stream.connectTo(address, remotePort);
            stream.startStreaming();
        }
        catch (IOException e){
            System.out.println("Couldnt start call");
        }
    }

    public CallState hangup(PrintWriter out){
        out.println("BYE");
        return new CallStateHangingUp();
    }

    public CallState byeReceived(AudioStreamUDP stream, PrintWriter out){
        out.println("OK");
        if(stream!=null)stream.close();
        out.close();
        return new CallStateIdle();
    }

    public void printState(){
        System.out.println("State: In call");
    }
}
