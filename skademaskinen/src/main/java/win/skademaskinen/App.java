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
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class App 
{
    static private JDA jda;
    public static void main( String[] args ) throws LoginException, InterruptedException, ClassNotFoundException, SQLException, IOException, ParseException{
        JSONObject config = Config.getConfig();
        jda = JDABuilder.createDefault(config.get("token").toString()).build();
        jda.addEventListener(new CommandListener());
        jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
        jda.getPresence().setActivity(Activity.playing("v2.0"));
        jda.awaitReady();
        setCommands();

    }
    static private void setCommands(){
        System.out.println("Setting commands");
        /*for(Command command : jda.retrieveCommands().complete()){
            command.delete().queue();
        }*/
        jda.updateCommands().addCommands(
            Commands.slash("ping", "send a pong back"),
            Commands.slash("color", "Pick a color for your name")
                .addOption(OptionType.STRING, "color", "Pick a color (Options: Green, Red, Blue, Gray, Yellow, Orange, White, Purple, Pink and Darkgreen)", true),
            Commands.slash("play", "Play a song from youtube")
                .addOption(OptionType.STRING, "url", "Youtube link to the song or playlist", true),
            Commands.slash("skip", "Skip to the next song"),
            Commands.slash("queue", "Get the current queue")
                .addOption(OptionType.INTEGER, "page", "Select a page (Default: 1)", false),
            Commands.slash("nowplaying", "Show the current song"),
            Commands.slash("disconnect", "Disconnect the bot from voice"),
            Commands.slash("pause", "Pause the bot"),
            Commands.slash("clear", "Clear the song queue"),
            Commands.slash("help", "Show a list of commands")
            ).queue();
    }
}
