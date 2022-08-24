package win.skademaskinen;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.dv8tion.jda.api.interactions.ModalInteraction;

public class Config {
    public static ArrayList<ModalInteraction> modals = new ArrayList<ModalInteraction>();
    
    @SuppressWarnings("unchecked")
    public static HashMap<String, Object> getConfig() throws IOException, ParseException{
        JSONParser parser = new JSONParser();
        try(FileReader reader = new FileReader("config.json")){
            return (JSONObject) parser.parse(reader);
        }catch(FileNotFoundException e){
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    public static HashMap<String, Object> getFile(String file) throws IOException, ParseException{
        JSONParser parser = new JSONParser();
        try(FileReader reader = new FileReader(file)){
            return (JSONObject) parser.parse(reader);
        }catch(FileNotFoundException e){
            return null;
        }
    }
}
