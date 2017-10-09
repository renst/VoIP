import AudioClasses.AudioStreamUDP;

import java.io.PrintWriter;

/**
 * Created by Martin on 2017-10-01.
 */
public abstract class CallState
{


    public CallState(){}

    //User commands
    public CallState hangup(PrintWriter out) {return this;}
    public CallState call(PrintWriter out) {return this;}
    public CallState callE(PrintWriter out) {return this;}

    public CallState acceptCall(AudioStreamUDP stream, PrintWriter out){System.out.println("No call to answer right now");return this;}
    public CallState declineCall(){return this;}

    //Incomming messsages
    public CallState errorReceived(){return new CallStateIdle();}
    public CallState inviteReceived(AudioStreamUDP stream, PrintWriter out){System.out.println("Erroneous message");closeConnection(stream,out);return new CallStateIdle();}
    public CallState byeReceived(AudioStreamUDP stream, PrintWriter out){System.out.println("Erroneous message");closeConnection(stream,out);return new CallStateIdle();}
    public CallState okReceived(AudioStreamUDP stream, PrintWriter out){System.out.println("Erroneous message");closeConnection(stream,out);return new CallStateIdle();}
    public CallState ackReceived(AudioStreamUDP stream, PrintWriter out, String remoteAddress,int remotePort){System.out.println("Erroneous message");closeConnection(stream,out);return new CallStateIdle();}
    public CallState torReceived(AudioStreamUDP stream, PrintWriter out, String remoteAddress,int remotePort){System.out.println("Erroneous message");closeConnection(stream,out);return new CallStateIdle();}


    public void closeConnection(AudioStreamUDP stream, PrintWriter out){
        if(stream != null) stream.close();
        if(out != null)out.close();
    }

    public CallState busyReceived(){return new CallStateIdle();}

    public boolean isBusy(){return false;}
    public void printState(){;}
}
