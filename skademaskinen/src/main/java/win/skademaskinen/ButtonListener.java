package win.skademaskinen;

import java.util.List;

import org.json.JSONObject;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

public class ButtonListener extends ListenerAdapter{
	public void onButtonInteraction(ButtonInteractionEvent event){
        EmbedBuilder builder = new EmbedBuilder();
        Member member = event.getMember();
        Guild guild = event.getGuild();
        if(event.getMessage().getInteraction() == null){
            switch(event.getComponentId()){
                case "add more":
                    TextInput input = TextInput.create("url", "URL or search term", TextInputStyle.SHORT).build();
                    event.replyModal(Modal.create("add more", "Add More").addActionRow(input).build()).queue();
                    break;
                case "show queue":
                    if(event.getMember().getVoiceState().inAudioChannel() && CommandListener.bots.containsKey(event.getGuild())){
                        int page = 0;
                        List<AudioTrack> tracks;
                        if(CommandListener.bots.get(event.getGuild()).getQueue().size()< 15){
                            tracks = CommandListener.bots.get(event.getGuild()).getQueue();
                        }
                        else{
                            tracks = CommandListener.bots.get(event.getGuild()).getQueue().subList(page*15, (page*15)+15);
                        }
                        builder.setTitle("Track queue");
                        int totalTime = 0;
                        for(AudioTrack track : tracks){
                            builder.addField("", "["+track.getInfo().title+"]("+track.getInfo().uri+")\n Duration: "+CommandListener.getTime(track.getDuration()), false);
                            totalTime += track.getDuration();
                        }
                        builder.setFooter("Total time remaining: " + CommandListener.getTime(totalTime-CommandListener.bots.get(event.getGuild()).getCurrentTrack().getDuration()) + " | Total tracks in queue: " + CommandListener.bots.get(event.getGuild()).getQueue().size());
                        AudioTrack current = CommandListener.bots.get(guild).player.getPlayingTrack();
                        builder.setDescription("Currently playing track:\n["+current.getInfo().title+"]("+current.getInfo().uri+")");
                        builder.appendDescription("\nDuration: "+CommandListener.getTime(current.getDuration()));
                        builder.setThumbnail("http://img.youtube.com/vi/"+current.getIdentifier()+"/0.jpg");
                        event.replyEmbeds(builder.build()).queue();
                    }
                    break;
                case "skip":
                    if(CommandListener.bots.containsKey(guild)){
                        event.deferReply().queue();
                        AudioTrack selectedTrack = null;
                        for(AudioTrack track : CommandListener.bots.get(guild).getQueue()){
                            if(track.getInfo().title.equals(event.getMessage().getEmbeds().get(0).getAuthor().getName())){
                                selectedTrack = track;
                            }
                        }
                        CommandListener.bots.get(guild).skip(selectedTrack, event.getHook());
                    }
                    break;
            }
            return;
        }
        switch(event.getMessage().getInteraction().getName()){
            case "poll":
            if(!Config.getJSON("polls.json").has(event.getMessageId())){
                Config.registerNewPoll(event.getMessageId());
            }
                JSONObject poll = Config.getPoll(event.getMessageId());
                if(!poll.has(member.getId())){
                    Config.addMemberToPoll(member.getId(), event.getMessageId());
                }
                Config.modifyPollEntryForMember(member.getId(), event.getMessageId(), event.getButton().getId());
                poll = Config.getPoll(event.getMessageId());
                MessageEmbed embed = event.getMessage().getEmbeds().get(0);
                builder.setTitle(embed.getTitle());
                builder.setDescription(embed.getDescription());

                List<Field> fields = embed.getFields();
                for(Field field: fields){
                    int value = 0;
                    for(String key: poll.keySet()){
                        if(poll.getJSONArray(key).toList().contains(field.getName())){
                            value++;
                        }
                    }
                    String newValue = "Votes: " + String.valueOf(value);
                    field = new Field(field.getName(), newValue, false);
                    builder.addField(field);
                }
                event.getMessage().editMessageEmbeds(builder.build()).queue();
                event.deferEdit().queue();
                break;
        }
        if(event.getComponentId().contains("add all")){
            event.deferReply().queue();
            List<SelectOption> urls = CommandListener.bots.get(event.getGuild()).selectMenus.get(event.getComponentId().replace("add all", "")).getOptions();
            for(SelectOption url : urls){
                if(!CommandListener.bots.containsKey(event.getGuild())){
                    CommandListener.bots.put(event.getGuild(), new MusicBot(event.getMember().getVoiceState().getChannel()));
                }
                CommandListener.bots.get(event.getGuild()).play(url.getValue(), event.getHook());
            }
        }
    }
}
