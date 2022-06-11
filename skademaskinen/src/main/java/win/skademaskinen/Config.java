package win.skademaskinen;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Config {
    
    public static JSONObject getConfig() throws IOException, ParseException{
        JSONParser parser = new JSONParser();
        try(FileReader reader = new FileReader("config.json")){
            return (JSONObject) parser.parse(reader);
        }catch(FileNotFoundException e){
            return null;
        }
    }
}
