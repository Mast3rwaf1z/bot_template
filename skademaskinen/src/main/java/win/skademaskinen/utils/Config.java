package win.skademaskinen.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


public class Config {
    public static ArrayList<ModalData> modals = new ArrayList<ModalData>();
    public static String path = "files/settings";
    private String token;
    private String clientId;
    private String clientSecret;
    private String deploymentHost;
    private static Config config;

    public Config(){
        try(BufferedReader reader = new BufferedReader(new FileReader(new File(path)))){
            for(String line : reader.lines().toList()){
                String key = line.split("=")[0];
                String value = line.split("=")[1];
                switch(key){
                    case "token":
                        token = value;
                        break;
                    case "clientId":
                        clientId = value;
                        break;
                    case "clientSecret":
                        clientSecret = value;
                        break;
                    case "deploymentHost":
                        deploymentHost = value;
                        break;
                    default:
                        Shell.printer(Colors.red("Error: invalid setting: ")+Colors.black(key));
                }
            }
        }
        catch(IOException e){
            Colors.exceptionHandler(e);
        }
    }

    public static Config getConfig(){
        if(config != null){
            return config;
        }
        else{
            config = new Config();
            return config;
        }
    }

    public String getToken(){
        return token;
    }

    public String getClientId(){
        return clientId;
    }

    public String getClientSecret(){
        return clientSecret;
    }

    public String getDeploymentHost(){
        return deploymentHost;
    }
    
    public static JSONObject readJSON(String file){
        try {
            return new JSONObject(new JSONTokener(new FileInputStream(new File(file))));
        } catch (JSONException | FileNotFoundException e) {
            Colors.exceptionHandler(e);
            return null;
        }
    }

    public static void writeJSON(String file, JSONObject data){
	    try(FileWriter writer = new FileWriter(file)){
	    	writer.write(data.toString(4));
	    }
        catch(IOException e){
            Colors.exceptionHandler(e);
        }
    }
}
