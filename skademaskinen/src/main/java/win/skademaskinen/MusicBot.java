package win.skademaskinen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu.Builder;
import net.dv8tion.jda.api.managers.AudioManager;

public class MusicBot {
    static private AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    private AudioPlayer player = playerManager.createPlayer();
    private TrackScheduler scheduler;
    private AudioChannel channel;
    private Guild guild;
    private AudioManager audioManager;
    public Map<String, SelectMenu> selectMenus = new HashMap<String, SelectMenu>();

    
    public MusicBot(AudioChannel channel){
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
    public void play(String url, InteractionHook hook) {
        connect();
		EmbedBuilder builder = new EmbedBuilder();
        Future<Void> future = playerManager.loadItem(url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
				builder.setDescription("[" + track.getInfo().title + "](" + track.getInfo().uri + ")");
                if(player.getPlayingTrack() != null){
                    scheduler.enqueue(track);
					builder.setTitle("Track queued");
                }
                else{
                    player.startTrack(track, false);
					builder.setTitle("Track started");
                }
                if(player.isPaused()){
                    builder.appendDescription("\nThe bot is paused!");
                }
                builder.setFooter("Length: " + track.getDuration()+"ms");
                builder.setThumbnail("http://img.youtube.com/vi/"+track.getIdentifier()+"/0.jpg");

            } 
            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if(playlist.isSearchResult()){
                    handleSearchResult(playlist);
                    return;
                }
				builder.setTitle("Playlist loaded");
                List<AudioTrack> tracks = playlist.getTracks();
                long duration = 0;
                for(AudioTrack track : tracks){
					builder.appendDescription("[" + track.getInfo().title + "](" + track.getInfo().uri + ")\n");
                    scheduler.enqueue(track);
                    duration = duration+track.getPosition();
                }
                if(player.isPaused()){
                    builder.appendDescription("\nThe bot is paused!");
                }
                if(player.getPlayingTrack() == null){
                    player.startTrack(playlist.getSelectedTrack(), false);
                }
                builder.setFooter("Total playlist length: " + duration+"ms");
                builder.setThumbnail("http://img.youtube.com/vi/"+playlist.getSelectedTrack().getIdentifier()+"/0.jpg");
            }

            private void handleSearchResult(AudioPlaylist playlist) {
                EmbedBuilder builder = new EmbedBuilder();
                Builder menuBuilder = SelectMenu.create("playlist");
                builder.setTitle("Choose a track to play:");
                for(AudioTrack track : playlist.getTracks()){
                    menuBuilder.addOption(track.getInfo().title, track.getInfo().uri);
                }
                menuBuilder.setPlaceholder("select a track");
                builder.setThumbnail("http://img.youtube.com/vi/"+playlist.getTracks().get(0).getIdentifier()+"/0.jpg");
                builder.setDescription("**Search term: **"+ url.replace("ytsearch:", " "));
                selectMenus = new HashMap<>();
                selectMenus.put(menuBuilder.getId(), menuBuilder.build());
                hook.editOriginalEmbeds(builder.build()).setActionRows(ActionRow.of(menuBuilder.build()), ActionRow.of(Button.secondary("add all"+menuBuilder.getId(), "Add all"))).queue();
                App.reader.printAbove("here");
            }

            @Override
            public void noMatches() {
				builder.setTitle("No matches");
            }
            @Override
            public void loadFailed(FriendlyException e) {
				builder.setTitle("Load failed");
				builder.appendDescription("Error code:\n");
				builder.appendDescription(e.getMessage());
            }
        });
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e1) {
            Colors.exceptionHandler(e1);
        }
        if(!builder.isEmpty()){
            hook.editOriginalEmbeds(builder.build()).setActionRow(Button.primary("add more", "Add More"), Button.secondary("show queue", "Show Queue")).queue();
        }
    }

    public void skip(SlashCommandInteractionEvent event) {
        if(player.getPlayingTrack() != null){
            player.stopTrack();
            if(!scheduler.isEmpty()){
                AudioTrack track = scheduler.dequeue();
                player.startTrack(track, false);
                event.reply("Started track: " + track.getInfo().title).queue();
            }
            else{
                event.reply("Queue is now empty").queue();
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
	public void connectToVoiceChannel(AudioChannel channel) {
        audioManager.openAudioConnection(channel);
	}


}
