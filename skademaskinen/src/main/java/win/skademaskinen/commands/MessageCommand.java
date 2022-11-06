package win.skademaskinen.commands;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class MessageCommand implements Command{
    private boolean successTag = false;
    private Member author;
    private SlashCommandInteractionEvent event;
    private Guild guild;

    public MessageCommand(SlashCommandInteractionEvent event){
        author = event.getMember();
        this.event = event;
        guild = event.getGuild();
    }

    @Override
    public String build() {
        return log("author: "+author.getUser().getAsTag()+" server: "+guild.getName(), successTag);
    }

    @Override
    public Object run() {
        if(author.hasPermission(Permission.ADMINISTRATOR)){
            Message announcement;
            if(event.getOption("channel_id") == null){
                announcement = event.getMessageChannel()
                    .getHistoryAround(event.getOption("message_id")
                    .getAsString(), 1)
                    .complete()
                    .getMessageById(event.getOption("message_id")
                    .getAsString());
            }
            else{
                announcement = guild.getTextChannelById(event.getOption("channel_id").getAsString())
                    .getHistoryAround(event.getOption("message_id")
                    .getAsString(), 1)
                    .complete()
                    .getMessageById(event.getOption("message_id")
                    .getAsString());
            }
            successTag = true;
            return MessageCreateData.fromMessage(announcement);
        }
        else{
            successTag = false;
            return permissionDenied();
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
