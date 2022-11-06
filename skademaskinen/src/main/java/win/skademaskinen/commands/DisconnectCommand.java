package win.skademaskinen.commands;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import win.skademaskinen.musicbot.MusicBot;

public class DisconnectCommand implements Command {
    private boolean successTag = false;
    private Member author;
    private Guild guild;

    public DisconnectCommand(SlashCommandInteractionEvent event){
        author = event.getMember();
        guild = event.getGuild();
    }

    @Override
    public String build() {
        return log(null, successTag);
    }

    @Override
    public String run() {
        if(author.getVoiceState().inAudioChannel()){
            if(guild.getSelfMember().getVoiceState().inAudioChannel()){
                MusicBot.getBots().get(guild).disconnect();
                return "Successfully disconnected bot!";
            }
            else{
                return "Error: the bot is not in a channel!";
            }
        }
        else{
            return "Error: you are not in a voice channel!";
        }
    }

    @Override
    public boolean shouldEphemeral() {
        return false;
    }

    @Override
    public List<ActionRow> getActionRows() {
        return new ArrayList<>();
    }
    
}
