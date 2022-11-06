package win.skademaskinen.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


public class Config {
    public static ArrayList<ModalData> modals = new ArrayList<ModalData>();
    private static String path = "files/config.json";
    private static JSONObject config;

    static void load() throws JSONException, FileNotFoundException{
        config = new JSONObject(new JSONTokener(new FileInputStream(new File(path))));
    }
    
    public static JSONObject getConfig() throws JSONException, FileNotFoundException {
        load();
        return config;
    }
    
    public static JSONObject getFile(String file){
        try {
            return new JSONObject(new JSONTokener(new FileInputStream(new File(file))));
        } catch (JSONException | FileNotFoundException e) {
            Colors.exceptionHandler(e);
            return null;
        }
    }

    public static void writeFile(String file, JSONObject data){
	    try(FileWriter writer = new FileWriter(file)){
	    	writer.write(data.toString(4));
	    }
        catch(IOException e){
            Colors.exceptionHandler(e);
        }
    }
}
