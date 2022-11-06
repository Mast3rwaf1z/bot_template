package win.skademaskinen.commands;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import win.skademaskinen.musicbot.MusicBot;

public class SkipCommand implements Command{
    private boolean successTag = false;
    private Member author;
    private Guild guild;

    public SkipCommand(SlashCommandInteractionEvent event){
        author = event.getMember();
        guild = event.getGuild();
    }

    @Override
    public String build() {
        return log(null, successTag);
    }

    @Override
    public MessageEmbed run() {
        if(author.getVoiceState().inAudioChannel()){
            if(guild.getSelfMember().getVoiceState().inAudioChannel()){
                successTag = true;
                return MusicBot.getBots().get(guild).skip();
            }
            else{
                successTag = false;
                return new EmbedBuilder().setTitle("Error: bot is not in your channel!").build();
            }
        }
        else{
            successTag = false;
            return new EmbedBuilder().setTitle("Error: you are not in an audio channel!").build();
        }
    }

    @Override
    public boolean shouldEphemeral() {
        return true;
    }

    @Override
    public List<ActionRow> getActionRows() {
        return new ArrayList<>();
    }
    
}
