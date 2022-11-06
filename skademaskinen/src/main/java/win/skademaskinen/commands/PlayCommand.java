package win.skademaskinen.commands;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import win.skademaskinen.musicbot.MusicBot;
import win.skademaskinen.musicbot.TrackLoadResultHandler;

public class PlayCommand implements Command {

    private boolean successTag = false;
    private SlashCommandInteractionEvent event;
    private Member author;
    private Guild guild;
    private List<ActionRow> actionRows = new ArrayList<>();

    public PlayCommand(SlashCommandInteractionEvent event){
        this.event = event;
        this.author = event.getMember();
        this.guild = event.getGuild();
    }

    @Override
    public MessageEmbed run() {
        if(author.getVoiceState().inAudioChannel()){
            MusicBot bot;
            if(MusicBot.getBots().containsKey(guild)){
                bot = MusicBot.getBots().get(guild);
            }
            else{
                MusicBot.addBot(guild, event.getMember().getVoiceState().getChannel().asVoiceChannel());
                bot = MusicBot.getBots().get(guild);
            }
            if(!guild.getSelfMember().getVoiceState().inAudioChannel()){
                bot.connectToVoiceChannel(author.getVoiceState().getChannel().asVoiceChannel());
            }
            TrackLoadResultHandler handler;
            try{
                new URL(event.getOption("url").getAsString());
                handler = bot.play(event.getOption("url").getAsString().strip(), event.getHook());
            }
            catch(MalformedURLException e){
                handler = bot.play("ytsearch:"+event.getOption("url").getAsString(), event.getHook());
            }
            successTag = true;
            return handler.getEmbed();

        }
        else{
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Invalid argument!");
            successTag = false;
            return builder.build();
        }

    }

    public String build(){
        return this.log("author: "+author.getUser().getAsTag()+" server: "+guild.getName(), this.successTag);
    }

    public boolean shouldEphemeral(){
        return false;
    }

    @Override
    public List<ActionRow> getActionRows() {
        // TODO Auto-generated method stub
        return actionRows;
    }
    
}
