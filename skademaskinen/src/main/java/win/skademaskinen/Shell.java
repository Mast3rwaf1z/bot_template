package win.skademaskinen;

import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.protobuf.TextFormat.ParseException;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

public class Shell {
	public static void shell() throws IOException, ParseException, org.json.simple.parser.ParseException{
        Scanner scanner = new Scanner(System.in);
        for(String line = ""; !line.equals("exit"); line = scanner.nextLine()){
            String arguments[] = line.split(" ");
            switch(arguments[0]){
                case "":
                    break;
                case "help":
                    System.out.println("+--------------------------------------------------------------------+");
                    System.out.println("\t"+Colors.blue("send")+":    Send a message in a channel");
                    System.out.println("\t"+Colors.blue("guilds")+":  Get info about a discord server");
                    System.out.println("\t"+Colors.blue("clear")+":   clear the terminal");
                    System.out.println("\t"+Colors.blue("exit")+":    Stop the bot");
                    System.out.println("\t"+Colors.blue("team")+":    Show a list of all raiders on the raid team");
                    System.out.println("+--------------------------------------------------------------------+");
                    break;
                case "send":
                    try{

                        System.out.print(Colors.purple("server id: "));
                        Guild server = App.jda.getGuildById(scanner.nextLine());
                        System.out.print(Colors.purple("channel id: "));
                        TextChannel channel = server.getTextChannelById(scanner.nextLine());
                        System.out.print(Colors.purple("Message to be sent: "));
                        channel.sendMessage(scanner.nextLine()).queue();
                    }
                    catch(IllegalArgumentException e){
                        System.out.println(Colors.red("Error: invalid id"));
                    }
                    break;
                case "guilds":
                    if(arguments.length > 1){
                        switch(arguments[1]){
                            case "list":
                                for(Guild guild : App.jda.getGuilds()){
                                    System.out.println(Colors.green("Name:          ") + guild.getName());
                                    System.out.println(Colors.green("ID:            ") + guild.getId());
                                    System.out.println(Colors.green("Member count:  ") + guild.getMemberCount());
                                    System.out.println(Colors.green("Channel count: ") + guild.getChannels().size() + "\n");
                                }
                                break;
                            case "help":
                                System.out.println("+--------------------------------------------------------------------+");
                                System.out.println("\t"+Colors.blue("list")+":        Print the list of guilds");
                                System.out.println("\t"+Colors.blue("<guild id>")+":  Prints a guild in the list");
                                System.out.println("+--------------------------------------------------------------------+");
                                break;
                            default:
                                Guild guild = App.jda.getGuildById(arguments[1]);
                                System.out.println(Colors.green("Name:          ") + guild.getName());
                                System.out.println(Colors.green("ID:            ") + guild.getId());
                                System.out.println(Colors.green("Member count:  ") + guild.getMemberCount());
                                System.out.println(Colors.green("Channel count: ") + guild.getChannels().size() + "\n");

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
                    break;
                case "clear":
                    System.out.println("\033[H\033[2J");
                    break;
                case "team":
                    HashMap<String, Object> team = Config.getFile("team.json");
                    if(arguments.length > 1){
                        switch(arguments[1]){
                            case "help":
                                System.out.println("+--------------------------------------------------------------------+");
                                System.out.println("\t"+Colors.blue("list")+":          List all members of the raid team");
                                System.out.println("\t"+Colors.blue("add")+":           Add a raider to the raid team");
                                System.out.println("\t"+Colors.blue("remove")+":        Removes a raider from the raid team");
                                System.out.println("\t"+Colors.blue("requirements")+":  Manages the requirements of the raid team");
                                System.out.println("\t"+Colors.blue("<member id>")+":   Shows a single member of the raid team");
                                System.out.println("+--------------------------------------------------------------------+");
                                break;
                            case "list":
                                for(String key : team.keySet()){
                                    RaidTeamManager.printRaider((JSONObject) team.get(key), key, App.jda.getGuildById("642852517197250560"));
                                }
                                break;
                            case "requirements":
                                if(arguments.length > 2){
                                    switch(arguments[2]){
                                        case "list":
                                            JSONObject raidForm = (JSONObject) Config.getFile("team_requirements.json").get("raid_form");
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
                                            break;
                                        case "add":
                                            RaidTeamManager.addRequirement(arguments[3], line.substring(arguments[0].length()+arguments[1].length()+arguments[2].length()+arguments[3].length()+4));
                                            System.out.println(Colors.green("Successfully added requirement!"));
                                            break;
                                        case "remove":
                                            RaidTeamManager.removeRequirement(arguments[3], line.substring(arguments[0].length()+arguments[1].length()+arguments[2].length()+arguments[3].length()+4));
                                            System.out.println(Colors.green("Successfully removed requirement!"));
                                            break;
                                        case "setilvl":
                                            RaidTeamManager.setIlvlRequirement(Integer.parseInt(arguments[3]));
                                            System.out.println(Colors.green("Successfully set minimum item level requirement!"));
                                            break;
                                        case "help":
                                            System.out.println("+--------------------------------------------------------------------+");
                                            System.out.println("\t"+Colors.blue("list")+":          List all requirements");
                                            System.out.println("\t"+Colors.blue("add")+":           Add a requirement to the raid team");
                                            System.out.println("\t"+Colors.blue("remove")+":        Removes a requirement from the raid team");
                                            System.out.println("\t"+Colors.blue("setilvl")+":       Set the required item level of the raid team");
                                            System.out.println("+--------------------------------------------------------------------+");
                                            break;
                                        default:
                                            System.out.println(Colors.red("Error: invalid argument! ") + Colors.black("[Example: team requirements add filled melee damage]"));
                                    }
                                }
                                else{
                                    JSONObject raidForm = (JSONObject) Config.getFile("team_requirements.json").get("raid_form");
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
                                }
                                break;
                            case "add":
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
                                break;
                            case "remove":
                                try{
                                    RaidTeamManager.removeRaider(App.jda.getGuildById("642852517197250560").retrieveMemberById(arguments[2]).complete());
                                    System.out.println(Colors.green("Successfully removed raider"));
                                }
                                catch(ErrorResponseException e){
                                    System.out.println(Colors.red("Error: invalid id"));
                                }
                                break;
                            default:
                                try{
                                    RaidTeamManager.printRaider((JSONObject) team.get(arguments[1]), arguments[1], App.jda.getGuildById("642852517197250560"));
                                }
                                catch(NumberFormatException e){
                                    System.out.println(Colors.red("Error: invalid id"));
                                }
                                break;
                                
                        }
                    }
                    else{
                        for(String key : team.keySet()){
                            RaidTeamManager.printRaider((JSONObject) team.get(key), key, App.jda.getGuildById("642852517197250560"));
                        }
                    }
                    break;
                default:
                    System.out.println(Colors.red("No such command!")+Colors.black(" [type help for a list of commands]"));
            }
            prompt();
        }
        scanner.close();
    }
    public static void prompt(){
        System.out.print("["+Colors.blue(App.jda.getSelfUser().getName())+"] > ");
    }
}
