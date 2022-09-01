package win.skademaskinen;

import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.json.simple.parser.ParseException;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

public class Shell {
	private static Guild guild = null;
	private static MessageChannel channel = null;

    public static void prompt(){
		if(guild == null){
			System.out.print("["+Colors.blue(App.jda.getSelfUser().getName())+"] > ");
		}
		else{
			if(channel == null){
				System.out.print("["+Colors.blue(App.jda.getSelfUser().getName() + Colors.green(" - ") + Colors.blue(guild.getName())) +"] > ");
			}
			else{
				System.out.print("["+Colors.blue(App.jda.getSelfUser().getName() + Colors.green(" - ") + Colors.blue(guild.getName())) + Colors.green(" - ") + Colors.blue(channel.getName())+ "] > ");
			}
		}
    }

	public static void shell() throws IOException, ParseException, org.json.simple.parser.ParseException{
        Scanner scanner = new Scanner(System.in);
        for(String line = ""; !line.equals("exit"); line = scanner.nextLine()){
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
                    System.out.println(Colors.red("No such command!")+Colors.black(" [type help for a list of commands]"));
            }
            prompt();
        }
        scanner.close();
    }

	private static void commands(String[] arguments) {
		switch(arguments[1]){
			case "update":
				App.setCommands(true);
		}
	}

	private static void help(){
		System.out.println(Colors.yellow(terminalWidthLine()));
		System.out.println("\t"+Colors.blue("guilds")+":  Get info about a discord server");
		System.out.println("\t"+Colors.blue("guilds")+":  Get info about a discord servers channels");
		System.out.println("\t"+Colors.blue("send")+":    Send a message in a selected channel");
		System.out.println("\t"+Colors.blue("clear")+":   clear the terminal");
		System.out.println("\t"+Colors.blue("exit")+":    Stop the bot");
		System.out.println("\t"+Colors.blue("team")+":    Show a list of all raiders on the raid team");
		System.out.println(Colors.yellow(terminalWidthLine()));

	}
	private static void send(String[] arguments){
		if(arguments.length == 2){
			if(guild != null){
				if(channel != null){
					channel.sendMessage(arguments[1]).queue();
				}
				else{
					System.out.println(Colors.red("Please select a channel"));
				}
			}
			else{
				System.out.println(Colors.red("Please select a guild"));
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
						System.out.println(Colors.green("Name:          ") + guild.getName());
						System.out.println(Colors.green("ID:            ") + guild.getId());
						System.out.println(Colors.green("Member count:  ") + guild.getMemberCount());
						System.out.println(Colors.green("Channel count: ") + guild.getChannels().size() + "\n");
					}
					catch(NumberFormatException | NullPointerException e){
						Colors.exceptionHandler(e, true);
					}

			}
		}
		else{
			for(Guild guild : App.jda.getGuilds()){
				System.out.println(Colors.green("Name:          ") + guild.getName());
				System.out.println(Colors.green("ID:            ") + guild.getId());
				System.out.println(Colors.green("Member count:  ") + guild.getMemberCount());
				System.out.println(Colors.green("Channel count: ") + guild.getChannels().size() + "\n");
			}
		}
	}
	private static void guildsHelp() {
		System.out.println(Colors.yellow(terminalWidthLine()));
		System.out.println("\t"+Colors.blue("list")+":        Print the list of guilds");
		System.out.println("\t"+Colors.blue("<guild id>")+":  Prints a guild in the list");
		System.out.println("\t"+Colors.blue("select")+":  	  Selects a given guild");
		System.out.println("\t"+Colors.blue("select")+":  	  Reset guild selection");
		System.out.println(Colors.yellow(terminalWidthLine()));
	}

	private static void guildsSelect(String[] arguments) {
		if(arguments.length == 3){
			guild = App.jda.getGuildById(arguments[2]);
		}
		else{
			System.out.println(Colors.red("Error: not enough arguments"));
		}
	}

	private static void guildsList() {
		for(Guild guild : App.jda.getGuilds()){
			System.out.println(Colors.green("Name:          ") + guild.getName());
			System.out.println(Colors.green("ID:            ") + guild.getId());
			System.out.println(Colors.green("Member count:  ") + guild.getMemberCount());
			System.out.println(Colors.green("Channel count: ") + guild.getChannels().size() + "\n");
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
						System.out.println(Colors.red("Error: invalid argument"));
				}

			}
			else{
				channelsList();
			}
		}
		else{
			System.out.println(Colors.red("Error: please select a guild"));
		}

	}
	private static void channelsSelect(String[] arguments) {
		if(arguments.length == 3){
				channel = guild.getTextChannelById(arguments[2]);
		}
		else{
			System.out.println(Colors.red("Error: not enough arguments"));
		}

	}

	private static void channelsList() {
		for(MessageChannel channel : guild.getTextChannels()){
			System.out.println();
			System.out.println(Colors.green("Name:	")+channel.getName());
			System.out.println(Colors.green("ID:	")+channel.getId());
		}
	}
	private static void clear(){
		System.out.println("\033[H\033[2J");
	}
	private static void team(String[] arguments, String line){
		HashMap<String, Object> team;
		try {
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
	} catch (IOException | ParseException e) {
		Colors.exceptionHandler(e, true);
	}

	}
	private static void teamHelp(){
		System.out.println(Colors.yellow(terminalWidthLine()));
		System.out.println("\t"+Colors.blue("list")+":          List all members of the raid team");
		System.out.println("\t"+Colors.blue("add")+":           Add a raider to the raid team");
		System.out.println("\t"+Colors.blue("remove")+":        Removes a raider from the raid team");
		System.out.println("\t"+Colors.blue("requirements")+":  Manages the requirements of the raid team");
		System.out.println("\t"+Colors.blue("<member id>")+":   Shows a single member of the raid team");
		System.out.println(Colors.yellow(terminalWidthLine()));

	}
	private static void teamList(HashMap<String, Object> team){
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
					System.out.println(Colors.red("Error: invalid argument! ") + Colors.black("[Example: team requirements add filled melee damage]"));
				break;
				
			}
		}
		else{
			requirementsList();
		}

	}
	private static void teamMember(String[] arguments, HashMap<String, Object> team){
		try{
			RaidTeamManager.printRaider((JSONObject) team.get(arguments[1]), arguments[1], App.jda.getGuildById("642852517197250560"));
		}
		catch(NumberFormatException e){
			System.out.println(Colors.red("Error: invalid id"));
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
				System.out.println(Colors.green("Successfully added raider"));
			}
			catch(ErrorResponseException e){
				System.out.println(Colors.red("Error: Failed to add raider"));
			}
		}
		else{
			System.out.println(Colors.red("Error: Too few arguments"));
			System.out.println(Colors.black("Example: [team add 214752462769356802 Skademanden argent-dawn melee]"));
		}

	}
	private static void teamRemove(String[] arguments){
		try{
			RaidTeamManager.removeRaider(App.jda.getGuildById("642852517197250560").retrieveMemberById(arguments[2]).complete());
			System.out.println(Colors.green("Successfully removed raider"));
		}
		catch(ErrorResponseException | NullPointerException e){
			System.out.println(Colors.red("Error: invalid id"));
		}

	}
	private static void requirementsList(){
		JSONObject raidForm;
		try {
			raidForm = (JSONObject) Config.getFile("team_requirements.json").get("raid_form");
			System.out.println(Colors.yellow("Filled roles:"));
			for(Object role : (JSONArray) raidForm.get("filled_roles")){
				System.out.println("\t"+role.toString());
			}
			System.out.println(Colors.yellow("Preferred roles:"));
			for(Object role : (JSONArray) raidForm.get("preferred_roles")){
				System.out.println("\t"+role.toString());
			}
			System.out.println(Colors.yellow("Needed classes:"));
			for(Object _class : (JSONArray) raidForm.get("needed_classes")){
				System.out.println("\t"+_class.toString());
			}
			System.out.println(Colors.yellow("Minimum item level:"));
			System.out.println("\t"+raidForm.get("minimum_ilvl"));
		} catch (IOException | ParseException | NullPointerException e) {
			Colors.exceptionHandler(e, false);
		}
	}
	private static void requirementsAdd(String[] arguments, String line){
		RaidTeamManager.addRequirement(arguments[3], line.substring(arguments[0].length()+arguments[1].length()+arguments[2].length()+arguments[3].length()+4));
		System.out.println(Colors.green("Successfully added requirement!"));

	}
	private static void requirementsRemove(String[] arguments, String line){
		RaidTeamManager.removeRequirement(arguments[3], line.substring(arguments[0].length()+arguments[1].length()+arguments[2].length()+arguments[3].length()+4));
		System.out.println(Colors.green("Successfully removed requirement!"));

	}
	private static void requirementsSetIlvl(String[] arguments){
		RaidTeamManager.setIlvlRequirement(Integer.parseInt(arguments[3]));
		System.out.println(Colors.green("Successfully set minimum item level requirement!"));

	}
	private static void requirementsHelp(){
		System.out.println(Colors.yellow(terminalWidthLine()));
		System.out.println("\t"+Colors.blue("list")+":          List all requirements");
		System.out.println("\t"+Colors.blue("add")+":           Add a requirement to the raid team");
		System.out.println("\t"+Colors.blue("remove")+":        Removes a requirement from the raid team");
		System.out.println("\t"+Colors.blue("setilvl")+":       Set the required item level of the raid team");
		System.out.println(Colors.yellow(terminalWidthLine()));
	}

	private static String terminalWidthLine(){
		String line = "+";
		try {
			Terminal terminal = TerminalBuilder.terminal();
			int width = terminal.getWidth();
			terminal.close();
			while(line.length() < width-1){
				line+="-";
			}
			line+="+";
			
		} catch (IOException e) {
			Colors.exceptionHandler(e, false);
		}
		return line;
	}
}
