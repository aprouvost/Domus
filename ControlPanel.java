package Domus;

import Camera_P2I.DetectionMain;
import Camera_P2I.VisualizationWindow;
import java.io.File;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class ControlPanel {

    private boolean arduinoConnected;
    private boolean connexionEstablished;
    private boolean cameraWorking;
    private FileWriter fw;
    private FileReader fr;
    private FileWriter fwFileCreation;
    private FileReader frFileCreation;

    private File user;  // un file par utilisateur
    private File userNames; // un file avec le nom de tous les utilisateurs

    private VisualizationWindow camPanel;
    private DetectionMain hand;
    private fenetreBaseDonnee databasePanel;
    private fenetreAccelerometre acceleroPanel;
    private fenetreConnexion connexionPanel;

    public void cameraState() {
        cameraWorking = hand.cameraIsOpened();
        if (cameraWorking == true) {
            // texte camera devient vert
        } else {
            //texte camera devient rouge
        }
    }


    public void connexionState() {
        connexionEstablished = connexionPanel.getconnexion();
        if (connexionEstablished == true) {
            // texte devient vert
        } else {
            // texte devient rouge
        }
    }

    public void arduinoState() {
        arduinoConnected = acceleroPanel.getconnexion();
        if (arduinoConnected == true) {
            // texte devient vert
        } else {
            // texte devient rouge
        }
    }

    public void userFileCreation() {
        userNames = new File("UserNames.txt");

    }



    public void newUser(String hue, String username, String saturation, String value, String adresseCSV) {
        // ursername: nom écrit par l'utilisateur
        user= new File( username+".txt")
        try {
            //Création de l'objet
            fw = new FileWriter("Usernames.txt", true);
            String str = username;
            fw.write(str);
            fw.close();

            fwFileCreation = new FileWriter(user);
            String strB = username;
            strB += "\t hue  : " +  hue + "\n";
            strB += "\t saturation  : " + saturation + "\n";
            strB += "\t value : " + value + "\n";
            strB += "\t adresse fichier CSV : " + adresseCSV + "\n";
            fwFileCreation.write(str);
            fwFileCreation.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String readUser(String username){
        String strB="";
        try{
            frFileCreation = new FileReader(username + ".txt");
            int i=0;
            while ((i = fr.read()) != -1)
                strB += (char) i;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strB;
    }


    public void eraseUser( String username){
        File user = new File(username + ".txt");
        user.delete();
    }


    public String readListUsers() {
        String str = "";
        try {
            fr = new FileReader("Usernames.txt");
            int i = 0;
            while ((i = fr.read()) != -1)
                str += (char) i;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }


}
