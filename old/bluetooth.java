package accelrecog;


public class bluetooth {

    private long milisLastReceived = 0;

    public void receivedPing (){
        milisLastReceived=System.currentTimeMillis();
    }

    public boolean getBluetoothState(){
        return ( System.currentTimeMillis()- milisLastReceived<=10000);
    }

}