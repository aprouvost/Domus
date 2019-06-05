package accelrecog.globalListener_actor;

import accelrecog.Shortcut;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import java.util.LinkedList;

public class GlobalListener {
    private GlobalKeyListener keyListener;
    private GlobalMouseListener mouseListener;
    public LinkedList<ActionR> userActions = new LinkedList<>();
    public Shortcut myShortCut;

    public GlobalListener(){
        keyListener = new GlobalKeyListener(this);
        mouseListener = new GlobalMouseListener(this);
        GlobalScreen.addNativeKeyListener(keyListener);
        GlobalScreen.addNativeMouseListener(mouseListener);
        GlobalScreen.addNativeMouseMotionListener(mouseListener);
    }
    public static void closeListeners(){
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
    }
    public void endIt(){
        if(myShortCut!=null){
            myShortCut.stopRecord();
            myShortCut=null;
        }
    }
}
