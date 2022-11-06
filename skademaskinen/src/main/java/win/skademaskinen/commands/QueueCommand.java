package win.skademaskinen.commands;

import java.util.ArrayList;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import win.skademaskinen.musicbot.MusicBot;
import win.skademaskinen.utils.Utils;

public class QueueCommand implements Command {
    private boolean successTag = false;
    private SlashCommandInteractionEvent event;
    private Guild guild;
    private Member author;

    public QueueCommand(SlashCommandInteractionEvent event){
        this.event = event;
        guild = event.getGuild();
        author = event.getMember();
    }

    @Override
    public String build() {
        return log("author: "+author.getUser().getAsTag()+" server: "+guild.getName(), successTag);
    }

    @Override
    public MessageEmbed run() {
        if(author.getVoiceState().inAudioChannel()){
            if(guild.getSelfMember().getVoiceState().inAudioChannel()){
                EmbedBuilder builder = new EmbedBuilder();
                int page = 0;
                for(OptionMapping option : event.getOptions()){
                    page = option.getAsInt()-1;
                }
                List<AudioTrack> tracks;
                if(MusicBot.getBots().get(guild).getQueue().size()< 15){
                    tracks = MusicBot.getBots().get(guild).getQueue();
                }
                else{
                    tracks = MusicBot.getBots().get(guild).getQueue().subList(page*15, (page*15)+15);
                }
                builder.setTitle("Track queue");
                for(AudioTrack track : tracks){
                    builder.addField("", "["+track.getInfo().title+"]("+track.getInfo().uri+")\n Duration: "+Utils.getTime(track.getDuration()), false);
                }
                int totalTime = 0;
                for(AudioTrack track : tracks){
                    totalTime += track.getDuration();
                }
                builder.setFooter("Total time remaining: " + Utils.getTime(totalTime-MusicBot.getBots().get(guild).getCurrentTrack().getDuration()) + " | Total tracks in queue: " + MusicBot.getBots().get(guild).getQueue().size());
                AudioTrack current = MusicBot.getBots().get(guild).player.getPlayingTrack();
                builder.setDescription("Currently playing track:\n["+current.getInfo().title+"]("+current.getInfo().uri+")");
                builder.appendDescription("\nDuration: "+Utils.getTime(current.getDuration()));
                builder.setThumbnail("http://img.youtube.com/vi/"+current.getIdentifier()+"/0.jpg");
                
                successTag = true;
                return builder.build();
            }
            else{
                successTag = false;
                return new EmbedBuilder().setTitle("Error: bot is not in a channel").build();
            }
        }
        else{
            successTag = false;
            return new EmbedBuilder().setTitle("Error: you are not in a channel!").build();
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
