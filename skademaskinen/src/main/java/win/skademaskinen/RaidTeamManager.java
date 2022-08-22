package win.skademaskinen;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.sym.Name;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

public class RaidTeamManager {
	static private String raidTeamMessageId = "987484728724705360";
	public static Map<Member, Character> getTeam(Guild guild) throws SQLException{
		//Name:	[id, main, class, spec, role, status, average ilvl, equipped ilvl]
		HashMap<Member, Character> team = new HashMap<>();
		DatabaseHandler databaseHandler = DatabaseHandler.getHandler();
		ResultSet raid_teamResultSet = databaseHandler.getTable("raid_team");
		while(raid_teamResultSet.next()){
			System.out.println(raid_teamResultSet.getString("id"));
			team.put(guild.retrieveMemberById(raid_teamResultSet.getString("id")).complete(), new Character(
				raid_teamResultSet.getString("main"), 
				raid_teamResultSet.getString("class"), 
				raid_teamResultSet.getString("specialization"), 
				raid_teamResultSet.getInt("role"), 
				raid_teamResultSet.getInt("status"), 
				raid_teamResultSet.getInt("average"), 
				raid_teamResultSet.getInt("equipped")));
		}
		return team;
	}
	public static void update(Guild guild){
		Message message = guild.getId().equals("642852517197250560") ? 
			guild.getTextChannelById("987475931004284978").getHistoryAround("987484728724705360", 1).complete().getMessageById("987484728724705360") :
			guild.getTextChannelById("889964274229854248").getHistoryAround("1011047944075628714", 1).complete().getMessageById("1011047944075628714");

		try {
			JSONObject team = (JSONObject) Config.getFile("team.json");
			EmbedBuilder builder = new EmbedBuilder()
				.setTitle("Raid Team")
				.setDescription("This is the raid team, this message will get updated with raid team members!")
				.setImage("https://cdn.discordapp.com/attachments/642853163774509116/922532262459867196/The_nut_hut.gif");
			
			
			for(Object key : team.keySet()){
				Object value = team.get(key);
				JSONObject raider = (JSONObject) value;
				builder.appendDescription("\n" + guild.retrieveMemberById((String) key).complete().getAsMention());
				builder.appendDescription(": " + (String) raider.get("name"));
				builder.appendDescription(" | **" + (String) raider.get("class")+"**");
				builder.appendDescription(" - **" + (String) raider.get("spec") + "**");
				builder.appendDescription("\nItem level: " +  String.valueOf(raider.get("ilvl")) + "/" + String.valueOf(raider.get("avg_ilvl")));
			}
			message.editMessageEmbeds(builder.build()).queue();
			
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static public String get_wow_api_token(){
		String token = "";
		try {
			JSONObject api = (JSONObject) Config.getConfig().get("wow_api");
			String id = (String) api.get("client_id");
			String secret = (String) api.get("client_secret");
			String command = "curl -u"+id+":"+secret+" -d grant_type=client_credentials https://eu.battle.net/oauth/token";
			JSONParser parser = new JSONParser();
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec(command);
			process.waitFor();
			token = (String) ((JSONObject) parser.parse(new BufferedReader(new InputStreamReader(process.getInputStream())))).get("access_token");
		} catch (IOException | ParseException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return token;
	}

	static public String get_class(String name, String server){
		String token = get_wow_api_token();
		String command = "curl https://eu.api.blizzard.com/profile/wow/character/"+server.toLowerCase()+"/"+name.toLowerCase()+"?namespace=profile-eu&locale=en_GB&access_token="+token;
		String _class = "";
		try {
			Process process = Runtime.getRuntime().exec(command);
			JSONParser parser = new JSONParser();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			process.waitFor();
			JSONObject data = (JSONObject) parser.parse(reader);
			_class = (String) ((JSONObject) data.get("character_class")).get("name");
		} catch (IOException | InterruptedException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return _class;
	}

	static public String get_spec(String name, String server){
		String token = get_wow_api_token();
		String command = "curl https://eu.api.blizzard.com/profile/wow/character/"+server.toLowerCase()+"/"+name.toLowerCase()+"?namespace=profile-eu&locale=en_GB&access_token="+token;
		String spec = "";
		try {
			Process process = Runtime.getRuntime().exec(command);
			JSONParser parser = new JSONParser();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			process.waitFor();
			JSONObject data = (JSONObject) parser.parse(reader);
			spec = (String) ((JSONObject) data.get("active_spec")).get("name");
		} catch (IOException | InterruptedException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return spec;
	}

	static public String get_ilvl(String name, String server){
		String token = get_wow_api_token();
		String command = "curl https://eu.api.blizzard.com/profile/wow/character/"+server.toLowerCase()+"/"+name.toLowerCase()+"?namespace=profile-eu&locale=en_GB&access_token="+token;
		String ilvl = "";
		try {
			Process process = Runtime.getRuntime().exec(command);
			JSONParser parser = new JSONParser();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			process.waitFor();
			JSONObject data = (JSONObject) parser.parse(reader);
			ilvl = String.valueOf(data.get("equipped_item_level"));
		} catch (IOException | InterruptedException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ilvl;
	}

	static public String get_avg_ilvl(String name, String server){
		String token = get_wow_api_token();
		String command = "curl https://eu.api.blizzard.com/profile/wow/character/"+server.toLowerCase()+"/"+name.toLowerCase()+"?namespace=profile-eu&locale=en_GB&access_token="+token;
		String avg_ilvl = "";
		try {
			Process process = Runtime.getRuntime().exec(command);
			JSONParser parser = new JSONParser();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			process.waitFor();
			JSONObject data = (JSONObject) parser.parse(reader);
			avg_ilvl = String.valueOf(data.get("average_item_level"));
		} catch (IOException | InterruptedException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return avg_ilvl;
	}

	static public String get_image(String name, String server){
		String token = get_wow_api_token();
		String command = "curl https://eu.api.blizzard.com/profile/wow/character/"+server.toLowerCase()+"/"+name.toLowerCase()+"/character-media?namespace=profile-eu&locale=en_GB&access_token="+token;
		String image = "";
		try {
			Process process = Runtime.getRuntime().exec(command);
			JSONParser parser = new JSONParser();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			process.waitFor();
			JSONObject data = (JSONObject) parser.parse(reader);
			image = (String) ((JSONObject)((JSONArray) data.get("assets")).get(0)).get("value");
		} catch (IOException | InterruptedException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return image;
	}

	static public String raidTeamMessageId(){
		return raidTeamMessageId;
	}

	public static void addRaider(List<ModalMapping> values, String userid, Guild guild) {
		try {
			JSONObject team = Config.getFile("team.json");
			JSONObject raider = new JSONObject();
			for(ModalMapping value : values){
				switch(value.getId()){
					case "name":
						raider.put("name", value.getAsString());
						break;
					case "server":
						raider.put("server", value.getAsString());
						break;
					case "role":
						raider.put("role", value.getAsString());
						break;
					case "raidtimes":
						if(value.getAsString().equalsIgnoreCase("yes")){
							raider.put("raid_times", true);
						}
						else{
							raider.put("raid_times", false);
						}
						break;
				}
			}
			raider.put("class", get_class((String)raider.get("name"), (String) raider.get("server")));
			raider.put("spec", get_spec((String)raider.get("name"), (String)raider.get("server")));
			raider.put("ilvl", get_ilvl((String)raider.get("name"), (String)raider.get("server")));
			raider.put("avg_ilvl", get_avg_ilvl((String)raider.get("name"), (String)raider.get("server")));
			team.put(userid, raider);
			try(FileWriter writer = new FileWriter("team.json")){
				writer.write(team.toJSONString());
			}
			update(guild);
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
