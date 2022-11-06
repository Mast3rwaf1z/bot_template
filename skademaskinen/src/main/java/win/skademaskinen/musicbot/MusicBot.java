package win.skademaskinen.musicbot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.managers.AudioManager;

public class MusicBot {
    static private AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    public AudioPlayer player = playerManager.createPlayer();
    TrackScheduler scheduler;
    private VoiceChannel channel;
    private Guild guild;
    private AudioManager audioManager;
    public Map<String, SelectMenu> selectMenus = new HashMap<String, SelectMenu>();
    private static HashMap<Guild, MusicBot> bots = new HashMap<>();

    
    public MusicBot(VoiceChannel channel){
        this.channel = channel;
        guild = channel.getGuild();
        audioManager = guild.getAudioManager();
        scheduler = new TrackScheduler(audioManager);
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
        audioManager.setSendingHandler(new AudioPlayerSendHandler(player));
        player.addListener(scheduler);
        connect();
    }

    private void connect() {
        if(!audioManager.isConnected()){
            audioManager.openAudioConnection(channel);
        }
    }
    public TrackLoadResultHandler play(String url, InteractionHook hook) {
        connect();
        TrackLoadResultHandler handler = new TrackLoadResultHandler(this, url);
        playerManager.loadItem(url, handler);
        while(!handler.isDone());
        return handler;
    }

    public MessageEmbed skip() {
        if(player.getPlayingTrack() != null){
            player.stopTrack();
            if(!scheduler.isEmpty()){
                AudioTrack track = scheduler.dequeue();
                player.startTrack(track, false);
                EmbedBuilder builder = new EmbedBuilder();
				builder.setDescription("[" + track.getInfo().title + "](" + track.getInfo().uri + ")");
                builder.setTitle("Track started");
                if(player.isPaused()){
                    builder.appendDescription("\nThe bot is paused!");
                }
                builder.setFooter("Length: " + track.getDuration()+"ms");
                builder.setThumbnail("http://img.youtube.com/vi/"+track.getIdentifier()+"/0.jpg");
                return builder.build();

            }
            else{
                disconnect();
                return new EmbedBuilder().setTitle("Queue is now empty!").build();
            }
        }
        else{
            return new EmbedBuilder().setTitle("Error: no track is playing!").build();
        }
    }

    public void skip(AudioTrack track, InteractionHook hook){
        if(scheduler.getQueue().contains(track)){
            scheduler.removeTrackFromQueue(track);
            hook.editOriginal("Successfully removed track from queue").queue();
            
        }
        else{
            if(track == null){
                player.stopTrack();
                if(scheduler.isEmpty()){
                    disconnect();
                }
                else{
                    AudioTrack next = scheduler.dequeue();
                    player.startTrack(next, false);
                }
                hook.editOriginal("Successfully skipped track!").queue();
            }
            else{
                hook.editOriginal("queue does not contain track").queue();
            }
        }
    }


    public List<AudioTrack> getQueue() {
        return scheduler.getQueue();
    }
    public AudioTrack getCurrentTrack() {
        return player.getPlayingTrack();
    }
    public void disconnect() {
        scheduler.emptyQueue();
        player.stopTrack();
        audioManager.closeAudioConnection();
    }
    public boolean pause() {
        if(player.isPaused()){
            player.setPaused(false);
            return false;
        }
        else{
            player.setPaused(true);
            return true;
        }
    }
    public void clear() {
        scheduler.emptyQueue();
    }
	public void connectToVoiceChannel(VoiceChannel channel) {
        audioManager.openAudioConnection(channel);
	}

    public static HashMap<Guild, MusicBot> getBots() {
        return bots;
    }

    public static void addBot(Guild guild, VoiceChannel channel) {
        bots.put(guild, new MusicBot(channel));
    }


}