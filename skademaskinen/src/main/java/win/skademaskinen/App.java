package win.skademaskinen;

import java.io.IOException;
import java.sql.SQLException;

import javax.security.auth.login.LoginException;

import org.json.JSONObject;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class App 
{
    static private JDA jda;
    public static void main( String[] args ) throws LoginException, InterruptedException, ClassNotFoundException, SQLException, IOException{
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
            Commands.slash("jail", "Send a user to jail")
                .addOption(OptionType.USER, "who", "Who to jail", true)
                .addOption(OptionType.INTEGER, "time", "Time(Default: seconds)", true)
                .addOption(OptionType.STRING, "measurement", "Seconds, minutes, hours, days", false),
            Commands.slash("color", "Pick a color for your name")
                .addOption(OptionType.STRING, "color", "Pick a color", true, true),
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
            Commands.slash("poop", "Get current poop count for you"),
            Commands.slash("leaderboard", "Get the leaderboard for the current server"),
            Commands.slash("poll", "Create a poll with entries")
                .addOption(OptionType.STRING, "message", "Message for your poll", true)
                .addOption(OptionType.STRING, "option1", "Poll option #1")
                .addOption(OptionType.STRING, "option2", "Poll option #2")
                .addOption(OptionType.STRING, "option3", "Poll option #3")
                .addOption(OptionType.STRING, "option4", "Poll option #4")
                .addOption(OptionType.STRING, "option5", "Poll option #5"),
            Commands.slash("brainfuck", "execute a brainfuck string")
                .addOption(OptionType.STRING, "code", "Code to be executed", true)
            ).queue();
        jda.getGuildById("988405633181155348").updateCommands().addCommands(Commands.slash("rolepicker", "create a rolepicker")).queue();
    }
}
