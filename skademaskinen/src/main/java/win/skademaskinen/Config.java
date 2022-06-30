package win.skademaskinen;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Config {
    
    public static JSONObject getConfig() throws IOException, ParseException{
        return getJSON("config.json");
    }
    
    public static JSONObject getJSON(String file) throws IOException, ParseException{
        JSONParser parser = new JSONParser();
        try(FileReader reader = new FileReader(file)){
            return (JSONObject) parser.parse(reader);
        }catch(FileNotFoundException e){
            return null;
        }
    }

    public static ArrayList<String> getPoll(String id) throws IOException, ParseException{
        JSONParser parser = new JSONParser();
        try(FileReader reader = new FileReader("polls.json")){
            JSONObject json = (JSONObject) parser.parse(reader);
            return (ArrayList<String>) json.get(id);
        }catch(FileNotFoundException e){
            return null;
        }
    }

    public static void registerNewPoll(String id) throws IOException, ParseException{
        JSONObject current = getJSON("polls.json");
        
    }
}
