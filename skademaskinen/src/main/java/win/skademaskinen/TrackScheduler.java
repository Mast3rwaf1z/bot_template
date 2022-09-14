package win.skademaskinen;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import net.dv8tion.jda.api.managers.AudioManager;

public class TrackScheduler extends AudioEventAdapter {
    private LinkedBlockingQueue<AudioTrack> queue = new LinkedBlockingQueue<AudioTrack>();
    AudioManager manager;
    TrackScheduler(AudioManager manager){
        this.manager = manager;
    }


    public void enqueue(AudioTrack track) {
        queue.offer(track);
    }
    public AudioTrack dequeue(){
        return queue.poll();
    }
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason){
        if(endReason.mayStartNext){
            player.startTrack(dequeue(), false);
        }
        else{
            Shell.printer(endReason.name());
        }
        if(queue.isEmpty() && player.getPlayingTrack() == null){
            new Thread(){
                public void run(){
                    try {
                        timeout(player);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }
    public boolean isEmpty() {
        return queue.isEmpty();
    }
    public ArrayList<AudioTrack> getQueue() {
        ArrayList<AudioTrack> tracks = new ArrayList<>();
        for(AudioTrack track : queue){
            tracks.add(track);
        }
        return tracks;
    }
    public void emptyQueue() {
        while(!queue.isEmpty()){
            queue.poll();
        }
    }
    private void timeout(AudioPlayer player) throws InterruptedException{
        Thread.sleep(1000);
        if(player.getPlayingTrack() == null){
            manager.closeAudioConnection();
        }
        
    }
	public void removeTrackFromQueue(AudioTrack track) {
        queue.remove(track);
	}
}
