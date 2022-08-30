package win.skademaskinen;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.security.auth.login.LoginException;

import org.json.simple.parser.ParseException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.ModalInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class App 
{
    static public JDA jda;
    static private HashMap<String, Object> config;
    public static void main( String[] args ) throws LoginException, InterruptedException, ClassNotFoundException, SQLException, IOException, ParseException{
        System.out.println(Colors.yellow("Deserializing modal interactions"));
        SerialModalContainer modals = SerialModalContainer.deserialize();
        if(modals != null){
            Config.modals = modals.get();
        }
        else{
            Config.modals = new ArrayList<ModalInteraction>();
        }
        System.out.println(Colors.yellow("Starting bot"));
        System.out.print(Colors.GREEN);
        config = Config.getConfig();
        jda = JDABuilder.createDefault(config.get("token").toString())
            .enableIntents(GatewayIntent.GUILD_MEMBERS)
            .build();
        jda.awaitReady();
        System.out.println(Colors.yellow("Adding event listeners"));
        jda.addEventListener(new CommandListener());
        jda.addEventListener(new ModalListener());
        jda.addEventListener(new ButtonListener());
        jda.addEventListener(new AutoCompleteListener());
        jda.addEventListener(new SelectMenuListener());
        jda.addEventListener(new GuildEventListener());
        jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
        setStatus("Jezdiboi");
        System.out.println(Colors.yellow("Setting commands"));
        setCommands();
        try{
            RaidTeamManager.update(jda.getGuildById("642852517197250560"));
        }
        catch(ErrorResponseException e){
            Colors.exceptionHandler(e, false);
        }
        System.out.print(Colors.RESET);
        System.out.println(Colors.yellow("Finished bot startup"));
        Shell.shell();
        System.out.println(Colors.yellow("Stopping bot"));
        jda.shutdown();
        System.out.println(Colors.yellow("Serializing modal interactions"));
        SerialModalContainer.serialize(new SerialModalContainer(Config.modals));
        System.exit(0);



    }
    static private void setCommands(){
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

    
}
