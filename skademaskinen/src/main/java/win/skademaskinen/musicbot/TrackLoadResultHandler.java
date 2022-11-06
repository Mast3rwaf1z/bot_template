package win.skademaskinen.musicbot;

import java.util.HashMap;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import win.skademaskinen.utils.Utils;

public class TrackLoadResultHandler implements AudioLoadResultHandler {
    EmbedBuilder builder = new EmbedBuilder();
    private MusicBot bot;
    private boolean isDone = false;
    private String url;
    private List<ActionRow> actionRows;

    public TrackLoadResultHandler(MusicBot bot, String url){
        this.bot = bot;
        this.url = url;
    }
    
    @Override
    public void trackLoaded(AudioTrack track) {
        builder.setDescription("[" + track.getInfo().title + "](" + track.getInfo().uri + ")");
        if(bot.player.getPlayingTrack() != null){
            bot.scheduler.enqueue(track);
            builder.setTitle("Track queued");
        }
        else{
            bot.player.startTrack(track, false);
            builder.setTitle("Track started");
        }
        if(bot.player.isPaused()){
            builder.appendDescription("\nThe bot is paused!");
        }
        builder.setFooter("Duration: " + Utils.getTime(track.getDuration()));
        builder.setAuthor(track.getInfo().title);
        builder.setThumbnail("http://img.youtube.com/vi/"+track.getIdentifier()+"/0.jpg");
        actionRows.add(ActionRow.of(Button.primary("add more", "Add More"), Button.secondary("show queue", "Show Queue"), Button.danger("skip", "Skip")));
        //hook.editOriginalEmbeds(builder.build()).setActionRow(Button.primary("add more", "Add More"), Button.secondary("show queue", "Show Queue"), Button.danger("skip", "Skip")).queue();
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
            bot.scheduler.enqueue(track);
            duration = duration+track.getPosition();
        }
        if(bot.player.isPaused()){
            builder.appendDescription("\nThe bot is paused!");
        }
        if(bot.player.getPlayingTrack() == null){
            bot.player.startTrack(playlist.getSelectedTrack(), false);
        }
        builder.setFooter("Total playlist length: " + duration+"ms");
        builder.setThumbnail("http://img.youtube.com/vi/"+playlist.getSelectedTrack().getIdentifier()+"/0.jpg");
        actionRows.add(ActionRow.of(Button.primary("add more", "Add More"), Button.secondary("show queue", "Show Queue")));
        //hook.editOriginalEmbeds(builder.build()).setActionRow(Button.primary("add more", "Add More"), Button.secondary("show queue", "Show Queue")).queue();
        isDone = true;
    }

    private void handleSearchResult(AudioPlaylist playlist) {
        EmbedBuilder builder = new EmbedBuilder();
        SelectMenu.Builder menuBuilder = SelectMenu.create("playlist");
        builder.setTitle("Choose a track to play:");
        for(AudioTrack track : playlist.getTracks()){
            menuBuilder.addOption(track.getInfo().title, track.getInfo().uri);
        }
        menuBuilder.setPlaceholder("select a track");
        builder.setThumbnail("http://img.youtube.com/vi/"+playlist.getTracks().get(0).getIdentifier()+"/0.jpg");
        builder.setDescription("**Search term: **"+ url.replace("ytsearch:", " "));
        bot.selectMenus = new HashMap<>();
        bot.selectMenus.put(menuBuilder.getId(), menuBuilder.build());
        actionRows.add(ActionRow.of(menuBuilder.build()));
        actionRows.add(ActionRow.of(Button.secondary("add all"+menuBuilder.getId(), "Add all")));
        //hook.editOriginalEmbeds(builder.build()).setComponents(ActionRow.of(menuBuilder.build()), ActionRow.of(Button.secondary("add all"+menuBuilder.getId(), "Add all"))).queue();
        isDone = true;
    }

    @Override
    public void noMatches() {
        builder.setTitle("No matches");
        actionRows.add(ActionRow.of(Button.primary("add more", "Add More"), Button.secondary("show queue", "Show Queue")));
        //hook.editOriginalEmbeds(builder.build()).setActionRow(Button.primary("add more", "Add More"), Button.secondary("show queue", "Show Queue")).queue();
        isDone = true;
    }
    @Override
    public void loadFailed(FriendlyException e) {
        builder.setTitle("Load failed");
        builder.appendDescription("Error code:\n");
        builder.appendDescription(e.getMessage());
        actionRows.add(ActionRow.of(Button.primary("add more", "Add More"), Button.secondary("show queue", "Show Queue")));
        //hook.editOriginalEmbeds(builder.build()).setActionRow(Button.primary("add more", "Add More"), Button.secondary("show queue", "Show Queue")).queue();
        isDone = true;
    }

    public MessageEmbed getEmbed(){
        return builder.build();
    }

    public boolean isDone() {
        return isDone;
    }
}
