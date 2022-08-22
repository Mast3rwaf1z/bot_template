package win.skademaskinen;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.dv8tion.jda.api.interactions.ModalInteraction;

public class Config {
    public static ArrayList<ModalInteraction> modals = new ArrayList<ModalInteraction>();
    
    public static JSONObject getConfig() throws IOException, ParseException{
        JSONParser parser = new JSONParser();
        try(FileReader reader = new FileReader("config.json")){
            return (JSONObject) parser.parse(reader);
        }catch(FileNotFoundException e){
            return null;
        }
    }
    
    public static JSONObject getFile(String file) throws IOException, ParseException{
        JSONParser parser = new JSONParser();
        try(FileReader reader = new FileReader(file)){
            return (JSONObject) parser.parse(reader);
        }catch(FileNotFoundException e){
            return null;
        }
    }
}
