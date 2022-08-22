package win.skademaskinen;

import java.io.IOException;
import java.sql.SQLException;

import javax.security.auth.login.LoginException;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class App 
{
    static private JDA jda;
    public static void main( String[] args ) throws LoginException, InterruptedException, ClassNotFoundException, SQLException, IOException, ParseException{
        System.out.println("Starting bot");
        JSONObject config = Config.getConfig();
        jda = JDABuilder.createDefault(config.get("token").toString()).build();
        jda.addEventListener(new CommandListener());
        jda.addEventListener(new ModalListener());
        jda.addEventListener(new ButtonListener());
        jda.addEventListener(new AutoCompleteListener());
        jda.addEventListener(new SelectMenuListener());
        jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
        jda.getPresence().setActivity(Activity.playing("v2.0"));
        System.out.println("await ready");
        jda.awaitReady();
        System.out.println("finished await");
        setCommands();

        for(Guild guild : jda.getGuilds()){
            RaidTeamManager.update(guild);
        }



    }
    static private void setCommands(){
        System.out.println("Setting commands");
        /*for(Command command : jda.retrieveCommands().complete()){
            command.delete().queue();
        }*/
        jda.updateCommands().addCommands(
            Commands.slash("ping", "send a pong back"),
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
            Commands.slash("roll", "roll a d100 for each entry")
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
			Commands.slash("welcomemessage", "ADMIN COMMAND: create an interactive welcome message"),
			Commands.slash("version", "Show the current version of the bot software (ephemeral message)"),
			Commands.slash("applicationform", "ADMIN COMMAND: Apply to the raid team"),
            Commands.slash("removeraider", "ADMIN COMMAND: Remove a raider from the raid team")
                .addOption(OptionType.USER, "raider", "user to be deleted", true),
            Commands.slash("addraider", "ADMIN COMMAND: Add a raider to the raid team manually")
                .addOption(OptionType.USER, "raider", "Mention of the raider", true)
                .addOption(OptionType.STRING, "name", "Character name", true)
                .addOption(OptionType.STRING, "server", "Character server", true, true)
                .addOption(OptionType.STRING, "role", "Character role", true, true)
                .addOption(OptionType.BOOLEAN, "raidtimes", "Can they raid on our raid times", true),
            Commands.slash("updateteam", "ADMIN COMMAND: update the raid team list"),
            Commands.slash("spawnmessage", "ADMIN COMMAND: spawn an empty message"),
            Commands.slash("editmessage", "ADMIN COMMAND: edit a custom embed")
                .addOption(OptionType.STRING, "messageid", "ID of the message to be edited", true)
                .addOption(OptionType.STRING, "description", "set a description")
                .addOption(OptionType.STRING, "title", "Set a title")
                .addOption(OptionType.STRING, "field", "Add a field, can be paired with inline option")
                .addOption(OptionType.BOOLEAN, "inline", "whether a field is put inline")
                .addOption(OptionType.STRING, "fieldname", "name for a field")
                .addOption(OptionType.STRING, "imageurl", "image url for the embed"),
            Commands.slash("announcement", "ADMIN COMMAND: create an announcement from an embed")
                .addOption(OptionType.STRING, "message_id", "announcement message", true)
            ).queue();
    }
}
