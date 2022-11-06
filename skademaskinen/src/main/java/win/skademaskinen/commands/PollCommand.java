package win.skademaskinen.commands;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

public class PollCommand implements Command{
    private boolean successTag = false;
    private Member author;
    private Guild guild;

    public PollCommand(SlashCommandInteractionEvent event){
        author = event.getMember();
        guild = event.getGuild();
    }

    @Override
    public String build() {
        return log("author: "+author.getUser().getAsTag()+" server: "+guild.getName(), successTag);
    }

    @Override
    public Object run() {
        if(author.hasPermission(Permission.ADMINISTRATOR)){
            TextInput description = TextInput.create("description", "Description", TextInputStyle.SHORT)
                .setPlaceholder("Write a description for a poll")
                .build();

            TextInput options = TextInput.create("options", "List of options", TextInputStyle.PARAGRAPH)
                .setPlaceholder("write a list of options (comma seperated)")
                .build();
            successTag = true;
            return Modal.create("poll_modal", "Create a poll").addActionRow(description).addActionRow(options).build();
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
