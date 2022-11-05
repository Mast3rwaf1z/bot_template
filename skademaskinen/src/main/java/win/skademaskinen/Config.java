package win.skademaskinen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


public class Config {
    public static ArrayList<ModalData> modals = new ArrayList<ModalData>();
    private static String path = "config.json";
    private static JSONObject config;

    static void load() throws JSONException, FileNotFoundException{
        config = new JSONObject(new JSONTokener(new FileInputStream(new File(path))));
    }
    
    public static JSONObject getConfig() throws JSONException, FileNotFoundException {
        load();
        return config;
    }
    
    public static JSONObject getFile(String file) throws JSONException, FileNotFoundException {
        return new JSONObject(new JSONTokener(new FileInputStream(new File(file))));
    }
}
