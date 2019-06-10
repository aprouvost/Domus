package global;

import accelrecog.Data;
import accelrecog.DataSet;
import accelrecog.Gesture;
import accelrecog.Shortcut;
import accelrecog.globalListener_actor.AType;
import accelrecog.globalListener_actor.ActionR;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.UUID;

public class interrogBD2 {
    public String adresseBD;
    public String nomLogin;
    public String mdp;
    Connection conn;

    public interrogBD2(){
        adresseBD = "jdbc:mysql://remotemysql.com:3306";
        nomLogin = "OThWXKoKWM";
        mdp = "6Kn8Qx9qSD";
       initConnection();
    }

   public void initConnection(){
        try {
            //Enregistrement de la classe du driver par le driverManager
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver trouve...");
            //Creation d'une connexion à la base de donnees
            conn = DriverManager.getConnection(adresseBD, nomLogin, mdp);
            System.out.println("Connexion etablie...");
            System.out.println("");
        } catch(Exception e){
            //Affiche le message d'erreur si une erreur se produit durant la connexion
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }
    public void savefirstHistory(LinkedList<Gesture> listeGestes, String idUser){
        for(int i =0; i<listeGestes.size();i++){
            String uniqueID = UUID.randomUUID().toString();
            for(int j = 0; j<listeGestes.get(i).mySets.size(); j++){
                insertDataset(uniqueID, listeGestes.get(i).mySets.get(j).myData);
            }
            insertGesture(idUser, listeGestes.get(i).myName, uniqueID);
            insertActions(uniqueID, listeGestes.get(i).myShortCut.myActions);

        }
    }

    public void saveHistory(LinkedList<Gesture> listeGestes, String idUser){
        int i = getLastGestureIndex(idUser);

        if(i==0){
            // jamais sauvegardé
            savefirstHistory(listeGestes, idUser);

        }else{
            //mettre à jour les gestures déjà sauvegardés : ajouter les derniers reinforcement
            for(int b =0; b<i;b++){
                String gestureId = listeGestes.get(b).myName;
                int lastReinforcementIndex = getLastReinforcementIndex(listeGestes.get(b).myName);
                for(int j = lastReinforcementIndex; j<listeGestes.get(b).mySets.size(); j++){
                    insertDataset(gestureId, listeGestes.get(b).mySets.get(j).myData);
                }
            }


            // ceux depuis l'indice n'ont jamais été sauvegardé
            for(int j = i; j<listeGestes.size(); j++){
                String uniqueID = UUID.randomUUID().toString();
                for(int c = 0 ; c<listeGestes.get(j).mySets.size() ; c++){
                    insertDataset(uniqueID, listeGestes.get(j).mySets.get(c).myData);
                }
                insertGesture(idUser, listeGestes.get(j).myName, uniqueID);
                insertActions(uniqueID, listeGestes.get(j).myShortCut.myActions);

            }


        }
    }
    public boolean verifieUser(String idUser) {
        try {
            String usedb = "use OThWXKoKWM";
            String sqlStr = "select idUser FROM userGestures where idUser = ?";
            PreparedStatement ps = conn.prepareStatement(sqlStr);
            ps.setString(1, idUser);
            Statement stmt = conn.createStatement();

            //execution de la requete
            stmt.executeQuery(usedb);
            ResultSet res = ps.executeQuery();


            while (res.next()) {
                String nom = res.getString(1);

                if(nom!=""){
                    return true;
                }
            }
            return false;

        }
        catch(Exception e){
            infoBox(e.getMessage(),"Erreur");

            return false;
        }
    }

    public int getLastReinforcementIndex(String idGesture){
        try {
            String usedb = "use OThWXKoKWM";

            Statement stmt = conn.createStatement();
            //execution de la requete
            stmt.executeQuery(usedb);
            String sqlStr = "select count(idReinforcement) FROM Dataset where idGesture = ?";
            PreparedStatement ps = conn.prepareStatement(sqlStr);
            ps.setString(1, idGesture);

            //execution de la requete
            ResultSet res = ps.executeQuery();
            res.next();
            int length= res.getInt(1);
            return length;


        }
        catch(Exception e){
            infoBox(e.getMessage(),"Erreur");
        }
        return 0;
    }

    public int getLastGestureIndex(String idUser){
        try {
            String usedb = "use OThWXKoKWM";

            Statement stmt = conn.createStatement();
            //execution de la requete
            stmt.executeQuery(usedb);
            String sqlStr = "select count(idGesture) FROM userGestures where idUser = ?";
            PreparedStatement ps = conn.prepareStatement(sqlStr);
            ps.setString(1, idUser);

            //execution de la requete
            ResultSet res = ps.executeQuery();
            res.next();
            int length= res.getInt(1);
            return length;

        }
        catch(Exception e){
            infoBox(e.getMessage(),"Erreur");

        }
        return 0;
    }

    public void insertGesture(String idUser, String nomGeste, String idGesture) {
        try {
            String usedb = "use OThWXKoKWM";

            Statement stmt = conn.createStatement();
            //execution de la requete
            stmt.executeQuery(usedb);
            String sqlStr = "INSERT INTO userGestures (idUser, idGesture, nomGeste) VALUES (?, ?, ?)";
            PreparedStatement ps1 = conn.prepareStatement(sqlStr);
            ps1.setString(1,idUser);
            ps1.setString(2, idGesture);
            ps1.setString(3, nomGeste);
            ps1.executeUpdate();

        }
        catch(Exception e){

            System.out.println(e.getMessage());
        }
    }
    public void insertGesture(String idUser, String nomGeste) {

        String uniqueID = UUID.randomUUID().toString();

        try {
            String usedb = "use OThWXKoKWM";

            Statement stmt = conn.createStatement();
            //execution de la requete
            stmt.executeQuery(usedb);
            String sqlStr = "INSERT INTO userGestures (idUser, idGesture, nomGeste) VALUES (?, ?, ?)";
            PreparedStatement ps1 = conn.prepareStatement(sqlStr);
            ps1.setString(1,idUser);
            ps1.setString(2, uniqueID);
            ps1.setString(3, nomGeste);
            ps1.executeUpdate();

        }
        catch(Exception e){

            System.out.println(e.getMessage());
        }
    }
    public void insertDataset(String idGesture, ArrayList<Data> myData) {

        String uniqueID = UUID.randomUUID().toString();

        try {
            String usedb = "use OThWXKoKWM";
            Statement stmt = conn.createStatement();
            //execution de la requete
            stmt.executeQuery(usedb);


            String sqlData = "INSERT INTO Data(idReinforcement, idMesure, accX, accY, accZ, flexMajeur, flexAnnul, flexAuric) VALUES (?, ?, ?, ?,?, ?, ?, ?)";
            PreparedStatement ps2 = conn.prepareStatement(sqlData);

            for (int i =0; i<myData.size(); i++) {

                ps2.setString(1,uniqueID);
                ps2.setInt(2, i);

                ps2.setDouble(3, myData.get(i).x);

                ps2.setDouble(4, myData.get(i).y);

                ps2.setDouble(5, myData.get(i).z);
                ps2.setBoolean(6, bytetoboolean(myData.get(i).a));
                ps2.setBoolean(7, bytetoboolean(myData.get(i).b));
                ps2.setBoolean(8, bytetoboolean(myData.get(i).c));
                ps2.executeUpdate();
            }
            String sqlDataset = "INSERT INTO Dataset(idGesture, idReinforcement, length) VALUES (?, ?, ?)";
            PreparedStatement ps1 = conn.prepareStatement(sqlDataset);
            ps1.setString(1,idGesture);
            ps1.setString(2, uniqueID);
            int length = myData.size();
            ps1.setInt(3, length);
            ps1.executeUpdate();
        }
        catch(Exception e){

            System.out.println(e.getMessage());
        }
    }

    public void insertActions(String idGesture, ArrayList<ActionR> actions) {
        try {
            String usedb = "use OThWXKoKWM";

            Statement stmt = conn.createStatement();
            //execution de la requete
            stmt.executeQuery(usedb);

            String sqlStr = "INSERT INTO actions (idGesture, indexAction, myType, coordX, coordY, codeClavier, cmdCode) VALUES (?, ?, ?, ?, ?,?,?)";
            PreparedStatement ps1 = conn.prepareStatement(sqlStr);
            for (int i =0; i<actions.size();i++) {
                ps1.setString(1,idGesture);
                ps1.setInt(2, i);
                ps1.setString(3,actions.get(i).myType.toString());
                if(actions.get(i).mousePos!=null){
                    ps1.setInt(4, actions.get(i).mousePos.x);
                    ps1.setInt(5, actions.get(i).mousePos.y);
                }else{
                    ps1.setInt(4, 0);
                    ps1.setInt(5, 0);
                }

                if(actions.get(i).code != 0){
                    ps1.setInt(6, actions.get(i).code);
                }else{
                    ps1.setInt(6, 0);
                }
                if(actions.get(i).cmd!=null){
                    ps1.setString(7,actions.get(i).cmd);
                }else{
                    ps1.setString(7,"");
                }

                ps1.executeUpdate();
            }


        }
        catch(Exception e){

            System.out.println(e.getMessage());
        }
    }

    public boolean bytetoboolean(byte aconvertir){
        return (aconvertir==(byte)(1));
    }

    public byte getvOut(boolean bool) {
        return (byte)(bool?1:0);
    }

    // permet d'afficher des messages popups
    public static void infoBox(String infoMessage, String titleBar)
    {
        JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
    }



    public DataSet recupererDataset(String idGesture, String idReinforcement) {
        try {
            String usedb = "use OThWXKoKWM";

            Statement stmt = conn.createStatement();
            //execution de la requete
            stmt.executeQuery(usedb);
            String sqlStr = "select length FROM Dataset where idGesture = ? and idReinforcement = ?";
            PreparedStatement ps = conn.prepareStatement(sqlStr);
            ps.setString(1, idGesture);
            ps.setString(2, idReinforcement);

            //execution de la requete

            ResultSet res = ps.executeQuery();
            res.next();
            int length = res.getInt("length");

            String sqlStr3 = "select accX, accY, accZ, flexMajeur, flexAnnul, flexAuric FROM Data where idReinforcement = ? order by idMesure";
            PreparedStatement ps3 = conn.prepareStatement(sqlStr3);
            ps3.setString(1, idReinforcement);
            ResultSet res3 = ps3.executeQuery();
            ArrayList<Data> myData = new ArrayList<Data>();
            while (res3.next()){
            double accX = res3.getDouble("accX");
            double accY = res3.getDouble("accY");
            double accZ = res3.getDouble("accZ");
            boolean flexMajeur = res3.getBoolean("flexMajeur");
            boolean flexAnnul = res3.getBoolean("flexAnnul");
            boolean flexAuric = res3.getBoolean("flexAuric");

            Data madonnee = new Data(accX, accY, accZ, getvOut(flexMajeur), getvOut(flexAnnul), getvOut(flexAuric));
            myData.add(madonnee);
        }


            String sqlStr2 = "select nomGeste FROM userGestures where idGesture = ?";
            PreparedStatement ps2 = conn.prepareStatement(sqlStr2);
            ps2.setString(1, idGesture);

            //execution de la requete
            ResultSet res2 = ps2.executeQuery();
            res2.next();
            String nomGeste = res2.getString("nomGeste");

            DataSet mondataset = new DataSet(myData,nomGeste);
            return mondataset;


        }
        catch(Exception e){
            infoBox(e.getMessage(),"Erreur");

        }
        return new DataSet(new ArrayList<Data>(), "null");
    }
    public Gesture recupererGesture(String idGesture, String nomGeste) {
        try {
            String usedb = "use OThWXKoKWM";

            Statement stmt = conn.createStatement();
            //execution de la requete
            stmt.executeQuery(usedb);
            String sqlStr = "select idReinforcement FROM Dataset where idGesture = ?";
            PreparedStatement ps = conn.prepareStatement(sqlStr);
            ps.setString(1, idGesture);

            String sqlStr2 = "select count(idReinforcement) FROM Dataset where idGesture = ?";
            PreparedStatement ps2 = conn.prepareStatement(sqlStr2);
            ps2.setString(1, idGesture);

            //execution de la requete
            ResultSet res = ps.executeQuery();
            ResultSet res2 = ps2.executeQuery();
            res2.next();
            String[] reinforcements = new String[res2.getInt(1)];

            res.beforeFirst();


            int i =0;
            while (res.next()) {
                reinforcements[i] = res.getString(1);
                i++;
            }

            LinkedList<DataSet> mySets = new LinkedList<>();
            for (int j =0; j<reinforcements.length; j++){
                mySets.add(recupererDataset(idGesture, reinforcements[j]));
            }
            Shortcut raccourci = new Shortcut(recupererActions(idGesture));
            Gesture gesture = new Gesture(mySets, nomGeste, raccourci);
            return gesture;


        }
        catch(Exception e){
            infoBox(e.getMessage(),"Erreur");
            ArrayList<ActionR> actions = new ArrayList<>();
            Shortcut raccourci = new Shortcut(actions);
            return new Gesture(new LinkedList<DataSet>(), "null", raccourci);
        }

    }

    public  LinkedList<Gesture> recupererHistory(String idUser) {
        try {
            String usedb = "use OThWXKoKWM";

            Statement stmt = conn.createStatement();
            //execution de la requete
            stmt.executeQuery(usedb);
            String sqlStr = "select idGesture, nomGeste FROM userGestures where idUser = ?";
            PreparedStatement ps = conn.prepareStatement(sqlStr);
            ps.setString(1, idUser);

            String sqlStr2 = "select count(idGesture) FROM userGestures where idUser = ?";
            PreparedStatement ps2 = conn.prepareStatement(sqlStr2);
            ps2.setString(1, idUser);

            //execution de la requete
            ResultSet res = ps.executeQuery();
            ResultSet res2 = ps2.executeQuery();

            LinkedList<Gesture> history = new LinkedList<>();
            res.beforeFirst();

            int i =0;
            while (res.next()) {
                history.add(recupererGesture(res.getString("idGesture"),res.getString("nomGeste")));
                i++;

            }

            return history;


        }
        catch(Exception e){
            infoBox(e.getMessage(),"Erreur");

        }
        return new LinkedList<Gesture>();
    }
    public ArrayList<ActionR> recupererActions(String idGesture) {
        ArrayList<ActionR> listeActions = new ArrayList<>();
        try {
            String usedb = "use OThWXKoKWM";

            Statement stmt = conn.createStatement();
            //execution de la requete
            stmt.executeQuery(usedb);
            String sqlStr = "select indexAction, myType, coordX, coordY, codeClavier, cmdCode FROM actions where idGesture = ? order by indexAction";
            PreparedStatement ps = conn.prepareStatement(sqlStr);
            ps.setString(1, idGesture);

            //execution de la requete
            ResultSet res = ps.executeQuery();

            res.beforeFirst();
            String myType = "";
            int coordX = 0;
            int coordY = 0;
            int codeClavier = 0;
            String cmdCode = "";
            while (res.next()) {


                try {

                    myType = res.getString("myType");
                    coordX = res.getInt("coordX");
                    coordY = res.getInt("coordY");
                    codeClavier = res.getInt("codeClavier");
                    cmdCode = res.getString("cmdCode");
                    ActionR action;

                    switch(myType){
                        default:
                           // action = new ActionR(AType.MOUSEPRESS);
                        case "MOUSEMOVE": action = new ActionR(AType.MOUSEMOVE, new Point(coordX, coordY));
                            break;

                        case "MOUSEPRESS" : action = new ActionR(AType.MOUSEPRESS,codeClavier);
                            break;

                        case "MOUSERELEASE" : action = new ActionR(AType.MOUSERELEASE,codeClavier);
                            break;

                        case "KEYPRESS" : action = new ActionR(AType.KEYPRESS, codeClavier);
                            break;

                        case "KEYRELEASE" : action = new ActionR(AType.KEYRELEASE, codeClavier);
                            break;

                        case "COMMAND" : action = new ActionR(cmdCode);
                        break;
                    }
                    listeActions.add(action);
                }catch(NullPointerException e){
                    System.out.println(e);
                }
            }
        }
        catch(Exception e){
            infoBox(e.getMessage(),"Erreur");

        }
        return listeActions;
    }
    public void insertUserSettings(String username, String[] prefs) {
        try {
            String usedb = "use OThWXKoKWM";
            Statement stmt = conn.createStatement();

            //execution de la requete
            stmt.executeQuery(usedb);
            String sqlStr = "INSERT INTO userSettings (idUser, hue, hueThresh, saturation, valuee, pourcentage, offsetX, offsetY) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps1 = conn.prepareStatement(sqlStr);
            ps1.setString(1,username);
            for(int i = 0; i < prefs.length; i++){

                ps1.setString(i+2, prefs[i]);
            }
            ps1.executeUpdate();

        }
        catch(Exception e) {

            e.printStackTrace();
        }
    }

    public void deleteUserSettings(String username) {
        try {
            String usedb = "use OThWXKoKWM";
            Statement stmt = conn.createStatement();

            //execution de la requete
            stmt.executeQuery(usedb);
            String sqlStr = " DELETE FROM userSettings WHERE idUser = ? ";
            PreparedStatement ps1 = conn.prepareStatement(sqlStr);
            ps1.setString(1,username);
            ps1.executeUpdate();

        }
        catch(Exception e){

            e.printStackTrace();
        }
    }



    public String[] recupererData(String username){
        try {
            String usedb = "use OThWXKoKWM";

            Statement stmt = conn.createStatement();
            //execution de la requete
            stmt.executeQuery(usedb);
            String sqlStr = "select hue, hueThresh, saturation, valuee, pourcentage, offsetX, offsetY FROM userSettings where idUser = ?";
            PreparedStatement ps = conn.prepareStatement(sqlStr);
            ps.setString(1, username);

            //execution de la requete
            ResultSet res = ps.executeQuery();

            String hue= res.getString("hue");
            String hueThresh = res.getString("hueThresh");
            String saturation= res.getString("saturation");
            String value= res.getString("valuee");
            String pourcentage = res.getString("pourcentage");
            String offsetX = res.getString("offsetX");
            String offsetY = res.getString("offsetY");

            String[] ret = {hue, hueThresh, saturation,value, pourcentage, offsetX, offsetY};
              return ret;


        }
        catch(Exception e){
            infoBox(e.getMessage(),"Erreur");

        }
        return null;
    }

    public LinkedList<String> recupererUsers(){
        try {
            String usedb = "use OThWXKoKWM";

            Statement stmt = conn.createStatement();
            //execution de la requete
            stmt.executeQuery(usedb);
            String sqlStr = "select idUser FROM userSettings";
            PreparedStatement ps = conn.prepareStatement(sqlStr);

            //execution de la requete
            ResultSet res = ps.executeQuery();
            LinkedList<String> users = new LinkedList<>();
            res.beforeFirst();

            int i =0;
            while (res.next()) {
                users.add(res.getString("idUser"));
            }
            return users;


        }
        catch(Exception e){
           // infoBox(e.getMessage(),"Erreur");

        }
        return null;
    }

}


