package accelrecog;

import accelrecog.globalListener_actor.AType;
import accelrecog.globalListener_actor.ActionR;
import accelrecog.globalListener_actor.Actor;
import accelrecog.globalListener_actor.GlobalListener;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Shortcut {
    GlobalListener listener;
    Actor robot;
    ArrayList<ActionR> myActions = new ArrayList<>();
    private Interface tempGUI;
    public Shortcut(GlobalListener globalListener){
        listener = globalListener;
    }

    public void run(){
        robot = new Actor();
        for(int i = 0;i<myActions.size();i++){
            System.out.println(myActions.get(i).myType.toString());
            switch (myActions.get(i).myType){
                case KEYPRESS:
                    robot.keyPress(myActions.get(i).code);
                    break;
                case MOUSEMOVE:
                    robot.mouseMove(myActions.get(i).mousePos.x,myActions.get(i).mousePos.y);
                    break;
                case KEYRELEASE:
                    robot.keyRelease(myActions.get(i).code);
                    break;
                case MOUSEPRESS:
                    robot.mousePress(myActions.get(i).code);
                    break;
                case MOUSERELEASE:
                    robot.mouseRelease(myActions.get(i).code);
                    break;
                case COMMAND:
                    Runtime rn = Runtime.getRuntime();
                    Process pr;
                    try {
                        pr = rn.exec(myActions.get(i).cmd);
                        new Thread(() -> {
                            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                            String line = null;

                            try {
                                while ((line = input.readLine()) != null)
                                    System.out.println(line);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }).start();

                        pr.waitFor();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
    public void startRecord(Interface GUI){
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
        listener.userActions.clear();
        listener.myShortCut=this;
        System.out.println("starting macro recording");
        GUI.desactivate();
        tempGUI = GUI;
    }

    public void newCmd(String code){
        myActions = new ArrayList<>();
        myActions.add(new ActionR(code));
        GlobalListener.closeListeners();
    }
    public void stopRecord(){
        myActions = new ArrayList<>(listener.userActions.size());
        ArrayList<ActionR> actionsToRemove = new ArrayList<>();

        for(ActionR aR : listener.userActions){
            myActions.add(aR);
        }
        for(int i=0;i<myActions.size()-1;i++){
            if(myActions.get(i+1).myType== AType.MOUSEMOVE && myActions.get(i).myType == AType.MOUSEMOVE){
                actionsToRemove.add(myActions.get(i));
            }
        }

        for(ActionR aR:actionsToRemove){
            myActions.remove(aR);
        }

        System.out.println("Stopping macro recording");
        GlobalListener.closeListeners();
        tempGUI.activate();
    }
}
