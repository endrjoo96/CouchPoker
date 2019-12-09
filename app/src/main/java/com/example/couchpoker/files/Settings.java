package com.example.couchpoker.files;

import com.example.couchpoker.MainActivity;
import com.example.couchpoker.string_keys.KEYWORD;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Settings {
    private static String _path = MainActivity.context.getApplicationInfo().dataDir + "/config.txt";
    private static String _username = null, _id = null;


    public static boolean isFileCorrect() {
        File f = new File(_path);
        if (f.exists()) {
            if((_id = readId())!=null && (_username = readUsername()) != null){
                return true;
            }
        }
        return false;
    }

    public static void saveUsername(String username){
        _username = username;
        File f = new File(_path);
        if(!f.exists())
            try {
                f.createNewFile();
            } catch (Exception ex){
                ex.printStackTrace();
            }
        if(getID()==null)
            _id = generateId();
        try {
            FileWriter writer = new FileWriter(f);
            String stuffToWrite = String.format("%s|%s\n%s|%s", KEYWORD.FILE.ID, getID(), KEYWORD.FILE.USERNAME, username);
            writer.write(stuffToWrite);
            writer.close();
        } catch (IOException ioex){
            ioex.printStackTrace();
        }
    }

    public static String getUsername(){
        if(_username==null)
            _username = readUsername();
        return _username;
    }

    public static String getID(){
        if(_id==null)
            _id = readId();
        return _id;
    }

    private static String readId() {
        String str = readFromFile(KEYWORD.FILE.ID);
        if (str != null)
            if (str.length() == 256)
                return str;
        return null;
    }

    private static String readUsername(){
        return readFromFile(KEYWORD.FILE.USERNAME);
    }

    private static String readFromFile(String KeyToExtract){
        File f = new File(_path);
        BufferedReader reader;
        String readedLine="";
        String value=null;
        try {
            reader = new BufferedReader(new FileReader(f));
            do {
                readedLine = reader.readLine();
            } while (!(readedLine == null || readedLine.substring(0, readedLine.indexOf("|")).equals(KeyToExtract)));
            if(readedLine!=null){
                if(!readedLine.isEmpty())
                    value = readedLine.substring(readedLine.indexOf("|")+1);
            }
        }
        catch (FileNotFoundException ex) {}
        catch (IOException ioex){}

        return value;
    }

    private static String generateId(){
        String ID="";
        Random r = new Random();
        for (int i=0;i<256;i++){
            ID += (Integer.toHexString(r.nextInt(16)));
        }
        return ID;
    }
}
