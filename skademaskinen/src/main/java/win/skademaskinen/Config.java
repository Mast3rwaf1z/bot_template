package win.skademaskinen;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Config {
    
    public static JSONObject getConfig(){
        return getJSON("config.json");
    }
    
    public static JSONObject getJSON(String file){
        JSONParser parser = new JSONParser();
        try(FileReader reader = new FileReader(file)){
            return (JSONObject) parser.parse(reader);
        }catch(IOException | ParseException e){
            return null;
        }
    }

    public static ArrayList<String> getPoll(String id){
        JSONParser parser = new JSONParser();
        try(FileReader reader = new FileReader("polls.json")){
            JSONObject json = (JSONObject) parser.parse(reader);
            return (ArrayList<String>) json.get(id);
        }catch(IOException | ParseException e){
            return null;
        }
    }

    public static void registerNewPoll(String id){
        JSONObject current = getJSON("polls.json");
        current.put(id, new JSONArray());
        try(FileWriter writer = new FileWriter("polls.json")){
            writer.write(current.toJSONString());
            writer.flush();
        }catch(IOException e){
        }
    }
    public static void addMemberToPoll(String memberId, String pollId){
        ArrayList<String> poll = getPoll(pollId);
        poll.add(memberId);
        JSONObject current = getJSON("polls.json");
        current.put(pollId, poll);
        try(FileWriter writer = new FileWriter("polls.json")){
            writer.write(current.toJSONString());
            writer.flush();
        }catch(IOException e){

        }

    }
}
