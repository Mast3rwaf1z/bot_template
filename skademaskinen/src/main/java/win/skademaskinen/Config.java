package win.skademaskinen;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class Config {
    
    public static JSONObject getConfig(){
        return getJSON("config.json");
    }
    
    public static JSONObject getJSON(String path){
        try(FileReader reader = new FileReader(path)) {
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            String jsonString = "";
            while((line = bufferedReader.readLine()) != null){
                jsonString+=line;
            }
            return new JSONObject(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void writeJSON(JSONObject object, String file){
        try(FileWriter writer = new FileWriter(file)){
            writer.write(object.toString(4));
            writer.flush();
        }catch(IOException e){}
    }
    public static JSONObject getPoll(String id){
        return (JSONObject) getJSON("polls.json").get(id);
    }

    public static void registerNewPoll(String id){
        JSONObject current = getJSON("polls.json");
        current.put(id, new JSONObject());
        writeJSON(current, "polls.json");
    }
    public static void addMemberToPoll(String memberId, String pollId){
        JSONObject poll = getPoll(pollId);
        poll.put(memberId, new ArrayList<>());
        JSONObject current = getJSON("polls.json");
        current.put(pollId, poll);
        writeJSON(current, "polls.json");
    }
    public static void modifyPollEntryForMember(String memberId, String pollId, String buttonId){
        JSONObject poll = getPoll(pollId);
        if(poll.getJSONArray(memberId).toList().contains(buttonId)){
            int index = poll.getJSONArray(memberId).toList().indexOf(buttonId);
            poll.getJSONArray(memberId).remove(index);
        }
        else{
            poll.getJSONArray(memberId).put(buttonId);
        }
        JSONObject current = getJSON("polls.json");
        current.put(pollId, poll);
        writeJSON(current, "polls.json");
    }
    public static JSONArray getPollEntriesForMember(String memberId, String pollId){
        return getPoll(pollId).getJSONArray(memberId);
    }
}
