package win.skademaskinen;

import java.io.IOException;
import java.util.ArrayList;

import javax.security.auth.login.LoginException;

import org.json.JSONObject;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import win.skademaskinen.WorldOfWarcraft.RaidTeamManager;
import win.skademaskinen.listeners.AutoCompleteListener;
import win.skademaskinen.listeners.ButtonListener;
import win.skademaskinen.listeners.CommandListener;
import win.skademaskinen.listeners.GuildEventListener;
import win.skademaskinen.listeners.MessageListener;
import win.skademaskinen.listeners.ModalListener;
import win.skademaskinen.listeners.SelectMenuListener;
import win.skademaskinen.listeners.VoiceChannelListener;
import win.skademaskinen.utils.Colors;
import win.skademaskinen.utils.Config;
import win.skademaskinen.utils.Log;
import win.skademaskinen.utils.Loggable;
import win.skademaskinen.utils.ModalData;
import win.skademaskinen.utils.Serializer;
import win.skademaskinen.utils.Shell;

public class App implements Loggable
{
    static public JDA jda;
    static private boolean successTag = false;
    static private Config config;

    public static void main(String[] args) {
        App app = new App();
        try {
            app.run();
            successTag = true;
            Log.appendLog(app);
        } catch (LoginException | ClassNotFoundException | InterruptedException | IOException e) {
            Colors.exceptionHandler(e);
            successTag = false;
            Log.appendLog(app);
        }
        System.exit(0);
    }
    
    public void run() throws LoginException, InterruptedException, ClassNotFoundException, IOException{
        Shell.printer(Colors.yellow("Starting bot"));
        System.out.print(Colors.GREEN);
        config = Config.getConfig();
        jda = JDABuilder.createDefault(config.getToken())
            .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
            .build();
        jda.awaitReady();
        Shell.printer(Colors.yellow("Deserializing modal interactions"));
        Serializer modals = Serializer.deserialize();
        if(modals != null){
            Config.modals = modals.get();
        }
        else{
            Config.modals = new ArrayList<ModalData>();
        }
        Shell.printer(Colors.yellow("Adding event listeners"));
        jda.addEventListener(new CommandListener());
        jda.addEventListener(new ModalListener());
        jda.addEventListener(new ButtonListener());
        jda.addEventListener(new AutoCompleteListener());
        jda.addEventListener(new SelectMenuListener());
        jda.addEventListener(new GuildEventListener());
        jda.addEventListener(new VoiceChannelListener());
        jda.addEventListener(new MessageListener());
        jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
        setStatus("Jezdiboi");
        Shell.printer(Colors.yellow("Setting commands"));
        setCommands(false);
        try{
            Shell.printer(Colors.yellow("updating raid team"));
            RaidTeamManager.update(jda.getGuildById("642852517197250560"));
        }
        catch(ErrorResponseException e){
            Colors.exceptionHandler(e);
        }
        System.out.print(Colors.RESET);
        Shell.printer(Colors.yellow("Finished bot startup"));
        successTag = true;
        Shell.shell();
        Shell.printer(Colors.yellow("Stopping bot"));
        jda.shutdown();
        Shell.printer(Colors.yellow("Serializing modal interactions"));
        Serializer.serialize(new Serializer(Config.modals));



    }
    static public void setCommands(boolean shouldReset){
        if(shouldReset){
            for(Command command : jda.retrieveCommands().complete()){
                command.delete().queue();
            }
        }
        jda.updateCommands().addCommands(
            Commands.slash("ping", "Send a pong back"),
            Commands.slash("play", "Play a song from youtube")
                .addOption(OptionType.STRING, "url", "Youtube link to the song or playlist", true),
            Commands.slash("skip", "Skip to the next song"),
            Commands.slash("queue", "Get the current queue")
                .addOption(OptionType.INTEGER, "page", "Select a page (Default: 1)", false),
            Commands.slash("nowplaying", "Show the current song"),
            Commands.slash("disconnect", "Disconnect the bot from voice"),
            Commands.slash("leave", "Disconnect the bot from voice"),
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
			Commands.slash("rolepicker", "ADMIN COMMAND: Create an interactive welcome message"),
			Commands.slash("version", "Show the current version of the bot software (ephemeral message)"),
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
                new SubcommandData("list", "list the raid team requirements"),
                new SubcommandData("form", "show the requirements form")),
            Commands.slash("poll", "ADMIN COMMAND: create a poll"),
            Commands.slash("featurerequest", "request for a feature to be added to the bot")
            ).queue();
    }
    public static void setStatus(String message){
        jda.getPresence().setActivity(Activity.playing(message));
    }
    @Override
    public String build() {
        return log("", successTag);
    }

    
}
