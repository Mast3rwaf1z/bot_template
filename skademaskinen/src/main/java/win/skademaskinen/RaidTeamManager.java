package win.skademaskinen;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

public class RaidTeamManager {
	static private String raidTeamMessageId = "987484728724705360";
	public static void update(Guild guild){
		try {
			if(!InetAddress.getLocalHost().getHostName().equalsIgnoreCase("Skademaskinen")){
				return;
			}
		} catch (UnknownHostException e) {
			Colors.exceptionHandler(e, false);
		}
		Message message = guild.getId().equals("642852517197250560") ? 
			guild.getTextChannelById("987475931004284978").getHistoryAround("987484728724705360", 1).complete().getMessageById("987484728724705360") :
			guild.getTextChannelById("889964274229854248").getHistoryAround("1011047944075628714", 1).complete().getMessageById("1011047944075628714");

		try {
			JSONObject team = (JSONObject) Config.getFile("team.json");
			EmbedBuilder builder = new EmbedBuilder()
				.setTitle("Raid Team")
				.setDescription("This is the raid team, this message will get updated with raid team members!")
				.setImage("https://cdn.discordapp.com/attachments/642853163774509116/922532262459867196/The_nut_hut.gif");
			
			ArrayList<String> tanks = new ArrayList<String>();
			ArrayList<String> healers = new ArrayList<String>();
			ArrayList<String> ranged = new ArrayList<String>();
			ArrayList<String> melee = new ArrayList<String>();
			for(Object key : team.keySet()){
				JSONObject raider = (JSONObject) team.get(key);
				switch(raider.get("role").toString().toLowerCase()){
					case "melee damage":
						melee.add((String)key);
						break;
					case "ranged damage":
						ranged.add((String)key);
						break;
					case "tank":
						tanks.add((String)key);
						break;
					case "healer":
						healers.add((String)key);

				}
			}
			builder.appendDescription("\n**Raid team composition:** "+tanks.size()+"/"+healers.size()+"/"+(ranged.size()+melee.size()));

			App.setStatus("Raid team: " + tanks.size()+"/"+healers.size()+"/"+(ranged.size()+melee.size()));
			String tanksMessage = "";
			for(String key : tanks){
				JSONObject raider = (JSONObject) team.get(key);
				tanksMessage+= "\n\n" + guild.retrieveMemberById(key).complete().getAsMention();
				tanksMessage+= "\n" + raider.get("name");
				if(!raider.get("server").toString().equalsIgnoreCase("argent-dawn")){
					tanksMessage+= " (" + raider.get("server") + ")";
				}
				tanksMessage+= "\n" + raider.get("class");
				tanksMessage+= "\n" + raider.get("spec");
				String ilvl = get_ilvl(raider.get("name").toString(), raider.get("server").toString());
				String avg_ilvl = get_avg_ilvl(raider.get("name").toString(), raider.get("server").toString());
				tanksMessage+= "\n" + ilvl + "/" + avg_ilvl + " ilvl";
			}
			builder.addField("Tanks:", tanksMessage, true); 
			String healersMessage = "";
			for(String key : healers){
				JSONObject raider = (JSONObject) team.get(key);
				healersMessage+= "\n\n" + guild.retrieveMemberById(key).complete().getAsMention();
				healersMessage+= "\n" + raider.get("name");
				if(!raider.get("server").toString().equalsIgnoreCase("argent-dawn")){
					healersMessage+= " (" + raider.get("server") + ")";
				}
				healersMessage+= "\n" + raider.get("class");
				healersMessage+= "\n" + raider.get("spec");
				String ilvl = get_ilvl(raider.get("name").toString(), raider.get("server").toString());
				String avg_ilvl = get_avg_ilvl(raider.get("name").toString(), raider.get("server").toString());
				healersMessage+= "\n" + ilvl + "/" + avg_ilvl + " ilvl";
			}
			builder.addField("Healers:", healersMessage, true);
			builder.addBlankField(false); 
			String rangedMessage = "";
			for(String key : ranged){
				JSONObject raider = (JSONObject) team.get(key);
				rangedMessage+= "\n\n" + guild.retrieveMemberById(key).complete().getAsMention();
				rangedMessage+= "\n" + raider.get("name");
				if(!raider.get("server").toString().equalsIgnoreCase("argent-dawn")){
					rangedMessage+= " (" + raider.get("server") + ")";
				}
				rangedMessage+= "\n" + raider.get("class");
				rangedMessage+= "\n" + raider.get("spec");
				String ilvl = get_ilvl(raider.get("name").toString(), raider.get("server").toString());
				String avg_ilvl = get_avg_ilvl(raider.get("name").toString(), raider.get("server").toString());
				rangedMessage+= "\n" + ilvl + "/" + avg_ilvl + " ilvl";
			}
			builder.addField("Ranged Damage:", rangedMessage, true); 
			String meleeMessage = "";
			for(String key : melee){
				JSONObject raider = (JSONObject) team.get(key);
				meleeMessage+= "\n\n" + guild.retrieveMemberById(key).complete().getAsMention();
				meleeMessage+= "\n" + raider.get("name");
				if(!raider.get("server").toString().equalsIgnoreCase("argent-dawn")){
					meleeMessage+= " (" + raider.get("server") + ")";
				}
				meleeMessage+= "\n" + raider.get("class");
				meleeMessage+= "\n" + raider.get("spec");
				String ilvl = get_ilvl(raider.get("name").toString(), raider.get("server").toString());
				String avg_ilvl = get_avg_ilvl(raider.get("name").toString(), raider.get("server").toString());
				meleeMessage+= "\n" + ilvl + "/" + avg_ilvl + " ilvl";
			}
			builder.addField("Melee Damage:", meleeMessage, true); 
			message.editMessageEmbeds(builder.build()).queue();
			
		} catch (IOException | ParseException | NullPointerException e) {
			Colors.exceptionHandler(e, false);
		}
	}

	@SuppressWarnings("deprecation")
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
			Colors.exceptionHandler(e, false);
		}
		return token;
	}

	@SuppressWarnings("deprecation")
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
		} catch (IOException | InterruptedException | ParseException | NullPointerException e) {
			Colors.exceptionHandler(e, false);
		}
		return _class;
	}

	@SuppressWarnings("deprecation")
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
		} catch (IOException | InterruptedException | ParseException | NullPointerException e) {
			Colors.exceptionHandler(e, false);
		}
		return spec;
	}

	@SuppressWarnings("deprecation")
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
		} catch (IOException | InterruptedException | ParseException | NullPointerException e) {
			Colors.exceptionHandler(e, false);
		}
		return ilvl;
	}

	@SuppressWarnings("deprecation")
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
		} catch (IOException | InterruptedException | ParseException | NullPointerException e) {
			Colors.exceptionHandler(e, false);
		}
		return avg_ilvl;
	}

	@SuppressWarnings("deprecation")
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
		} catch (IOException | InterruptedException | ParseException | NullPointerException e) {
			Colors.exceptionHandler(e, false);
		}
		return image;
	}

	static public String raidTeamMessageId(){
		return raidTeamMessageId;
	}

	
	public static void removeRaider(Member member) {
		try {
			HashMap<String, Object> team = Config.getFile("team.json");
			team.remove(member.getId());
			try(FileWriter writer = new FileWriter("team.json")){
				writer.write(((JSONObject) team).toJSONString());
			}
			update(member.getGuild());
		} catch (IOException | ParseException | NullPointerException e) {
			Colors.exceptionHandler(e, false);
		}
		
	}
	public static void removeRaider(User user, Guild guild) {
		try {
			HashMap<String, Object> team = Config.getFile("team.json");
			team.remove(user.getId());
			try(FileWriter writer = new FileWriter("team.json")){
				writer.write(((JSONObject) team).toJSONString());
			}
			update(guild);
		} catch (IOException | ParseException | NullPointerException e) {
			Colors.exceptionHandler(e, false);
		}
		
	}
	public static void addRaider(String name, String server, String role, String id, Guild guild) {
		try {
			HashMap<String, Object> team = Config.getFile("team.json");
			HashMap<String, Object> raider = new HashMap<String, Object>();
			String cap = name.substring(0, 1).toUpperCase() + name.substring(1);
			raider.put("name", cap);
			raider.put("server", server);
			raider.put("role", role);
			raider.put("class", get_class((String)raider.get("name"), (String) raider.get("server")));
			raider.put("spec", get_spec((String)raider.get("name"), (String)raider.get("server")));
			raider.put("ilvl", get_ilvl((String)raider.get("name"), (String)raider.get("server")));
			raider.put("avg_ilvl", get_avg_ilvl((String)raider.get("name"), (String)raider.get("server")));
			team.put(id, raider);
			try(FileWriter writer = new FileWriter("team.json")){
				writer.write(((JSONObject)team).toJSONString());
			}
			update(guild);
		} catch (IOException | ParseException | NullPointerException e) {
			Colors.exceptionHandler(e, false);
		}
	}
	@SuppressWarnings("unchecked")
	public static void addRequirement(String type, String value){
		try {
			switch(type){
				case "filled":
				case "filled roles":
					type = "filled_roles";
					break;
				case "needed":
					type = "needed_classes";
					case "needed classes":
					break;
				case "preferred":
				case "preferred roles":
					type = "preferred_roles";
			}
			HashMap<String, Object> file = Config.getFile("team_requirements.json");
			HashMap<String, Object> requirements = (HashMap<String, Object>) file.get("raid_form");
			ArrayList<String> requirement = (ArrayList<String>) requirements.get(type);
			requirement.add(value);
			requirements.put(type, requirement);
			file.put("raid_form", requirements);
			try(FileWriter writer = new FileWriter("team_requirements.json")){
				writer.write(((JSONObject)file).toJSONString());
			}
		} catch (IOException | ParseException | NullPointerException e) {
			Colors.exceptionHandler(e, false);
		}
	}

	@SuppressWarnings("unchecked")
	public static void removeRequirement(String type, String value){
		try {
			switch(type){
				case "filled":
				case "filled roles":
					type = "filled_roles";
					break;
				case "needed":
				case "needed classes":
					type = "needed_classes";
					break;
				case "preferred":
				case "preferred roles":
					type = "preferred_roles";
			}
			HashMap<String, Object> file = Config.getFile("team_requirements.json");
			HashMap<String, Object> requirements = (HashMap<String, Object>) file.get("raid_form");
			ArrayList<String> requirement = (ArrayList<String>) requirements.get(type);
			requirement.remove(value);
			requirements.put(type, requirement);
			file.put("raid_form", requirements);
			try(FileWriter writer = new FileWriter("team_requirements.json")){
				writer.write(((JSONObject)file).toJSONString());
			}
		} catch (IOException | ParseException | NullPointerException e) {
			Colors.exceptionHandler(e, false);
		}
	}

	@SuppressWarnings("unchecked")
	public static void setIlvlRequirement(int ilvl){
		try {
			HashMap<String, Object> file = Config.getFile("team_requirements.json");
			HashMap<String, Object> requirements = (HashMap<String, Object>) file.get("raid_form");
			requirements.put("minimum_ilvl", ilvl);
			file.put("raid_form", requirements);
			try(FileWriter writer = new FileWriter("team_requirements.json")){
				writer.write(((JSONObject)file).toJSONString());
			}
			
		} catch (IOException | ParseException | NullPointerException e) {
			Colors.exceptionHandler(e, false);
		}
	}
	public static void printRaider(JSONObject raider, String name, Guild guild){
		System.out.println();
		System.out.print(Colors.green("Discord name:                   "));
		System.out.println(guild.retrieveMemberById(name).complete().getEffectiveName());
		System.out.print(Colors.green("Character name:                 "));
		System.out.println(raider.get("name"));
		System.out.print(Colors.green("Server:                         "));
		System.out.println(raider.get("server"));
		System.out.print(Colors.green("Role:                           "));
		System.out.println(raider.get("role"));
		System.out.print(Colors.green("Class:                          "));
		System.out.println(raider.get("class"));
		System.out.print(Colors.green("Specialization:                 "));
		System.out.println(raider.get("spec"));
		System.out.print(Colors.green("Item level/average item level:  "));
		System.out.println(raider.get("ilvl")+"/"+raider.get("avg_ilvl"));

	}

}
