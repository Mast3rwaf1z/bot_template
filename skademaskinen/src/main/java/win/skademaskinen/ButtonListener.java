package win.skademaskinen;

import java.util.List;

import org.json.JSONObject;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ButtonListener extends ListenerAdapter{
	public void onButtonInteraction(ButtonInteractionEvent event){
        Guild guild = event.getGuild();
        Member member = event.getMember();
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
                EmbedBuilder builder = new EmbedBuilder();
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
    }
}
