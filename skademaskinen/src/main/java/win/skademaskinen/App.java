package win.skademaskinen;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Scanner;

import javax.security.auth.login.LoginException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.Command.Subcommand;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class App 
{
    static private JDA jda;
    static private HashMap<String, Object> config;
    public static void main( String[] args ) throws LoginException, InterruptedException, ClassNotFoundException, SQLException, IOException, ParseException{
        System.out.println("Starting bot");
        config = Config.getConfig();
        jda = JDABuilder.createDefault(config.get("token").toString()).build();
        jda.addEventListener(new CommandListener());
        jda.addEventListener(new ModalListener());
        jda.addEventListener(new ButtonListener());
        jda.addEventListener(new AutoCompleteListener());
        jda.addEventListener(new SelectMenuListener());
        jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
        setStatus("Jezdiboi");
        System.out.println("await ready");
        jda.awaitReady();
        System.out.println("finished await");
        setCommands();
        RaidTeamManager.update(jda.getGuildById("642852517197250560"));

        cli();
        System.exit(0);



    }
    static private void setCommands(){
        System.out.println("Setting commands");
        /*for(Command command : jda.retrieveCommands().complete()){
            command.delete().queue();
        }*/
        jda.updateCommands().addCommands(
            Commands.slash("ping", "Send a pong back"),
            Commands.slash("play", "Play a song from youtube")
                .addOption(OptionType.STRING, "url", "Youtube link to the song or playlist", true),
            Commands.slash("skip", "Skip to the next song"),
            Commands.slash("queue", "Get the current queue")
                .addOption(OptionType.INTEGER, "page", "Select a page (Default: 1)", false),
            Commands.slash("nowplaying", "Show the current song"),
            Commands.slash("disconnect", "Disconnect the bot from voice"),
            Commands.slash("pause", "Pause the bot"),
            Commands.slash("clear", "Clear the song queue"),
            Commands.slash("help", "Show a list of commands"),
            Commands.slash("roll", "Roll a d100 for each entry")
                .addOption(OptionType.STRING, "entry1", "entry", true)
                .addOption(OptionType.STRING, "entry2", "entry", false)
                .addOption(OptionType.STRING, "entry3", "entry", false)
                .addOption(OptionType.STRING, "entry4", "entry", false)
                .addOption(OptionType.STRING, "entry5", "entry", false)
                .addOption(OptionType.STRING, "entry6", "entry", false)
                .addOption(OptionType.STRING, "entry7", "entry", false)
                .addOption(OptionType.STRING, "entry8", "entry", false)
			    .addOption(OptionType.STRING, "entry9", "entry", false)
			    .addOption(OptionType.STRING, "entry10", "entry", false),
			Commands.slash("welcomemessage", "ADMIN COMMAND: Create an interactive welcome message"),
			Commands.slash("version", "Show the current version of the bot software (ephemeral message)"),
			Commands.slash("applicationform", "ADMIN COMMAND: Apply to the raid team"),
            Commands.slash("removeraider", "ADMIN COMMAND: Remove a raider from the raid team")
                .addOption(OptionType.USER, "raider", "User to be deleted", true),
            Commands.slash("addraider", "ADMIN COMMAND: Add a raider to the raid team manually")
                .addOption(OptionType.USER, "raider", "Mention of the raider", true)
                .addOption(OptionType.STRING, "name", "Character name", true)
                .addOption(OptionType.STRING, "server", "Character server", true, true)
                .addOption(OptionType.STRING, "role", "Character role", true, true),
            Commands.slash("updateteam", "ADMIN COMMAND: Update the raid team list"),
            Commands.slash("spawnmessage", "ADMIN COMMAND: Spawn an empty message"),
            Commands.slash("editmessage", "ADMIN COMMAND: Edit a custom embed")
                .addOption(OptionType.STRING, "messageid", "ID of the message to be edited", true)
                .addOption(OptionType.STRING, "description", "Set a description")
                .addOption(OptionType.STRING, "title", "Set a title")
                .addOption(OptionType.STRING, "field", "Add a field, can be paired with inline option")
                .addOption(OptionType.BOOLEAN, "inline", "Whether a field is put inline")
                .addOption(OptionType.STRING, "fieldname", "Name for a field")
                .addOption(OptionType.STRING, "imageurl", "Image url for the embed"),
            Commands.slash("message", "ADMIN COMMAND: Create an announcement from an embed")
                .addOption(OptionType.STRING, "message_id", "Announcement message", true)
                .addOption(OptionType.STRING, "channel_id", "Optionally a different textchannel", false),
            Commands.slash("team", "ADMIN COMMAND: Handle the raid team")
                .addSubcommands(new SubcommandData("add", "Add a raider to the raid team manually")
                    .addOption(OptionType.USER, "raider", "Mention of the raider", true)
                    .addOption(OptionType.STRING, "name", "Character name", true)
                    .addOption(OptionType.STRING, "server", "Character server", true, true)
                    .addOption(OptionType.STRING, "role", "Character role", true, true),
                new SubcommandData("remove", "Remove a raider from the raid team")
                    .addOption(OptionType.USER, "raider", "User to be deleted", true),
                new SubcommandData("update", "Update the raid team list"),
                new SubcommandData("form", "Apply to the raid team")),    
            Commands.slash("requirements", "ADMIN COMMAND: raid team requirements options")
                .addSubcommands(new SubcommandData("add", "Add a requirement")
                    .addOption(OptionType.STRING, "type", "The type of requirement", true, true)
                    .addOption(OptionType.STRING, "value", "The additional requirement", true, true),
                new SubcommandData("remove", "Remove a requirement")
                    .addOption(OptionType.STRING, "type", "The type of requirement", true, true)
                    .addOption(OptionType.STRING, "value", "The additional requirement", true, true),
                new SubcommandData("setilvl", "Sets the ilvl requirement")
                    .addOption(OptionType.INTEGER, "ilvl", "The desired item level", true),
                new SubcommandData("list", "list the raid team requirements"))
            ).queue();
    }
    public static void setStatus(String message){
        jda.getPresence().setActivity(Activity.playing(message));
    }

    private static void cli() throws IOException, ParseException{
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
                        Guild server = jda.getGuildById(scanner.nextLine());
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
                                for(Guild guild : jda.getGuilds()){
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
                                Guild guild = jda.getGuildById(arguments[1]);
                                System.out.println(Colors.green("Name:          ") + guild.getName());
                                System.out.println(Colors.green("ID:            ") + guild.getId());
                                System.out.println(Colors.green("Member count:  ") + guild.getMemberCount());
                                System.out.println(Colors.green("Channel count: ") + guild.getChannels().size() + "\n");

                        }
                    }
                    else{
                        for(Guild guild : jda.getGuilds()){
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
                                System.out.println("\t"+Colors.blue("<member id>")+":  Shows a single member of the raid team");
                                System.out.println("+--------------------------------------------------------------------+");
                                break;
                            case "list":
                                for(String key : team.keySet()){
                                    RaidTeamManager.printRaider((JSONObject) team.get(key), key, jda.getGuildById("642852517197250560"));
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
                                        RaidTeamManager.addRaider(name, _server, role, id, jda.getGuildById("642852517197250560"));
                                        System.out.println(Colors.green("Successfully added raider"));
                                    }
                                    catch(ErrorResponseException e){
                                        System.out.println(Colors.red("Error: Failed to add raider"));
                                    }
                                }
                                else{
                                    System.out.println(Colors.red("Error: Too few arguments"));
                                    System.out.println(Colors.black("Example: [team add 692410386657574952 Skademanden argent-dawn melee]"));
                                }
                                break;
                            case "remove":
                                try{
                                    RaidTeamManager.removeRaider(jda.getGuildById("642852517197250560").retrieveMemberById(arguments[2]).complete());
                                    System.out.println(Colors.green("Successfully removed raider"));
                                }
                                catch(ErrorResponseException e){
                                    System.out.println(Colors.red("Error: invalid id"));
                                }
                                break;
                            default:
                                try{
                                    RaidTeamManager.printRaider((JSONObject) team.get(arguments[1]), arguments[1], jda.getGuildById("642852517197250560"));
                                }
                                catch(NumberFormatException e){
                                    System.out.println(Colors.red("Error: invalid id"));
                                }
                                break;
                                
                        }
                    }
                    else{
                        for(String key : team.keySet()){
                            RaidTeamManager.printRaider((JSONObject) team.get(key), key, jda.getGuildById("642852517197250560"));
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
        System.out.print("["+Colors.blue(jda.getSelfUser().getName())+"] > ");
    }
}
