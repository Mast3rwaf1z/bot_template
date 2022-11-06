package win.skademaskinen.commands;

import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class SpawnMessageCommand implements Command{
    private boolean successTag = false;
    private List<ActionRow> actionRows;
    private Member author;
    private Guild guild;

    public SpawnMessageCommand(SlashCommandInteractionEvent event){
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
            actionRows.add(ActionRow.of(Button.success("finish_button", "Finish"), 
            Button.secondary("set_title", "Set Title"),
            Button.secondary("set_description", "Set Description"),
            Button.secondary("add_field", "Add Field"),
            Button.secondary("add_image", "Add Image")));
            actionRows.add(ActionRow.of(Button.danger("clear_embed", "Clear")));
            successTag = true;
            return new EmbedBuilder().setTitle("empty embed").build();
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
        return actionRows;
    }
    
}
