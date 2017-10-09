import AudioClasses.AudioStreamUDP;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;

/**
 * Created by Martin on 2017-10-01.
 */
public class CallHandler {

    private PrintWriter out;
    private AudioStreamUDP stream;
    private String remoteAddr;
    private int remotePort;
    private CallState currentState;

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public enum CallEvent { USER_INVITE, INVITE, ACCEPT, DECLINE,
        TOR, ACK,  USER_BYE, BYE, OK,ERROR, USER_INVITEE, BUSY};


    public CallHandler() {
        try {
            stream = new AudioStreamUDP();
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentState = new CallStateIdle();
    }

    public void setStream(AudioStreamUDP stream){
        this.stream = stream;
    }

    public int getStreamPort(){
        return stream.getLocalPort();
    }


    public void setOut(PrintWriter out){
        this.out = out;
    }

    public synchronized void processNextEvent (CallEvent event) {

            switch (event) {
                case USER_INVITE: currentState = currentState.call(out); break;
                case USER_INVITEE: currentState = currentState.callE(out); break;
                case USER_BYE: currentState = currentState.hangup(out); break;
                case BYE: currentState = currentState.byeReceived(stream,out);break;
                case INVITE: currentState = currentState.inviteReceived(stream,out); break;
                case ACCEPT: currentState.acceptCall(stream,out); break;
                case DECLINE: currentState = currentState.declineCall(); break;
                case ACK: currentState = currentState.ackReceived(stream,out, remoteAddr, remotePort); break;
                case TOR: currentState = currentState.torReceived(stream,out,remoteAddr, remotePort); break;
                case BUSY: currentState = currentState.busyReceived(); break;
                case ERROR: currentState = currentState.errorReceived(); break;
                case OK: currentState = currentState.okReceived(stream,out);break;
            }

    }


    public boolean isBusy(){
        return currentState.isBusy();
    }
    public void printState() {
        currentState.printState();
    }
}
