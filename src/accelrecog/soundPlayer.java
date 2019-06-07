package accelrecog;


import javax.sound.sampled.*;
import java.io.IOException;

public class soundPlayer{
    String loginSound;
    String killEveryone;

    // récupére les adresses des fichiers sons et les stockes en string
    public soundPlayer(){
        loginSound = "Welcome.wav";
        killEveryone = "bloop.wav";
    }

    // joue le son à partir de son type
    public void joueSon(String type){
        String typedeSon="";
        switch(type){
            case "loginSound":
                typedeSon = loginSound;
                break;
            case "killEveryone":
                typedeSon = killEveryone;

        }
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(getClass().getResource(typedeSon));
            // Get a sound clip resource.
            Clip clip = AudioSystem.getClip();
            // Open audio clip and load samples from the audio input stream.
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException f) {
            f.printStackTrace();
        } catch (IOException f) {
            f.printStackTrace();
        } catch (LineUnavailableException f) {
            f.printStackTrace();
        }

    }





}
