package win.skademaskinen.utils;

import java.io.IOException;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp.Capability;
import org.jline.widget.AutosuggestionWidgets;
import org.json.JSONArray;
import org.json.JSONObject;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import win.skademaskinen.App;
import win.skademaskinen.WorldOfWarcraft.RaidTeamManager;

public class Shell{
	private static Guild guild = null;
	private static TextChannel channel = null;
	private static LineReader reader = LineReaderBuilder.builder().build();

    public static String prompt(){
		if(guild == null){
			return "["+Colors.blue("The Nut Bot")+"] > ";
		}
		if(channel == null){
			return "["+Colors.blue("The Nut Bot" + Colors.green(" - ") + Colors.blue(guild.getName())) +"] > ";
		}
		return "["+Colors.blue("The Nut Bot" + Colors.green(" - ") + Colors.blue(guild.getName())) + Colors.green(" - ") + Colors.blue(channel.getName())+ "] > ";
    }

	public static void shell() throws IOException{
		AutosuggestionWidgets autosuggestionWidgets = new AutosuggestionWidgets(reader);
		autosuggestionWidgets.enable();
        for(String line = ""; !line.equals("exit"); line = reader.readLine(prompt())){
            String arguments[] = line.split(" ");
            switch(arguments[0]){
                case "":
                    break;
                case "help":
					help();
                    break;
                case "send":
					send(arguments);
                    break;
                case "guilds":
					guilds(arguments);
                    break;
				case "channels":
					channels(arguments);
					break;
                case "clear":
					clear();
                    break;
                case "team":
					team(arguments, line);
                    break;
				case "commands":
					commands(arguments);
                default:
                    printer(Colors.red("No such command!")+Colors.black(" [type help for a list of commands]"));
            }
        }
    }

	private static void commands(String[] arguments) {
		switch(arguments[1]){
			case "update":
				App.setCommands(true);
		}
	}

	private static void help(){
		printer(Colors.yellow(terminalWidthLine()));
		printer("\t"+Colors.blue("guilds")+":  Get info about a discord server");
		printer("\t"+Colors.blue("guilds")+":  Get info about a discord servers channels");
		printer("\t"+Colors.blue("send")+":    Send a message in a selected channel");
		printer("\t"+Colors.blue("clear")+":   clear the terminal");
		printer("\t"+Colors.blue("exit")+":    Stop the bot");
		printer("\t"+Colors.blue("team")+":    Show a list of all raiders on the raid team");
		printer(Colors.yellow(terminalWidthLine()));

	}
	private static void send(String[] arguments){
		if(arguments.length == 2){
			if(guild != null){
				if(channel != null){
					channel.sendMessage(arguments[1]).queue();
				}
				else{
					printer(Colors.red("Please select a channel"));
				}
			}
			else{
				printer(Colors.red("Please select a guild"));
			}
		}
	}
	private static void guilds(String[] arguments){
		if(arguments.length > 1){
			switch(arguments[1]){
				case "list":
					guildsList();
					break;
				case "help":
					guildsHelp();
					break;
				case "select":
					guildsSelect(arguments);
					break;
				case "reset":
					guild = null;
					channel = null;
					break;
				default:
					try{
						Guild guild = App.jda.getGuildById(arguments[1]);
						printer(Colors.green("Name:          ") + guild.getName());
						printer(Colors.green("ID:            ") + guild.getId());
						printer(Colors.green("Member count:  ") + guild.getMemberCount());
						printer(Colors.green("Channel count: ") + guild.getChannels().size() + "\n");
					}
					catch(NumberFormatException | NullPointerException e){
						Colors.exceptionHandler(e);
					}

			}
		}
		else{
			for(Guild guild : App.jda.getGuilds()){
				printer(Colors.green("Name:          ") + guild.getName());
				printer(Colors.green("ID:            ") + guild.getId());
				printer(Colors.green("Member count:  ") + guild.getMemberCount());
				printer(Colors.green("Channel count: ") + guild.getChannels().size() + "\n");
			}
		}
	}
	private static void guildsHelp() {
		printer(Colors.yellow(terminalWidthLine()));
		printer("\t"+Colors.blue("list")+":        Print the list of guilds");
		printer("\t"+Colors.blue("<guild id>")+":  Prints a guild in the list");
		printer("\t"+Colors.blue("select")+":  	  Selects a given guild");
		printer("\t"+Colors.blue("select")+":  	  Reset guild selection");
		printer(Colors.yellow(terminalWidthLine()));
	}

	private static void guildsSelect(String[] arguments) {
		if(arguments.length == 3){
			guild = App.jda.getGuildById(arguments[2]);
		}
		else{
			printer(Colors.red("Error: not enough arguments"));
		}
	}

	private static void guildsList() {
		for(Guild guild : App.jda.getGuilds()){
			printer(Colors.green("Name:          ") + guild.getName());
			printer(Colors.green("ID:            ") + guild.getId());
			printer(Colors.green("Member count:  ") + guild.getMemberCount());
			printer(Colors.green("Channel count: ") + guild.getChannels().size() + "\n");
		}
	}

	private static void channels(String[] arguments){
		if(guild != null){
			if(arguments.length > 1){
				switch(arguments[1]){
					case "list":
						channelsList();
						break;
					case "select":
						channelsSelect(arguments);
						break;
					case "reset":
						channel = null;
						break;
					default:
						printer(Colors.red("Error: invalid argument"));
				}

			}
			else{
				channelsList();
			}
		}
		else{
			printer(Colors.red("Error: please select a guild"));
		}

	}
	private static void channelsSelect(String[] arguments) {
		if(arguments.length == 3){
				channel = guild.getTextChannelById(arguments[2]);
		}
		else{
			printer(Colors.red("Error: not enough arguments"));
		}

	}

	private static void channelsList() {
		for(TextChannel channel : guild.getTextChannels()){
			printer("");
			printer(Colors.green("Name:	")+channel.getName());
			printer(Colors.green("ID:	")+channel.getId());
		}
	}
	private static void clear(){
		reader.getTerminal().puts(Capability.clear_screen);
	}
	private static void team(String[] arguments, String line){
		JSONObject team;
		team = Config.getFile("team.json");
		switch(arguments[1]){
			case "help":
				teamHelp();
				break;
			case "list":
				teamList(team);
				break;
			case "requirements":
				teamRequirements(arguments, line);
				break;
			case "add":
				teamAdd(arguments);
				break;
			case "remove":
				teamRemove(arguments);
				break;
			default:
				teamMember(arguments, team);
				break;
		}

	}
	private static void teamHelp(){
		printer(Colors.yellow(terminalWidthLine()));
		printer("\t"+Colors.blue("list")+":          List all members of the raid team");
		printer("\t"+Colors.blue("add")+":           Add a raider to the raid team");
		printer("\t"+Colors.blue("remove")+":        Removes a raider from the raid team");
		printer("\t"+Colors.blue("requirements")+":  Manages the requirements of the raid team");
		printer("\t"+Colors.blue("<member id>")+":   Shows a single member of the raid team");
		printer(Colors.yellow(terminalWidthLine()));

	}
	private static void teamList(JSONObject team){
		for(String key : team.keySet()){
			RaidTeamManager.printRaider((JSONObject) team.get(key), key, App.jda.getGuildById("642852517197250560"));
		}

	}
	private static void teamRequirements(String[] arguments, String line){
		if(arguments.length > 2){

			switch(arguments[2]){
				case "list":
				requirementsList();
				break;
				case "add":
				requirementsAdd(arguments, line);
				break;
			case "remove":
				requirementsRemove(arguments, line);
				break;
			case "setilvl":
				requirementsSetIlvl(arguments);
				break;
				case "help":
				requirementsHelp();
				break;
				default:
					printer(Colors.red("Error: invalid argument! ") + Colors.black("[Example: team requirements add filled melee damage]"));
				break;
				
			}
		}
		else{
			requirementsList();
		}

	}
	private static void teamMember(String[] arguments, JSONObject team){
		try{
			RaidTeamManager.printRaider((JSONObject) team.get(arguments[1]), arguments[1], App.jda.getGuildById("642852517197250560"));
		}
		catch(NumberFormatException e){
			printer(Colors.red("Error: invalid id"));
		}

	}
	private static void teamAdd(String[] arguments){
		if(arguments.length == 6){
			String id = arguments[2];
			String name = arguments[3];
			String _server = arguments[4];
			String role = arguments[5];
			if(role.equalsIgnoreCase("melee") || role.equalsIgnoreCase("ranged")){
				role+=" damage";
			}
			try{
				RaidTeamManager.addRaider(name, _server, role, id, App.jda.getGuildById("642852517197250560"));
				printer(Colors.green("Successfully added raider"));
			}
			catch(ErrorResponseException e){
				printer(Colors.red("Error: Failed to add raider"));
			}
		}
		else{
			printer(Colors.red("Error: Too few arguments"));
			printer(Colors.black("Example: [team add 214752462769356802 Skademanden argent-dawn melee]"));
		}

	}
	private static void teamRemove(String[] arguments){
		try{
			RaidTeamManager.removeRaider(App.jda.getGuildById("642852517197250560").retrieveMemberById(arguments[2]).complete());
			printer(Colors.green("Successfully removed raider"));
		}
		catch(ErrorResponseException | NullPointerException e){
			printer(Colors.red("Error: invalid id"));
		}

	}
	private static void requirementsList(){
		JSONObject raidForm;
		raidForm = (JSONObject) Config.getFile("team_requirements.json").get("raid_form");
		printer(Colors.yellow("Filled roles:"));
		for(Object role : (JSONArray) raidForm.get("filled_roles")){
			printer("\t"+role.toString());
		}
		printer(Colors.yellow("Preferred roles:"));
		for(Object role : (JSONArray) raidForm.get("preferred_roles")){
			printer("\t"+role.toString());
		}
		printer(Colors.yellow("Needed classes:"));
		for(Object _class : (JSONArray) raidForm.get("needed_classes")){
			printer("\t"+_class.toString());
		}
		printer(Colors.yellow("Minimum item level:"));
		printer("\t"+raidForm.get("minimum_ilvl"));
	}
	private static void requirementsAdd(String[] arguments, String line){
		RaidTeamManager.addRequirement(arguments[3], line.substring(arguments[0].length()+arguments[1].length()+arguments[2].length()+arguments[3].length()+4));
		printer(Colors.green("Successfully added requirement!"));

	}
	private static void requirementsRemove(String[] arguments, String line){
		RaidTeamManager.removeRequirement(arguments[3], line.substring(arguments[0].length()+arguments[1].length()+arguments[2].length()+arguments[3].length()+4));
		printer(Colors.green("Successfully removed requirement!"));

	}
	private static void requirementsSetIlvl(String[] arguments){
		RaidTeamManager.setIlvlRequirement(Integer.parseInt(arguments[3]));
		printer(Colors.green("Successfully set minimum item level requirement!"));

	}
	private static void requirementsHelp(){
		printer(Colors.yellow(terminalWidthLine()));
		printer("\t"+Colors.blue("list")+":          List all requirements");
		printer("\t"+Colors.blue("add")+":           Add a requirement to the raid team");
		printer("\t"+Colors.blue("remove")+":        Removes a requirement from the raid team");
		printer("\t"+Colors.blue("setilvl")+":       Set the required item level of the raid team");
		printer(Colors.yellow(terminalWidthLine()));
	}

	private static String terminalWidthLine(){
		String line = "+";
		Terminal terminal = reader.getTerminal();
		int width = terminal.getWidth();
		while(line.length() < width-1){
			line+="-";
		}
		line+="+";
			
		return line;
	}

	public static void printer(String string){
		reader.printAbove(string);
	}
}
