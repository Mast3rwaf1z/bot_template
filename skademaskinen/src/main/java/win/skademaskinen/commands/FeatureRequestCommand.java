package win.skademaskinen.commands;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

public class FeatureRequestCommand implements Command{
    private boolean successTag = false;
    private Member author;
    private Guild guild;

    public FeatureRequestCommand(SlashCommandInteractionEvent event){
        author = event.getMember();
        guild = event.getGuild();
    }

    @Override
    public String build() {
        return log("author: "+author.getUser().getAsTag()+" server: "+guild.getName(), successTag);
    }

    @Override
    public Modal run() {
        TextInput featureName = TextInput.create("name", "Feature name", TextInputStyle.SHORT).setPlaceholder("Write a name for your feature").build();
        TextInput featureDescription = TextInput.create("description", "Feature Description", TextInputStyle.PARAGRAPH).setPlaceholder("please write a description for your request").build();
        successTag = true;
        return Modal.create("featurerequest", "Feature Request").addActionRow(featureName).addActionRow(featureDescription).build();
    }

    @Override
    public boolean shouldEphemeral() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<ActionRow> getActionRows() {
        // TODO Auto-generated method stub
        return new ArrayList<>();
    }
    
}
