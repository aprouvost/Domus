package Domus;

import java.sql.Time;

public class bluetooth {

    public boolean connexionEstablished;
    public long milisLastReceived;

    public bluetooth(){
        connexionEstablished= false;
    }

    public void getCurrentTime(){
        milisLastReceived=System.currentTimeMillis();
    }

    public void receivedPing ( boolean pingReceived){
        connexionEstablished= true;
    }

    public void getBluetoothState(){
        if ( System.currentTimeMillis()- milisLastReceived >10){
            connexionEstablished= false;
        }
        if ( System.currentTimeMillis()- milisLastReceived<=10){
            connexionEstablished= true;
        }
    }

}
