import AudioClasses.AudioStreamUDP;

import java.io.PrintWriter;

/**
 * Created by Martin on 2017-10-01.
 */
public abstract class CallStateUncallable extends CallState
{
    public CallStateUncallable(){
    }

    public CallState call(PrintWriter out){
        System.out.println("Cannot call right now, callhandler is busy");
        return this;
    }



    @Override
    public boolean isBusy(){
        return true;
    }

}
